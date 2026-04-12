import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'project.settings')
django.setup()

from courses.models import Video, Course

# Update Python course videos with descriptions
python_course = Course.objects.get(slug='python_for_beginners')
videos = Video.objects.filter(course=python_course).order_by('serial_number')

descriptions = [
    "Introduction to Python programming. Learn about Python syntax, variables, and basic concepts.",
    "Understanding data types in Python: strings, integers, floats, and booleans.",
    "Working with Python operators: arithmetic, comparison, logical, and assignment operators.",
    "Control flow in Python: if statements, elif, and else conditions.",
    "Loops in Python: for loops and while loops with practical examples.",
    "Functions in Python: defining functions, parameters, and return values.",
    "Introduction to Python modules and importing libraries."
]

print(f'Updating videos for {python_course.name}...\n')

for i, v in enumerate(videos):
    if i < len(descriptions):
        v.description = descriptions[i]
    
    # Make first video a preview
    if i == 0:
        v.is_preview = True
    else:
        v.is_preview = False
    
    v.save()
    print(f'{v.serial_number}. {v.title}')
    print(f'   Preview: {v.is_preview}')
    print(f'   Description: {v.description[:60]}...\n')

print('All videos updated!')
