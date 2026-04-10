# TODO: Django DB Migration from SQLite to PostgreSQL (Neon)

## Steps:
- [x] 1. Update requirements.txt (add psycopg[binary]==3.2.3)
- [x] 2. Update project/project/settings.py (DATABASES config with provided PG URL)
- [x] 3. Install deps: cd project && ven\\Scripts\\activate && pip install -r requirements.txt
- [x] 4. Remove local db.sqlite3
- [x] 5. python manage.py makemigrations
## Все шаги завершены!

База данных успешно мигрирована на PostgreSQL (Neon).

- Backend теперь использует удаленную PG БД.
- Сервер запущен: http://127.0.0.1:8000
- Проверьте TODO.md и logs терминала на ошибки подключения.

**Дополнительно:** Создайте суперюзера командой `python project/manage.py createsuperuser` для /admin/.

Задача выполнена!
