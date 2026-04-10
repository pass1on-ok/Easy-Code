from django.contrib import admin
from courses.models import Course, UserCourse, Tag, Prerequesite, Learning, Video

class TagAdmin(admin.TabularInline):
    model = Tag

class LearningAdmin(admin.TabularInline):
    model = Learning

class PrerequesiteAdmin(admin.TabularInline):
    model = Prerequesite

class VideoAdmin(admin.TabularInline):
    model = Video

class CourseAdmin(admin.ModelAdmin):
    inlines = [TagAdmin, LearningAdmin, PrerequesiteAdmin, VideoAdmin]
    list_display = ('name', 'price', 'active', 'teacher_names')

    def teacher_names(self, obj):
        return ', '.join([teacher.user.username for teacher in obj.teachers.all()])
    teacher_names.short_description = 'Teachers'

admin.site.register(Course, CourseAdmin)
admin.site.register(Video)
admin.site.register(UserCourse)


