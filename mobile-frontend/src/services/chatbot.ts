/**
 * Platform-focused chatbot for EasyCode.
 * Answers questions about courses, pricing, enrollment, certificates, and account.
 */

type Lang = 'kz' | 'en' | 'py'

interface Rule {
  keywords: string[]
  replies: Record<Lang, string>
}

const rules: Rule[] = [
  // ── Greetings ──────────────────────────────────────────────────────────────
  {
    keywords: ['сәлем', 'hello', 'hi', 'hey', 'привет', 'здравствуй', 'сәл'],
    replies: {
      kz: 'Сәлем! EasyCode платформасына қош келдіңіз 👋\nМен курстар, баға, тіркелу немесе сертификат туралы сұрақтарға жауап бере аламын. Не білгіңіз келеді?',
      en: 'Hello! Welcome to EasyCode 👋\nI can help you with courses, pricing, enrollment, and certificates. What would you like to know?',
      py: 'Привет! Добро пожаловать на EasyCode 👋\nЯ могу помочь с вопросами о курсах, ценах, записи и сертификатах. Что вас интересует?',
    },
  },

  // ── What courses are available ─────────────────────────────────────────────
  {
    keywords: ['қандай курс', 'курстар бар', 'what courses', 'какие курсы', 'тізім', 'список курс', 'course list', 'барлық курс', 'все курсы', 'all courses'],
    replies: {
      kz: 'Қазір 3 курс бар:\n\n📗 Python негіздері — ТЕГІН\n📘 Java кәсіби деңгей — 3 920 ₸ (4 900 ₸-дан 20% жеңілдік)\n📙 Web Full-Stack: Vue + Django — 7 110 ₸ (7 900 ₸-дан 10% жеңілдік)\n\nТолық ақпарат үшін «Курстар» бетіне өтіңіз.',
      en: 'We currently have 3 courses:\n\n📗 Python Basics — FREE\n📘 Java Advanced — ₸3,920 (20% off from ₸4,900)\n📙 Web Full-Stack: Vue + Django — ₸7,110 (10% off from ₸7,900)\n\nVisit the Courses page for full details.',
      py: 'Сейчас доступны 3 курса:\n\n📗 Основы Python — БЕСПЛАТНО\n📘 Java Продвинутый — 3 920 ₸ (скидка 20% от 4 900 ₸)\n📙 Web Full-Stack: Vue + Django — 7 110 ₸ (скидка 10% от 7 900 ₸)\n\nПодробности на странице Курсы.',
    },
  },

  // ── Python course ──────────────────────────────────────────────────────────
  {
    keywords: ['python', 'питон', 'пайтон', 'python курс', 'python негіз'],
    replies: {
      kz: '📗 Python негіздері курсы:\n• Баға: ТЕГІН\n• 5 бейне сабақ\n• Бастаушыларға арналған\n• Синтаксис, функциялар, тізімдер, файлдар\n• 2 оқу материалы кіреді\n\nТіркелу үшін курс бетіне кіріп «Тегін тіркел» түймесін басыңыз.',
      en: '📗 Python Basics course:\n• Price: FREE\n• 5 video lessons\n• Designed for beginners\n• Covers syntax, functions, lists, and files\n• 2 study materials included\n\nGo to the course page and click "Enroll Free".',
      py: '📗 Курс «Основы Python»:\n• Цена: БЕСПЛАТНО\n• 5 видеоуроков\n• Для начинающих\n• Синтаксис, функции, списки, файлы\n• 2 учебных материала\n\nПерейди на страницу курса и нажми «Записаться бесплатно».',
    },
  },

  // ── Java course ────────────────────────────────────────────────────────────
  {
    keywords: ['java', 'джава', 'java курс', 'java кәсіби', 'java продвинутый'],
    replies: {
      kz: '📘 Java кәсіби деңгей курсы:\n• Баға: 3 920 ₸ (20% жеңілдік)\n• 6 бейне сабақ\n• Spring Boot, REST API, JWT, тестілеу\n• 3 оқу материалы кіреді\n• Алдын ала Python немесе кез келген тілді білу керек\n\nТөлем жасау үшін курс бетіне өтіңіз.',
      en: '📘 Java Advanced course:\n• Price: ₸3,920 (20% off)\n• 6 video lessons\n• Spring Boot, REST API, JWT, testing\n• 3 study materials included\n• Basic programming knowledge required\n\nVisit the course page to pay and enroll.',
      py: '📘 Курс «Java Продвинутый»:\n• Цена: 3 920 ₸ (скидка 20%)\n• 6 видеоуроков\n• Spring Boot, REST API, JWT, тестирование\n• 3 учебных материала\n• Нужны базовые знания программирования\n\nПерейди на страницу курса для оплаты.',
    },
  },

  // ── Web / Vue / Django course ──────────────────────────────────────────────
  {
    keywords: ['web', 'vue', 'django', 'full-stack', 'fullstack', 'веб', 'фулстек', 'full stack'],
    replies: {
      kz: '📙 Web Full-Stack: Vue + Django курсы:\n• Баға: 7 110 ₸ (10% жеңілдік)\n• 6 бейне сабақ\n• Vue 3, TypeScript, Django REST, JWT, Stripe\n• 3 оқу материалы кіреді\n• HTML/CSS/JavaScript және Python негіздері керек\n\nТөлем жасау үшін курс бетіне өтіңіз.',
      en: '📙 Web Full-Stack: Vue + Django course:\n• Price: ₸7,110 (10% off)\n• 6 video lessons\n• Vue 3, TypeScript, Django REST, JWT, Stripe\n• 3 study materials included\n• HTML/CSS/JS and Python basics required\n\nVisit the course page to pay and enroll.',
      py: '📙 Курс «Web Full-Stack: Vue + Django»:\n• Цена: 7 110 ₸ (скидка 10%)\n• 6 видеоуроков\n• Vue 3, TypeScript, Django REST, JWT, Stripe\n• 3 учебных материала\n• Нужны основы HTML/CSS/JS и Python\n\nПерейди на страницу курса для оплаты.',
    },
  },

  // ── Price / Cost ───────────────────────────────────────────────────────────
  {
    keywords: ['баға', 'price', 'цена', 'стоимость', 'cost', 'бағасы', 'қанша', 'сколько', 'how much', 'тегін', 'бесплатн', 'free', 'жеңілдік', 'скидка', 'discount'],
    replies: {
      kz: '💰 Баға тізімі:\n\n• Python негіздері — ТЕГІН\n• Java кәсіби деңгей — 3 920 ₸ (4 900 ₸-дан 20% жеңілдік)\n• Web Full-Stack — 7 110 ₸ (7 900 ₸-дан 10% жеңілдік)\n\nТөлем mock режимінде жұмыс істейді — нақты ақша алынбайды.',
      en: '💰 Pricing:\n\n• Python Basics — FREE\n• Java Advanced — ₸3,920 (20% off ₸4,900)\n• Web Full-Stack — ₸7,110 (10% off ₸7,900)\n\nPayment runs in mock mode — no real money is charged.',
      py: '💰 Цены:\n\n• Основы Python — БЕСПЛАТНО\n• Java Продвинутый — 3 920 ₸ (скидка 20% от 4 900 ₸)\n• Web Full-Stack — 7 110 ₸ (скидка 10% от 7 900 ₸)\n\nОплата работает в тестовом режиме — реальные деньги не списываются.',
    },
  },

  // ── How to enroll / pay ────────────────────────────────────────────────────
  {
    keywords: ['тіркелу', 'запись', 'enroll', 'register', 'тіркел', 'записаться', 'sign up', 'қалай алуға', 'как записаться', 'how to enroll', 'сатып алу', 'купить', 'buy', 'төлем', 'оплата', 'payment'],
    replies: {
      kz: '📝 Курсқа жазылу қадамдары:\n\n1. Аккаунтқа кіріңіз (немесе тіркеліңіз)\n2. «Курстар» бетіне өтіңіз\n3. Қажетті курсты таңдаңыз\n4. «Тіркел» немесе «Төлем» түймесін басыңыз\n5. Тегін курстарға бірден кіруге болады\n6. Ақылы курстар үшін mock-төлем жасаңыз\n\nСұрақ болса қайта жазыңыз!',
      en: '📝 How to enroll:\n\n1. Log in (or create an account)\n2. Go to the Courses page\n3. Select your course\n4. Click "Enroll" or "Pay"\n5. Free courses — instant access\n6. Paid courses — complete mock payment\n\nFeel free to ask if you have more questions!',
      py: '📝 Как записаться на курс:\n\n1. Войди в аккаунт (или зарегистрируйся)\n2. Перейди на страницу Курсы\n3. Выбери нужный курс\n4. Нажми «Записаться» или «Оплатить»\n5. Бесплатные курсы — доступ сразу\n6. Платные — пройди тестовую оплату\n\nЕсли есть вопросы — пиши!',
    },
  },

  // ── Certificate ────────────────────────────────────────────────────────────
  {
    keywords: ['сертификат', 'certificate', 'куәлік', 'диплом', 'diploma', 'аяқтау', 'завершить', 'finish', 'complete'],
    replies: {
      kz: '📜 Сертификат туралы:\n\nКурсты аяқтағаннан кейін сертификат беріледі.\n• PDF форматта жүктеледі\n• Атыңыз бен курс атауы жазылады\n• «Менің курстарым» бетінен жүктей аласыз\n\nСертификат алу үшін барлық сабақты аяқтаңыз.',
      en: '📜 About certificates:\n\nYou receive a certificate after completing the course.\n• Downloaded as PDF\n• Includes your name and course title\n• Available from the "My Courses" page\n\nComplete all lessons to receive your certificate.',
      py: '📜 О сертификатах:\n\nПосле завершения курса выдаётся сертификат.\n• Скачивается в формате PDF\n• Содержит ваше имя и название курса\n• Доступен на странице «Мои курсы»\n\nЗавершите все уроки, чтобы получить сертификат.',
    },
  },

  // ── Account / Login / Register ────────────────────────────────────────────
  {
    keywords: ['аккаунт', 'account', 'кіру', 'войти', 'login', 'тіркелу', 'регистрация', 'signup', 'парол', 'пароль', 'password', 'ұмыттым', 'забыл'],
    replies: {
      kz: '🔑 Аккаунт туралы:\n\n• Жаңа аккаунт ашу: «Кіру» бетіндегі «Тіркелу» сілтемесін басыңыз\n• Кіру: логин мен парольді енгізіңіз\n• Профильді өңдеу: «Профиль → Профильді өңдеу»\n• Парольді ұмыттыңыз ба? Қазір автоматты қалпына келтіру жоқ — қолдау қызметіне хабарласыңыз.',
      en: '🔑 Account info:\n\n• New account: click "Sign Up" on the Login page\n• Login: enter your username and password\n• Edit profile: go to "Profile → Edit Profile"\n• Forgot password? Automatic reset is not available yet — contact support.',
      py: '🔑 Информация об аккаунте:\n\n• Новый аккаунт: нажми «Регистрация» на странице входа\n• Войти: введи логин и пароль\n• Редактировать профиль: «Профиль → Редактировать профиль»\n• Забыл пароль? Автоматический сброс пока недоступен — обратись в поддержку.',
    },
  },

  // ── My courses / Progress ─────────────────────────────────────────────────
  {
    keywords: ['менің курстарым', 'my courses', 'мои курсы', 'сатып алдым', 'купил', 'purchased', 'прогресс', 'progress', 'барлық сабақ', 'все уроки'],
    replies: {
      kz: '📚 «Менің курстарым» бөлімі:\n\nСатып алған немесе тегін тіркелген курстарыңыз осында сақталады.\n• Астыңғы панельдегі «Менің курстарым» белгішесін басыңыз\n• Немесе «Профиль → Менің курстарым» арқылы өтіңіз',
      en: '📚 "My Courses" section:\n\nYour purchased and enrolled courses are saved here.\n• Tap "My Courses" in the bottom navigation\n• Or go to "Profile → My Courses"',
      py: '📚 Раздел «Мои курсы»:\n\nЗдесь хранятся все купленные и бесплатные курсы.\n• Нажми «Мои курсы» в нижнем меню\n• Или перейди «Профиль → Мои курсы»',
    },
  },

  // ── Teacher ────────────────────────────────────────────────────────────────
  {
    keywords: ['мұғалім', 'teacher', 'преподаватель', 'учитель', 'кто ведет', 'кім оқытады', 'who teaches', 'автор', 'author'],
    replies: {
      kz: '👨‍🏫 Курстар оқытушысы:\n\nАлибек Жаксыбеков — 10 жыл Java және Python тәжірибесі бар кәсіби разработчик.\n\nМұғалім панелі арқылы курс статистикасын да қарауға болады.',
      en: '👨‍🏫 Course instructor:\n\nAlibek Zhaksybekov — a professional developer with 10 years of Java and Python experience.\n\nThe Teacher Panel allows viewing course statistics.',
      py: '👨‍🏫 Преподаватель курсов:\n\nАлибек Жаксыбеков — профессиональный разработчик с 10-летним опытом в Java и Python.\n\nЧерез панель преподавателя можно смотреть статистику курсов.',
    },
  },

  // ── Support / Contact ─────────────────────────────────────────────────────
  {
    keywords: ['қолдау', 'support', 'поддержка', 'байланыс', 'contact', 'связаться', 'проблема', 'problem', 'мәселе', 'ошибка', 'error', 'жұмыс істемейді', 'не работает'],
    replies: {
      kz: '🛠 Қолдау қызметі:\n\nЕгер техникалық мәселе болса немесе сұрағыңыз қалса:\n• «Туралы» бетіндегі байланыс ақпаратын қараңыз\n• Немесе осы чат арқылы сипаттаңыз — мүмкіндігінше жауап береміз',
      en: '🛠 Support:\n\nIf you have a technical issue or question:\n• Check contact info on the "About" page\n• Or describe your issue here — we will do our best to help',
      py: '🛠 Поддержка:\n\nЕсли возникла техническая проблема или вопрос:\n• Посмотри контактную информацию на странице «О нас»\n• Или опиши проблему здесь — постараемся помочь',
    },
  },

  // ── Thanks ────────────────────────────────────────────────────────────────
  {
    keywords: ['рахмет', 'спасибо', 'thank', 'thanks', 'алғыс', 'сау бол'],
    replies: {
      kz: 'Өтінеміз! 😊 Тағы сұрақтарыңыз болса қойыңыз.',
      en: 'You are welcome! 😊 Feel free to ask anything else.',
      py: 'Пожалуйста! 😊 Если есть ещё вопросы — спрашивай.',
    },
  },

  // ── Help / What can you do ────────────────────────────────────────────────
  {
    keywords: ['не білесің', 'не сұрауға', 'что умеешь', 'help', 'көмек', 'помощь', 'what can you'],
    replies: {
      kz: 'Мен мына сұрақтарға жауап бере аламын:\n\n• Қандай курстар бар?\n• Курс бағасы қанша?\n• Қалай тіркелуге болады?\n• Сертификат қалай алынады?\n• Аккаунт / кіру мәселелері\n• Мұғалім туралы ақпарат\n\nСұрақ қойыңыз! 🎓',
      en: 'I can answer questions about:\n\n• Available courses\n• Course pricing\n• How to enroll\n• How to get a certificate\n• Account and login issues\n• Instructor information\n\nJust ask! 🎓',
      py: 'Я могу ответить на вопросы о:\n\n• Доступных курсах\n• Ценах на курсы\n• Как записаться\n• Как получить сертификат\n• Аккаунт и вход\n• Информация о преподавателе\n\nСпрашивай! 🎓',
    },
  },
]

const fallback: Record<Lang, string> = {
  kz: 'Кешіріңіз, бұл сұрақты түсінбедім 🤔\nМен курстар, баға, тіркелу, сертификат және аккаунт туралы сұрақтарға жауап бере аламын. «Не білесің?» деп жазсаңыз — тізімді көрсетемін.',
  en: 'Sorry, I did not understand that 🤔\nI can help with courses, pricing, enrollment, certificates, and account questions. Type "help" to see what I can do.',
  py: 'Извини, не понял вопрос 🤔\nЯ помогаю с вопросами о курсах, ценах, записи, сертификатах и аккаунте. Напиши «помощь», чтобы увидеть список.',
}

export function getBotReply(input: string, lang: Lang): string {
  const lower = input.toLowerCase()
  for (const rule of rules) {
    if (rule.keywords.some((kw) => lower.includes(kw))) {
      return rule.replies[lang]
    }
  }
  return fallback[lang]
}
