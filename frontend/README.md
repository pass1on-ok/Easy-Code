# Easy Code - Запуск (Windows)

## Требования
- [Node.js 18+](https://nodejs.org/)
- [Python 3.11+](https://www.python.org/downloads/) (включить "Add Python to PATH")

## Первый запуск

**Backend:**
```powershell
cd project
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
python manage.py migrate
cd ..
```

**Frontend:**
```powershell
cd frontend
npm install
cd ..
```

## Запуск серверов

Откройте **два окна PowerShell**:

**Окно 1:**
```powershell
cd project
.venv\Scripts\Activate.ps1
python manage.py runserver
```

**Окно 2:**
```powershell
cd frontend
npm run dev
```

→ Открыть: **http://localhost:5173**

## Ошибки

**Execution Policy:**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

**Порт занят:**
```powershell
netstat -ano | findstr :5173
taskkill /PID [номер_из_команды_выше] /F
```
