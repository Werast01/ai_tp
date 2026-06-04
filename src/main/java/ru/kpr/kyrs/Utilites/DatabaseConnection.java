package ru.kpr.kyrs.Utilites;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Вспомогательный класс для управления подключением к базе данных.
 * Реализует аутентификацию через механизмы СУБД PostgreSQL.
 * После вызова initConnection() каждый вызов getConnection() создаёт
 * новое соединение с сохранёнными учётными данными.
 */
public final class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private static String connectionUrl;
    private static String storedUser;
    private static String storedPassword;
    private static boolean initialized = false;

    static {
        String dbUrlBase = "jdbc:postgresql://localhost:5432/";
        String dbName = "ais_tp";
        String dbSchema = "scheme1";

        try (InputStream is = DatabaseConnection.class
                .getResourceAsStream("/ru/kpr/kyrs/config.properties")) {
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                dbUrlBase = prop.getProperty("db.url", dbUrlBase);
                dbName = prop.getProperty("db.name", dbName);
                dbSchema = prop.getProperty("db.schema", dbSchema);
                logger.debug("Загружены настройки подключения: url={}, db={}, schema={}",
                        dbUrlBase, dbName, dbSchema);
            } else {
                logger.warn("Файл config.properties не найден, используются значения по умолчанию");
            }
        } catch (IOException ex) {
            logger.error("Ошибка загрузки config.properties", ex);
        }
        connectionUrl = dbUrlBase + dbName + "?currentSchema=" + dbSchema;
    }

    /** Приватный конструктор — утилитный класс. */
    private DatabaseConnection() {
    }

    /**
     * Выполняет первичную проверку подключения с заданными учётными данными
     * и сохраняет их для последующего использования в getConnection().
     *
     * @param user     имя пользователя базы данных
     * @param password пароль пользователя базы данных
     * @throws SQLException если соединение не удалось установить
     */
    public static void initConnection(String user, String password) throws SQLException {
        logger.info("Попытка подключения к {} пользователем {}", connectionUrl, user);
        // Проверяем подключение
        try (Connection testConn = DriverManager.getConnection(connectionUrl, user, password)) {
            if (testConn.isValid(3)) {
                storedUser = user;
                storedPassword = password;
                initialized = true;
                logger.info("Соединение с базой данных проверено для пользователя {}", user);
            }
        }
    }

    /**
     * Создаёт и возвращает новое соединение с базой данных.
     * Использует учётные данные, установленные при вызове initConnection().
     *
     * @return новое соединение с базой данных
     * @throws SQLException если соединение не установлено или произошла ошибка
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException("Соединение не инициализировано. Вызовите initConnection() перед использованием.");
        }
        return DriverManager.getConnection(connectionUrl, storedUser, storedPassword);
    }

    /**
     * Проверяет, была ли выполнена инициализация подключения.
     *
     * @return true, если initConnection() был вызван успешно
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Сбрасывает сохранённые учётные данные.
     * Вызывается при завершении работы приложения.
     */
    public static void closeConnection() {
        initialized = false;
        storedUser = null;
        storedPassword = null;
        logger.info("Данные подключения к базе данных сброшены");
    }
}
