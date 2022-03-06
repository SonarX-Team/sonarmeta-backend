# Generated by Django 3.2.9 on 2022-03-06 17:07

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0011_alter_userresourceentry_options'),
    ]

    operations = [
        migrations.CreateModel(
            name='ResourceMaterialSettings',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('nodes', models.TextField()),
                ('resource', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, related_name='material_settngs', to='sonarmeta.resource')),
            ],
        ),
        migrations.CreateModel(
            name='ResourceLightSettings',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('light_switch', models.BooleanField(default=False)),
                ('light_one_type', models.CharField(choices=[('无', '无'), ('平行光', '平行光'), ('点光源', '点光源'), ('聚光灯', '聚光灯'), ('半球光', '半球光')], default='无', max_length=10)),
                ('light_one_color', models.CharField(blank=True, max_length=10, null=True)),
                ('light_one_position_x', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_position_y', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_position_z', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_intensity', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_cast_shadow', models.BooleanField(default=False)),
                ('light_one_shadow_bias', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_attach_to_camera', models.BooleanField(default=False)),
                ('light_one_decay', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_angle', models.CharField(blank=True, max_length=255, null=True)),
                ('light_one_penumbra', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_type', models.CharField(choices=[('无', '无'), ('平行光', '平行光'), ('点光源', '点光源'), ('聚光灯', '聚光灯'), ('半球光', '半球光')], default='无', max_length=10)),
                ('light_two_color', models.CharField(blank=True, max_length=10, null=True)),
                ('light_two_position_x', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_position_y', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_position_z', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_intensity', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_cast_shadow', models.BooleanField(default=False)),
                ('light_two_shadow_bias', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_attach_to_camera', models.BooleanField(default=False)),
                ('light_two_decay', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_angle', models.CharField(blank=True, max_length=255, null=True)),
                ('light_two_penumbra', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_type', models.CharField(choices=[('无', '无'), ('平行光', '平行光'), ('点光源', '点光源'), ('聚光灯', '聚光灯'), ('半球光', '半球光')], default='无', max_length=10)),
                ('light_three_color', models.CharField(blank=True, max_length=10, null=True)),
                ('light_three_position_x', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_position_y', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_position_z', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_intensity', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_cast_shadow', models.BooleanField(default=False)),
                ('light_three_shadow_bias', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_attach_to_camera', models.BooleanField(default=False)),
                ('light_three_decay', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_angle', models.CharField(blank=True, max_length=255, null=True)),
                ('light_three_penumbra', models.CharField(blank=True, max_length=255, null=True)),
                ('env_light_switch', models.BooleanField(default=True)),
                ('env_texture', models.TextField(blank=True, null=True)),
                ('env_orientaion', models.CharField(blank=True, max_length=255, null=True)),
                ('env_brightness', models.CharField(blank=True, max_length=255, null=True)),
                ('resource', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, related_name='light_settngs', to='sonarmeta.resource')),
            ],
        ),
        migrations.CreateModel(
            name='ResourceBasicSettings',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('model_position_x', models.CharField(max_length=255)),
                ('model_position_y', models.CharField(max_length=255)),
                ('model_position_z', models.CharField(max_length=255)),
                ('model_rotation_x', models.CharField(max_length=255)),
                ('model_rotation_y', models.CharField(max_length=255)),
                ('model_rotation_z', models.CharField(max_length=255)),
                ('camera_position_x', models.CharField(max_length=255)),
                ('camera_position_y', models.CharField(max_length=255)),
                ('camera_position_z', models.CharField(max_length=255)),
                ('camera_rotation_x', models.CharField(max_length=255)),
                ('camera_rotation_y', models.CharField(max_length=255)),
                ('camera_rotation_z', models.CharField(max_length=255)),
                ('fov', models.CharField(max_length=255)),
                ('background_switch', models.BooleanField(default=True)),
                ('background_choice', models.CharField(choices=[('颜色', '颜色'), ('图片', '图片'), ('环境', '环境')], default='颜色', max_length=10)),
                ('background_color', models.CharField(blank=True, max_length=10, null=True)),
                ('background_image', models.TextField(blank=True, null=True)),
                ('background_env', models.TextField(blank=True, null=True)),
                ('background_env_brightness', models.CharField(max_length=255)),
                ('resource', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, related_name='basic_settngs', to='sonarmeta.resource')),
            ],
        ),
    ]