# Generated by Django 3.2.9 on 2022-04-02 15:54

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0026_auto_20220402_1532'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='resourcebasicsettings',
            name='camera_look_at_x',
        ),
        migrations.RemoveField(
            model_name='resourcebasicsettings',
            name='camera_look_at_y',
        ),
        migrations.RemoveField(
            model_name='resourcebasicsettings',
            name='camera_look_at_z',
        ),
    ]