from django.db import models
from django.contrib.auth.models import AbstractUser


class User(AbstractUser):
    phone = models.CharField(max_length=255, unique=True)

    # change USERNAME_FIELD to phone so that
    # user can login with their phones
    USERNAME_FIELD = 'phone'

    def __str__(self):
        return self.username

    class Meta:
        ordering = ['username']
