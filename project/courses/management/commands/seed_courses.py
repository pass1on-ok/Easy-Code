from django.core.management.base import BaseCommand
from courses.models import Course, Video
from teacher.models import Teacher

COURSE_DATA = [
    {
        'name': 'Python for Beginners',
        'slug': 'python_for_beginners',
        'description': 'Complete Python course for absolute beginners. Learn basics, functions, OOP, and more.',
        'price': 4999,
        'discount': 0,
        'active': True,
        'thumbnail': 'files/thumbnail/python.png',
        'resource': 'files/resource/python.png',
        'product_id': 'price_1Q9pqmRtJj59SsmcdpFPW87j',
        'video_count': 7,
    },
    {
        'name': 'C++ for Beginners',
        'slug': 'cplusplus_for_beginners',
        'description': 'Learn C++ from scratch. Pointers, classes, inheritance, and modern C++ features.',
        'price': 4999,
        'discount': 0,
        'active': True,
        'thumbnail': None,
        'resource': 'files/resource/cplusplus.png',
        'product_id': 'price_1QBBSFRtJj59Ssmc63UrBIBY',
        'video_count': 7,
    },
    {
        'name': 'JavaScript for Beginners',
        'slug': 'javascript_for_beginners',
        'description': 'Master JavaScript fundamentals, DOM manipulation, ES6+, async programming.',
        'price': 4999,
        'discount': 0,
        'active': True,
        'thumbnail': 'files/thumbnail/JavaScript-logo.png',
        'resource': 'files/resource/javascript-logo.png',
        'product_id': 'price_1QBeLtRtJj59SsmcimeFFy6J',
        'video_count': 7,
    },
    {
        'name': 'React Framework',
        'slug': 'react_framework',
        'description': 'Build modern web apps with React. Hooks, Context, Router, and advanced concepts.',
        'price': 5999,
        'discount': 0,
        'active': True,
        'thumbnail': 'files/thumbnail/react.png',
        'resource': 'files/resource/react.png',
        'product_id': 'price_1QBf5sRtJj59SsmclADkKqnN',
        'video_count': 7,
    },
    {
        'name': 'Vue.js Framework',
        'slug': 'vuejs_framework',
        'description': 'Build reactive web apps with Vue.js. Components, Vuex/Pinia, Router, Composition API.',
        'price': 5999,
        'discount': 0,
        'active': True,
        'thumbnail': 'files/thumbnail/vue.png',
        'resource': 'files/resource/vue.png',
        'product_id': 'price_1QBfY2RtJj59SsmcLbL0y6g6',
        'video_count': 7,
    },
    {
        'name': 'Unity Game Development',
        'slug': 'unity_game_development',
        'description': 'Create games with Unity engine. C# scripting, physics, animations, multi-platform deployment.',
        'price': 5999,
        'discount': 0,
        'active': True,
        'thumbnail': 'files/thumbnail/unity.png',
        'resource': 'files/resource/unity.png',
        'product_id': 'price_1QBgWBRtJj59Ssmckp0mjDnB',
        'video_count': 7,
    },
]

class Command(BaseCommand):
    help = 'Seed sample courses with videos'

    def handle(self, *args, **options):
        created_courses = 0
        created_videos = 0

        for data in COURSE_DATA:
            course, created = Course.objects.update_or_create(
                slug=data['slug'],
                defaults={
                    'name': data['name'],
                    'description': data['description'],
                    'price': data['price'],
                    'discount': data['discount'],
                    'active': data['active'],
                    'thumbnail': data['thumbnail'],
                    'length': 420,  # 7 hours
                    'product_id': data['product_id'],
                    'resource': data['resource'],
                }
            )
            created_courses += 1 if created else 0
            self.stdout.write(self.style.SUCCESS(f'Updated/Created course: {course.name} (thumbnail: {course.thumbnail})'))

            # Create videos
            for i in range(1, data['video_count'] + 1):
                is_preview = i == 1
                video_url = 'https://www.youtube.com/embed/dQw4w9WgXcQ'
                video_id = f'{data["slug"][:12]}_{i}'

                video, created_video = Video.objects.update_or_create(
                    course=course,
                    serial_number=i,
                    defaults={
                        'title': f'Video {i}: Lecture {i}',
                        'is_preview': is_preview,
                        'video_url': video_url,
                        'video_id': video_id,
                    }
                )
                if created_video:
                    created_videos += 1

        self.stdout.write(self.style.SUCCESS(f'Seed complete: {created_courses} courses updated, {created_videos} videos created'))

