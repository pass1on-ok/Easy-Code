from django.db import models

#from teacher.models import Teacher



class Course(models.Model):
    name = models.CharField(max_length = 50, null = False)
    slug = models.CharField(max_length = 50, null = True, unique = True)
    #teacher = models.ForeignKey('teacher.Teacher', on_delete=models.CASCADE, related_name="courses")
    description = models.CharField(max_length = 1000, null = True)
    price = models.IntegerField(null = False)
    discount = models.IntegerField(null = False, default = 0)
    active = models.BooleanField(default= False)
    thumbnail = models.ImageField(upload_to = "files/thumbnail")
    date = models.DateTimeField(auto_now_add = True)
    resource = models.FileField(upload_to = "files/resource")
    length = models.IntegerField(null = False)
    product_id = models.CharField (max_length = 200, null = False)
    #teacher = models.ForeignKey(Teacher, on_delete=models.CASCADE, related_name="courses")
    

    def __str__(self):
        return self.name
    

class CourseProperty(models.Model):
    description = models.CharField(max_length = 100, null = False)
    course = models.ForeignKey(Course, null = False, on_delete = models.CASCADE)
    
    class Meta:
        abstract = True

class Tag(CourseProperty):
    pass

class Prerequesite(CourseProperty):
    pass

class Learning(CourseProperty):
    pass

