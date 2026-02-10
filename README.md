# EasyCode 🎓

**Educational platform** built with Django, PostgreSQL, Stripe, JWT, Bootstrap, Swagger, and PyTest.

## 🚀 Features
- Role-based access (Student & Teacher)
- JWT Authentication & Authorization
- Payment integration using **Stripe**
- Video lessons with online exams
- Auto-generated PDF certificates upon course completion
- Fully documented API via **Swagger**
- High test coverage using **PyTest**

## 📦 Tech Stack
- Backend: Django (Python), PostgreSQL
- Frontend: Bootstrap
- Auth: JSON Web Tokens (JWT)
- Payments: Stripe
- API Docs: Swagger
- Testing: PyTest

## 🔧 Setup & Installation
```bash
git clone https://github.com/pass1on-ok/Easy-Code.git
cd Easy-Code
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
# Setup .env:
# SECRET_KEY=...
# STRIPE_API_KEY=...
# DATABASE_URL=...
python manage.py migrate
python manage.py runserver
