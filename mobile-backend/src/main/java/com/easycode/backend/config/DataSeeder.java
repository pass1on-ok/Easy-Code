package com.easycode.backend.config;

import com.easycode.backend.entity.Course;
import com.easycode.backend.entity.CourseMaterial;
import com.easycode.backend.entity.Profile;
import com.easycode.backend.entity.User;
import com.easycode.backend.entity.Video;
import com.easycode.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds demo data on startup when no courses exist yet.
 * Disabled in production via @Profile("!prod").
 *
 * Demo accounts:
 *   teacher / Teacher123!
 *   student / Student123!
 */
@Component
@org.springframework.context.annotation.Profile("!prod")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository     userRepository;
    private final ProfileRepository  profileRepository;
    private final CourseRepository   courseRepository;
    private final PasswordEncoder    passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            log.info("DataSeeder: data already exists, skipping.");
            return;
        }

        log.info("DataSeeder: seeding demo data...");

        User teacher = createUser("teacher", "teacher@easycode.kz", "Teacher123!", "Алибек", "Жаксыбеков");
        createProfile(teacher, "TEACHER", "10 жыл Java және Python тәжірибесі");

        User student = createUser("student", "student@easycode.kz", "Student123!", "Айгерим", "Сейтова");
        createProfile(student, "STUDENT", null);

        // ── Course 1: Python 基础 (free) ─────────────────────────────────────
        Course python = Course.builder()
                .name("Python негіздері")
                .slug("python-basics")
                .description("Программалаудың алғашқы қадамдары. Python тілін нөлден үйреніңіз: синтаксис, функциялар, файлдармен жұмыс. Бағдарламашы болу үшін ең жақсы бастама.")
                .price(0)
                .discount(0)
                .isPublished(true)
                .thumbnail("https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Python-logo-notext.svg/800px-Python-logo-notext.svg.png")
                .teacher(teacher)
                .build();

        python.setTags(List.of("Python", "Бастаушылар", "Программалау"));
        python.setPrerequisites(List.of(
                "Компьютерді негізгі деңгейде білу",
                "Математика бойынша мектеп курсы",
                "Оқуға ынта"
        ));
        python.setLearnings(List.of(
                "Python синтаксисін меңгеру",
                "Функциялар мен модульдерді жазу",
                "Файлдармен және деректермен жұмыс",
                "Нақты есептерді шешу"
        ));

        addVideos(python, List.of(
                video(1, "Python-ға кіріспе", "Python дегеніміз не және неге оны үйрену керек?", "https://www.youtube.com/embed/_uQrJ0TkZlc", 620, true),
                video(2, "Айнымалылар мен деректер типтері", "int, float, str, bool — Python типтері туралы", "https://www.youtube.com/embed/khKv-8q7YmY", 740, true),
                video(3, "Шарттар мен циклдар", "if/else, for, while — программа ағымын басқару", "https://www.youtube.com/embed/DZwmZ8Usvnk", 855, false),
                video(4, "Функциялар", "def, параметрлер, return мәні", "https://www.youtube.com/embed/9Os0o3wzS_I", 920, false),
                video(5, "Тізімдер мен сөздіктер", "list, dict, set — деректер құрылымдары", "https://www.youtube.com/embed/W8KRzm-HUcc", 1010, false)
        ));

        addMaterials(python, List.of(
                material("Python орнату нұсқаулығы", "https://docs.python.org/3/using/index.html", "Windows, macOS, Linux үшін орнату"),
                material("Python Cheat Sheet", "https://perso.limsi.fr/pointal/_media/python:cours:mementopython3-english.pdf", "Негізгі синтаксис анықтамалығы")
        ));

        courseRepository.save(python);

        // ── Course 2: Java Advanced (paid) ───────────────────────────────────
        Course java = Course.builder()
                .name("Java кәсіби деңгей")
                .slug("java-advanced")
                .description("Spring Boot, OOP тереңдетілген, дизайн паттерндер, деректер базасымен жұмыс. Бэкенд разработчик болуға жол ашатын толық курс.")
                .price(4900)
                .discount(20)
                .isPublished(true)
                .thumbnail("https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg")
                .teacher(teacher)
                .build();

        java.setTags(List.of("Java", "Spring Boot", "Backend", "OOP"));
        java.setPrerequisites(List.of(
                "Python немесе кез келген бағдарламалау тілін білу",
                "ООП негіздері",
                "Терминалмен жұмыс"
        ));
        java.setLearnings(List.of(
                "Java 17 синтаксисі мен мүмкіндіктері",
                "Spring Boot қосымша жасау",
                "REST API дизайны",
                "JPA / Hibernate арқылы дерекқормен жұмыс",
                "JWT аутентификациясын енгізу"
        ));

        addVideos(java, List.of(
                video(1, "Java 17 жаңалықтары", "Record, sealed class, pattern matching", "https://www.youtube.com/embed/l9AzO1FMgM8", 910, true),
                video(2, "Spring Boot жобасын бастау", "Spring Initializr, Maven, application.yml", "https://www.youtube.com/embed/9SGDpanrc8U", 1320, true),
                video(3, "REST Controller жазу", "@RestController, @GetMapping, ResponseEntity", "https://www.youtube.com/embed/vtPkZShrvXQ", 1150, false),
                video(4, "JPA Entity және Repository", "@Entity, JpaRepository, JPQL сұраулары", "https://www.youtube.com/embed/8SGI_XS5OPw", 1440, false),
                video(5, "Spring Security + JWT", "JWT filter, BCrypt, stateless auth", "https://www.youtube.com/embed/X80nJ5T7YpE", 1680, false),
                video(6, "Тестілеу: JUnit 5 + MockMvc", "@SpringBootTest, @WebMvcTest, assertions", "https://www.youtube.com/embed/flpmSXVTqBI", 1200, false)
        ));

        addMaterials(java, List.of(
                material("Spring Boot Reference", "https://docs.spring.io/spring-boot/docs/current/reference/html/", "Ресми Spring Boot құжаттамасы"),
                material("Java 17 API Docs", "https://docs.oracle.com/en/java/javase/17/docs/api/", "Java 17 API анықтамасы"),
                material("Бастапқы код (GitHub)", "https://github.com/spring-projects/spring-boot", "Курс бойынша код мысалдары")
        ));

        courseRepository.save(java);

        // ── Course 3: Web Full-Stack (paid) ──────────────────────────────────
        Course web = Course.builder()
                .name("Web Full-Stack: Vue + Django")
                .slug("web-fullstack")
                .description("Vue 3 (TypeScript) фронтенд пен Django бэкендін бірге үйреніңіз. Нақты жоба арқылы толық стектік веб-разработчик болыңыз.")
                .price(7900)
                .discount(10)
                .isPublished(true)
                .thumbnail("https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Vue.js_Logo_2.svg/800px-Vue.js_Logo_2.svg.png")
                .teacher(teacher)
                .build();

        web.setTags(List.of("Vue 3", "Django", "TypeScript", "Full-Stack"));
        web.setPrerequisites(List.of(
                "HTML, CSS, JavaScript негіздері",
                "Python білу",
                "Командалық жолмен жұмыс"
        ));
        web.setLearnings(List.of(
                "Vue 3 Composition API және TypeScript",
                "Pinia state management",
                "Django REST Framework арқылы API жасау",
                "JWT аутентификациясы",
                "Stripe интеграциясы"
        ));

        addVideos(web, List.of(
                video(1, "Vue 3 жобасын бастау", "Vite, TypeScript, Composition API", "https://www.youtube.com/embed/VeNfHj6MhgA", 780, true),
                video(2, "Pinia — State Management", "defineStore, ref, computed, actions", "https://www.youtube.com/embed/JGC7aAC-3y8", 890, true),
                video(3, "Vue Router 4", "Маршруттар, навигация гвардтары, lazy loading", "https://www.youtube.com/embed/GJGaefCsNNw", 1050, false),
                video(4, "Django REST Framework", "Serializers, ViewSets, APIView", "https://www.youtube.com/embed/TmsD8QObgAY", 1380, false),
                video(5, "JWT аутентификациясы", "SimpleJWT, токен жаңарту, CORS", "https://www.youtube.com/embed/PUzgZrS_piQ", 1240, false),
                video(6, "Деплой: Railway + Vercel", "Өндіріске шығару, environment variables", "https://www.youtube.com/embed/2IK3DFHRFfw", 1560, false)
        ));

        addMaterials(web, List.of(
                material("Vue 3 ресми құжаттама", "https://vuejs.org/guide/introduction.html", "Vue 3 толық анықтамасы"),
                material("Django REST Framework", "https://www.django-rest-framework.org/", "DRF ресми анықтамасы"),
                material("TypeScript Handbook", "https://www.typescriptlang.org/docs/handbook/intro.html", "TypeScript оқу нұсқаулығы")
        ));

        courseRepository.save(web);

        log.info("DataSeeder: created 3 courses, 2 users (teacher / student). Done.");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User createUser(String username, String email, String rawPassword,
                            String firstName, String lastName) {
        if (userRepository.existsByUsername(username)) {
            return userRepository.findByUsername(username).orElseThrow();
        }
        User u = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .build();
        return userRepository.save(u);
    }

    private Profile createProfile(User user, String role, String bio) {
        Profile p = Profile.builder()
                .user(user)
                .role(role)
                .bio(bio)
                .build();
        return profileRepository.save(p);
    }

    private void addVideos(Course course, List<Video> videos) {
        videos.forEach(v -> v.setCourse(course));
        course.getVideos().addAll(videos);
    }

    private void addMaterials(Course course, List<CourseMaterial> materials) {
        materials.forEach(m -> m.setCourse(course));
        course.getMaterials().addAll(materials);
    }

    private Video video(int serial, String title, String description,
                        String url, int durationSec, boolean isPreview) {
        return Video.builder()
                .title(title)
                .description(description)
                .videoUrl(url)
                .serialNumber(serial)
                .durationSec(durationSec)
                .isPreview(isPreview)
                .build();
    }

    private CourseMaterial material(String title, String fileUrl, String description) {
        return CourseMaterial.builder()
                .title(title)
                .fileUrl(fileUrl)
                .description(description)
                .build();
    }
}
