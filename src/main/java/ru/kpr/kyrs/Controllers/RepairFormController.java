package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.RepairDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.Repair;
import ru.kpr.kyrs.Pogo.Worker;

/**
 * Контроллер формы создания и редактирования ремонта.
 */
public class RepairFormController {

    private static final Logger logger = LoggerFactory.getLogger(RepairFormController.class);

    private final RepairDaoInt repairDao;
    private final CarDaoInt carDao;
    private final WorkerDaoInt workerDao;
    private Repair currentRepair;

    @FXML private Label titleLabel;
    @FXML private ComboBox<Car> carComboBox;
    @FXML private ComboBox<Worker> mechanicComboBox;
    @FXML private DatePicker repairDatePicker;
    @FXML private TextArea resultArea;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы ремонта.
     *
     * @param repairDao  DAO для работы с ремонтами
     * @param carDao     DAO для работы с автомобилями
     * @param workerDao  DAO для работы с сотрудниками
     */
    public RepairFormController(RepairDaoInt repairDao, CarDaoInt carDao, WorkerDaoInt workerDao) {
        this.repairDao = repairDao;
        this.carDao = carDao;
        this.workerDao = workerDao;
    }

    @FXML
    public void initialize() {
        loadCars();
        loadWorkers();
        setupCellFactories();
        setupValidation();
    }

    private void loadCars() {
        try {
            carComboBox.setItems(FXCollections.observableArrayList(carDao.getAll()));
        } catch (Exception e) {
            logger.error("Ошибка загрузки автомобилей: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить автомобили: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadWorkers() {
        try {
            mechanicComboBox.setItems(FXCollections.observableArrayList(workerDao.getAll()));
        } catch (Exception e) {
            logger.error("Ошибка загрузки механиков: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить сотрудников: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupCellFactories() {
        carComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Car item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getMarka() + " " + item.getModel() + " (" + item.getGosNumber() + ")");
            }
        });
        carComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Car item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getMarka() + " " + item.getGosNumber());
            }
        });

        mechanicComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Worker item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFamilya() + " " + item.getName());
            }
        });
        mechanicComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Worker item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFamilya() + " " + item.getName());
            }
        });
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                carComboBox.valueProperty().isNull()
                        .or(mechanicComboBox.valueProperty().isNull())
                        .or(repairDatePicker.valueProperty().isNull())
        );
    }

    /**
     * Заполняет форму данными ремонта для редактирования.
     *
     * @param repair ремонт для редактирования
     */
    public void setForm(Repair repair) {
        this.currentRepair = repair;
        titleLabel.setText("Редактирование ремонта");
        repairDatePicker.setValue(repair.getRepairDate());
        resultArea.setText(repair.getResult() != null ? repair.getResult() : "");
        if (repair.getCarId() != null) {
            carComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(repair.getCarId().getId()))
                    .findFirst().ifPresent(carComboBox::setValue);
        }
        if (repair.getMechanicId() != null) {
            mechanicComboBox.getItems().stream()
                    .filter(w -> w.getId().equals(repair.getMechanicId().getId()))
                    .findFirst().ifPresent(mechanicComboBox::setValue);
        }
        logger.debug("Форма ремонта открыта для редактирования: id={}", repair.getId());
    }

    @FXML
    private void save() {
        if (carComboBox.getValue() == null || mechanicComboBox.getValue() == null
                || repairDatePicker.getValue() == null) {
            showAlert("Ошибка", "Заполните все обязательные поля!", Alert.AlertType.ERROR);
            return;
        }

        Repair repair = (currentRepair != null)
                ? currentRepair
                : new Repair(null, null, null, null, null);
        repair.setCarId(carComboBox.getValue());
        repair.setMechanicId(mechanicComboBox.getValue());
        repair.setRepairDate(repairDatePicker.getValue());
        repair.setResult(resultArea.getText().trim());

        try {
            if (currentRepair == null) {
                repairDao.add(repair);
                logger.info("Ремонт создан: авто={}", repair.getCarId().getId());
            } else {
                repairDao.update(repair);
                logger.info("Ремонт обновлён: id={}", repair.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения ремонта: {}", e.getMessage(), e);
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
