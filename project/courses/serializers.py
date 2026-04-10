# serializers.py
from rest_framework import serializers
from .models import Course
from django.contrib.auth.models import User  # Импорт модели User, если она стандартная
from .models import Course, CourseMaterial, Video

class VideoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Video
        fields = ['id', 'title', 'serial_number', 'video_url', 'is_preview', 'video_id']

class CourseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Course
        fields = [
            'id', 'name', 'slug', 'description', 'price', 'discount', 'thumbnail', 'length', 'active', 'product_id'
        ]

class CourseCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Course
        fields = [
            'id', 'name', 'slug', 'description', 'price', 'discount', 'active',
            'thumbnail', 'resource', 'length', 'product_id'
        ]
        extra_kwargs = {
            'slug': {'required': False, 'allow_blank': True},
            'thumbnail': {'required': False, 'allow_null': True},
            'resource': {'required': False, 'allow_null': True}
        }

class CourseDetailSerializer(serializers.ModelSerializer):
    videos = VideoSerializer(source='video_set', many=True, read_only=True)
    
    class Meta:
        model = Course
        fields = ['id', 'name', 'slug', 'description', 'price', 'discount', 'thumbnail', 'length', 'active', 'resource', 'videos']

class AddStudentSerializer(serializers.Serializer):
    student = serializers.PrimaryKeyRelatedField(queryset=User.objects.all())
    course = serializers.PrimaryKeyRelatedField(queryset=Course.objects.all())

class CourseMaterialSerializer(serializers.ModelSerializer):
    class Meta:
        model = CourseMaterial  # Предполагаем, что у вас есть такая модель для материалов
        fields = ['id', 'course', 'file', 'description']

