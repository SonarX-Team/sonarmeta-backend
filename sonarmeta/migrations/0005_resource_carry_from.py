# Generated by Django 3.2.9 on 2022-01-19 16:35

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0004_resource_price'),
    ]

    operations = [
        migrations.AddField(
            model_name='resource',
            name='carry_from',
            field=models.CharField(blank=True, max_length=255, null=True),
        ),
    ]