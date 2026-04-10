#!/usr/bin/env python
import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'project.settings')
django.setup()

from django.db import connection

print("=" * 60)
print("PostgreSQL Database Verification")
print("=" * 60)

# Get database info
print(f"\nDatabase: {connection.settings_dict['NAME']}")
print(f"Host: {connection.settings_dict['HOST']}")
print(f"User: {connection.settings_dict['USER']}")

# List all tables
with connection.cursor() as cursor:
    cursor.execute("SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_name;")
    tables = cursor.fetchall()
    
    if tables:
        print(f"\nFound {len(tables)} tables:")
        for table in tables:
            table_name = table[0]
            # Get row count
            cursor.execute(f"SELECT COUNT(*) FROM {table_name};")
            count = cursor.fetchone()[0]
            print(f"  ✓ {table_name:40} ({count:,} rows)")
    else:
        print("\n⚠ No tables found!")

# Check for any SQLite references
print("\n" + "=" * 60)
print("SQLite Configuration Check")
print("=" * 60)

with connection.cursor() as cursor:
    # Check Django apps configuration
    from django.conf import settings
    db_engine = settings.DATABASES['default']['ENGINE']
    print(f"\nConfigured Database Engine: {db_engine}")
    
    if 'sqlite' in db_engine.lower():
        print("⚠ WARNING: SQLite is still configured!")
    else:
        print("✓ No SQLite configuration found")

print("\n" + "=" * 60)
