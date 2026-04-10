# Course Previews Fix - Progress Tracker

## Steps:
- [x] 1. Confirm project/files/thumbnail/ directory structure ✓
- [x] 2. Download/rename images for all 6 courses in project/files/thumbnail/ (run manual curl/ren for cplusplus.png, javascript.png, vuejs.png)
- [x] 3. Edit project/courses/management/commands/seed_courses.py with correct thumbnail paths and full defaults ✓
- [ ] 4. Run `cd ../project && python manage.py seed_courses` to update DB
- [ ] 5. Verify in frontend/Django admin (restart servers)

**Current: Steps 1-3 complete. Run seed command next.**

