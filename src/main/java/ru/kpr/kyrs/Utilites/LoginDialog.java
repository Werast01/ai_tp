package ru.kpr.kyrs.Utilites;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Диалоговое окно авторизации пользователя.
 * Запрашивает логин и пароль для подключения к базе данных PostgreSQL.
 * Аутентификация выполняется средствами СУБД.
 */
public class LoginDialog {

    private static final Logger logger = LoggerFactory.getLogger(LoginDialog.class);

    /**
     * Отображает диалог авторизации и ожидает ввода пользователя.
     *
     * @return Optional с результатом авторизации, или пустой Optional если пользователь отменил вход
     */
    public Optional<LoginResult> showAndWait() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(LocaleManager.get("auth.title"));
        dialogStage.setResizable(false);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label userLabel = new Label(LocaleManager.get("auth.login"));
        TextField userField = new TextField();
        userField.setPromptText("postgres");

        Label passLabel = new Label(LocaleManager.get("auth.password"));
        PasswordField passField = new PasswordField();

        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        Button btnLogin = new Button(LocaleManager.get("auth.btn.login"));
        Button btnCancel = new Button(LocaleManager.get("auth.btn.cancel"));
        btnLogin.setDefaultButton(true);
        btnCancel.setCancelButton(true);

        HBox buttonBar = new HBox(10, btnLogin, btnCancel);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(15, grid, buttonBar);
        root.setPadding(new Insets(10));

        // Массивы для хранения результата (обходим ограничение effectively final)
        final String[] username = {null};
        final String[] password = {null};
        final boolean[] okClicked = {false};

        btnLogin.setOnAction(e -> {
            String u = userField.getText().trim();
            String p = passField.getText();
            if (u.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(LocaleManager.get("msg.error.title"));
                alert.setHeaderText(null);
                alert.setContentText(LocaleManager.get("auth.error.empty"));
                alert.showAndWait();
                return;
            }
            username[0] = u;
            password[0] = p;
            okClicked[0] = true;
            logger.debug("Пользователь {} подтвердил ввод данных для входа", u);
            dialogStage.close();
        });

        btnCancel.setOnAction(e -> {
            okClicked[0] = false;
            logger.debug("Пользователь отменил ввод данных для входа");
            dialogStage.close();
        });

        dialogStage.setScene(new Scene(root, 300, 160));
        dialogStage.showAndWait();

        if (okClicked[0]) {
            return Optional.of(new LoginResult(username[0], password[0]));
        }
        return Optional.empty();
    }

    /**
     * Хранит результат авторизации: логин и пароль.
     */
    public static class LoginResult {
        private final String username;
        private final String password;

        /**
         * Создаёт результат авторизации.
         *
         * @param username имя пользователя
         * @param password пароль
         */
        public LoginResult(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /** @return имя пользователя */
        public String getUsername() {
            return username;
        }

        /** @return пароль */
        public String getPassword() {
            return password;
        }
    }
}
