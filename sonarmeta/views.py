from django.db.models import Count, Q
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.filters import SearchFilter
from rest_framework.mixins import RetrieveModelMixin, CreateModelMixin, UpdateModelMixin, DestroyModelMixin
from rest_framework.permissions import SAFE_METHODS, AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.viewsets import GenericViewSet, ModelViewSet
from . import models, serializers, pagination


class ProfileViewSet(RetrieveModelMixin, GenericViewSet):
    http_method_names = ['get', 'patch', 'head', 'options']
    queryset = models.Profile.objects.all()
    serializer_class = serializers.ProfileSerializer
    permission_classes = [AllowAny]

    # endpoint: sonarmeta/profiles/me/
    # GET or PUT current user's profile
    @action(detail=False, methods=['GET', 'PATCH'])
    def me(self, request):
        # if there is no profile matched to User
        # then it will be created automatically
        try:
            profile = models.Profile.objects.get(user_id=request.user.id)
        except models.Profile.DoesNotExist:
            profile = models.Profile.objects.create(
                user_id=request.user.id, username=f'sonarmeta-{request.user.id}')

        if request.method == 'GET':
            serializer = serializers.ProfileSerializer(profile)
            return Response(serializer.data)
        elif request.method == 'PATCH':
            serializer = serializers \
                .ProfileSerializer(profile, data=request.data)
            serializer.is_valid(raise_exception=True)
            serializer.save()
            return Response(serializer.data)

    # endpoint: sonarmeta/profiles/subscribers/
    @action(detail=False, methods=['GET'])
    def subscribers(self, request):
        profile = models.Profile.objects.get(user_id=request.user.id)
        serializer = serializers.SubscribeProfileSerializer(profile)
        return Response(serializer.data)


class UserFavoriteViewSet(ModelViewSet):
    '''
    This viewset is used to get histories of current profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.UserResourceFavoriteSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['resource__title', 'resource__tags']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.UserResourceFavorite.objects \
            .prefetch_related('profile') \
            .filter(profile_id=profile_id)


class UserHistoryViewSet(ModelViewSet):
    '''
    This viewset is used to get resource histories of current profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.UserResourceHistorySerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['resource__title', 'resource__tags']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.UserResourceHistory.objects \
            .prefetch_related('profile') \
            .filter(profile_id=profile_id)


class UserSubscribeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserSubscribeSerializer
    pagination_class = pagination.TwelvePagination

    def get_queryset(self):
        return models.UserSubscribe.objects \
            .filter(creator_id=self.kwargs['profile_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        return {
            'creator_id': self.kwargs['profile_pk'],
            'subscriber_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserBlacklistViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserBlacklistSerializer
    pagination_class = pagination.TwelvePagination

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.UserBlacklist.objects.filter(preventer_id=profile_id)

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        return {
            'be_prevented_id': self.kwargs['profile_pk'],
            'preventer_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class MessageViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.MessageSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.Message.objects.filter(profile_id=profile_id)


class ResourceSeriesViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    queryset = models.ResourceSeries.objects.all()
    serializer_class = serializers.ResourceSeriesSerializer
    pagination_class = pagination.TwelvePagination

    def get_permissions(self):
        if self.request.method == 'POST' or self.request.method == 'DELETE':
            return [IsAuthenticated()]
        return [AllowAny()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # Override the resource series model viewset destory(DELETE) method
    # First, we need to DELETE all the branches attched to this profile in this series
    # Next, we need to figure out whether this profile is the fk of this series or not
    # If it is, we need to set the fk of this series to NULL(None in Python)
    # Else, we do nothing
    # Finally, we response front end with the standard 204 code
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        series = models.ResourceSeries.objects.get(pk=kwargs['pk'])
        models.ResourceBranch.objects \
            .filter(series_id=kwargs['pk'], profile_id=profile_id) \
            .delete()
        if series.profile_id == profile_id:
            series.profile_id = None
            series.save()
        return Response(status=status.HTTP_204_NO_CONTENT)

    # endpoint: sonarmeta/series/recommendations/
    # This method is used to recommend series for Series page
    @action(detail=False, methods=['GET'])
    def recommendations(self, request):
        series = models.ResourceSeries.objects \
            .prefetch_related('profile') \
            .all()
        serializer = serializers \
            .DisplayResourceSeriesSerializer(series, many=True)
        return Response(serializer.data)


class MeResourceSeriesViewSet(ModelViewSet):
    '''
    This ViewSet is used to get current profile's series in series management page
    Searching, sorting and Pagination is also the responsibilities of this ViewSet
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.ResourceSeriesSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.ResourceSeries.objects \
            .prefetch_related("branches") \
            .filter(Q(branches__profile__id=profile_id) | Q(profile_id=profile_id)) \
            .distinct()


class ResourceBranchViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceBranchSerializer
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title']

    def get_queryset(self):
        return models.ResourceBranch.objects \
            .prefetch_related('profile__user') \
            .filter(series_id=self.kwargs['series_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'series_id': self.kwargs['series_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class ResourceViewSet(CreateModelMixin,
                      RetrieveModelMixin,
                      UpdateModelMixin,
                      DestroyModelMixin,
                      GenericViewSet):
    '''
    This ViewSet's GET method is only used to retrieve,
    since ResourceSerializer is very complex and it will cause
    a lot of SQL sentences, which will slow down the system
    '''
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    queryset = models.Resource.objects.all().prefetch_related('profile', 'branch')
    serializer_class = serializers.ResourceSerializer

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # endpoint: sonarmeta/resources/attach/
    # This method is used to attach a resource to a branch
    @action(detail=False, methods=['POST'])
    def attach(self, request):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        for item in request.data.get("resource_id_list"):
            resource = models.Resource.objects.get(pk=item)
            if resource.profile_id == profile_id:
                resource.branch_id = request.data.get("branch_id")
                resource.save()
            else:
                return Response(status.HTTP_401_UNAUTHORIZED)
        return Response(status=status.HTTP_201_CREATED)

    # endpoint: sonarmeta/resources/detach/
    # This method is used to detach a resource from a branch
    @action(detail=False, methods=['DELETE'])
    def detach(self, request):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        resource = models.Resource.objects \
            .get(pk=request.data.get("resource_id"))
        if resource.profile_id == profile_id:
            resource.branch_id = None
            resource.save()
            return Response(status=status.HTTP_204_NO_CONTENT)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class RecommendationResourceViewSet(ModelViewSet):
    '''
    This viewset is used to recommend resources
    '''
    http_method_names = ['get', 'head', 'options']
    queryset = models.Resource.objects \
        .prefetch_related('profile') \
        .filter(status='P')
    serializer_class = serializers.RecommendResourceSerializer
    permission_classes = [AllowAny]
    pagination_class = pagination.TwelvePagination


class SearchResourceViewSet(ModelViewSet):
    '''
    This viewset is used to search resources
    '''
    http_method_names = ['get', 'head', 'options']
    queryset = models.Resource.objects.all().prefetch_related('profile')
    serializer_class = serializers.SearchResourceSerializer
    permission_classes = [AllowAny]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title', 'tags']


class MeResourceViewSet(ModelViewSet):
    '''
    This viewset is used to get resources of the current profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.SimpleResourceSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title', 'tags']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.Resource.objects.prefetch_related('profile').filter(profile_id=profile_id)


class BranchResourceViewSet(ModelViewSet):
    '''
    This viewset is used to get resources of a certain branch
    Only be used at the series management page, and do searching
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.SimpleResourceSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title', 'tags']

    def get_queryset(self):
        branch_id = self.kwargs['branch_pk']
        return models.Resource.objects.prefetch_related('profile').filter(branch_id=branch_id)


class ResourceReviewHeatViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceReviewSerializer
    pagination_class = pagination.TwelvePagination

    # user resource review is designed to list at the review's side
    # so get its queryset by resource id and sort by heat
    def get_queryset(self):
        return models.ResourceReview.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk']) \
            .annotate(num_likes=Count('likes__like_flag', filter=Q(likes__like_flag=True))-Count('likes__like_flag', filter=Q(likes__like_flag=False))) \
            .order_by('-num_likes', '-time')

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class ResourceReviewNewestViewSet(ModelViewSet):
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.ResourceReviewSerializer
    pagination_class = pagination.TwelvePagination

    # user resource review is designed to list at the review's side
    # so get its queryset by resource id and sort by time
    def get_queryset(self):
        return models.ResourceReview.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk']) \
            .order_by('-time')

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]


class ResourceReplyViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceReplySerializer
    pagination_class = pagination.TwelvePagination

    # user resource reply is designed to list at the reply's side
    # so get its queryset by review id
    def get_queryset(self):
        return models.ResourceReply.objects \
            .prefetch_related('profile__user') \
            .filter(review_id=self.kwargs['review_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'review_id': self.kwargs['review_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceHistoryViewSet(ModelViewSet):
    '''
    This ViewSet is not used to GET user's entry history
    Since it is from the perspective of resources
    See GET histories of user at ProfileViewSet
    '''
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.DisplayUserResourceHistorySerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return models.UserResourceHistory.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceEntryViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'head', 'options']
    serializer_class = serializers.UserResourceEntrySerializer

    # user resource entry is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceEntry.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceLikeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserResourceLikeSerializer

    # user resource like is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceLike.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceFavoriteViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.DisplayUserResourceFavoriteSerializer

    # user resource favorite is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceFavorite.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceDownloadViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'head', 'options']
    serializer_class = serializers.UserResourceDownloadSerializer

    # user resource download is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceDownload.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceShareViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'head', 'options']
    serializer_class = serializers.UserResourceShareSerializer

    # user resource share is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceShare.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserReviewLikeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.UserReviewLikeSerializer

    # user review like is designed to list at the review's side
    # so get its queryset by review id
    def get_queryset(self):
        return models.UserReviewLike.objects \
            .prefetch_related('profile__user') \
            .filter(review_id=self.kwargs['review_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'review_id': self.kwargs['review_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserReplyLikeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.UserReplyLikeSerializer

    # user reply like is designed to list at the reply's side
    # so get its queryset by reply id

    def get_queryset(self):
        return models.UserReplyLike.objects \
            .prefetch_related('profile__user') \
            .filter(reply_id=self.kwargs['reply_pk'])

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'reply_id': self.kwargs['reply_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }
