package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.TechnicalInspectionDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.WorkerDaoInt;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.TechnicalInspection;
import ru.kpr.kyrs.Pogo.Worker;

/**
 * Контроллер формы создания и редактирования технического осмотра.
 */
public class TechnicalInspectionFormController {

    private static final Logger logger = LoggerFactory.getLogger(TechnicalInspectionFormController.class);

    private final TechnicalInspectionDaoInt inspectionDao;
    private final CarDaoInt carDao;
    private final WorkerDaoInt workerDao;
    private TechnicalInspection currentInspection;

    @FXML private Label titleLabel;
    @FXML private ComboBox<Car> carComboBox;
    @FXML private ComboBox<Worker> mechanicComboBox;
    @FXML private DatePicker inspectionDatePicker;
    @FXML private TextArea resultArea;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы технического осмотра.
     *
     * @param inspectionDao DAO для работы с техосмотрами
     * @param carDao        DAO для работы с автомобилями
     * @param workerDao     DAO для работы с сотрудниками
     */
    public TechnicalInspectionFormController(TechnicalInspectionDaoInt inspectionDao,
                                              CarDaoInt carDao, WorkerDaoInt workerDao) {
        this.inspectionDao = inspectionDao;
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
            logger.error("Ошибка загрузки сотрудников: {}", e.getMessage(), e);
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
                        .or(inspectionDatePicker.valueProperty().isNull())
        );
    }

    /**
     * Заполняет форму данными техосмотра для редактирования.
     *
     * @param inspection техосмотр для редактирования
     */
    public void setForm(TechnicalInspection inspection) {
        this.currentInspection = inspection;
        titleLabel.setText("Редактирование техосмотра");
        inspectionDatePicker.setValue(inspection.getInspectionDate());
        resultArea.setText(inspection.getResult() != null ? inspection.getResult() : "");
        if (inspection.getCarId() != null) {
            carComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(inspection.getCarId().getId()))
                    .findFirst().ifPresent(carComboBox::setValue);
        }
        if (inspection.getMechanicId() != null) {
            mechanicComboBox.getItems().stream()
                    .filter(w -> w.getId().equals(inspection.getMechanicId().getId()))
                    .findFirst().ifPresent(mechanicComboBox::setValue);
        }
        logger.debug("Форма техосмотра открыта для редактирования: id={}", inspection.getId());
    }

    @FXML
    private void save() {
        if (carComboBox.getValue() == null || mechanicComboBox.getValue() == null
                || inspectionDatePicker.getValue() == null) {
            showAlert("Ошибка", "Заполните все обязательные поля!", Alert.AlertType.ERROR);
            return;
        }

        TechnicalInspection inspection = (currentInspection != null)
                ? currentInspection
                : new TechnicalInspection(null, null, null, null, null);
        inspection.setCarId(carComboBox.getValue());
        inspection.setMechanicId(mechanicComboBox.getValue());
        inspection.setInspectionDate(inspectionDatePicker.getValue());
        inspection.setResult(resultArea.getText().trim());

        try {
            if (currentInspection == null) {
                inspectionDao.add(inspection);
                logger.info("Техосмотр создан: авто={}", inspection.getCarId().getId());
            } else {
                inspectionDao.update(inspection);
                logger.info("Техосмотр обновлён: id={}", inspection.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения техосмотра: {}", e.getMessage(), e);
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
