#!/usr/bin/env python
import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'project.settings')
django.setup()

from django.contrib.auth.models import User

if not User.objects.filter(username='testuser').exists():
    User.objects.create_user(username='testuser', email='test@example.com', password='testpass123')
    print('✓ Test user created: testuser / testpass123')
else:
    print('✓ Test user already exists')
