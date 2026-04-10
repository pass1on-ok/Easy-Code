import pytest
from courses.models import Course, Video

@pytest.mark.django_db
def test_course_creation():
    course = Course.objects.create(
        name="Test Course",
        slug="test-course",
        description="A test course",
        price=100,  # Ensure price is provided
        discount=10,
        active=True,
        length=60,  # Provide length
        date="2024-12-01",
    )
    assert course.name == "Test Course"
    assert course.price == 100  # Ensure price is correct
    assert course.discount == 10

@pytest.mark.django_db
def test_video_creation():
    course = Course.objects.create(
        name="Course", 
        slug="course", 
        description="Test Course", 
        price=100,  # Provide price
        discount=10,
        active=True,
        length=60,  # Provide length
        date="2024-12-01",
    )
    video = Video.objects.create(
        course=course,
        title="Test Video",
        serial_number=1,
    )
    assert video.title == "Test Video"
    assert video.course.name == "Course"