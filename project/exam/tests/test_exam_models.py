import pytest
from django.contrib.auth.models import User
from courses.models import Course, Video
from exam.models import Question, UserAnswer, TestResult

@pytest.mark.django_db
def test_question_creation():
    course = Course.objects.create(
        name="Course with Question",
        slug="course-with-question",
        description="Course for testing question creation",
        price=100,  # Provide price
        discount=10,
        active=True,
        length=60,  # Provide length
        date="2024-12-01",
    )
    video = Video.objects.create(course=course, title="Test Video", serial_number=1)
    question = Question.objects.create(
        video=video,
        question_text="What is 2 + 2?",
        option_1="3",
        option_2="4",
        option_3="5",
        option_4="6",
        correct_option=2,  # Option 2 is correct
    )
    assert question.question_text == "What is 2 + 2?"
    assert question.correct_option == 2

@pytest.mark.django_db
def test_user_answer_creation():
    user = User.objects.create_user(username="testuser", password="testpassword")
    course = Course.objects.create(
        name="Test Course", 
        slug="test-course", 
        price=100,  # Provide price
        length=60,  # Provide length
        discount=10, 
        active=True, 
        date="2024-12-01"
    )
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
    
    user_answer = UserAnswer.objects.create(
        user=user,
        question=question,
        selected_option=2,
        is_correct=True
    )

    assert user_answer.user == user
    assert user_answer.is_correct is True

@pytest.mark.django_db
def test_test_result_creation():
    user = User.objects.create_user(username="testuser", password="testpassword")
    course = Course.objects.create(
        name="Test Course", 
        slug="test-course", 
        price=100,  # Provide price
        length=60,  # Provide length
        discount=10, 
        active=True, 
        date="2024-12-01"
    )
    video = Video.objects.create(course=course, title="Test Video", serial_number=1)
    
    test_result = TestResult.objects.create(
        user=user,
        video=video,
        score=1,
        total_questions=1
    )

    assert test_result.score == 1
    assert test_result.total_questions == 1