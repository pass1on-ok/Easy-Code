from django.urls import path
from . import views
from rest_framework.decorators import api_view
from .views import TakeTestView, FinalTestView

urlpatterns = [
    path('take_test/<slug:slug>/', TakeTestView.as_view(), name='take_test'),
    path('final_test/<slug:slug>/', FinalTestView.as_view(), name='final_test'),
]
