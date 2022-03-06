from pyexpat import model
from random import choices
from django.conf import settings
from django.core.exceptions import ValidationError
from django.db import models


class Profile(models.Model):
    PROFILE_GENDER_MALE = 'M'
    PROFILE_GENDER_FEMALE = 'F'
    PROFILE_GENDER_SECRET = 'S'

    PROFILE_GENDER_CHOICES = [
        (PROFILE_GENDER_MALE, 'male'),
        (PROFILE_GENDER_FEMALE, 'female'),
        (PROFILE_GENDER_SECRET, 'secret'),
    ]

    username = models.CharField(max_length=255, unique=True)
    avatar = models.TextField(null=True, blank=True)
    description = models.TextField(null=True, blank=True)
    birth_date = models.DateField(null=True, blank=True)
    gender = models.CharField(
        max_length=1,
        choices=PROFILE_GENDER_CHOICES,
        default=PROFILE_GENDER_SECRET
    )
    history_flag = models.BooleanField(default=True)
    user = models.OneToOneField(
        settings.AUTH_USER_MODEL, on_delete=models.CASCADE)

    def __str__(self):
        return self.username

    class Meta:
        ordering = ['user']


class UserSubscribe(models.Model):
    creator = models.ForeignKey(
        Profile, on_delete=models.CASCADE, related_name='be_followed')
    subscriber = models.ForeignKey(
        Profile, on_delete=models.CASCADE, related_name='follow')
    time = models.DateTimeField(auto_now_add=True)

    def clean(self):
        if self.creator == self.subscriber:
            raise ValidationError('You cannot subscribe yourself.')

    def __str__(self):
        return f'{self.creator} is subscribed by {self.subscriber}'

    class Meta:
        ordering = ['-time']
        unique_together = [['creator', 'subscriber']]


class UserBlacklist(models.Model):
    be_prevented = models.ForeignKey(
        Profile, on_delete=models.CASCADE, related_name='be_prevented')
    preventer = models.ForeignKey(
        Profile, on_delete=models.CASCADE, related_name='prevent')

    def clean(self):
        if self.be_prevented == self.preventer:
            raise ValidationError('You cannot prevent yourself.')

    def __str__(self):
        return f'{self.be_prevented} is prevented by {self.preventer}'

    class Meta:
        unique_together = [['be_prevented', 'preventer']]


class Message(models.Model):
    MESSAGE_TYPE_LIKE = 'L'
    MESSAGE_TYPE_REPLY = 'R'
    MESSAGE_TYPE_SYSTEM = 'S'

    MESSAGE_TYPE_CHOICES = [
        (MESSAGE_TYPE_LIKE, 'like'),
        (MESSAGE_TYPE_REPLY, 'reply'),
        (MESSAGE_TYPE_SYSTEM, 'system'),
    ]

    content = models.TextField()
    type = models.CharField(
        max_length=1, choices=MESSAGE_TYPE_CHOICES, default=MESSAGE_TYPE_LIKE)
    time = models.DateTimeField(auto_now_add=True)
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return self.content


class ResourceSeries(models.Model):
    title = models.CharField(max_length=255)
    description = models.TextField(null=True, blank=True)
    time = models.DateTimeField(auto_now_add=True)
    profile = models.ForeignKey(
        Profile, on_delete=models.SET_NULL, null=True, blank=True)

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-time']


class ResourceBranch(models.Model):
    title = models.CharField(max_length=255)
    time = models.DateTimeField(auto_now_add=True)
    series = models.ForeignKey(
        ResourceSeries,
        on_delete=models.CASCADE,
        related_name='branches'
    )
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-time']


class Resource(models.Model):
    RESOURCE_STATUS_UNRELEASED = 'U'
    RESOURCE_STATUS_CHECKING = 'C'
    RESOURCE_STATUS_PASSED = 'P'
    RESOURCE_STATUS_FAILED = 'F'

    RESOURCE_STATUS_CHOICES = [
        (RESOURCE_STATUS_UNRELEASED, 'unreleased'),
        (RESOURCE_STATUS_CHECKING, 'checking'),
        (RESOURCE_STATUS_PASSED, 'passed'),
        (RESOURCE_STATUS_FAILED, 'failed'),
    ]

    RESOURCE_TYPE_ORIGINAL = 'O'
    RESOURCE_TYPE_CARRY = 'C'

    RESOURCE_TYPE_CHOICES = [
        (RESOURCE_TYPE_ORIGINAL, 'original'),
        (RESOURCE_TYPE_CARRY, 'carry'),
    ]

    RESOURCE_CATEGORY_ANIMALS = 'A'
    RESOURCE_CATEGORY_PLANTS = 'P'
    RESOURCE_CATEGORY_CARS = 'C'

    RESOURCE_CATEGORY_CHOICES = [
        (RESOURCE_CATEGORY_ANIMALS, 'animals'),
        (RESOURCE_CATEGORY_PLANTS, 'plants'),
        (RESOURCE_CATEGORY_CARS, 'cars'),
    ]

    RESOURCE_DOWNLOAD_TYPE_FREE = 'F'
    RESOURCE_DOWNLOAD_TYPE_SALE = 'S'
    RESOURCE_DOWNLOAD_TYPE_NO = 'N'

    RESOURCE_TYPE_DOWNLOAD_CHOICES = [
        (RESOURCE_DOWNLOAD_TYPE_FREE, 'free'),
        (RESOURCE_DOWNLOAD_TYPE_SALE, 'sale'),
        (RESOURCE_DOWNLOAD_TYPE_NO, 'no')
    ]

    status = models.CharField(
        max_length=1,
        choices=RESOURCE_STATUS_CHOICES,
        default=RESOURCE_STATUS_UNRELEASED
    )
    path = models.TextField()
    path_folder_list = models.TextField()
    type = models.CharField(
        max_length=1,
        choices=RESOURCE_TYPE_CHOICES,
        default=RESOURCE_TYPE_ORIGINAL
    )
    title = models.CharField(max_length=255)
    description = models.TextField(null=True, blank=True)
    tags = models.CharField(max_length=1024)
    category = models.CharField(
        max_length=1,
        choices=RESOURCE_CATEGORY_CHOICES,
        default=RESOURCE_CATEGORY_ANIMALS
    )
    download_type = models.CharField(
        max_length=1,
        choices=RESOURCE_TYPE_DOWNLOAD_CHOICES,
        default=RESOURCE_DOWNLOAD_TYPE_FREE
    )
    price = models.DecimalField(max_digits=6, decimal_places=2)
    no_carry = models.BooleanField()
    carry_from = models.CharField(max_length=255, null=True, blank=True)
    no_commercial = models.BooleanField()
    cover = models.TextField(null=True, blank=True)
    sticky_review_id = models.PositiveIntegerField(null=True, blank=True)
    time = models.DateTimeField(auto_now_add=True)
    status_change_timestamp = models.CharField(
        max_length=255, null=True, blank=True)
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)
    branch = models.ForeignKey(
        ResourceBranch,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name='resources'
    )

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-time']


class ResourceBasicSettings(models.Model):
    BACKGROUND_COLOR = '颜色'
    BACKGROUND_IMAGE = '图片'
    BACKGROUND_ENV = '环境'

    BACKGROUND_CHOICES = [
        (BACKGROUND_COLOR, '颜色'),
        (BACKGROUND_IMAGE, '图片'),
        (BACKGROUND_ENV, '环境')
    ]

    model_position_x = models.CharField(max_length=255)
    model_position_y = models.CharField(max_length=255)
    model_position_z = models.CharField(max_length=255)
    model_rotation_x = models.CharField(max_length=255)
    model_rotation_y = models.CharField(max_length=255)
    model_rotation_z = models.CharField(max_length=255)
    camera_position_x = models.CharField(max_length=255)
    camera_position_y = models.CharField(max_length=255)
    camera_position_z = models.CharField(max_length=255)
    camera_rotation_x = models.CharField(max_length=255)
    camera_rotation_y = models.CharField(max_length=255)
    camera_rotation_z = models.CharField(max_length=255)
    fov = models.CharField(max_length=255)
    background_switch = models.BooleanField(default=True)
    background_choice = models.CharField(
        max_length=10,
        choices=BACKGROUND_CHOICES,
        default=BACKGROUND_COLOR,
    )
    background_color = models.CharField(max_length=10, null=True, blank=True)
    background_image = models.TextField(null=True, blank=True)
    background_env = models.TextField(null=True, blank=True)
    background_env_brightness = models.CharField(max_length=255)
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='basic_settngs'
    )

    def __str__(self):
        return f'Basic settings of {self.resource.title}'


class ResourceLightSettings(models.Model):
    LIGHT_TYPE_NONE = '无'
    LIGHT_TYPE_DIRECTIONALLIGHT = '平行光'
    LIGHT_TYPE_POINTLIGHT = '点光源'
    LIGHT_TYPE_SPOTLIGHT = '聚光灯'
    LIGHT_TYPE_HEMISPHERELIGHT = '半球光'

    LIGHT_TYPE_CHOICES = [
        (LIGHT_TYPE_NONE, '无'),
        (LIGHT_TYPE_DIRECTIONALLIGHT, '平行光'),
        (LIGHT_TYPE_POINTLIGHT, '点光源'),
        (LIGHT_TYPE_SPOTLIGHT, '聚光灯'),
        (LIGHT_TYPE_HEMISPHERELIGHT, '半球光')
    ]

    light_switch = models.BooleanField(default=False)
    light_one_type = models.CharField(
        max_length=10,
        choices=LIGHT_TYPE_CHOICES,
        default=LIGHT_TYPE_NONE
    )
    light_one_color = models.CharField(max_length=10, null=True, blank=True)
    light_one_position_x = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_position_y = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_position_z = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_intensity = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_cast_shadow = models.BooleanField(default=False)
    light_one_shadow_bias = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_attach_to_camera = models.BooleanField(default=False)
    light_one_decay = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_angle = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_one_penumbra = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_type = models.CharField(
        max_length=10,
        choices=LIGHT_TYPE_CHOICES,
        default=LIGHT_TYPE_NONE
    )
    light_two_color = models.CharField(max_length=10, null=True, blank=True)
    light_two_position_x = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_position_y = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_position_z = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_intensity = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_cast_shadow = models.BooleanField(default=False)
    light_two_shadow_bias = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_attach_to_camera = models.BooleanField(default=False)
    light_two_decay = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_angle = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_two_penumbra = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_type = models.CharField(
        max_length=10,
        choices=LIGHT_TYPE_CHOICES,
        default=LIGHT_TYPE_NONE
    )
    light_three_color = models.CharField(max_length=10, null=True, blank=True)
    light_three_position_x = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_position_y = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_position_z = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_intensity = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_cast_shadow = models.BooleanField(default=False)
    light_three_shadow_bias = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_attach_to_camera = models.BooleanField(default=False)
    light_three_decay = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_angle = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    light_three_penumbra = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    env_light_switch = models.BooleanField(default=True)
    env_texture = models.TextField(null=True, blank=True)
    env_orientaion = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    env_brightness = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='light_settngs'
    )

    def __str__(self):
        return f'Light settings of {self.resource.title}'


class ResourceMaterialSettings(models.Model):
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='material_settngs'
    )

    def __str__(self):
        return f'Material settings of {self.resource.title}'


class ResourceNodeMaterialSettings(models.Model):
    PBR_WORKFLOW_METALNESS = '金属度'
    PBR_WORKFLOW_SPECULAR = '反射率'

    NORMAL_BUMP_FLAG_NORMAL = '法线贴图'
    NORMAL_BUMP_FLAG_BUMP = '凹凸贴图'

    SIDE_FRONT = '前面'
    SIDE_BACK = '后面'
    SIDE_DOUBLE = '双面'

    PBR_WORKFLOW_CHOICES = [
        (PBR_WORKFLOW_METALNESS, '金属度'),
        (PBR_WORKFLOW_SPECULAR, '反射率')
    ]

    NORMAL_BUMP_CHOICES = [
        (NORMAL_BUMP_FLAG_NORMAL, '法线贴图'),
        (NORMAL_BUMP_FLAG_BUMP, '凹凸贴图')
    ]

    SIDE_CHOICES = [
        (SIDE_FRONT, '前面'),
        (SIDE_BACK, '后面'),
        (SIDE_DOUBLE, '双面')
    ]

    name = models.CharField(max_length=255)
    pbr_workflow = models.CharField(
        max_length=10,
        choices=PBR_WORKFLOW_CHOICES,
        default=PBR_WORKFLOW_METALNESS
    )
    base_color_color = models.CharField(
        max_length=10,
        null=True,
        blank=True
    )
    base_color_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    base_color_texture = models.TextField(null=True, blank=True)
    metalness_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    metalness_texture = models.TextField(null=True, blank=True)
    albedo_color = models.CharField(
        max_length=10,
        null=True,
        blank=True
    )
    albedo_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    albedo_texture = models.TextField(null=True, blank=True)
    specular_color = models.CharField(
        max_length=10,
        null=True,
        blank=True
    )
    specular_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    specular_texture = models.TextField(null=True, blank=True)
    metalness_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    metalness_texture = models.TextField(null=True, blank=True)
    sheen_switch = models.BooleanField(default=False)
    sheen_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    sheen_color = models.CharField(
        max_length=10,
        null=True,
        blank=True
    )
    sheen_color_texture = models.TextField(null=True, blank=True)
    sheen_roughness_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    sheen_roughness_texture = models.TextField(null=True, blank=True)
    clearcoat_switch = models.BooleanField(default=False)
    clearcoat_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    clearcoat_texture = models.TextField(null=True, blank=True)
    clearcoat_normal_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    clearcoat_normal_texture = models.TextField(null=True, blank=True)
    clearcoat_normal_invert_y = models.BooleanField(default=False)
    clearcoat_roughness_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    clearcoat_roughness_texture = models.TextField(null=True, blank=True)
    reflectivity = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    ambient_occlusion_switch = models.BooleanField(default=False)
    ambient_occlusion_intensity = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    ambient_occlusion_texture = models.TextField(null=True, blank=True)
    emissive_switch = models.BooleanField(default=False)
    emissive_intensity = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    emissive_color = models.CharField(
        max_length=10,
        null=True,
        blank=True
    )
    emissive_texture = models.TextField(null=True, blank=True)
    matcap = models.TextField(null=True, blank=True)
    matcap_color = models.CharField(
        max_length=10,
        null=True,
        blank=True
    )
    displacement_switch = models.BooleanField(default=False)
    displacement_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    displacement_texture = models.TextField(null=True, blank=True)
    normal_bump_switch = models.BooleanField(default=False)
    normal_bump_flag = models.CharField(
        max_length=10,
        choices=NORMAL_BUMP_CHOICES,
        default=None
    )
    material = models.ForeignKey(
        ResourceMaterialSettings,
        on_delete=models.CASCADE,
        related_name='node_settings'
    )
    normal_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    normal_texture = models.TextField(null=True, blank=True)
    normal_invert_y = models.BooleanField(default=False)
    bump_scale = models.CharField(
        max_length=255,
        null=True,
        blank=True
    )
    bump_texture = models.TextField(null=True, blank=True)
    side = models.CharField(
        max_length=10,
        choices=SIDE_CHOICES,
        default=SIDE_FRONT
    )

    def __str__(self):
        return f'Material node settings of material {self.material.id}'


class ResourceReview(models.Model):
    content = models.TextField()
    time = models.DateTimeField(auto_now_add=True)
    resource = models.ForeignKey(Resource, on_delete=models.CASCADE)
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return self.content


class ResourceReply(models.Model):
    content = models.TextField()
    at_sign_id = models.PositiveIntegerField(null=True, blank=True)
    at_sign = models.CharField(max_length=255, null=True, blank=True)
    time = models.DateTimeField(auto_now_add=True)
    review = models.ForeignKey(
        ResourceReview, on_delete=models.CASCADE, related_name='replies')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return self.content


class UserResourceEntry(models.Model):
    resource = models.ForeignKey(
        Resource, on_delete=models.CASCADE, related_name='entries')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)
    time = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f'{self.profile} viewed {self.resource} at {self.time}'

    class Meta:
        ordering = ['-time']


class UserResourceLike(models.Model):
    resource = models.ForeignKey(
        Resource, on_delete=models.CASCADE, related_name='likes')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return f'{self.profile} liked {self.resource}'

    class Meta:
        unique_together = [['resource', 'profile']]


class UserResourceFavorite(models.Model):
    resource = models.ForeignKey(
        Resource, on_delete=models.CASCADE, related_name='favorites')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)
    time = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f'{self.profile} collected {self.resource}'

    class Meta:
        ordering = ['-time']
        unique_together = [['resource', 'profile']]


class UserResourceDownload(models.Model):
    resource = models.ForeignKey(
        Resource, on_delete=models.CASCADE, related_name='downloads')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return f'{self.profile} downloaded {self.resource}'

    class Meta:
        unique_together = [['resource', 'profile']]


class UserResourceShare(models.Model):
    resource = models.ForeignKey(
        Resource, on_delete=models.CASCADE, related_name='shares')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return f'{self.profile} shared {self.resource}'

    class Meta:
        unique_together = [['resource', 'profile']]


class UserResourceHistory(models.Model):
    resource = models.ForeignKey(
        Resource, on_delete=models.CASCADE, related_name='histories')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)
    time = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f'{self.profile} entered {self.resource} at {self.time}'

    class Meta:
        ordering = ['-time']
        unique_together = [['resource', 'profile']]


class UserReviewLike(models.Model):
    like_flag = models.BooleanField()
    review = models.ForeignKey(
        ResourceReview, on_delete=models.CASCADE, related_name='likes')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        if self.like_flag:
            return f'{self.profile} gived {self.review} a like'
        return f'{self.profile} gived {self.review} a dislike'

    class Meta:
        unique_together = [['review', 'profile']]


class UserReplyLike(models.Model):
    like_flag = models.BooleanField()
    reply = models.ForeignKey(
        ResourceReply, on_delete=models.CASCADE, related_name='likes')
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)

    def __str__(self):
        if self.like_flag:
            return f'{self.profile} gived {self.reply} a like'
        return f'{self.profile} gived {self.reply} a dislike'

    class Meta:
        unique_together = [['reply', 'profile']]
