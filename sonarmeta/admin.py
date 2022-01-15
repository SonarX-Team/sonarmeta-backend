from django.contrib import admin
from . import models


@admin.register(models.Profile)
class ProfileAdmin(admin.ModelAdmin):
    list_display = ['user', 'phone', 'email', 'description']
    list_select_related = ['user']
    list_per_page = 20
    autocomplete_fields = ['user']
    search_fields = ['user__username__i', 'user__phone']

    @admin.display(ordering='user__phone')
    def phone(self, profile):
        return profile.user.phone

    @admin.display(ordering='user__email')
    def email(self, profile):
        return profile.user.email


@admin.register(models.UserSubscribe)
class UserSubscribeAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['creator', 'subscriber']
    search_fields = ['creator']


@admin.register(models.Message)
class MessageAdmin(admin.ModelAdmin):
    list_display = ['content', 'type', 'profile', 'time']
    list_per_page = 20
    autocomplete_fields = ['profile']


@admin.register(models.ResourceSeries)
class ResourceSeriesAdmin(admin.ModelAdmin):
    list_display = ['title', 'profile', 'time']
    list_per_page = 20
    search_fields = ['title', 'profile']


@admin.register(models.ResourceBranch)
class ResourceBranchAdmin(admin.ModelAdmin):
    list_display = ['title', 'series', 'profile', 'time']
    list_per_page = 20
    autocomplete_fields = ['series', 'profile']
    search_fields = ['title']


@admin.register(models.Resource)
class ResourceAdmin(admin.ModelAdmin):
    list_display = ['title', 'entry', 'branch', 'profile', 'time']
    list_per_page = 20
    autocomplete_fields = ['branch', 'profile']
    search_fields = ['title']


@admin.register(models.ResourceReview)
class ResourceReviewAdmin(admin.ModelAdmin):
    list_display = ['content', 'resource', 'profile', 'time']
    list_per_page = 20
    autocomplete_fields = ['resource', 'profile']
    search_fields = ['content']


@admin.register(models.ResourceReply)
class ResourceReplyAdmin(admin.ModelAdmin):
    list_display = ['content', 'at_sign_id',
                    'at_sign', 'review', 'profile', 'time']
    list_per_page = 20
    autocomplete_fields = ['review', 'profile']
    search_fields = ['content']


@admin.register(models.UserResourceLike)
class UserResourceLikeAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['resource', 'profile']


@admin.register(models.UserResourceFavorite)
class UserResourceFavoriteAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['resource', 'profile']


@admin.register(models.UserResourceDownload)
class UserResourceDownloadAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['resource', 'profile']


@admin.register(models.UserResourceShare)
class UserResourceShareAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['resource', 'profile']


@admin.register(models.UserResourceHistory)
class UserResourceHistoryAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['resource', 'profile']


@admin.register(models.UserReviewLike)
class UserReviewLikeAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['review', 'profile']


@admin.register(models.UserReplyLike)
class UserReplyLikeAdmin(admin.ModelAdmin):
    list_per_page = 20
    autocomplete_fields = ['reply', 'profile']
