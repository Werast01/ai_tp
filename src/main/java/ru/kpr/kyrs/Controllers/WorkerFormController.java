package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Worker;
import ru.kpr.kyrs.Pogo.WorkerType;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер формы создания и редактирования сотрудника.
 */
public class WorkerFormController {

    private static final Logger logger = LoggerFactory.getLogger(WorkerFormController.class);

    private final WorkerDaoInt workerDao;
    private Worker currentWorker;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField familyaField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<WorkerType> positionComboBox;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы сотрудника.
     *
     * @param workerDao DAO для работы с сотрудниками
     */
    public WorkerFormController(WorkerDaoInt workerDao) {
        this.workerDao = workerDao;
    }

    @FXML
    public void initialize() {
        loadPositions();
        setupValidation();
        setupPositionCellFactory();
    }

    private void loadPositions() {
        List<WorkerType> positions = new ArrayList<>();
        String sql = "SELECT id, position FROM scheme1.worker_types WHERE is_active = true ORDER BY position";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                positions.add(new WorkerType(rs.getLong("id"), rs.getString("position")));
            }
            logger.debug("Загружено {} должностей", positions.size());
        } catch (SQLException e) {
            logger.error("Ошибка загрузки должностей: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить список должностей: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        positionComboBox.setItems(FXCollections.observableArrayList(positions));
    }

    private void setupPositionCellFactory() {
        positionComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(WorkerType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getPosition());
            }
        });
        positionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(WorkerType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getPosition());
            }
        });
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                nameField.textProperty().isEmpty()
                        .or(familyaField.textProperty().isEmpty())
                        .or(phoneField.textProperty().isEmpty())
                        .or(positionComboBox.valueProperty().isNull())
        );
    }

    /**
     * Заполняет форму данными сотрудника для редактирования.
     *
     * @param worker сотрудник для редактирования
     */
    public void setForm(Worker worker) {
        this.currentWorker = worker;
        titleLabel.setText("Редактирование сотрудника");
        nameField.setText(nullSafe(worker.getName()));
        familyaField.setText(nullSafe(worker.getFamilya()));
        lastNameField.setText(nullSafe(worker.getLastName()));
        phoneField.setText(nullSafe(worker.getPhoneNumber()));
        emailField.setText(nullSafe(worker.getEmail()));
        if (worker.getPositionId() != null) {
            positionComboBox.getItems().stream()
                    .filter(wt -> wt.getId().equals(worker.getPositionId().getId()))
                    .findFirst()
                    .ifPresent(positionComboBox::setValue);
        }
        logger.debug("Форма сотрудника открыта для редактирования: id={}", worker.getId());
    }

    @FXML
    private void save() {
        String name = nameField.getText().trim();
        String familya = familyaField.getText().trim();
        String phone = phoneField.getText().trim();
        WorkerType position = positionComboBox.getValue();

        if (name.isEmpty() || familya.isEmpty() || phone.isEmpty() || position == null) {
            showAlert("Ошибка", "Заполните все обязательные поля!", Alert.AlertType.ERROR);
            return;
        }

        Worker worker = (currentWorker != null)
                ? currentWorker
                : new Worker(null, null, null, null, null, null, null);
        worker.setName(name);
        worker.setFamilya(familya);
        worker.setLastName(lastNameField.getText().trim());
        worker.setPhoneNumber(phone);
        worker.setEmail(emailField.getText().trim());
        worker.setPositionId(position);

        try {
            if (currentWorker == null) {
                workerDao.add(worker);
                logger.info("Сотрудник создан: {} {}", familya, name);
            } else {
                workerDao.update(worker);
                logger.info("Сотрудник обновлён: id={}", worker.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения сотрудника: {}", e.getMessage(), e);
            showAlert("Ошибка сохранения", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        ((Stage) btnSave.getScene().getWindow()).close();
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
