package com.easycode.backend.config;

import com.easycode.backend.entity.Course;
import com.easycode.backend.entity.CourseMaterial;
import com.easycode.backend.entity.Profile;
import com.easycode.backend.entity.QuizQuestion;
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

    private final UserRepository         userRepository;
    private final ProfileRepository      profileRepository;
    private final CourseRepository       courseRepository;
    private final VideoRepository        videoRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final PasswordEncoder        passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        boolean coursesExist = courseRepository.count() > 0;

        if (coursesExist) {
            log.info("DataSeeder: courses exist — checking for missing quiz questions...");
            seedMissingQuizzes();
            log.info("DataSeeder: quiz check done.");
            return;
        }

        log.info("DataSeeder: seeding demo data...");

        User teacher = createUser("teacher", "teacher@easycode.kz", "Teacher123!", "Алибек", "Жаксыбеков");
        createProfile(teacher, "TEACHER", "10 жыл Java және Python тәжірибесі");

        User student = createUser("student", "student@easycode.kz", "Student123!", "Айгерим", "Сейтова");
        createProfile(student, "STUDENT", null);

        // ── Course 1: Python (Web aligned) ───────────────────────────────────
        Course python = Course.builder()
                .name("Python бастаушыларға")
                .slug("python-bastauyshylar")
                .description("Программалаудың алғашқы қадамдары. Python тілін нөлден үйреніңіз: синтаксис, функциялар, файлдармен жұмыс. Бағдарламашы болу үшін ең жақсы бастама.")
                .price(10000)
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
                video(1, "Python-ға кіріспе",
                        "Жаттығу:\n" +
                                "1) Python деген не? 2–3 сөйлеммен түсіндіріңіз.\n" +
                                "2) Python қай салаларда қолданылады? Кемінде 3 мысал жазыңыз (мысалы: веб, деректер талдауы, автоматтандыру).\n" +
                                "3) Неге Python-ды үйрену керек? 3 себеп келтіріңіз.\n" +
                                "4) Жоғарыдағы кодты көшіріп жазыңыз да, өз мәндеріңізге өзгертіңіз (аты, жас, is_student).\n" +
                                "Нәтиже: қысқа мәтін жауап + жаңартылған код.",
                        "https://www.youtube.com/embed/_uQrJ0TkZlc", 620, true),
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

        // ── Course 2: C++ (Web aligned) ──────────────────────────────────────
        Course cpp = Course.builder()
                .name("C++ бастаушыларға")
                .slug("cpp-bastauyshylar")
                .description("C++ тілінің негіздерін меңгеріңіз: айнымалылар, шарттар, циклдар, функциялар және қарапайым алгоритмдер. Салаға сенімді бастау үшін.")
                .price(5000)
                .discount(0)
                .isPublished(true)
                .thumbnail("https://upload.wikimedia.org/wikipedia/commons/1/18/ISO_C%2B%2B_Logo.svg")
                .teacher(teacher)
                .build();

        cpp.setTags(List.of("C++", "Бастаушылар", "Алгоритмдер"));
        cpp.setPrerequisites(List.of(
                "Компьютерді негізгі деңгейде білу",
                "Математика бойынша мектеп курсы",
                "Оқуға ынта"
        ));
        cpp.setLearnings(List.of(
                "C++ синтаксисі және негізгі құрылымдар",
                "Шарттар мен циклдарды қолдану",
                "Функциялар және массивтер",
                "Қарапайым алгоритмдік есептерді шығару"
        ));

        addVideos(cpp, List.of(
                video(1, "C++-қа кіріспе", "C++ деген не және қайда қолданылады?", "https://www.youtube.com/embed/vLnPwxZdW4Y", 860, true),
                video(2, "Айнымалылар және типтер", "int, double, char, bool — негізгі типтер", "https://www.youtube.com/embed/8jLOx1hD3_o", 920, true),
                video(3, "Шарттар мен циклдар", "if/else, for, while қолдану", "https://www.youtube.com/embed/0T4aNn1N5vE", 980, false),
                video(4, "Функциялар", "Параметрлер, return, прототиптер", "https://www.youtube.com/embed/7bnk3K8N5qA", 1050, false),
                video(5, "Массивтер және жолдар", "array, string негіздері", "https://www.youtube.com/embed/McojvctVsUs", 1040, false)
        ));

        addMaterials(cpp, List.of(
                material("cplusplus.com анықтамалығы", "https://cplusplus.com/doc/tutorial/", "C++ оқу құралы"),
                material("Алгоритмдер жинағы", "https://cp-algorithms.com/", "Есептер мен түсіндірмелер")
        ));

        courseRepository.save(cpp);

        // ── Course 3: JavaScript (Web aligned) ───────────────────────────────
        Course js = Course.builder()
                .name("JavaScript бастаушыларға")
                .slug("javascript-bastauyshylar")
                .description("Интерактивті веб-дамыту үшін JavaScript тілін үйреніңіз: айнымалылар, функциялар, DOM, оқиғалар және шағын жобалар.")
                .price(15000)
                .discount(0)
                .isPublished(true)
                .thumbnail("https://upload.wikimedia.org/wikipedia/commons/6/6a/JavaScript-logo.png")
                .teacher(teacher)
                .build();

        js.setTags(List.of("JavaScript", "Frontend", "Бастаушылар"));
        js.setPrerequisites(List.of(
                "Компьютерді негізгі деңгейде білу",
                "Интернет және браузермен жұмыс",
                "Оқуға ынта"
        ));
        js.setLearnings(List.of(
                "JavaScript синтаксисі және негізгі ұғымдар",
                "Функциялар мен массивтер",
                "DOM және оқиғалар (events)",
                "Қарапайым веб-жобалар жасау"
        ));

        addVideos(js, List.of(
                video(1, "JavaScript-ке кіріспе", "JS деген не және не үшін керек?", "https://www.youtube.com/embed/W6NZfCO5SIk", 900, true),
                video(2, "Айнымалылар және типтер", "let/const, string/number/boolean", "https://www.youtube.com/embed/hdI2bqOjy3c", 980, true),
                video(3, "Функциялар", "function, arrow functions, параметрлер", "https://www.youtube.com/embed/2Ji-clqUYnA", 1020, false),
                video(4, "Массивтер және объектілер", "Array, Object, әдістер", "https://www.youtube.com/embed/R8rmfD9Y5-c", 1110, false),
                video(5, "DOM және оқиғалар", "querySelector, addEventListener", "https://www.youtube.com/embed/0ik6X4DJKCc", 1260, false)
        ));

        addMaterials(js, List.of(
                material("MDN JavaScript", "https://developer.mozilla.org/en-US/docs/Web/JavaScript", "JavaScript ресми анықтамалығы"),
                material("JavaScript Info", "https://javascript.info/", "Практикалық оқу құралы")
        ));

        courseRepository.save(js);

        // Seed quiz questions for all courses at once (videos have IDs after save)
        seedMissingQuizzes();

        log.info("DataSeeder: created 3 courses, 2 users (teacher / student). Done.");
    }

    /** Seeds quiz questions for every video that doesn't have any yet. Idempotent. */
    private void seedMissingQuizzes() {
        List<Course> courses = courseRepository.findAll();
        log.info("DataSeeder: found {} courses to check", courses.size());

        for (Course course : courses) {
            String slug = course.getSlug();
            // Load videos directly — no lazy loading issues
            List<Video> videos = videoRepository.findByCourseIdOrderBySerialNumberAsc(course.getId());
            log.info("DataSeeder: course '{}' has {} videos", slug, videos.size());

            for (Video video : videos) {
                boolean hasQuestions = !quizQuestionRepository.findByVideoIdOrderByIdAsc(video.getId()).isEmpty();
                if (hasQuestions) {
                    log.debug("DataSeeder: video {} already has questions, skipping", video.getId());
                    continue;
                }
                List<QuizQuestion> questions = buildQuestions(slug, video.getSerialNumber(), video);
                if (questions.isEmpty()) {
                    log.warn("DataSeeder: no questions defined for slug='{}' lesson={}", slug, video.getSerialNumber());
                    continue;
                }
                quizQuestionRepository.saveAll(questions);
                log.info("DataSeeder: seeded {} questions for video id={} (lesson {})", questions.size(), video.getId(), video.getSerialNumber());
            }
        }
    }

    private List<QuizQuestion> buildQuestions(String slug, int lesson, Video video) {
        return switch (slug) {

            // ── Python ──────────────────────────────────────────────────────
            case "python-bastauyshylar", "python-basics" -> switch (lesson) {
                case 1 -> List.of(
                    q(video, "Python деген не?",
                        List.of("Бағдарламалау тілі", "Операциялық жүйе", "Графикалық редактор", "Компьютер ойыны"), 0),
                    q(video, "Python-ның негізгі артықшылығы қандай?",
                        List.of("Оқуға жеңіл және түсінікті синтаксис", "Тек бір ғана құрылғыда жұмыс істейді", "Интернетсіз мүлде істемейді", "Тек ойын жасауға арналған"), 0),
                    q(video, "Python қай салаларда қолданылады?",
                        List.of("Веб, деректер талдауы, жасанды интеллект", "Тек принтер баптау", "Тек мәтін теру", "Тек калькулятор жасау"), 0)
                );
                case 2 -> List.of(
                    q(video, "Төмендегілердің қайсысы бүтін сан (int) типі?",
                        List.of("42", "\"42\"", "42.0", "True"), 0),
                    q(video, "Python-да жол (string) типі қалай жазылады?",
                        List.of("Тырнақшаға алынған мәтін", "Бүтін сан", "Тек үтірлі сан", "Логикалық мән"), 0),
                    q(video, "Python-да логикалық (bool) мәндер қандай?",
                        List.of("True және False", "Yes және No", "1 және 0 ғана", "On және Off"), 0)
                );
                case 3 -> List.of(
                    q(video, "Python-да 'егер' шарты қалай жазылады?",
                        List.of("if шарт:", "when шарт:", "case шарт:", "check шарт:"), 0),
                    q(video, "for циклі не үшін қолданылады?",
                        List.of("Белгілі рет санына қайталау үшін", "Тек шарт тексеру үшін", "Файл ашу үшін", "Функция анықтау үшін"), 0),
                    q(video, "while циклі қашан тоқтайды?",
                        List.of("Шарт жалған болғанда", "Бағдарлама жабылғанда", "Ешқашан тоқтамайды", "5 рет орындалған соң"), 0)
                );
                case 4 -> List.of(
                    q(video, "Python-да функция қалай анықталады?",
                        List.of("def атауы():", "function атауы():", "func атауы():", "define атауы():"), 0),
                    q(video, "return операторы не үшін керек?",
                        List.of("Функциядан мән қайтару үшін", "Бағдарламаны тоқтату үшін", "Шарт тексеру үшін", "Айнымалы жариялау үшін"), 0),
                    q(video, "Функция параметрі деген не?",
                        List.of("Функцияға берілетін енгізу мәні", "Функция атауы", "Функция нәтижесі", "Функция орналасқан файл"), 0)
                );
                case 5 -> List.of(
                    q(video, "Python тізімі (list) қалай жазылады?",
                        List.of("[1, 2, 3]", "{1, 2, 3}", "(1, 2, 3)", "<1, 2, 3>"), 0),
                    q(video, "Сөздік (dict) элементіне қалай қол жеткіземіз?",
                        List.of("d[\"кілт\"]", "d(\"кілт\")", "d.кілт", "d->кілт"), 0),
                    q(video, "list.append() әдісі не істейді?",
                        List.of("Тізімнің соңына элемент қосады", "Бірінші элементті жояды", "Тізімді сұрыптайды", "Тізімді тазартады"), 0)
                );
                default -> List.of();
            };

            // ── C++ / Java ──────────────────────────────────────────────────
            case "cpp-bastauyshylar", "java-advanced" -> switch (lesson) {
                case 1 -> List.of(
                    q(video, "C++ қандай тілге жатады?",
                        List.of("Компилятор тілі (compiled)", "Интерпретатор тілі", "Сценарий тілі (script)", "Разметка тілі"), 0),
                    q(video, "C++ программасының бастапқы нүктесі қандай функция?",
                        List.of("main()", "start()", "begin()", "run()"), 0),
                    q(video, "C++-да экранға мәтін шығару үшін не қолданылады?",
                        List.of("cout <<", "print()", "System.out", "echo"), 0)
                );
                case 2 -> List.of(
                    q(video, "C++-да бүтін сан типі қалай жарияланады?",
                        List.of("int x = 5;", "integer x = 5;", "var x = 5;", "number x = 5;"), 0),
                    q(video, "C++-да үтірлі сан үшін қандай тип қолданылады?",
                        List.of("double немесе float", "int", "char", "bool"), 0),
                    q(video, "bool типінің мүмкін мәндері қандай?",
                        List.of("true және false", "Yes және No", "1 және 2", "on және off"), 0)
                );
                case 3 -> List.of(
                    q(video, "C++-да шарт операторы қалай жазылады?",
                        List.of("if (шарт) { }", "when (шарт) { }", "check (шарт) { }", "cond (шарт) { }"), 0),
                    q(video, "for циклінің дұрыс жазылысы қандай?",
                        List.of("for (int i=0; i<5; i++)", "for i in range(5):", "foreach (i; 5)", "loop i to 5"), 0),
                    q(video, "while циклі не уақытта жұмыс істейді?",
                        List.of("Шарт ақиқат (true) болғанда", "Шарт жалған (false) болғанда", "Тек бір рет", "Ешқашан"), 0)
                );
                case 4 -> List.of(
                    q(video, "C++-да функция қалай анықталады?",
                        List.of("қайтару_типі атауы(параметрлер) { }", "def атауы():", "function атауы() { }", "func атауы() { }"), 0),
                    q(video, "void қайтару типі нені білдіреді?",
                        List.of("Функция ештеңе қайтармайды", "Функция бүтін сан қайтарады", "Функция жол қайтарады", "Функция логикалық мән қайтарады"), 0),
                    q(video, "Функция прототипі не үшін керек?",
                        List.of("Функцияны пайдаланудан бұрын компиляторға хабарлау үшін", "Функцияны жылдамдату үшін", "Функцияны жою үшін", "Функцияны кітапханаға сақтау үшін"), 0)
                );
                case 5 -> List.of(
                    q(video, "C++-да массив қалай жарияланады?",
                        List.of("int arr[5];", "array arr = new int[5];", "list<int> arr(5);", "var arr[5];"), 0),
                    q(video, "Массивтің бірінші элементінің индексі қандай?",
                        List.of("0", "1", "-1", "Кез келген сан"), 0),
                    q(video, "C++-да жол (string) пайдалану үшін не қосу керек?",
                        List.of("#include <string>", "#include <text>", "#include <char>", "#import string"), 0)
                );
                case 6 -> List.of(
                    q(video, "Java-да Thread жасаудың негізгі жолдары?",
                        List.of("Thread extends немесе Runnable implements", "Тек Thread extends", "Тек new Thread()", "Thread.create() арқылы"), 0),
                    q(video, "synchronized кілт сөзі не үшін керек?",
                        List.of("Бір уақытта бір ғана thread кіруін шектеу үшін", "Thread жылдамдату үшін", "Thread жасау үшін", "Thread тоқтату үшін"), 0),
                    q(video, "Deadlock деген не?",
                        List.of("Екі thread бір-бірін күтіп тоқтап қалуы", "Thread тым жылдам жұмыс істеуі", "Жадтың толып кетуі", "Программаның сынып қалуы"), 0)
                );
                default -> List.of();
            };

            // ── JavaScript / Web ────────────────────────────────────────────
            case "javascript-bastauyshylar", "web-fullstack" -> switch (lesson) {
                case 1 -> List.of(
                    q(video, "JavaScript қай ортада жұмыс істейді?",
                        List.of("Браузерде және сервер (Node.js) жағында", "Тек операциялық жүйеде", "Тек принтерде", "Тек мобильде"), 0),
                    q(video, "JavaScript коды HTML-ге қалай қосылады?",
                        List.of("<script> тегі арқылы", "<code> тегі арқылы", "<js> тегі арқылы", "<program> тегі арқылы"), 0),
                    q(video, "JavaScript не үшін қолданылады?",
                        List.of("Веб-беттерді интерактивті ету", "Тек суреттерді өңдеу", "Тек мәліметтер базасы", "Тек серверлерді баптау"), 0)
                );
                case 2 -> List.of(
                    q(video, "JavaScript-та өзгермейтін айнымалы қалай жарияланады?",
                        List.of("const", "let", "var", "static"), 0),
                    q(video, "typeof \"Сәлем\" нәтижесі қандай?",
                        List.of("\"string\"", "\"text\"", "\"char\"", "\"word\""), 0),
                    q(video, "JavaScript-та логикалық ЖӘНЕ операторы қандай?",
                        List.of("&&", "AND", "and", "&"), 0)
                );
                case 3 -> List.of(
                    q(video, "JavaScript-та функция қалай анықталады?",
                        List.of("function атауы() { }", "def атауы():", "func атауы() { }", "method атауы() { }"), 0),
                    q(video, "Arrow function дұрыс жазылысы қандай?",
                        List.of("const f = () => { }", "const f = -> { }", "const f = => { }", "const f = function => { }"), 0),
                    q(video, "JavaScript-та функцияны шақыру үшін не жазылады?",
                        List.of("атауы()", "call атауы", "run атауы()", "exec атауы()"), 0)
                );
                case 4 -> List.of(
                    q(video, "JavaScript массивінің бірінші элементіне қалай қол жеткіземіз?",
                        List.of("arr[0]", "arr[1]", "arr.first()", "arr.get(0)"), 0),
                    q(video, "Массивке элемент қосу үшін қандай әдіс қолданылады?",
                        List.of("push()", "add()", "append()", "insert()"), 0),
                    q(video, "JavaScript объектісінде қасиетке қалай қол жеткіземіз?",
                        List.of("obj.қасиет немесе obj[\"қасиет\"]", "obj->қасиет", "obj::қасиет", "get(obj, қасиет)"), 0)
                );
                case 5 -> List.of(
                    q(video, "HTML элементін id бойынша табу үшін не қолданылады?",
                        List.of("document.getElementById()", "document.getElement()", "document.findById()", "document.query()"), 0),
                    q(video, "addEventListener не үшін қолданылады?",
                        List.of("Оқиғаны (event) тыңдау үшін", "Элемент жасау үшін", "CSS қосу үшін", "Беттің тақырыбын өзгерту үшін"), 0),
                    q(video, "HTML элементінің мазмұнын өзгерту үшін қандай қасиет қолданылады?",
                        List.of("innerHTML немесе textContent", "innerText тек", "content", "value тек"), 0)
                );
                case 6 -> List.of(
                    q(video, "Docker не үшін қолданылады?",
                        List.of("Қолданбаны контейнерге орап іске қосу үшін", "Код жазу үшін", "Мәліметтер базасы жасау үшін", "CSS стиль беру үшін"), 0),
                    q(video, "CI/CD деген не?",
                        List.of("Тексеру мен деплойды автоматтандыру процесі", "Код жазу стилі", "Мәліметтер базасы протоколы", "JavaScript фреймворкі"), 0),
                    q(video, "HTTPS HTTP-тен қалай ерекшеленеді?",
                        List.of("HTTPS шифрлауды (TLS/SSL) қолданады", "HTTPS жылдамырақ қана", "Айырмашылығы жоқ", "HTTP қауіпсізірек"), 0)
                );
                default -> List.of();
            };

            default -> List.of();
        };
    }

    private QuizQuestion q(Video video, String prompt, List<String> options, int correctIndex) {
        return QuizQuestion.builder()
                .video(video)
                .prompt(prompt)
                .options(options)
                .correctIndex(correctIndex)
                .build();
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
