from zoneinfo import available_timezones
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
        (PROFILE_GENDER_SECRET, 'secret')
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
    wechat = models.CharField(max_length=255, null=True, blank=True)
    history_flag = models.BooleanField(default=True)
    available_balance = models.DecimalField(max_digits=12, decimal_places=2, default=0)
    user = models.OneToOneField(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE
    )

    def __str__(self):
        return self.username

    class Meta:
        ordering = ['user']


class ThreeDViewerOwner(models.Model):
    top_limit = models.PositiveIntegerField(default=0)
    allow_origins = models.TextField()
    retired_time = models.DateTimeField()
    profile = models.OneToOneField(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return self.profile.username

    class Meta:
        ordering = ['profile']


class CustommadeDesigner(models.Model):
    profile = models.OneToOneField(Profile, on_delete=models.CASCADE)

    def __str__(self):
        return self.profile.username

    class Meta:
        ordering = ['profile']


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
        (MESSAGE_TYPE_SYSTEM, 'system')
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


class CustommadeRequirement(models.Model):
    LEVEL_LOW = 'L'
    LEVEL_MIDDLE = 'M'
    LEVEL_HIGH = 'H'

    LEVEL_CHOICES = [
        (LEVEL_LOW, '低模'),
        (LEVEL_MIDDLE, '中模'),
        (LEVEL_HIGH, '高模'),
    ]

    STATUS_PENDING = 'P'
    STATUS_UNDERWAWY = 'P'
    STATUS_FINISHED = 'F'

    STATUS_CHOICES = [
        (STATUS_PENDING, '待抢单'),
        (STATUS_UNDERWAWY, '进行中'),
        (STATUS_FINISHED, '已完成')
    ]

    title = models.CharField(max_length=255)
    description = models.TextField()
    images = models.TextField()
    budget = models.DecimalField(max_digits=12, decimal_places=2)
    deposit = models.DecimalField(max_digits=12, decimal_places=2)
    amount = models.PositiveIntegerField(default=1)
    level = models.CharField(
        max_length=10,
        choices=LEVEL_CHOICES,
        default=LEVEL_MIDDLE
    )
    status = models.CharField(
        max_length=10,
        choices=STATUS_CHOICES,
        default=STATUS_PENDING
    )
    time = models.DateTimeField(auto_now_add=True)
    profile = models.ForeignKey(Profile, on_delete=models.CASCADE)
    intended_designers = models.TextField()

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-time']


class CustommadeOrder(models.Model):
    LEVEL_LOW = 'L'
    LEVEL_MIDDLE = 'M'
    LEVEL_HIGH = 'H'

    LEVEL_CHOICES = [
        (LEVEL_LOW, '低模'),
        (LEVEL_MIDDLE, '中模'),
        (LEVEL_HIGH, '高模'),
    ]

    STATUS_CONTRACT_PENDING = 'CP'
    STATUS_DEPOSIT_PENDING = 'DP'
    STATUS_DEPOSIT_CHECKING = 'DC'
    STATUS_REMAIN_PENDING = 'RP'
    STATUS_REMAIN_CHECKING = 'RC'
    STATUS_FINISHED = 'F'

    STATUS_CHOICES = [
        (STATUS_CONTRACT_PENDING, '待签合同'),
        (STATUS_DEPOSIT_PENDING, '待付定金'),
        (STATUS_DEPOSIT_CHECKING, '待验收定金'),
        (STATUS_REMAIN_PENDING, '待付尾款'),
        (STATUS_REMAIN_CHECKING, '待验收尾款'),
        (STATUS_FINISHED, '已完成'),
    ]

    title = models.CharField(max_length=255)
    description = models.TextField()
    images = models.TextField()
    budget = models.DecimalField(max_digits=12, decimal_places=2)
    deposit = models.DecimalField(max_digits=12, decimal_places=2)
    paid = models.DecimalField(max_digits=12, decimal_places=2)
    paid_checked = models.DecimalField(max_digits=12, decimal_places=2)
    refund = models.DecimalField(max_digits=12, decimal_places=2)
    amount = models.PositiveIntegerField(default=1)
    level = models.CharField(
        max_length=10,
        choices=LEVEL_CHOICES,
        default=LEVEL_MIDDLE
    )
    status = models.CharField(
        max_length=10,
        choices=STATUS_CHOICES,
        default=STATUS_CONTRACT_PENDING
    )
    contract = models.TextField()
    receipt = models.TextField()
    dynamic = models.TextField()
    customer = models.ForeignKey(Profile, on_delete=models.CASCADE)
    designer = models.ForeignKey(CustommadeDesigner, on_delete=models.CASCADE)
    create_time = models.DateTimeField(auto_now_add=True)
    ddl_time = models.DateTimeField(null=True, blank=True)

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-create_time']


class Resource(models.Model):
    RESOURCE_STATUS_UNRELEASED = 'U'
    RESOURCE_STATUS_CHECKING = 'C'
    RESOURCE_STATUS_PASSED = 'P'
    RESOURCE_STATUS_CUSTOMMADE_ORDER_UNRELEASED = 'COU'
    RESOURCE_STATUS_CUSTOMMADE_ORDER_CHECKING = 'COC'
    RESOURCE_STATUS_CUSTOMMADE_ORDER_PASSED = 'COP'
    RESOURCE_STATUS_FAILED = 'F'

    RESOURCE_STATUS_CHOICES = [
        (RESOURCE_STATUS_UNRELEASED, '未发布'),
        (RESOURCE_STATUS_CHECKING, '审核中'),
        (RESOURCE_STATUS_PASSED, '已通过'),
        (RESOURCE_STATUS_CUSTOMMADE_ORDER_UNRELEASED, '订制订单未发布'),
        (RESOURCE_STATUS_CUSTOMMADE_ORDER_CHECKING, '订制订单审核中'),
        (RESOURCE_STATUS_CUSTOMMADE_ORDER_PASSED, '订制订单已通过'),
        (RESOURCE_STATUS_FAILED, '未通过')
    ]

    RESOURCE_TYPE_ORIGINAL = 'O'
    RESOURCE_TYPE_CARRY = 'C'

    RESOURCE_TYPE_CHOICES = [
        (RESOURCE_TYPE_ORIGINAL, 'original'),
        (RESOURCE_TYPE_CARRY, 'carry')
    ]

    RESOURCE_CATEGORY_ANIMALS_PETS = 'AP'
    RESOURCE_CATEGORY_ARCHITECTURE = 'A'
    RESOURCE_CATEGORY_ART_ABSTRACT = 'AA'
    RESOURCE_CATEGORY_CARS_VEHICLES = 'CV'
    RESOURCE_CATEGORY_CHARACTERS_CREATURES = 'CC'
    RESOURCE_CATEGORY_CULTURAL_HERITAGE_HISTORY = 'CH'
    RESOURCE_CATEGORY_ELECTRONICS_GADGETS = 'EG'
    RESOURCE_CATEGORY_FASHION_STYLE = 'FS'
    RESOURCE_CATEGORY_FOOD_DRINK = 'FD'
    RESOURCE_CATEGORY_FURNITURE_HOME = 'FH'
    RESOURCE_CATEGORY_MUSIC = 'M'
    RESOURCE_CATEGORY_NATURE_PLANTS = 'NP'
    RESOURCE_CATEGORY_PLACES_TRAVEL = 'PT'
    RESOURCE_CATEGORY_SCIENCE_TECHNOLOGY = 'ST'
    RESOURCE_CATEGORY_SPORTS_FITNESS = 'SF'
    RESOURCE_CATEGORY_WEAPONS_MILITARY = 'WM'

    RESOURCE_CATEGORY_CHOICES = [
        (RESOURCE_CATEGORY_ANIMALS_PETS, '动物与宠物'),
        (RESOURCE_CATEGORY_ARCHITECTURE, '建筑'),
        (RESOURCE_CATEGORY_ART_ABSTRACT, '艺术与抽象'),
        (RESOURCE_CATEGORY_CARS_VEHICLES, '汽车与交通工具'),
        (RESOURCE_CATEGORY_CHARACTERS_CREATURES, '人物与生物'),
        (RESOURCE_CATEGORY_CULTURAL_HERITAGE_HISTORY, '文化遗产与历史'),
        (RESOURCE_CATEGORY_ELECTRONICS_GADGETS, '电子产品与工具'),
        (RESOURCE_CATEGORY_FASHION_STYLE, '时尚与风格'),
        (RESOURCE_CATEGORY_FOOD_DRINK, '食物与饮料'),
        (RESOURCE_CATEGORY_FURNITURE_HOME, '家具与家居'),
        (RESOURCE_CATEGORY_MUSIC, '音乐'),
        (RESOURCE_CATEGORY_NATURE_PLANTS, '自然与植物'),
        (RESOURCE_CATEGORY_PLACES_TRAVEL, '地点与旅行'),
        (RESOURCE_CATEGORY_SCIENCE_TECHNOLOGY, '科学与技术'),
        (RESOURCE_CATEGORY_SPORTS_FITNESS, '运动与健身'),
        (RESOURCE_CATEGORY_WEAPONS_MILITARY, '武器与军事')
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
        max_length=10,
        choices=RESOURCE_STATUS_CHOICES,
        default=RESOURCE_STATUS_UNRELEASED
    )
    path = models.TextField()
    attached = models.TextField(null=True, blank=True)
    path_folder_list = models.TextField()
    key = models.CharField(max_length=255, null=True, blank=True)
    type = models.CharField(
        max_length=1,
        choices=RESOURCE_TYPE_CHOICES,
        default=RESOURCE_TYPE_ORIGINAL
    )
    title = models.CharField(max_length=255)
    description = models.TextField(null=True, blank=True)
    tags = models.CharField(max_length=1024)
    category = models.CharField(
        max_length=2,
        choices=RESOURCE_CATEGORY_CHOICES,
        default=RESOURCE_CATEGORY_ANIMALS_PETS
    )
    download_type = models.CharField(
        max_length=1,
        choices=RESOURCE_TYPE_DOWNLOAD_CHOICES,
        default=RESOURCE_DOWNLOAD_TYPE_FREE
    )
    price = models.DecimalField(max_digits=12, decimal_places=2)
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
    custommade_order = models.ForeignKey(
        CustommadeOrder,
        on_delete=models.CASCADE,
        null=True,
        blank=True,
        related_name='resources'
    )

    def __str__(self):
        return self.title

    class Meta:
        ordering = ['-time']


class ResourceBasicSettings(models.Model):
    SHADING_LIT = '照明'
    SHADING_SHADELESS = '无阴影'

    SHADING_CHOICES = [
        (SHADING_LIT, '照明'),
        (SHADING_SHADELESS, '无阴影')
    ]

    BACKGROUND_COLOR = '颜色'
    BACKGROUND_IMAGE = '图片'
    BACKGROUND_ENV = '环境'

    BACKGROUND_CHOICES = [
        (BACKGROUND_COLOR, '颜色'),
        (BACKGROUND_IMAGE, '图片'),
        (BACKGROUND_ENV, '环境')
    ]

    model_rotation_x = models.CharField(max_length=255)
    model_rotation_y = models.CharField(max_length=255)
    model_rotation_z = models.CharField(max_length=255)
    camera_position_x = models.CharField(max_length=255)
    camera_position_y = models.CharField(max_length=255)
    camera_position_z = models.CharField(max_length=255)
    shading = models.CharField(
        max_length=10,
        choices=SHADING_CHOICES,
        default=SHADING_LIT
    )
    fov = models.CharField(max_length=255)
    near = models.CharField(max_length=255)
    background_switch = models.BooleanField(default=True)
    background_choice = models.CharField(
        max_length=10,
        choices=BACKGROUND_CHOICES,
        default=BACKGROUND_COLOR
    )
    background_color = models.CharField(max_length=10, null=True, blank=True)
    background_image = models.TextField(null=True, blank=True)
    background_env = models.TextField(null=True, blank=True)
    background_env_brightness = models.CharField(max_length=255)
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='basic_settings'
    )

    def __str__(self):
        return f'Basic settings of {self.resource}'


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
    light_one_position_x = models.CharField(max_length=255)
    light_one_position_y = models.CharField(max_length=255)
    light_one_position_z = models.CharField(max_length=255)
    light_one_color = models.CharField(max_length=10)
    light_one_ground_color = models.CharField(max_length=10)
    light_one_intensity = models.CharField(max_length=255)
    light_one_cast_shadow = models.BooleanField(default=False)
    light_one_shadow_bias = models.CharField(max_length=255)
    light_one_attached_to_camera = models.BooleanField(default=False)
    light_one_decay = models.CharField(max_length=255)
    light_one_angle = models.CharField(max_length=255)
    light_one_penumbra = models.CharField(max_length=255)
    light_two_type = models.CharField(
        max_length=10,
        choices=LIGHT_TYPE_CHOICES,
        default=LIGHT_TYPE_NONE
    )
    light_two_position_x = models.CharField(max_length=255)
    light_two_position_y = models.CharField(max_length=255)
    light_two_position_z = models.CharField(max_length=255)
    light_two_color = models.CharField(max_length=10)
    light_two_ground_color = models.CharField(max_length=10)
    light_two_intensity = models.CharField(max_length=255)
    light_two_cast_shadow = models.BooleanField(default=False)
    light_two_shadow_bias = models.CharField(max_length=255)
    light_two_attached_to_camera = models.BooleanField(default=False)
    light_two_decay = models.CharField(max_length=255)
    light_two_angle = models.CharField(max_length=255)
    light_two_penumbra = models.CharField(max_length=255)
    light_three_type = models.CharField(
        max_length=10,
        choices=LIGHT_TYPE_CHOICES,
        default=LIGHT_TYPE_NONE
    )
    light_three_position_x = models.CharField(max_length=255)
    light_three_position_y = models.CharField(max_length=255)
    light_three_position_z = models.CharField(max_length=255)
    light_three_color = models.CharField(max_length=10)
    light_three_ground_color = models.CharField(max_length=10)
    light_three_intensity = models.CharField(max_length=255)
    light_three_cast_shadow = models.BooleanField(default=False)
    light_three_shadow_bias = models.CharField(max_length=255)
    light_three_attached_to_camera = models.BooleanField(default=False)
    light_three_decay = models.CharField(max_length=255)
    light_three_angle = models.CharField(max_length=255)
    light_three_penumbra = models.CharField(max_length=255)
    env_switch = models.BooleanField(default=True)
    env_texture = models.TextField(null=True, blank=True)
    env_orientation = models.CharField(max_length=255)
    env_brightness = models.CharField(max_length=255)
    ambient_directional_light_switch = models.BooleanField(default=True)
    ambient_directional_light_intensity = models.CharField(max_length=255)
    ambient_directional_light_bias = models.CharField(max_length=255)
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='light_settings'
    )

    def __str__(self):
        return f'Light settings of {self.resource}'


class ResourceMaterialSettings(models.Model):
    CURRENT_PBR_WORKFLOW_METALNESS = '金属度'
    CURRENT_PBR_WORKFLOW_SPECULAR = '镜面'

    CURRENT_PBR_WORKFLOW_CHOICES = [
        (CURRENT_PBR_WORKFLOW_METALNESS, '金属度'),
        (CURRENT_PBR_WORKFLOW_SPECULAR, '镜面')
    ]

    current_pbr_workflow = models.CharField(
        max_length=10,
        choices=CURRENT_PBR_WORKFLOW_CHOICES,
        default=CURRENT_PBR_WORKFLOW_METALNESS
    )
    textures = models.TextField(null=True, blank=True)
    paramters = models.TextField()
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='material_settings'
    )

    def __str__(self):
        return f'Material settings of {self.resource}'


class ResourcePostProcessingSettings(models.Model):
    switch = models.BooleanField(default=True)
    ssao_switch = models.BooleanField(default=False)
    ssao_radius = models.CharField(max_length=255)
    ssao_intensity = models.CharField(max_length=255)
    ssao_bias = models.CharField(max_length=255)
    chromatic_aberration_switch = models.BooleanField(default=False)
    chromatic_aberration_offset = models.CharField(max_length=255)
    vignette_switch = models.BooleanField(default=False)
    vignette_offset = models.CharField(max_length=255)
    vignette_darkness = models.CharField(max_length=255)
    bloom_switch = models.BooleanField(default=False)
    bloom_intensity = models.CharField(max_length=255)
    bloom_luminance_threshold = models.CharField(max_length=255)
    bloom_radius = models.CharField(max_length=255)
    resource = models.OneToOneField(
        Resource,
        on_delete=models.CASCADE,
        related_name='post_processing_settings'
    )

    def __str__(self):
        return f'Post processing settings of {self.resource}'


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
