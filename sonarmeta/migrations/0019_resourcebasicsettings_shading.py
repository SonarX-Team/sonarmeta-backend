# Generated by Django 3.2.9 on 2022-03-23 14:54

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0018_resourcematerialsettings_current_pbr_workflow'),
    ]

    operations = [
        migrations.AddField(
            model_name='resourcebasicsettings',
            name='shading',
            field=models.CharField(choices=[('照明', '照明'), ('无阴影', '无阴影')], default='照明', max_length=10),
        ),
    ]
