import pytest
from django.urls import reverse
from django.contrib.auth.models import User
from courses.models import Course, Video
from exam.models import Question, TestResult, UserAnswer

@pytest.mark.django_db
def test_take_test_get(client, django_user_model):
    user = django_user_model.objects.create_user(username='testuser', password='testpassword')
    course = Course.objects.create(name="Test Course", slug="test-course")
    video = Video.objects.create(course=course, title="Test Video", serial_number=1)
    question = Question.objects.create(
        question_text="What is 2 + 2?",
        video=video,
        option_1="3",
        option_2="4",
        option_3="5",
        option_4="6",
        correct_option=2
    )

    client.login(username='testuser', password='testpassword')
    
    url = reverse('take-test', kwargs={'slug': course.slug})
    response = client.get(url, {'lecture': video.serial_number})
    
    assert response.status_code == 200
    assert "What is 2 + 2?" in response.content.decode()

@pytest.mark.django_db
def test_take_test_post(client, django_user_model):
    user = django_user_model.objects.create_user(username='testuser', password='testpassword')
    course = Course.objects.create(name="Test Course", slug="test-course")
    video = Video.objects.create(course=course, title="Test Video", serial_number=1)
    question = Question.objects.create(
        question_text="What is 2 + 2?",
        video=video,
        option_1="3",
        option_2="4",
        option_3="5",
        option_4="6",
        correct_option=2
    )
    
    client.login(username='testuser', password='testpassword')
    
    post_data = {str(question.id): 2}
    url = reverse('take-test', kwargs={'slug': course.slug})
    response = client.post(url, post_data)
    
    assert response.status_code == 200
    assert "Your score is" in response.content.decode()

    test_result = TestResult.objects.get(user=user, video=video)
    assert test_result.score == 1
    assert test_result.total_questions == 1
