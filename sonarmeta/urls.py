from cgitb import lookup
from django.template import base
from django.urls import path, include
from rest_framework_nested import routers
from . import views

router = routers.DefaultRouter()

# endpoint: sonarmeta/profiles/
router.register('profiles', views.ProfileViewSet)
# endpoint: sonarmeta/messages/
router.register('messages', views.MessageViewSet, basename='message')
# endpoint: sonarmeta/series/
router.register('series', views.ResourceSeriesViewSet)
# endpoint: sonarmeta/series-me/
router.register('series-me', views.MeResourceSeriesViewSet, basename='series')
# endpoint: sonarmeta/resources/
router.register('resources', views.ResourceViewSet, basename='resource')
# endpoint: sonarmeta/resources-recommendation/
router.register('resources-recommendation', views.RecommendationResourceViewSet,
                basename='resource-recommendation')
# endpoint: sonarmeta/resources-search/
router.register('resources-search', views.SearchResourceViewSet)
# endpoint: sonarmeta/resources-me/
router.register('resources-me', views.MeResourceViewSet, basename='resource-me')
# endpoint: sonarmeta/resources-choice/
router.register('resources-choice', views.ChoiceResourceViewSet, basename='resource-choice')
# endpoint: sonarmeta/follows-me/
router.register('follows-me', views.UserFollowsViewset, basename='follow-me')
# endpoint: sonarmeta/followers-me/
router.register('followers-me', views.UserFollowersViewset,
                basename='follower-me')
# endpoint: sonarmeta/histories-me/
router.register('histories-me', views.UserHistoryViewSet,
                basename='history-me')
# endpoint: sonarmeta/favorites-me/
router.register('favorites-me', views.UserFavoriteViewSet,
                basename='favorite-me')


profiles_router = routers.NestedDefaultRouter(
    router, 'profiles', lookup='profile')
# endpoint: sonarmeta/profiles/{profile_pk}/subscribe/
profiles_router.register(
    'subscribe', views.UserSubscribeViewSet, basename='profile-subscribe')
# endpoint: sonarmeta/profiles/{profile_pk}/blacklist/
profiles_router.register(
    'blacklist', views.UserBlacklistViewSet, basename='profile-blacklist')


series_router = routers.NestedDefaultRouter(
    router, 'series', lookup='series')
# endpoint: sonarmeta/series/{series_pk}/branches/
series_router.register(
    'branches', views.ResourceBranchViewSet, basename='series-branch')


branches_router = routers.NestedDefaultRouter(
    series_router, 'branches', lookup='branch')
# endpoint: sonarmeta/series/{series_pk}/branches/{branch_pk}/resources/
branches_router.register(
    'resources', views.BranchResourceViewSet, basename='series-branch-resource')


resources_router = routers.NestedDefaultRouter(
    router, 'resources', lookup='resource')
# endpoint: sonarmeta/resources/{resource_pk}/basic-settings/
resources_router.register(
    'basic-settings', views.ResourceBasicSettingsViewSet, basename='resource-basic-settings')
# endpoint: sonarmeta/resources/{resource_pk}/light-settings/
resources_router.register(
    'light-settings', views.ResourceLightSettingsViewSet, basename='resource-light-settings')
# endpoint: sonarmeta/resources/{resource_pk}/material-settings/
resources_router.register(
    'material-settings', views.ResourceMaterialSettingsViewSet, basename='resource-material-settings')
# endpoint: sonarmeta/resources/{resource_pk}/post-processing-settings/
resources_router.register(
    'post-processing-settings', views.ResourcePostProcessingSettingsViewSet, basename='resource-post-processing-settings')
# endpoint: sonarmeta/resources/{resource_pk}/reviews/
resources_router.register(
    'reviews', views.ResourceReviewHeatViewSet, basename='resource-review')
# endpoint: sonarmeta/resources/{resource_pk}/reviews-newest/
resources_router.register(
    'reviews-newest', views.ResourceReviewNewestViewSet, basename='resource-review')
# endpoint: sonarmeta/resources/{resource_pk}/histories/
resources_router.register(
    'histories', views.UserResourceHistoryViewSet, basename='resource-history')
# endpoint: sonarmeta/resources/{resource_pk}/entries/
resources_router.register(
    'entries', views.UserResourceEntryViewSet, basename='resource-entry')
# endpoint: sonarmeta/resources/{resource_pk}/likes/
resources_router.register(
    'likes', views.UserResourceLikeViewSet, basename='resource-like')
# endpoint: sonarmeta/resources/{resource_pk}/favorites/
resources_router.register(
    'favorites', views.UserResourceFavoriteViewSet, basename='resource-favorite')
# endpoint: sonarmeta/resources/{resource_pk}/downloads/
resources_router.register(
    'downloads', views.UserResourceDownloadViewSet, basename='resource-download')
# endpoint: sonarmeta/resources/{resource_pk}/shares/
resources_router.register(
    'shares', views.UserResourceShareViewSet, basename='resource-share')


reviews_router = routers.NestedDefaultRouter(
    resources_router, 'reviews', lookup='review')
# endpoint: sonarmeta/resources/{resource_pk}/reviews/{review_pk}/replies/
reviews_router.register('replies', views.ResourceReplyViewSet,
                        basename='resource-review-reply')
# endpoint: sonarmeta/resources/{resource_pk}/reviews/{review_pk}/likes/
reviews_router.register('likes', views.UserReviewLikeViewSet,
                        basename='resource-review-like')


replies_router = routers.NestedDefaultRouter(
    reviews_router, 'replies', lookup='reply')
# endpoint: sonarmeta/resources/{resource_pk}/reviews/{review_pk}/replies/{reply_pk}/likes
replies_router.register('likes', views.UserReplyLikeViewSet,
                        basename='resource-review-reply-like')


# URLConfig
urlpatterns = [
    path('', include(router.urls)),
    path('', include(profiles_router.urls)),
    path('', include(series_router.urls)),
    path('', include(branches_router.urls)),
    path('', include(resources_router.urls)),
    path('', include(reviews_router.urls)),
    path('', include(replies_router.urls)),
]
