from django.contrib import admin
from django.urls import path, include
from django.shortcuts import HttpResponse
from courses.views import home, coursePage, SignupView, LoginView, signout, checkout
from django.conf.urls.static import static
from django.conf import settings
from django.urls import path
from . import views
from .views.courses import final_test_page
from .views.courses import CourseListView
from .views.api_auth import signup_api
from .views.api_courses import enroll_course_api, purchased_courses_api, course_detail_api

urlpatterns = [
    path('', home, name = 'home'),
    path('logout/', signout, name = 'logout'),
    path('signup/', SignupView.as_view(), name = 'signup'),
    path('api/signup/', signup_api, name = 'api_signup'),
    path('login/', LoginView.as_view(), name = 'login'),
    path('course/<slug:slug>/', views.coursePage, name='course_page'),
    path('api/course/<slug:slug>/', course_detail_api, name='api_course_detail'),
    path('check-out/<str:slug>/', enroll_course_api, name = 'api_enroll'),
    path('check-out/<str:slug>', checkout, name = 'checkpage'),
    path('payments/', include('user_payment.urls')),
    path('course/<slug:slug>/final_test_page/', final_test_page, name='final_test_page'),
    path('api/courses/', CourseListView.as_view(), name='course-list'),
    path('api/purchased-courses/', purchased_courses_api, name='api_purchased_courses'),
] 

urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)