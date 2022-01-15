from django.db.models.fields import IntegerField
from djoser.serializers import UserSerializer as BaseUserSerializer
from rest_framework import serializers
from . import models


class UserSerializer(BaseUserSerializer):
    class Meta(BaseUserSerializer.Meta):
        fields = ['id', 'username']


class UserSubscribeSerializer(serializers.ModelSerializer):
    '''
    This serializer is used to provide subscribe relationships,
    which will be nested into other serializers
    '''
    creator_id = serializers.IntegerField(read_only=True)
    subscriber_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        creator_id = self.context['creator_id']
        subscriber_id = self.context['subscriber_id']
        return models.UserSubscribe.objects \
            .create(creator_id=creator_id, subscriber_id=subscriber_id, **validated_data)

    class Meta:
        model = models.UserSubscribe
        fields = ['id', 'creator_id', 'subscriber_id']


class ProfileSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    be_followed = UserSubscribeSerializer(many=True, read_only=True)
    follow = UserSubscribeSerializer(many=True, read_only=True)

    class Meta:
        model = models.Profile
        fields = ['id', 'user', 'avatar', 'birth_date', 'history_flag',
                  'description', 'gender', 'be_followed', 'follow']


class SimpleProfileSerializer(serializers.ModelSerializer):
    '''
    This serializer is used to provide some simple infos of profile,
    which will be nested into other serializers
    '''
    user = UserSerializer(read_only=True)
    be_followed = UserSubscribeSerializer(many=True, read_only=True)
    follow = UserSubscribeSerializer(many=True, read_only=True)

    class Meta:
        model = models.Profile
        fields = ['id', 'user', 'avatar', 'history_flag',
                  'description', 'be_followed', 'follow']


class MicroProfileSerializer(serializers.ModelSerializer):
    '''
    This serializer is used to provide some basic infos of profile,
    which will be nested into other serializers
    '''
    user = UserSerializer(read_only=True)

    class Meta:
        model = models.Profile
        fields = ['id', 'user', 'avatar', 'history_flag']


class MessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Message
        fields = ['id', 'profile', 'content', 'type']


class UserReplyLikeSerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in reply serializer
    to provide likes related to a certain reply
    so it is defined before its parent entity serializer
    '''
    reply_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        reply_id = self.context['reply_id']
        profile_id = self.context['profile_id']
        return models.UserReplyLike.objects \
            .create(reply_id=reply_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserReplyLike
        fields = ['id', 'reply_id', 'profile_id', 'like_flag']


class ResourceReplySerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in review serializer
    to provide replies related to a certain review
    so it is defined before its parent entity serializer
    '''
    review_id = serializers.IntegerField(read_only=True)
    profile = SimpleProfileSerializer(read_only=True)
    likes = UserReplyLikeSerializer(many=True, read_only=True)

    def create(self, validated_data):
        review_id = self.context['review_id']
        profile_id = self.context['profile_id']
        return models.ResourceReply.objects \
            .create(review_id=review_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.ResourceReply
        fields = ['id', 'review_id', 'profile',
                  'content', 'likes', 'time', 'at_sign_id', 'at_sign']


class UserReviewLikeSerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in review serializer
    to provide likes related to a certain review
    so it is defined before its parent entity serializer
    '''
    review_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        review_id = self.context['review_id']
        profile_id = self.context['profile_id']
        return models.UserReviewLike.objects \
            .create(review_id=review_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserReviewLike
        fields = ['id', 'review_id', 'profile_id', 'like_flag']


class ResourceReviewSerializer(serializers.ModelSerializer):
    resource_id = serializers.IntegerField(read_only=True)
    profile = SimpleProfileSerializer(read_only=True)
    replies = ResourceReplySerializer(many=True, read_only=True)
    likes = UserReviewLikeSerializer(many=True, read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.ResourceReview.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.ResourceReview
        fields = ['id', 'resource_id', 'profile',
                  'content', 'replies', 'likes', 'time']


class UserResourceLikeSerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in resource serializer
    to provide likes related to a certain resource
    so it is defined before its parent entity serializer
    '''
    resource_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.UserResourceLike.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserResourceLike
        fields = ['id', 'resource_id', 'profile_id']


class UserResourceFavoriteSerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in resource serializer
    to provide favorites related to a certain resource
    so it is defined before its parent entity serializer
    '''
    resource_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.UserResourceFavorite.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserResourceFavorite
        fields = ['id', 'resource_id', 'profile_id']


class UserResourceDownloadSerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in resource serializer
    to provide downloads related to a certain resource
    so it is defined before its parent entity serializer
    '''
    resource_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.UserResourceDownload.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserResourceDownload
        fields = ['id', 'resource_id', 'profile_id']


class UserResourceShareSerializer(serializers.ModelSerializer):
    '''
    This serializer will be used in resource serializer
    to provide shares related to a certain resource
    so it is defined before its parent entity serializer
    '''
    resource_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.UserResourceShare.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserResourceShare
        fields = ['id', 'resource_id', 'profile_id']


class SimpleResourceSerializer(serializers.ModelSerializer):
    branch_id = serializers.IntegerField(read_only=True)
    likes = UserResourceLikeSerializer(many=True, read_only=True)
    favorites = UserResourceFavoriteSerializer(many=True, read_only=True)
    downloads = UserResourceDownloadSerializer(many=True, read_only=True)
    shares = UserResourceShareSerializer(many=True, read_only=True)

    class Meta:
        model = models.Resource
        fields = ['id', 'title', 'description', 'cover', 'entry', 'likes',
                  'favorites', 'downloads', 'shares', 'branch_id', 'time']


class UserResourceHistorySerializer(serializers.ModelSerializer):
    resource = SimpleResourceSerializer(read_only=True)
    profile = MicroProfileSerializer(read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.UserResourceHistory.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserResourceHistory
        fields = ['id', 'resource', 'profile', 'time']


class SimpleUserResourceHistorySerializer(serializers.ModelSerializer):
    resource_id = serializers.IntegerField(read_only=True)
    profile_id = serializers.IntegerField(read_only=True)

    def create(self, validated_data):
        resource_id = self.context['resource_id']
        profile_id = self.context['profile_id']
        return models.UserResourceHistory.objects \
            .create(resource_id=resource_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.UserResourceHistory
        fields = ['id', 'resource_id', 'profile_id', 'time']


class ResourceSerializer(serializers.ModelSerializer):
    profile = SimpleProfileSerializer(read_only=True)
    branch_id = serializers.IntegerField(read_only=True)
    likes = UserResourceLikeSerializer(many=True, read_only=True)
    favorites = UserResourceFavoriteSerializer(many=True, read_only=True)
    downloads = UserResourceDownloadSerializer(many=True, read_only=True)
    shares = UserResourceShareSerializer(many=True, read_only=True)
    histories = SimpleUserResourceHistorySerializer(many=True, read_only=True)

    def create(self, validated_data):
        profile_id = self.context['profile_id']
        return models.Resource.objects.create(profile_id=profile_id, **validated_data)

    class Meta:
        model = models.Resource
        fields = ['id', 'status', 'path', 'type_and_authority',
                  'title', 'description', 'category',
                  'download_type', 'carry', 'no_commercial',
                  'entry', 'cover', 'sticky_review_id',
                  'time', 'profile', 'branch_id', 'tags',
                  'likes', 'favorites', 'downloads', 'shares', 'histories']


class ResourceBranchSerializer(serializers.ModelSerializer):
    resources = SimpleResourceSerializer(many=True, read_only=True)
    profile = MicroProfileSerializer(read_only=True)

    def create(self, validated_data):
        series_id = self.context['series_id']
        profile_id = self.context['profile_id']
        return models.ResourceBranch.objects.create(series_id=series_id, profile_id=profile_id, **validated_data)

    class Meta:
        model = models.ResourceBranch
        fields = ['id', 'title', 'resources', 'profile', 'time']


class ResourceSeriesSerializer(serializers.ModelSerializer):
    branches = ResourceBranchSerializer(many=True, read_only=True)
    profile = MicroProfileSerializer(read_only=True)

    def create(self, validated_data):
        profile_id = self.context['profile_id']
        return models.ResourceSeries.objects.create(profile_id=profile_id, **validated_data)

    class Meta:
        model = models.ResourceSeries
        fields = ['id', 'title', 'description', 'branches', 'profile', 'time']
