package ru.kpr.kyrs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Controllers.*;
import ru.kpr.kyrs.Dao.Classes.*;
import ru.kpr.kyrs.Utilites.DatabaseConnection;
import ru.kpr.kyrs.Utilites.LocaleManager;
import ru.kpr.kyrs.Utilites.LoginDialog;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Главный класс приложения АИС Транспортного Предприятия.
 * Отвечает за инициализацию, авторизацию пользователя и запуск главного окна.
 */
public class MainApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    /** Ссылка на главное окно приложения для доступа из контроллеров. */
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        logger.info("Запуск приложения АИС Транспортного Предприятия");

        // Установка локали по умолчанию (русский язык)
        LocaleManager.setLocale(new Locale("en"));

        // Цикл аутентификации через механизмы СУБД
        LoginDialog loginDialog = new LoginDialog();
        while (true) {
            Optional<LoginDialog.LoginResult> result = loginDialog.showAndWait();

            if (result.isEmpty()) {
                logger.info("Пользователь отменил вход в систему. Завершение работы.");
                Platform.exit();
                return;
            }

            String username = result.get().getUsername();
            String password = result.get().getPassword();

            try {
                DatabaseConnection.initConnection(username, password);
                logger.info("Успешная аутентификация пользователя {}", username);
                break;
            } catch (SQLException ex) {
                logger.error("Ошибка аутентификации для пользователя {}: {}", username, ex.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(LocaleManager.get("auth.error.title"));
                alert.setHeaderText(LocaleManager.get("auth.error.header"));
                alert.setContentText(LocaleManager.get("auth.error.content") + "\n\n" + ex.getMessage());
                alert.showAndWait();
            }
        }

        // Загрузка главного окна
        loadMainWindow(stage);
    }

    /**
     * Загружает и отображает главное окно приложения.
     *
     * @param stage основное окно JavaFX
     */
    public void loadMainWindow(Stage stage) {
        try {
            ResourceBundle bundle = LocaleManager.getBundle();
            logger.info("Загружены ресурсы для локали: {}", LocaleManager.getCurrentLocale().getLanguage());

            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("main-view.fxml"),
                    bundle
            );

            // Внедрение зависимостей через фабрику контроллеров
            fxmlLoader.setControllerFactory(controllerClass -> {
                if (controllerClass == MainController.class) {
                    return new MainController(
                            new ClientDao(), new ClientRequestDao(),
                            new WorkerDao(), new DriverDao(new WorkerDao()),
                            new CarDao(), new TechnicalInspectionDao(new CarDao(), new WorkerDao()),
                            new RepairDao(new CarDao(), new WorkerDao()), new TripDao(new DriverDao(new WorkerDao()), new CarDao(), new AddressDao()),
                            new AddressDao()
                    );
                }

                try {
                    return controllerClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    logger.error("Не удалось создать контроллер: {}", controllerClass.getName(), e);
                    throw new RuntimeException("Ошибка создания контроллера: " + controllerClass.getName(), e);
                }
            });

            Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
            stage.setTitle(bundle.getString("app.title"));
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                logger.info("Завершение работы приложения");
                DatabaseConnection.closeConnection();
            });
            stage.show();
            logger.debug("Главное окно отображено");

        } catch (IOException e) {
            logger.error("Ошибка загрузки главного окна FXML", e);
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        logger.info("Приложение завершило работу");
        DatabaseConnection.closeConnection();
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch();
    }
}
