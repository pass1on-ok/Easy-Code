# teacher/admin.py
from django.contrib import admin
from .models import Teacher

# Регистрируем модель Teacher в админке
@admin.register(Teacher)
class TeacherAdmin(admin.ModelAdmin):
    list_display = ('user', 'bio')
    filter_horizontal = ('courses',)


