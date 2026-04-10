from django.shortcuts import render, get_object_or_404, redirect
from courses.models import Course, Video
from .models import Question, UserAnswer, TestResult 
from django.contrib.auth.decorators import login_required
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.views import APIView
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi
from rest_framework.permissions import IsAuthenticated
from user_payment.models import UserPayment


class TakeTestView(APIView):
    permission_classes = [IsAuthenticated]

    lecture_param = openapi.Parameter(
        'lecture', openapi.IN_QUERY, description="Serial number of the lecture", type=openapi.TYPE_STRING
    )

    @swagger_auto_schema(manual_parameters=[lecture_param], responses={200: "Questions data"})
    def get(self, request, slug):
        course = get_object_or_404(Course, slug=slug)
        
        # Check if user has access to this course
        has_access = UserPayment.objects.filter(
            app_user=request.user, 
            course=course, 
            payment_bool=True
        ).exists()
        
        serial_number = request.GET.get('lecture')
        if not serial_number:
            return Response({"error": "Lecture serial number is required"}, status=400)

        video = get_object_or_404(Video, serial_number=serial_number, course=course)
        
        # If user doesn't have access and video is not preview, deny access
        if not has_access and not video.is_preview:
            return Response({"error": "You don't have access to this course"}, status=403)
        
        questions = video.questions.all()
        if not questions.exists():
            return Response({"error": "No questions available for this test."}, status=404)

        questions_data = []
        for q in questions:
            questions_data.append({
                "id": q.id,
                "question_text": q.question_text,
                "option_1": q.option_1 or "Option 1",
                "option_2": q.option_2 or "Option 2",
                "option_3": q.option_3 or "Option 3",
                "option_4": q.option_4 or "Option 4"
            })
        return Response({"questions": questions_data})

    @swagger_auto_schema(manual_parameters=[lecture_param], responses={200: "Test results or question data"})
    def post(self, request, slug):
        course = get_object_or_404(Course, slug=slug)
        
        # Check if user has access to this course
        has_access = UserPayment.objects.filter(
            app_user=request.user, 
            course=course, 
            payment_bool=True
        ).exists()
        
        serial_number = request.GET.get('lecture')
        if not serial_number:
            return Response({"error": "Lecture serial number is required"}, status=400)

        video = get_object_or_404(Video, serial_number=serial_number, course=course)
        
        # If user doesn't have access and video is not preview, deny access
        if not has_access and not video.is_preview:
            return Response({"error": "You don't have access to this course"}, status=403)
        
        questions = video.questions.all()

        score = 0
        total_questions = questions.count()
        results_detail = []
        for question in questions:
            selected_option = request.data.get(str(question.id))
            selected_option_int = None
            if selected_option is not None:
                try:
                    selected_option_int = int(selected_option)
                except (ValueError, TypeError):
                    selected_option_int = None

            is_correct = selected_option_int == question.correct_option
            if selected_option_int is not None:
                if is_correct:
                    score += 1

                UserAnswer.objects.create(
                    user=request.user,
                    question=question,
                    selected_option=selected_option_int,
                    is_correct=is_correct
                )

            option_map = {
                1: question.option_1,
                2: question.option_2,
                3: question.option_3,
                4: question.option_4,
            }
            results_detail.append({
                "question_id": question.id,
                "question_text": question.question_text,
                "selected_option": selected_option_int,
                "selected_text": option_map.get(selected_option_int, None),
                "correct_option": question.correct_option,
                "correct_text": option_map.get(question.correct_option, None),
                "is_correct": is_correct,
            })

        passed = total_questions > 0 and (score / total_questions) >= 0.7
        TestResult.objects.update_or_create(
            user=request.user, video=video,
            defaults={"score": score, "total_questions": total_questions, "passed": passed}
        )
        return Response({
            "score": score,
            "total_questions": total_questions,
            "passed": passed,
            "results_detail": results_detail,
        })


class FinalTestView(APIView):
    permission_classes = [IsAuthenticated]

    lecture_param = openapi.Parameter(
        'lecture', openapi.IN_QUERY, description="Final exam flag", type=openapi.TYPE_STRING
    )

    def _final_questions(self, course):
        if course.name == "Python for Beginners":
            return [
                ("What is a variable in Python?", ["A container for data", "A type of loop", "An error", "None of the above"], 0),
                ("What is a function?", ["A block of code", "A data type", "An operator", "None of the above"], 0),
                ("How do you declare a list in Python?", ["[]", "{}", "()", "<>"] , 0),
                ("What is the difference between a list and a tuple?", ["Lists are mutable, tuples are not", "Tuples are mutable, lists are not", "They are the same", "None of the above"], 0),
                ("What is the purpose of the 'return' statement?", ["To exit a function and return a value", "To declare a variable", "To start a loop", "None of the above"], 0),
                ("What is Python?", ["A programming language", "A type of snake", "An IDE", "None of the above"], 0),
                ("Which of the following is a Python data type?", ["int", "for", "define", "None of the above"], 0),
                ("What keyword is used to create a function?", ["def", "function", "define", "None of the above"], 0),
                ("Which symbol is used for comments in Python?", ["#", "//", "/* */", "None of the above"], 0),
                ("What is the output of print(2 + 3)?", ["5", "23", "Error", "None of the above"], 0)
            ]
        elif course.name == "C++ for Beginners":
            return [
                ("What is a pointer in C++?", ["A variable that stores memory address", "A data type", "An operator", "None of the above"], 0),
                ("What are constructors and destructors?", ["Special functions in classes", "Loop controls", "Variable types", "None of the above"], 0),
                ("How do you declare an array in C++?", ["int arr[10];", "list arr = []", "array arr{};", "None of the above"], 0),
                ("What is function overloading?", ["Using multiple functions with the same name but different parameters", "Using functions inside loops", "A function that calls itself", "None of the above"], 0),
                ("What is the purpose of header files?", ["To define functions and macros", "To write code", "To create classes", "None of the above"], 0),
                ("What is inheritance in C++?", ["A mechanism to create a new class from an existing class", "A loop", "A function", "None of the above"], 0),
                ("What is polymorphism?", ["Ability of a function to have multiple forms", "A data type", "A pointer", "None of the above"], 0),
                ("How do you define a class in C++?", ["class ClassName { }", "def ClassName() { }", "function ClassName() { }", "None of the above"], 0),
                ("Which operator is used to access members of a class?", [". (dot operator)", "* (asterisk)", "& (ampersand)", "None of the above"], 0),
                ("How do you comment a line in C++?", ["//", "#", "/* */", "None of the above"], 0)
            ]
        elif course.name == "JavaScript for beginners":
            return [
                ("What is JavaScript used for?", ["Creating interactive web pages", "Server management", "Database handling", "None of the above"], 0),
                ("How do you declare a variable in JavaScript?", ["let", "int", "define", "None of the above"], 0),
                ("What is the difference between 'let' and 'var'?", ["'let' has block scope, 'var' does not", "'var' has block scope, 'let' does not", "They are the same", "None of the above"], 0),
                ("What is an event in JavaScript?", ["An action that happens in the browser", "A loop", "A variable", "None of the above"], 0),
                ("What is the purpose of 'this' keyword?", ["Refers to the current object", "Declares a variable", "Starts a loop", "None of the above"], 0),
                ("Which symbol is used for comments in JavaScript?", ["//", "#", "/* */", "None of the above"], 0),
                ("What is the purpose of the 'for' loop?", ["To iterate over a sequence", "To create a function", "To declare a variable", "None of the above"], 0),
                ("What does DOM stand for?", ["Document Object Model", "Data Object Management", "Desktop Operating Machine", "None of the above"], 0),
                ("How do you write 'Hello World' in an alert box?", ["alert('Hello World')", "msg('Hello World')", "console.log('Hello World')", "None of the above"], 0),
                ("Which operator is used for strict equality in JavaScript?", ["===", "==", "=", "!=="], 0)
            ]
        elif course.name == "React Framework":
            return [
                ("What is React?", ["A JavaScript library for building user interfaces", "A database management tool", "A CSS framework", "None of the above"], 0),
                ("What is JSX?", ["A syntax extension for JavaScript", "A new programming language", "A CSS preprocessor", "None of the above"], 0),
                ("How do you create a component in React?", ["Using a function or a class", "Using HTML directly", "By creating a database", "None of the above"], 0),
                ("What is the virtual DOM?", ["A lightweight copy of the real DOM", "A CSS file", "A backend framework", "None of the above"], 0),
                ("What is the use of props in React?", ["To pass data between components", "To define styles", "To manage state", "None of the above"], 0),
                ("What is state in React?", ["An object that determines component rendering", "A function", "A CSS style", "None of the above"], 0),
                ("How do you handle events in React?", ["Using event handlers like onClick", "Using loops", "Using CSS", "None of the above"], 0),
                ("What is a React Hook?", ["A function that lets you use state and lifecycle features", "A CSS function", "A type of variable", "None of the above"], 0),
                ("How do you create a React app?", ["Using create-react-app command", "Using npm install", "By writing HTML", "None of the above"], 0),
                ("What is a higher-order component?", ["A function that takes a component and returns a new component", "A built-in component", "A CSS class", "None of the above"], 0)
            ]
        elif course.name == "Vue JS Framework":
            return [
                ("What is Vue.js?", ["A progressive JavaScript framework", "A CSS library", "A database", "None of the above"], 0),
                ("What is a Vue component?", ["A reusable instance with its own data and logic", "A CSS element", "A type of variable", "None of the above"], 0),
                ("How do you declare a Vue component?", ["Vue.component('component-name', {})", "var component = { }", "function component() { }", "None of the above"], 0),
                ("What is a Vue directive?", ["A special token in the markup", "A component", "A data property", "None of the above"], 0),
                ("What is the Vue virtual DOM?", ["A lightweight copy of the real DOM", "A CSS framework", "A backend handler", "None of the above"], 0),
                ("What is a Vue store?", ["A centralized state manager", "A file system", "A database", "None of the above"], 0),
                ("What is a single-file component in Vue?", ["A file that contains template, script, and style", "A CSS file", "An image file", "None of the above"], 0),
                ("How do you bind data in Vue?", ["Using v-bind or :", "Using JavaScript only", "Using a server", "None of the above"], 0),
                ("How do you create a reactive property in Vue?", ["Using ref or reactive", "Using class", "Using SQL", "None of the above"], 0),
                ("How do you navigate between pages in Vue Router?", ["Using RouterLink or router.push()", "Using HTML anchor tags only", "Using Python", "None of the above"], 0)
            ]
        else:
            return []

    @swagger_auto_schema(manual_parameters=[lecture_param], responses={200: "Final questions data"})
    def get(self, request, slug):
        course = get_object_or_404(Course, slug=slug)
        has_access = UserPayment.objects.filter(app_user=request.user, course=course, payment_bool=True).exists()
        if not has_access:
            return Response({"error": "You don't have access to this course"}, status=403)

        questions = self._final_questions(course)
        if not questions:
            return Response({"error": "Final exam is not available for this course."}, status=404)

        questions_data = []
        for idx, (question_text, options, _) in enumerate(questions, start=1):
            questions_data.append({
                "id": idx,
                "question": question_text,
                "options": options
            })

        return Response({"questions": questions_data})

    @swagger_auto_schema(manual_parameters=[lecture_param], responses={200: "Final test result"})
    def post(self, request, slug):
        course = get_object_or_404(Course, slug=slug)
        has_access = UserPayment.objects.filter(app_user=request.user, course=course, payment_bool=True).exists()
        if not has_access:
            return Response({"error": "You don't have access to this course"}, status=403)

        questions = self._final_questions(course)
        if not questions:
            return Response({"error": "Final exam is not available for this course."}, status=404)

        score = 0
        total_questions = len(questions)
        for idx, (_, _, correct_option) in enumerate(questions, start=1):
            selected_option = request.data.get(str(idx))
            if selected_option is not None:
                try:
                    selected_option = int(selected_option)
                except (ValueError, TypeError):
                    continue
                if selected_option == correct_option:
                    score += 1

        passed = total_questions > 0 and (score / total_questions) >= 0.7
        return Response({"score": score, "total_questions": total_questions, "passed": passed})


# @login_required
# def take_test(request, slug):
#     course = get_object_or_404(Course, slug=slug)
#     serial_number = request.GET.get('lecture')
#     if not serial_number:
#         return redirect('course_page', slug=slug)

#     video = get_object_or_404(Video, serial_number=serial_number, course=course)
#     questions = video.questions.all()

#     if not questions.exists():
#         return render(request, 'exam/no_questions.html', {
#             'video': video,
#             'course': course,
#             'error': "В тесте отсутствуют вопросы."
#         })

#     test_result = TestResult.objects.filter(user=request.user, video=video).first()

#     if request.method == 'POST':
#         score = 0
#         total_questions = questions.count()

#         for question in questions:
#             selected_option = request.POST.get(str(question.id))
#             if selected_option:
#                 selected_option = int(selected_option)
#                 is_correct = selected_option == question.correct_option

#                 if is_correct:
#                     score += 1

#                 UserAnswer.objects.create(
#                     user=request.user,
#                     question=question,
#                     selected_option=selected_option,
#                     is_correct=is_correct
#                 )

#         if test_result:
#             test_result.score = score
#             test_result.total_questions = total_questions
#             test_result.save()
#         else:
#             TestResult.objects.create(
#                 user=request.user,
#                 video=video,
#                 score=score,
#                 total_questions=total_questions
#             )

#         return render(request, 'exam/results.html', {
#             'score': score,
#             'total': total_questions,
#             'course': course
#         })

#     return render(request, 'exam/take_test.html', {
#         'course': course,
#         'video': video,
#         'questions': questions,
#         'test_result': test_result
#     })

@login_required
def take_test(request, slug):
    course = get_object_or_404(Course, slug=slug)
    serial_number = request.GET.get('lecture')
    if not serial_number:
        return redirect('course_page', slug=slug)

    video = get_object_or_404(Video, serial_number=serial_number, course=course)
    questions = video.questions.all()

    if not questions.exists():
        return render(request, 'exam/no_questions.html', {
            'video': video,
            'course': course,
            'error': "В тесте отсутствуют вопросы."
        })


    test_result = TestResult.objects.filter(user=request.user, video=video).first()

    if request.method == 'POST':
        score = 0
        total_questions = questions.count()

        for question in questions:
            selected_option = request.POST.get(str(question.id))
            if selected_option:
                selected_option = int(selected_option)
                is_correct = selected_option == question.correct_option

                if is_correct:
                    score += 1


                UserAnswer.objects.create(
                    user=request.user,
                    question=question,
                    selected_option=selected_option,
                    is_correct=is_correct
                )

        if test_result:
            test_result.score = score
            test_result.total_questions = total_questions
            test_result.save()
        else:
            TestResult.objects.create(
                user=request.user,
                video=video,
                score=score,
                total_questions=total_questions
            )

        return render(request, 'exam/results.html', {
            'score': score,
            'total': total_questions,
            'course': course
        })

    return render(request, 'exam/take_test.html', {
        'course': course,
        'video': video,
        'questions': questions,
        'test_result': test_result
    })
