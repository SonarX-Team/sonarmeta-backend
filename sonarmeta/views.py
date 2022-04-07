from django.db.models import Sum, Count, Q
from rest_framework import status
from rest_framework.decorators import action
from rest_framework.filters import SearchFilter
from rest_framework.mixins import RetrieveModelMixin, CreateModelMixin, UpdateModelMixin, DestroyModelMixin
from rest_framework.permissions import SAFE_METHODS, AllowAny, IsAuthenticated, IsAuthenticatedOrReadOnly
from rest_framework.response import Response
from rest_framework.viewsets import GenericViewSet, ModelViewSet
from django_filters.rest_framework import DjangoFilterBackend
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


class UserFavoriteViewSet(ModelViewSet):
    '''
    This viewset is used to get histories of the current profile
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
    This viewset is used to get resource histories of the current profile
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


class UserFollowsViewset(ModelViewSet):
    '''
    This viewset is used to get follows of the current profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.UserFollowsSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['creator__username']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.UserSubscribe.objects \
            .filter(subscriber_id=profile_id)


class UserFollowersViewset(ModelViewSet):
    '''
    This viewset is used to get followers of the current profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.UserFollowersSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['subscriber__username']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.UserSubscribe.objects \
            .filter(creator_id=profile_id)


class UserSubscribeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserSubscribeSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        return models.UserSubscribe.objects \
            .filter(creator_id=self.kwargs['profile_pk'])

    def get_serializer_context(self):
        return {
            'creator_id': self.kwargs['profile_pk'],
            'subscriber_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        subscriber_id = models.UserSubscribe.objects \
            .get(pk=kwargs['pk']).subscriber_id
        if profile_id == subscriber_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserBlacklistViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.UserBlacklistSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    pagination_class = pagination.TwelvePagination

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.UserBlacklist.objects.filter(preventer_id=profile_id)

    def get_serializer_context(self):
        return {
            'be_prevented_id': self.kwargs['profile_pk'],
            'preventer_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        preventer_id = models.UserBlacklist.objects \
            .get(pk=kwargs['pk']).preventer_id
        if profile_id == preventer_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class MessageViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.MessageSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.Message.objects.filter(profile_id=profile_id)

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.Message.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class ResourceSeriesViewSet(RetrieveModelMixin, CreateModelMixin, DestroyModelMixin, GenericViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    queryset = models.ResourceSeries.objects.all()
    serializer_class = serializers.ResourceSeriesSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

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
            .all()[:12]
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
            .prefetch_related('branches') \
            .filter(Q(branches__profile__id=profile_id) | Q(profile_id=profile_id)) \
            .distinct()


class ProfileResourceSeriesViewSet(ModelViewSet):
    '''
    This ViewSet is used to get space profile's series in space page
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.ResourceSeriesSerializer
    permission_classes = [AllowAny]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title']

    def get_queryset(self):
        return models.ResourceSeries.objects \
            .prefetch_related('branches') \
            .filter(Q(branches__profile__id=self.kwargs['profile_pk']) | Q(profile_id=self.kwargs['profile_pk'])) \
            .distinct()


class ResourceBranchViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceBranchSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter, DjangoFilterBackend]
    filterset_fields = ['profile__id']
    search_fields = ['title']

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

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.ResourceBranch.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.ResourceBranch.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # endpoint: sonarmeta/series/{series_pk}/branches/covers/
    @action(detail=False, methods=['GET'])
    def covers(self, request, *args, **kwargs):
        count = 0
        covers = []
        branches = models.ResourceBranch.objects \
            .prefetch_related('profile__user') \
            .filter(series_id=kwargs['series_pk']) \
            .annotate(heat=Sum('resources__entries')) \
            .order_by('-heat')
        length = 0
        for branch in branches:
            if len(branch.resources.all()) > length:
                length = len(branch.resources.all())
        for i in length:
            if count < 4 and i < length:
                for branch in branches:
                    if count < 4 and len(branch.resources.all()) > i and branch.resources.all()[i]:
                        covers.append(branch.resources.all()[i].cover)
                        count += 1
        return Response(covers, status=status.HTTP_200_OK)


class ResourceViewSet(RetrieveModelMixin, CreateModelMixin, UpdateModelMixin, DestroyModelMixin, GenericViewSet):
    '''
    This ViewSet's GET method is only used to retrieve,
    since ResourceSerializer is very complex and it will cause
    a lot of SQL sentences, which will slow down the system
    '''
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    queryset = models.Resource.objects.all().prefetch_related('profile', 'branch')
    serializer_class = serializers.ResourceSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.Resource.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.Resource.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # endpoint: sonarmeta/resources/attach/
    # This method is used to attach a resource to a branch
    @action(detail=False, methods=['POST'])
    def attach(self, request):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        for item in request.data.get('resource_id_list'):
            resource = models.Resource.objects.get(pk=item)
            if resource.profile_id == profile_id and resource.status == 'P':
                resource.branch_id = request.data.get('branch_id')
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
            .get(pk=request.data.get('resource_id'))
        if resource.profile_id == profile_id:
            resource.branch_id = None
            resource.save()
            return Response(status=status.HTTP_204_NO_CONTENT)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class ResourceBasicSettingsViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'head', 'options']
    serializer_class = serializers.ResourceBasicSettingsSerailizer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        return models.ResourceBasicSettings.objects \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk']
        }

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        resource_id = models.ResourceBasicSettings.objects \
            .get(pk=kwargs['pk']).resource_id
        owner_profile_id = models.Resource.objects \
            .get(pk=resource_id).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class ResourceLightSettingsViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'head', 'options']
    serializer_class = serializers.ResourceLightSettingsSerailizer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        return models.ResourceLightSettings.objects \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk']
        }

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        resource_id = models.ResourceLightSettings.objects \
            .get(pk=kwargs['pk']).resource_id
        owner_profile_id = models.Resource.objects \
            .get(pk=resource_id).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class ResourceMaterialSettingsViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'head', 'options']
    serializer_class = serializers.ResourceMaterialSettingsSerailizer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        return models.ResourceMaterialSettings.objects \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk']
        }

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        resource_id = models.ResourceMaterialSettings.objects \
            .get(pk=kwargs['pk']).resource_id
        owner_profile_id = models.Resource.objects \
            .get(pk=resource_id).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class ResourcePostProcessingSettingsViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'head', 'options']
    serializer_class = serializers.ResourcePostProcessingSettingsSerailizer
    permission_classes = [IsAuthenticatedOrReadOnly]

    def get_queryset(self):
        return models.ResourcePostProcessingSettings.objects \
            .filter(resource_id=self.kwargs['resource_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk']
        }

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        resource_id = models.ResourcePostProcessingSettings.objects \
            .get(pk=kwargs['pk']).resource_id
        owner_profile_id = models.Resource.objects \
            .get(pk=resource_id).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
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
    This viewset is used to provide resources of the current profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.SimpleResourceSerializer
    permission_classes = [IsAuthenticated]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter, DjangoFilterBackend]
    filterset_fields = ['status']
    search_fields = ['title', 'tags']

    def get_queryset(self):
        profile_id = models.Profile.objects \
            .get(user_id=self.request.user.id).id
        return models.Resource.objects.prefetch_related('profile').filter(profile_id=profile_id)


class ProfileResourceViewSet(ModelViewSet):
    '''
    This viewset is used to provide resources of the space profile
    '''
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.MicroResourceSerializer
    permission_classes = [AllowAny]
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title', 'tags']

    def get_queryset(self):
        return models.Resource.objects.prefetch_related('profile').filter(profile_id=self.kwargs['profile_pk'])


class ChoiceResourceViewSet(ModelViewSet):
    '''
    This viewset is used to provide resource choices of the current profile
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
        return models.Resource.objects \
            .prefetch_related('profile') \
            .filter(profile_id=profile_id, status='P', branch_id=None)


class BranchResourceViewSet(ModelViewSet):
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.SimpleResourceSerializer
    pagination_class = pagination.TwelvePagination
    filter_backends = [SearchFilter]
    search_fields = ['title', 'tags']

    def get_queryset(self):
        branch_id = self.kwargs['branch_pk']
        return models.Resource.objects.prefetch_related('profile').filter(branch_id=branch_id)


class ResourceReviewHeatViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceReviewSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    pagination_class = pagination.TwelvePagination

    # user resource review is designed to list at the review's side
    # so get its queryset by resource id and sort by heat
    def get_queryset(self):
        return models.ResourceReview.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk']) \
            .annotate(num_likes=Count('likes__like_flag', filter=Q(likes__like_flag=True))-Count('likes__like_flag', filter=Q(likes__like_flag=False))) \
            .order_by('-num_likes', '-time')

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'resource_id': self.kwargs['resource_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.ResourceReview.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # endpoint: sonarmeta/resources/{resource_pk}/reviews/sticky/
    # This method is used to get the sticky review of its resource
    @action(detail=False, methods=['GET'])
    def sticky(self, request, *args, **kwargs):
        resource = models.Resource.objects.get(pk=kwargs['resource_pk'])
        review = models.ResourceReview.objects \
            .get(pk=resource.sticky_review_id)
        serializer = serializers.ResourceReviewSerializer(review)
        return Response(serializer.data)


class ResourceReviewNewestViewSet(ModelViewSet):
    http_method_names = ['get', 'head', 'options']
    serializer_class = serializers.ResourceReviewSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    pagination_class = pagination.TwelvePagination

    # user resource review is designed to list at the review's side
    # so get its queryset by resource id and sort by time
    def get_queryset(self):
        return models.ResourceReview.objects \
            .prefetch_related('profile__user') \
            .filter(resource_id=self.kwargs['resource_pk']) \
            .order_by('-time')


class ResourceReplyViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.ResourceReplySerializer
    permission_classes = [IsAuthenticatedOrReadOnly]
    pagination_class = pagination.TwelvePagination

    # user resource reply is designed to list at the reply's side
    # so get its queryset by review id
    def get_queryset(self):
        return models.ResourceReply.objects \
            .prefetch_related('profile__user') \
            .filter(review_id=self.kwargs['review_pk'])

    def get_serializer_context(self):
        if self.request.method in SAFE_METHODS:
            return super().get_serializer_context()
        return {
            'review_id': self.kwargs['review_pk'],
            'profile_id': models.Profile.objects.get(user_id=self.request.user.id).id,
        }

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.ResourceReply.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


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

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserResourceHistory.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserResourceHistory.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserResourceEntryViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'head', 'options']
    serializer_class = serializers.UserResourceEntrySerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

    # user resource entry is designed to list at the resource's side
    # so get its queryset by resource id
    def get_queryset(self):
        return models.UserResourceEntry.objects \
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
    permission_classes = [IsAuthenticatedOrReadOnly]

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

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserResourceLike.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserResourceFavoriteViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'delete', 'head', 'options']
    serializer_class = serializers.DisplayUserResourceFavoriteSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

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

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserResourceFavorite.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserResourceDownloadViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'head', 'options']
    serializer_class = serializers.UserResourceDownloadSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

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

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserResourceDownload.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserResourceShareViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'head', 'options']
    serializer_class = serializers.UserResourceShareSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

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

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserResourceShare.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserReviewLikeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.UserReviewLikeSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

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

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserReviewLike.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserReviewLike.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)


class UserReplyLikeViewSet(ModelViewSet):
    http_method_names = ['get', 'post', 'patch', 'delete', 'head', 'options']
    serializer_class = serializers.UserReplyLikeSerializer
    permission_classes = [IsAuthenticatedOrReadOnly]

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

    # Override the update method in order to verift owner authentication
    def update(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserReplyLike.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().update(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)

    # Override the destroy method in order to verify owner authentication
    def destroy(self, request, *args, **kwargs):
        profile_id = models.Profile.objects.get(user_id=request.user.id).id
        owner_profile_id = models.UserReplyLike.objects \
            .get(pk=kwargs['pk']).profile_id
        if profile_id == owner_profile_id:
            return super().destroy(request, *args, **kwargs)
        else:
            return Response(status=status.HTTP_401_UNAUTHORIZED)
