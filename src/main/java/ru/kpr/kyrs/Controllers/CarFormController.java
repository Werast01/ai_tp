package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.CarCondition;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер формы создания и редактирования автомобиля.
 */
public class CarFormController {

    private static final Logger logger = LoggerFactory.getLogger(CarFormController.class);

    private final CarDaoInt carDao;
    private Car currentCar;

    @FXML private Label titleLabel;
    @FXML private TextField markaField;
    @FXML private TextField modelField;
    @FXML private TextField gosNumberField;
    @FXML private TextField vinNumberField;
    @FXML private TextField insuranceField;
    @FXML private TextField yearField;
    @FXML private TextField capacityField;
    @FXML private ComboBox<CarCondition> conditionComboBox;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private TextField fuelNormField;

    /**
     * Создаёт контроллер формы автомобиля.
     *
     * @param carDao DAO для работы с автомобилями
     */
    public CarFormController(CarDaoInt carDao) {
        this.carDao = carDao;
    }

    @FXML
    public void initialize() {
        loadConditions();
        setupValidation();
        setupConditionCellFactory();
        // Только цифры в числовых полях
        yearField.textProperty().addListener((obs, o, n) -> {
            if (n != null && !n.matches("\\d{0,4}")) yearField.setText(o);
        });
        capacityField.textProperty().addListener((obs, o, n) -> {
            if (n != null && !n.matches("\\d*")) capacityField.setText(o);
        });
        // Только цифры и точка в поле нормы расхода
        fuelNormField.textProperty().addListener((obs, o, n) -> {
            if (n != null && !n.matches("\\d*\\.?\\d*")) fuelNormField.setText(o);
        });
    }

    private void loadConditions() {
        List<CarCondition> conditions = new ArrayList<>();
        String sql = "SELECT id, \"conditionName\" FROM scheme1.car_conditions ORDER BY \"conditionName\"";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                conditions.add(new CarCondition(rs.getLong("id"), rs.getString("conditionName")));
            }
            logger.debug("Загружено {} состояний автомобилей", conditions.size());
        } catch (SQLException e) {
            logger.error("Ошибка загрузки состояний автомобилей: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить список состояний: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        conditionComboBox.setItems(FXCollections.observableArrayList(conditions));
    }

    private void setupConditionCellFactory() {
        conditionComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CarCondition item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getConditionName());
            }
        });
        conditionComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(CarCondition item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getConditionName());
            }
        });
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                markaField.textProperty().isEmpty()
                        .or(modelField.textProperty().isEmpty())
                        .or(gosNumberField.textProperty().isEmpty())
                        .or(capacityField.textProperty().isEmpty())
        );
    }

    /**
     * Заполняет форму данными автомобиля для редактирования.
     *
     * @param car автомобиль для редактирования
     */
    public void setForm(Car car) {
        this.currentCar = car;
        titleLabel.setText("Редактирование автомобиля");
        markaField.setText(nullSafe(car.getMarka()));
        modelField.setText(nullSafe(car.getModel()));
        gosNumberField.setText(nullSafe(car.getGosNumber()));
        vinNumberField.setText(nullSafe(car.getVinNumber()));
        insuranceField.setText(nullSafe(car.getInsurance()));
        yearField.setText(car.getYearOfManufacture() != null ? car.getYearOfManufacture().toString() : "");
        capacityField.setText(car.getCapacity() != null ? car.getCapacity().toString() : "");
        if (car.getConditionId() != null) {
            conditionComboBox.getItems().stream()
                    .filter(cc -> cc.getId().equals(car.getConditionId().getId()))
                    .findFirst()
                    .ifPresent(conditionComboBox::setValue);
        }
        fuelNormField.setText(car.getFuelNorm() != null ? car.getFuelNorm().toString() : "");
        logger.debug("Форма автомобиля открыта для редактирования: id={}", car.getId());
    }

    @FXML
    private void save() {
        String marka = markaField.getText().trim();
        String model = modelField.getText().trim();
        String gosNumber = gosNumberField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        if (marka.isEmpty() || model.isEmpty() || gosNumber.isEmpty() || capacityStr.isEmpty()) {
            showAlert("Ошибка", "Заполните обязательные поля: марка, модель, гос.номер, вместимость!", Alert.AlertType.ERROR);
            return;
        }

        Integer year = null;
        Integer capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (!yearField.getText().trim().isEmpty()) {
                year = Integer.parseInt(yearField.getText().trim());
                if (year < 1900 || year > 2100) {
                    showAlert("Ошибка", "Год выпуска должен быть в диапазоне 1900–2100!", Alert.AlertType.ERROR);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Год и вместимость должны быть числами!", Alert.AlertType.ERROR);
            return;
        }

        Car car = new Car(
                currentCar != null ? currentCar.getId() : null,
                marka, model, gosNumber, year,
                vinNumberField.getText().trim(),
                insuranceField.getText().trim(),
                conditionComboBox.getValue(),
                capacity
        );
        String fuelNormStr = fuelNormField.getText().trim();
        car.setFuelNorm(fuelNormStr.isEmpty() ? null : Double.parseDouble(fuelNormStr));

        try {
            if (currentCar == null) {
                carDao.add(car);
                logger.info("Автомобиль создан: {} {} {}", marka, model, gosNumber);
            } else {
                carDao.update(car);
                logger.info("Автомобиль обновлён: id={}", car.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения автомобиля: {}", e.getMessage(), e);
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
