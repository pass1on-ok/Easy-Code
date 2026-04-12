package com.easycode.backend.controller;

import com.easycode.backend.entity.QuizQuestion;
import com.easycode.backend.entity.Video;
import com.easycode.backend.repository.CourseRepository;
import com.easycode.backend.repository.QuizQuestionRepository;
import com.easycode.backend.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dev-only admin endpoints. Disabled in production via @Profile("!prod").
 */
@RestController
@RequestMapping("/api/admin")
@Profile("!prod")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final CourseRepository       courseRepository;
    private final VideoRepository        videoRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * GET /api/admin/seed-quizzes
     * Seeds quiz questions for every video that has none.
     * Idempotent — safe to call multiple times.
     */
    @GetMapping("/seed-quizzes")
    @Transactional
    public ResponseEntity<Map<String, Object>> seedQuizzes() {
        List<Map<String, Object>> videos_status = new ArrayList<>();
        List<String> seeded = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        var courses = courseRepository.findAll();

        for (var course : courses) {
            String slug = course.getSlug();
            List<Video> videos = videoRepository.findByCourseIdOrderBySerialNumberAsc(course.getId());

            for (Video video : videos) {
                long existingCount = quizQuestionRepository.findByVideoIdOrderByIdAsc(video.getId()).size();
                Map<String, Object> entry = new java.util.LinkedHashMap<>();
                entry.put("video_id", video.getId());
                entry.put("course_slug", slug);
                entry.put("lesson", video.getSerialNumber());
                entry.put("existing_questions", existingCount);

                if (existingCount > 0) {
                    entry.put("action", "skipped");
                    skipped.add("video " + video.getId() + " (" + slug + " L" + video.getSerialNumber() + ") already has " + existingCount + " questions");
                    videos_status.add(entry);
                    continue;
                }
                List<QuizQuestion> questions = buildQuestions(slug, video.getSerialNumber(), video);
                if (questions.isEmpty()) {
                    entry.put("action", "no_definition");
                    warnings.add("no questions defined for slug='" + slug + "' lesson=" + video.getSerialNumber());
                } else {
                    quizQuestionRepository.saveAll(questions);
                    entry.put("action", "seeded");
                    entry.put("questions_added", questions.size());
                    seeded.add("video " + video.getId() + " (" + slug + " L" + video.getSerialNumber() + ") → " + questions.size() + " questions");
                }
                videos_status.add(entry);
            }
        }

        return ResponseEntity.ok(Map.of(
                "total_courses", courses.size(),
                "total_seeded", seeded.size(),
                "total_skipped", skipped.size(),
                "seeded", seeded,
                "skipped", skipped,
                "warnings", warnings,
                "videos", videos_status
        ));
    }

    // ── Quiz question definitions ────────────────────────────────────────────

    private List<QuizQuestion> buildQuestions(String slug, int lesson, Video video) {
        return switch (slug) {

            // ── Python (both slug variants) ──────────────────────────────────
            case "python-basics", "python-bastauyshylar" -> switch (lesson) {
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

            // ── Java (both slug variants) ────────────────────────────────────
            case "java-advanced", "cpp-bastauyshylar" -> switch (lesson) {
                case 1 -> List.of(
                    q(video, "Java-да OOP негізгі принциптері қандай?",
                        List.of("Инкапсуляция, Мұрагерлік, Полиморфизм, Абстракция", "Компиляция, Интерпретация, Линковка", "Класс, Объект, Метод ғана", "Цикл, Шарт, Функция"), 0),
                    q(video, "Java-да класс қалай анықталады?",
                        List.of("class ClassName { }", "def ClassName:", "struct ClassName { }", "object ClassName { }"), 0),
                    q(video, "Java-да объект қалай жасалады?",
                        List.of("new ClassName()", "create ClassName()", "ClassName.new()", "make ClassName()"), 0)
                );
                case 2 -> List.of(
                    q(video, "Java-да мұрагерлік қалай жазылады?",
                        List.of("class Child extends Parent { }", "class Child inherits Parent { }", "class Child : Parent { }", "class Child implements Parent { }"), 0),
                    q(video, "Полиморфизм деген не?",
                        List.of("Бір интерфейс арқылы әртүрлі типтерді басқару", "Тек бір класс болуы", "Методты жасыру", "Айнымалы типін өзгерту"), 0),
                    q(video, "@Override аннотациясы не үшін қолданылады?",
                        List.of("Ата-класс методын қайта анықтау үшін", "Жаңа метод жасау үшін", "Методты жасыру үшін", "Класс жасау үшін"), 0)
                );
                case 3 -> List.of(
                    q(video, "Java Collections-та List пен Set айырмашылығы?",
                        List.of("List ретті, Set қайталанбайды", "List жылдам, Set баяу", "Айырмашылығы жоқ", "Set ретті, List қайталанбайды"), 0),
                    q(video, "HashMap не үшін қолданылады?",
                        List.of("Кілт-мән жұптарын сақтау үшін", "Тек сандарды сақтау үшін", "Ретті тізім үшін", "Файлдарды сақтау үшін"), 0),
                    q(video, "ArrayList пен LinkedList айырмашылығы?",
                        List.of("ArrayList индекстеу жылдам, LinkedList қосу/жою жылдам", "LinkedList индекстеу жылдам", "Екеуі де бірдей", "ArrayList қосу жылдам"), 0)
                );
                case 4 -> List.of(
                    q(video, "Java-да exception handling үшін қандай блоктар қолданылады?",
                        List.of("try-catch-finally", "if-else-end", "begin-rescue-ensure", "do-except-done"), 0),
                    q(video, "Checked және Unchecked exception айырмашылығы?",
                        List.of("Checked компиляцияда тексеріледі, Unchecked runtime-да", "Екеуі де бірдей", "Unchecked компиляцияда тексеріледі", "Checked тек runtime-да"), 0),
                    q(video, "finally блогы қашан орындалады?",
                        List.of("Exception болсын-болмасын әрқашан", "Тек exception болғанда", "Тек сәтті аяқталғанда", "Ешқашан орындалмайды"), 0)
                );
                case 5 -> List.of(
                    q(video, "Java Generics не үшін қолданылады?",
                        List.of("Тип қауіпсіздігін қамтамасыз ету үшін", "Кодты баяулату үшін", "Тек String типі үшін", "Массив жасау үшін"), 0),
                    q(video, "List<T> мысалындағы T деген не?",
                        List.of("Тип параметрі (Type parameter)", "Айнымалы аты", "Метод аты", "Класс аты"), 0),
                    q(video, "Wildcard <?> не білдіреді?",
                        List.of("Кез келген тип", "Тек Integer тип", "Бос тип", "String тип"), 0)
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

            // ── Web / JavaScript (both slug variants) ───────────────────────
            case "web-fullstack", "javascript-bastauyshylar" -> switch (lesson) {
                case 1 -> List.of(
                    q(video, "HTML деген не?",
                        List.of("Веб-беттің құрылымын сипаттайтын тіл", "Бағдарламалау тілі", "Мәліметтер базасы тілі", "Стиль беру тілі"), 0),
                    q(video, "HTML-де тақырып тегтері қандай?",
                        List.of("<h1> - <h6>", "<title> - <subtitle>", "<header> - <footer>", "<p> - <div>"), 0),
                    q(video, "HTML-де сілтеме жасау үшін қандай тег қолданылады?",
                        List.of("<a href='url'>", "<link url=''>", "<url href=''>", "<ref src=''>"), 0)
                );
                case 2 -> List.of(
                    q(video, "CSS не үшін қолданылады?",
                        List.of("HTML элементтеріне стиль беру үшін", "Веб-бетті программалау үшін", "Мәліметтер базасымен жұмыс үшін", "Сервер баптау үшін"), 0),
                    q(video, "CSS-те класс селекторы қалай жазылады?",
                        List.of(".classname { }", "#classname { }", "classname { }", "@classname { }"), 0),
                    q(video, "CSS Flexbox не үшін қолданылады?",
                        List.of("Элементтерді икемді орналастыру үшін", "Түс беру үшін", "Шрифт өзгерту үшін", "Анимация жасау үшін"), 0)
                );
                case 3 -> List.of(
                    q(video, "JavaScript-те DOM деген не?",
                        List.of("HTML құрылымының программалық моделі", "Мәліметтер базасы", "CSS стилі", "Сервер протоколы"), 0),
                    q(video, "JavaScript-те элементті класс бойынша табу?",
                        List.of("document.getElementsByClassName()", "document.findClass()", "document.getClass()", "document.selectClass()"), 0),
                    q(video, "fetch() API не үшін қолданылады?",
                        List.of("Серверге HTTP сұраныс жіберу үшін", "DOM элементін табу үшін", "Стиль беру үшін", "Цикл жасау үшін"), 0)
                );
                case 4 -> List.of(
                    q(video, "REST API деген не?",
                        List.of("HTTP протоколы арқылы ресурстармен жұмыс жасау архитектурасы", "Мәліметтер базасы тілі", "JavaScript кітапханасы", "CSS фреймворк"), 0),
                    q(video, "HTTP GET сұранысы не үшін қолданылады?",
                        List.of("Деректерді алу үшін", "Жаңа деректер жасау үшін", "Деректерді жою үшін", "Деректерді жаңарту үшін"), 0),
                    q(video, "JSON деген не?",
                        List.of("Деректерді тасымалдауға арналған формат", "Бағдарламалау тілі", "Мәліметтер базасы", "CSS фреймворк"), 0)
                );
                case 5 -> List.of(
                    q(video, "SQL-де деректерді таңдау командасы?",
                        List.of("SELECT * FROM table", "GET * FROM table", "FETCH * FROM table", "READ * FROM table"), 0),
                    q(video, "SQL-де жаңа жол қосу командасы?",
                        List.of("INSERT INTO table VALUES(...)", "ADD INTO table VALUES(...)", "PUT INTO table VALUES(...)", "SET INTO table VALUES(...)"), 0),
                    q(video, "Реляциялық мәліметтер базасында PRIMARY KEY не?",
                        List.of("Кестедегі әрбір жолды бірегей анықтайтын өріс", "Ең маңызды баған аты", "Бірінші баған", "Индекстелген кез келген баған"), 0)
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
}
