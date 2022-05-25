# Generated by Django 3.2.9 on 2022-05-25 09:24

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0032_auto_20220508_1609'),
    ]

    operations = [
        migrations.CreateModel(
            name='ThreeDViewerOwner',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('secret_key', models.CharField(max_length=255)),
                ('allow_origin', models.TextField()),
                ('retired_time', models.DateTimeField()),
                ('profile', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to='sonarmeta.profile')),
            ],
            options={
                'ordering': ['profile'],
            },
        ),
    ]
