import os
import django

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'project.settings')
django.setup()

from courses.models import Video, Course

# Update Python course videos
python_course = Course.objects.get(slug='python_for_beginners')
videos = Video.objects.filter(course=python_course).order_by('serial_number')

print(f'Updating videos for {python_course.name}...')

for v in videos:
    old_url = v.video_url
    if not v.video_url.startswith('https://'):
        # Extract video ID and create proper embed URL
        video_id = v.video_url.split('?')[0]
        v.video_url = f'https://www.youtube.com/embed/{video_id}'
        v.save()
        print(f'{v.serial_number}. {v.title}: Updated')
    else:
        print(f'{v.serial_number}. {v.title}: Already has full URL')

print('\nAll videos updated!')
print(f'Total videos: {videos.count()}')
