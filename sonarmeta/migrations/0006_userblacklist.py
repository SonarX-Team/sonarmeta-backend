# Generated by Django 3.2.9 on 2022-01-25 11:44

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('sonarmeta', '0005_resource_carry_from'),
    ]

    operations = [
        migrations.CreateModel(
            name='UserBlacklist',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('be_prevented', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='be_prevented', to='sonarmeta.profile')),
                ('preventer', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='prevent', to='sonarmeta.profile')),
            ],
            options={
                'unique_together': {('be_prevented', 'preventer')},
            },
        ),
    ]
