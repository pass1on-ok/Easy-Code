# Easy Code - Инструкция по установке

## Требования

- [Node.js 18+](https://nodejs.org/)
- [Python 3.11+](https://www.python.org/downloads/) (включить "Add Python to PATH")

## Шаг 1: Установка Backend (Django)

**Windows:**
```powershell
cd project
..\ven\Scripts\Activate.ps1
pip install -r requirements.txt
python manage.py migrate
cd ..
```

**Mac/Linux:**
```bash
cd project
source ../ven/bin/activate
pip install -r requirements.txt
python manage.py migrate
cd ..
```

## Шаг 2: Установка Frontend (Vue.js)

```bash
cd frontend
npm install
cd ..
```

## Шаг 3: Запуск проекта

Откройте **два терминала** одновременно:

**Терминал 1 - Backend:**

Windows:
```powershell
cd project
..\ven\Scripts\Activate.ps1
python manage.py runserver
```

Mac/Linux:
```bash
cd project
source ../ven/bin/activate
python manage.py runserver
```

**Терминал 2 - Frontend:**

```bash
cd frontend
npm run dev
```

## Открыть сайт

→ **Сайт:** http://localhost:5173/

→ **API:** http://localhost:8000/
