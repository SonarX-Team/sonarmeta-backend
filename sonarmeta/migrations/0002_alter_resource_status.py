# Generated by Django 3.2.9 on 2022-01-18 10:21

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0001_initial'),
    ]

    operations = [
        migrations.AlterField(
            model_name='resource',
            name='status',
            field=models.CharField(choices=[('U', 'unreleased'), ('C', 'checking'), ('P', 'passed'), ('F', 'failed')], default='U', max_length=1),
        ),
    ]
