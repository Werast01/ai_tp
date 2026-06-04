package ru.kpr.kyrs.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.ClientDaoInt;
import ru.kpr.kyrs.Pogo.Client;

/**
 * Контроллер формы создания и редактирования клиента.
 */
public class ClientFormController {

    private static final Logger logger = LoggerFactory.getLogger(ClientFormController.class);

    private final ClientDaoInt clientDao;
    private Client currentClient;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField familyaField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы клиента.
     *
     * @param clientDao DAO для работы с клиентами
     */
    public ClientFormController(ClientDaoInt clientDao) {
        this.clientDao = clientDao;
    }

    @FXML
    public void initialize() {
        setupValidation();
        // Ограничение ввода телефона
        phoneField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("[+\\d\\-\\s()]*")) {
                phoneField.setText(oldVal);
            }
        });
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                nameField.textProperty().isEmpty()
                        .or(familyaField.textProperty().isEmpty())
                        .or(phoneField.textProperty().isEmpty())
        );
    }

    /**
     * Заполняет форму данными клиента для редактирования.
     *
     * @param client клиент для редактирования
     */
    public void setForm(Client client) {
        this.currentClient = client;
        titleLabel.setText("Редактирование клиента");
        nameField.setText(nullSafe(client.getName()));
        familyaField.setText(nullSafe(client.getFamilya()));
        lastNameField.setText(nullSafe(client.getLastName()));
        phoneField.setText(nullSafe(client.getPhoneNumber()));
        emailField.setText(nullSafe(client.getEmail()));
        logger.debug("Форма клиента открыта для редактирования: id={}", client.getId());
    }

    @FXML
    private void save() {
        String name = nameField.getText().trim();
        String familya = familyaField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || familya.isEmpty() || phone.isEmpty()) {
            showAlert("Ошибка", "Имя, фамилия и телефон обязательны!", Alert.AlertType.ERROR);
            return;
        }

        if (!email.isEmpty() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showAlert("Ошибка", "Некорректный формат email!", Alert.AlertType.ERROR);
            return;
        }

        Client client = (currentClient != null)
                ? currentClient
                : new Client(null, null, null, null, null, null);
        client.setName(name);
        client.setFamilya(familya);
        client.setLastName(lastName.isEmpty() ? null : lastName);
        client.setPhoneNumber(phone);
        client.setEmail(email.isEmpty() ? null : email);

        try {
            if (currentClient == null) {
                clientDao.add(client);
                logger.info("Клиент создан: {} {}", familya, name);
            } else {
                clientDao.update(client);
                logger.info("Клиент обновлён: id={}", client.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения клиента: {}", e.getMessage(), e);
            showAlert("Ошибка сохранения", extractMessage(e), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancel() {
        logger.debug("Отмена редактирования клиента");
        close();
    }

    private void close() {
        ((Stage) btnSave.getScene().getWindow()).close();
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }

    private String extractMessage(Exception e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("phoneNumber")) return "Такой номер телефона уже существует.";
        if (msg != null && msg.contains("email")) return "Такой email уже существует.";
        return msg != null ? msg : "Неизвестная ошибка";
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
