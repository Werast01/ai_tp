package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.DriverDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Driver;
import ru.kpr.kyrs.Pogo.Worker;

/**
 * Контроллер формы создания и редактирования водителя.
 */
public class DriverFormController {

    private static final Logger logger = LoggerFactory.getLogger(DriverFormController.class);

    private final DriverDaoInt driverDao;
    private final WorkerDaoInt workerDao;
    private Driver currentDriver;

    @FXML private Label titleLabel;
    @FXML private ComboBox<Worker> workerComboBox;
    @FXML private TextField licenseField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы водителя.
     *
     * @param driverDao DAO для работы с водителями
     * @param workerDao DAO для работы с сотрудниками
     */
    public DriverFormController(DriverDaoInt driverDao, WorkerDaoInt workerDao) {
        this.driverDao = driverDao;
        this.workerDao = workerDao;
    }

    @FXML
    public void initialize() {
        loadWorkers();
        setupValidation();
        setupWorkerCellFactory();
    }

    private void loadWorkers() {
        try {
            workerComboBox.setItems(FXCollections.observableArrayList(workerDao.getAll()));
            logger.debug("Загружено {} сотрудников для выбора водителя", workerComboBox.getItems().size());
        } catch (Exception e) {
            logger.error("Ошибка загрузки сотрудников: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить список сотрудников: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupWorkerCellFactory() {
        workerComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Worker item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFamilya() + " " + item.getName());
            }
        });
        workerComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Worker item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFamilya() + " " + item.getName());
            }
        });
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                workerComboBox.valueProperty().isNull()
                        .or(licenseField.textProperty().isEmpty())
        );
    }

    /**
     * Заполняет форму данными водителя для редактирования.
     *
     * @param driver водитель для редактирования
     */
    public void setForm(Driver driver) {
        this.currentDriver = driver;
        titleLabel.setText("Редактирование водителя");
        licenseField.setText(driver.getLicense() != null ? driver.getLicense() : "");
        if (driver.getWorkerId() != null) {
            workerComboBox.getItems().stream()
                    .filter(w -> w.getId().equals(driver.getWorkerId().getId()))
                    .findFirst()
                    .ifPresent(workerComboBox::setValue);
        }
        logger.debug("Форма водителя открыта для редактирования: id={}", driver.getId());
    }

    @FXML
    private void save() {
        Worker worker = workerComboBox.getValue();
        String license = licenseField.getText().trim();

        if (worker == null || license.isEmpty()) {
            showAlert("Ошибка", "Выберите сотрудника и укажите номер водительского удостоверения!", Alert.AlertType.ERROR);
            return;
        }

        Driver driver = (currentDriver != null)
                ? currentDriver
                : new Driver(null, null, null);
        driver.setWorkerId(worker);
        driver.setLicense(license);

        try {
            if (currentDriver == null) {
                driverDao.add(driver);
                logger.info("Водитель создан: {} права={}", worker.getFamilya(), license);
            } else {
                driverDao.update(driver);
                logger.info("Водитель обновлён: id={}", driver.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения водителя: {}", e.getMessage(), e);
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
