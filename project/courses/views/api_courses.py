from rest_framework import status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
from courses.models import Course, UserCourse, Video
from django.shortcuts import get_object_or_404


@api_view(['GET'])
@permission_classes([AllowAny])
def course_detail_api(request, slug):
    """
    API endpoint to get course details including videos and materials
    """
    try:
        course = get_object_or_404(Course, slug=slug)
        videos = course.video_set.all().order_by("serial_number")
        
        thumbnail_url = None
        if course.thumbnail:
            thumbnail_url = request.build_absolute_uri(course.thumbnail.url)

        # Get materials if they exist
        materials = []
        if hasattr(course, 'materials'):
            for material in course.materials.all():
                materials.append({
                    'id': material.id,
                    'title': material.title,
                    'file': request.build_absolute_uri(material.file.url) if material.file else None
                })
        
        # Get videos
        video_list = []
        for video in videos:
            video_data = {
                'id': video.id,
                'title': video.title if hasattr(video, 'title') else f'Video {video.serial_number}',
                'serial_number': video.serial_number,
                'is_preview': video.is_preview if hasattr(video, 'is_preview') else False,
                'description': video.description if hasattr(video, 'description') else None,
            }
            if hasattr(video, 'video_url'):
                video_data['video_url'] = video.video_url
            video_list.append(video_data)

        course_data = {
            'id': course.id,
            'name': course.name,
            'slug': course.slug,
            'description': course.description,
            'price': course.price,
            'discount': course.discount,
            'thumbnail': thumbnail_url,
            'length': course.length if hasattr(course, 'length') else None,
            'videos': video_list,
            'materials': materials
        }

        return Response(course_data, status=status.HTTP_200_OK)
    
    except Exception as e:
        return Response(
            {'error': str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def enroll_course_api(request, slug):
    """
    API endpoint for enrolling in a course
    """
    course = get_object_or_404(Course, slug=slug)
    user = request.user

    # Check if user is already enrolled
    existing_enrollment = UserCourse.objects.filter(user=user, course=course).first()
    
    if existing_enrollment:
        return Response(
            {'message': 'You are already enrolled in this course'},
            status=status.HTTP_200_OK
        )

    # Create enrollment
    try:
        enrollment = UserCourse.objects.create(
            user=user,
            course=course
        )

        return Response({
            'message': 'Successfully enrolled in course',
            'course': {
                'id': course.id,
                'name': course.name,
                'slug': course.slug,
            },
            'enrollment_date': enrollment.date
        }, status=status.HTTP_201_CREATED)

    except Exception as e:
        return Response(
            {'error': str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def purchased_courses_api(request):
    """
    API endpoint to get user's purchased courses
    """
    user = request.user
    enrollments = UserCourse.objects.filter(user=user).select_related('course')
    
    courses = []
    for enrollment in enrollments:
        course = enrollment.course
        thumbnail_url = request.build_absolute_uri(course.thumbnail.url) if course.thumbnail else None
        
        courses.append({
            'id': course.id,
            'name': course.name,
            'slug': course.slug,
            'description': course.description,
            'price': course.price,
            'discount': course.discount,
            'thumbnail': thumbnail_url,
            'enrollment_date': enrollment.date,
            'completed': enrollment.completed,
            'grade': enrollment.grade
        })
    
    return Response(courses, status=status.HTTP_200_OK)
