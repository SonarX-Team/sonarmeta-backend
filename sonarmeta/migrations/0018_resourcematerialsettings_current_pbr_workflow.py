# Generated by Django 3.2.9 on 2022-03-08 11:33

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0017_rename_env_orientaion_resourcelightsettings_env_orientation'),
    ]

    operations = [
        migrations.AddField(
            model_name='resourcematerialsettings',
            name='current_pbr_workflow',
            field=models.CharField(choices=[('金属度', '金属度'), ('反射率', '反射率')], default='金属度', max_length=10),
        ),
    ]