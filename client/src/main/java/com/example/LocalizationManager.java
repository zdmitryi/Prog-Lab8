package com.example;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class LocalizationManager {
    private static final Map<String, String> RU = new HashMap<>();
    private static final Map<String, String> CS = new HashMap<>();
    private static final Map<String, String> DA = new HashMap<>();
    private static final Map<String, String> ES_CO = new HashMap<>();
    public static LocalizationManager instance;
    private Map<String, String> currentLocation;
    private Locale currentLocale;
    static {
        RU.put("auth.title", "Авторизация");
        RU.put("auth.login", "Логин");
        RU.put("auth.choose.lang", "Язык");
        RU.put("auth.password", "Пароль");
        RU.put("auth.login.in", "Войти");
        RU.put("auth.register.in", "Зарегистрироваться");
        RU.put("auth.error", "Неверный логин или пароль");
        RU.put("auth.success", "Успешный вход");
        RU.put("auth.registered", "Регистрация успешна");
        RU.put("auth.empty_fields", "Заполните все поля");
        RU.put("auth.connecting", "Подключение...");
        RU.put("main.title", "Управление коллекцией");
        RU.put("main.user", "Пользователь");
        RU.put("main.refresh", "Обновить");
        RU.put("main.add", "Добавить");
        RU.put("main.clear", "Очистить");
        RU.put("main.remove", "Удалить");
        RU.put("main.update", "Изменить");
        RU.put("main.show", "Показать все");
        RU.put("main.lang", "Язык");
        RU.put("main.exit", "Выход");
        RU.put("main.filter", "Фильтр");
        RU.put("table.id", "ID");
        RU.put("table.name", "Название");
        RU.put("table.x", "X");
        RU.put("table.y", "Y");
        RU.put("table.students", "Студенты");
        RU.put("table.expelled", "Отчислены");
        RU.put("table.form", "Форма обучения");
        RU.put("table.semester", "Семестр");
        RU.put("table.admin", "Админ");
        RU.put("table.owner", "Владелец");
        RU.put("error.network", "Ошибка соединения с сервером");
        RU.put("error.server", "Ошибка сервера");
        CS.put("auth.title", "Autorizace");
        CS.put("auth.login", "Přihlášení");
        CS.put("auth.password", "Heslo");
        CS.put("auth.login.in", "Přihlásit");
        CS.put("auth.register.in", "Registrovat");
        CS.put("auth.error", "Nesprávné přihlášení nebo heslo");
        CS.put("auth.success", "Úspěšné přihlášení");
        CS.put("auth.choose.lang", "Jazyk");
        CS.put("auth.registered", "Registrace úspěšná");
        CS.put("auth.empty_fields", "Vyplňte všechna pole");
        CS.put("auth.connecting", "Připojování...");
        CS.put("main.title", "Správa kolekce");
        CS.put("main.user", "Uživatel");
        CS.put("main.refresh", "Obnovit");
        CS.put("main.add", "Přidat");
        CS.put("main.clear", "Vyčistit");
        CS.put("main.remove", "Odstranit");
        CS.put("main.update", "Upravit");
        CS.put("main.show", "Zobrazit vše");
        CS.put("main.lang", "Jazyk");
        CS.put("main.exit", "Ukončit");
        CS.put("main.filter", "Filtr");
        CS.put("table.id", "ID");
        CS.put("table.name", "Název");
        CS.put("table.x", "X");
        CS.put("table.y", "Y");
        CS.put("table.students", "Studenti");
        CS.put("table.expelled", "Vyloučeni");
        CS.put("table.form", "Forma vzdělávání");
        CS.put("table.semester", "Semestr");
        CS.put("table.admin", "Správce");
        CS.put("table.owner", "Vlastník");
        CS.put("error.network", "Chyba připojení k serveru");
        CS.put("error.server", "Chyba serveru");
        DA.put("auth.title", "Autorisation");
        DA.put("auth.login", "Login");
        DA.put("auth.password", "Adgangskode");
        DA.put("auth.login.in", "Log ind");
        DA.put("auth.register.in", "Registrer");
        DA.put("auth.error", "Forkert login eller adgangskode");
        DA.put("auth.success", "Succesfuld login");
        DA.put("auth.registered", "Registrering gennemført");
        DA.put("auth.empty_fields", "Udfyld alle felter");
        DA.put("auth.connecting", "Opretter forbindelse...");
        DA.put("auth.choose.lang", "Sprog");
        DA.put("main.title", "Samlingsstyring");
        DA.put("main.user", "Bruger");
        DA.put("main.refresh", "Opdater");
        DA.put("main.add", "Tilføj");
        DA.put("main.clear", "Ryd");
        DA.put("main.remove", "Slet");
        DA.put("main.update", "Rediger");
        DA.put("main.show", "Vis alle");
        DA.put("main.lang", "Sprog");
        DA.put("main.exit", "Afslut");
        DA.put("main.filter", "Filtrer");
        DA.put("table.id", "ID");
        DA.put("table.name", "Navn");
        DA.put("table.x", "X");
        DA.put("table.y", "Y");
        DA.put("table.students", "Studerende");
        DA.put("table.expelled", "Bortvist");
        DA.put("table.form", "Uddannelsesform");
        DA.put("table.semester", "Semester");
        DA.put("table.admin", "Administrator");
        DA.put("table.owner", "Ejer");
        DA.put("error.network", "Forbindelsesfejl til server");
        DA.put("error.server", "Serverfejl");
        ES_CO.put("auth.title", "Autorización");
        ES_CO.put("auth.login", "Inicio de sesión");
        ES_CO.put("auth.password", "Contraseña");
        ES_CO.put("auth.login.in", "Ingresar");
        ES_CO.put("auth.register.in", "Registrarse");
        ES_CO.put("auth.error", "Inicio de sesión o contraseña incorrectos");
        ES_CO.put("auth.success", "Inicio de sesión exitoso");
        ES_CO.put("auth.registered", "Registro exitoso");
        ES_CO.put("auth.empty_fields", "Complete todos los campos");
        ES_CO.put("auth.connecting", "Conectando...");
        ES_CO.put("auth.choose.lang", "Idioma");
        ES_CO.put("main.title", "Gestión de colección");
        ES_CO.put("main.user", "Usuario");
        ES_CO.put("main.refresh", "Actualizar");
        ES_CO.put("main.add", "Agregar");
        ES_CO.put("main.clear", "Limpiar");
        ES_CO.put("main.remove", "Eliminar");
        ES_CO.put("main.update", "Modificar");
        ES_CO.put("main.show", "Mostrar todo");
        ES_CO.put("main.lang", "Idioma");
        ES_CO.put("main.exit", "Salir");
        ES_CO.put("main.filter", "Filtrar");
        ES_CO.put("table.id", "ID");
        ES_CO.put("table.name", "Nombre");
        ES_CO.put("table.x", "X");
        ES_CO.put("table.y", "Y");
        ES_CO.put("table.students", "Estudiantes");
        ES_CO.put("table.expelled", "Expulsados");
        ES_CO.put("table.form", "Forma de educación");
        ES_CO.put("table.semester", "Semestre");
        ES_CO.put("table.admin", "Administrador");
        ES_CO.put("table.owner", "Propietario");
        ES_CO.put("error.network", "Error de conexión con el servidor");
        ES_CO.put("error.server", "Error del servidor");
    }
    LocalizationManager() {
        currentLocale = new Locale("ru");
        currentLocation = RU;
    }
    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }
    public String get(String key) {
        return currentLocation.getOrDefault(key,  key);
    }
    public void setLocale(Locale locale) {
        this.currentLocale = locale;
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        if ("es".equals(lang) && "CO".equals(country)) {
            currentLocation = ES_CO;
        } else {
            switch (lang) {
                case "ru": currentLocation = RU; break;
                case "cs": currentLocation= CS; break;
                case "da": currentLocation = DA; break;
                default: currentLocation = RU;
            }
        }
    }
    public Locale getCurrentLocale() {
        return currentLocale;
    }
}