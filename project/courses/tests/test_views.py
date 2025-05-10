import pytest
from django.urls import reverse
from django.contrib.auth.models import User

@pytest.mark.django_db
def test_signup_get(client):
    url = reverse('signup')  # Update the name to match your `SignupView` URL pattern
    response = client.get(url)
    assert response.status_code == 200
    assert "form" in response.context

@pytest.mark.django_db
def test_signup_post(client):
    url = reverse('signup')
    data = {
        "username": "testuser",
        "password1": "complexpassword123",
        "password2": "complexpassword123"
    }
    response = client.post(url, data)
    assert response.status_code == 302
    assert User.objects.filter(username="testuser").exists()

@pytest.mark.django_db
def test_login_view(client):
    url = reverse('login')
    response = client.get(url)
    assert response.status_code == 200
    assert "form" in response.context

@pytest.mark.django_db
def test_signout_view(client, django_user_model):
    user = django_user_model.objects.create_user(username="testuser", password="testpassword")
    client.login(username="testuser", password="testpassword")
    response = client.get(reverse('logout'))
    assert response.status_code == 302  # Redirect to home
