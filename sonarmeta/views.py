from ast import Or
from django.db.models import Count, Q, Sum
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.filters import SearchFilter, OrderingFilter
from rest_framework.mixins import RetrieveModelMixin
from rest_framework.permissions import SAFE_METHODS, AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.viewsets import GenericViewSet, ModelViewSet
from . import models, serializers


class ProfileViewSet(RetrieveModelMixin, GenericViewSet):
    http_method_names = ['get', 'head', 'options']
    queryset = models.Profile.objects.all()
    serializer_class = serializers.ProfileSerializer
    permission_classes = [AllowAny]

    # endpoint: sonarmeta/profiles/me/
    # GET or PUT current user's profile
    @action(detail=False, methods=['GET', 'PATCH'])
    def me(self, request):
        # if there is no profile matched to User
        # then it will be created automatically
        (profile, created) = models.Profile.objects.get_or_create(
            user_id=request.user.id)

        if request.method == 'GET':
            serializer = serializers.ProfileSerializer(profile)
            return Response(serializer.data)
        elif request.method == 'PATCH':
            serializer = serializers \
                .ProfileSerializer(profile, data=request.data)
            serializer.is_valid(raise_exception=True)
            serializer.save()
            return Response(serializer.data)

    # endpoint: sonarmeta/profiles/histories/
    # GET current user's histories
    @action(detail=False, methods=['GET'])
    def histories(self, request):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        histories = models.UserResourceHistory.objects \
            .prefetch_related('profile') \
            .filter(profile_id=profile_id)
        serializer = serializers \
            .UserResourceHistorySerializer(histories, many=True)
        return Response(serializer.data)


class UserSubscribeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserSubscribeSerializer

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


class MeResourceSeriesViewSet(ModelViewSet):
    '''
    This ViewSet is used to get current profile's series in series management page
    Searching, sorting and Pagination is also the responsibilities of this ViewSet
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.ResourceSeriesSerializer
    permission_classes = [IsAuthenticated]
    filter_backends = [SearchFilter, OrderingFilter]
    search_fields = ['title']

    def get_queryset(self):
        profile_id = models.Profile.objects.get(
            user_id=self.request.user.id).id
        return models.ResourceSeries.objects \
            .prefetch_related("branches") \
            .filter(Q(branches__profile__id=profile_id) | Q(profile_id=profile_id)) \
            .distinct()


class ResourceBranchViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    queryset = models.ResourceBranch.objects.all().prefetch_related('profile', 'series')
    serializer_class = serializers.ResourceBranchSerializer

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    def get_queryset(self):
        return models.ResourceBranch.objects \
            .prefetch_related('profile__user') \
            .filter(series_id=self.kwargs['series_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'series_id': self.kwargs['series_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class ResourceViewSet(ModelViewSet):
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

    # endpoint: sonarmeta/resources/recommendations/
    # This method is used to recommend resources for Home and Resource page
    @action(detail=False, methods=['GET'])
    def recommendations(self, request):
        resources = models.Resource.objects \
            .prefetch_related('profile') \
            .all()
        serializer = serializers \
            .RecommendResourceSerializer(resources, many=True)
        return Response(serializer.data)

    # endpoint: sonarmeta/resources/me/
    @action(detail=False, methods=['GET'])
    def me(self, request):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        resources = models.Resource.objects \
            .prefetch_related('profile') \
            .filter(profile_id=profile_id)
        serializer = serializers \
            .SimpleResourceSerializer(resources, many=True)
        return Response(serializer.data)

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


class SearchResourceViewSet(ModelViewSet):
    '''
    This method is used to search resources
    '''
    http_method_names = ['get', 'head', 'options']
    queryset = models.Resource.objects.all().prefetch_related('profile')
    serializer_class = serializers.SearchResourceSerializer
    permission_classes = [AllowAny]
    filter_backends = [SearchFilter]
    search_fields = ['title', 'tags']


class ResourceReviewViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceReviewSerializer

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

    # endpoint: sonarmeta/resources/{resource_pk}/reviews/newest/
    # GET reviews by newest
    @action(detail=False, methods=['GET'])
    def newest(self, request, *args, **kwargs):
        reviews = models.ResourceReview.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=kwargs['resource_pk']) \
            .order_by('-time')
        serializer = serializers.ResourceReviewSerializer(reviews, many=True)
        return Response(serializer.data)


class ResourceReplyViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceReplySerializer

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
    serializer_class = serializers.UserResourceHistorySerializer
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


class UserResourceLikeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserResourceLikeSerializer

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    # user resource like is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceLike.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }


class UserResourceFavoriteViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserResourceFavoriteSerializer

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    # user resource favorite is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceFavorite.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

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

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    # user resource download is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceDownload.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

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

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    # user resource share is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceShare.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk'])

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

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    # user review like is designed to list at the review's side
    # so get its queryset by review id
    def get_queryset(self):
        return models.UserReviewLike.objects \
            .prefetch_related('profile__user') \
            .filter(review_id=self.kwargs['review_pk'])

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

    def get_permissions(self):
        if self.request.method in SAFE_METHODS:
            return [AllowAny()]
        return [IsAuthenticated()]

    # user reply like is designed to list at the reply's side
    # so get its queryset by reply id
    def get_queryset(self):
        return models.UserReplyLike.objects \
            .prefetch_related('profile__user') \
            .filter(reply_id=self.kwargs['reply_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'reply_id': self.kwargs['reply_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }
