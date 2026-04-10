#!/usr/bin/env python
import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'project.settings')
django.setup()

from django.db import connection
from django.contrib.auth.models import User
from courses.models import Course

print("=" * 70)
print("Complete Data Verification - SQLite to PostgreSQL Migration")
print("=" * 70)

# Check Users
print("\n📊 USER DATA")
print("-" * 70)
try:
    users = User.objects.all()
    print(f"✓ Django Auth Users: {users.count()} records")
    for user in users:
        print(f"  - {user.username} (id: {user.id})")
except Exception as e:
    print(f"✗ Error reading users: {e}")

# Check Courses
print("\n📚 COURSE DATA")
print("-" * 70)
try:
    courses = Course.objects.all()
    print(f"✓ Courses: {courses.count()} records")
    for course in courses[:10]:  # Show first 10
        print(f"  - {course.title if hasattr(course, 'title') else course.name} (id: {course.id})")
except Exception as e:
    print(f"✗ Error reading courses: {e}")

# Database info
print("\n🗄️  DATABASE INFORMATION")
print("-" * 70)
db_config = connection.settings_dict
print(f"✓ Engine: {db_config['ENGINE']}")
print(f"✓ Database: {db_config['NAME']}")
print(f"✓ Host: {db_config['HOST']}")
print(f"✓ Port: {db_config.get('PORT', '5432')}")

# Final check for SQLite
print("\n🔍 SQLITE REFERENCES CHECK")
print("-" * 70)
import os
project_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sqlite_files = []
for root, dirs, files in os.walk(project_dir):
    for file in files:
        if file.endswith('.sqlite3') or file.endswith('.db'):
            if '.venv' not in root:  # Skip venv
                sqlite_files.append(os.path.join(root, file))

if sqlite_files:
    print(f"⚠ Found {len(sqlite_files)} SQLite file(s):")
    for f in sqlite_files:
        print(f"  - {f}")
else:
    print("✓ No SQLite database files found in project directory")

print("\n" + "=" * 70)
print("✅ MIGRATION VERIFICATION COMPLETE")
print("=" * 70)
