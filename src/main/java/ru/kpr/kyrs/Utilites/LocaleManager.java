package ru.kpr.kyrs.Utilites;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Менеджер локализации приложения.
 * Управляет текущей локалью и загрузкой файлов ресурсов.
 * Поддерживаемые языки: русский (ru), английский (en), немецкий (de).
 */
public final class LocaleManager {

    private static final Logger logger = LoggerFactory.getLogger(LocaleManager.class);

    /** Базовое имя файла ресурсов локализации. */
    private static final String BUNDLE_NAME = "ru/kpr/kyrs/messages";

    /** Текущая локаль приложения. */
    private static Locale currentLocale = new Locale("ru");

    /** Текущий пакет ресурсов. */
    private static ResourceBundle currentBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);

    /** Приватный конструктор — утилитный класс. */
    private LocaleManager() {
    }

    /**
     * Устанавливает новую локаль и перезагружает файл ресурсов.
     *
     * @param locale новая локаль
     */
    public static void setLocale(Locale locale) {
        currentLocale = locale;
        currentBundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLocale);
        Locale.setDefault(currentLocale);
        logger.info("Локаль изменена на: {}", currentLocale.getLanguage());
    }

    /**
     * Возвращает текущую локаль приложения.
     *
     * @return текущая локаль
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Возвращает текущий пакет ресурсов (ResourceBundle).
     *
     * @return текущий пакет ресурсов
     */
    public static ResourceBundle getBundle() {
        return currentBundle;
    }

    /**
     * Возвращает локализованную строку по ключу.
     * Если ключ не найден, возвращает сам ключ в квадратных скобках.
     *
     * @param key ключ строки в файле ресурсов
     * @return локализованная строка
     */
    public static String get(String key) {
        try {
            return currentBundle.getString(key);
        } catch (Exception e) {
            logger.warn("Ключ локализации не найден: {}", key);
            return "[" + key + "]";
        }
    }
}
