package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.AddressDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.CarDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.DriverDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.TripDaoInt;
import ru.kpr.kyrs.Pogo.Address;
import ru.kpr.kyrs.Pogo.Car;
import ru.kpr.kyrs.Pogo.Driver;
import ru.kpr.kyrs.Pogo.Trip;
import ru.kpr.kyrs.Pogo.Worker;

/**
 * Контроллер формы создания и редактирования выезда.
 */
public class TripFormController {

    private static final Logger logger = LoggerFactory.getLogger(TripFormController.class);

    private final TripDaoInt tripDao;
    private final DriverDaoInt driverDao;
    private final CarDaoInt carDao;
    private final AddressDaoInt addressDao;
    private Trip currentTrip;

    @FXML private Label titleLabel;
    @FXML private ComboBox<Driver> driverComboBox;
    @FXML private ComboBox<Car> carComboBox;
    @FXML private ComboBox<Address> startPointComboBox;
    @FXML private ComboBox<Address> endPointComboBox;
    @FXML private Spinner<Integer> fuelBeforeSpinner;
    @FXML private Spinner<Integer> fuelAfterSpinner;
    @FXML private Spinner<Integer> mileageBeforeSpinner;
    @FXML private Spinner<Integer> mileageAfterSpinner;
    @FXML private DatePicker startDatePicker;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы выезда.
     *
     * @param tripDao    DAO для работы с выездами
     * @param driverDao  DAO для работы с водителями
     * @param carDao     DAO для работы с автомобилями
     * @param addressDao DAO для работы с адресами
     */
    public TripFormController(TripDaoInt tripDao, DriverDaoInt driverDao,
                               CarDaoInt carDao, AddressDaoInt addressDao) {
        this.tripDao = tripDao;
        this.driverDao = driverDao;
        this.carDao = carDao;
        this.addressDao = addressDao;
    }

    @FXML
    public void initialize() {
        fuelBeforeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        fuelAfterSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
        mileageBeforeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999999, 0));
        mileageAfterSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999999, 0));

        loadDrivers();
        loadCars();
        loadAddresses();
        setupCellFactories();
        setupValidation();
    }

    private void loadDrivers() {
        try {
            driverComboBox.setItems(FXCollections.observableArrayList(driverDao.getAll()));
        } catch (Exception e) {
            logger.error("Ошибка загрузки водителей: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить водителей: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadCars() {
        try {
            carComboBox.setItems(FXCollections.observableArrayList(carDao.getAll()));
        } catch (Exception e) {
            logger.error("Ошибка загрузки автомобилей: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить автомобили: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAddresses() {
        try {
            var addresses = addressDao.getAll();
            startPointComboBox.setItems(FXCollections.observableArrayList(addresses));
            endPointComboBox.setItems(FXCollections.observableArrayList(addresses));
        } catch (Exception e) {
            logger.error("Ошибка загрузки адресов: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить адреса: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupCellFactories() {
        driverComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Driver item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                Worker w = item.getWorkerId();
                setText(w != null ? w.getFamilya() + " " + w.getName() + " [" + item.getLicense() + "]"
                        : item.getLicense());
            }
        });
        driverComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Driver item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                Worker w = item.getWorkerId();
                setText(w != null ? w.getFamilya() + " " + w.getName() : item.getLicense());
            }
        });

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

        startPointComboBox.setCellFactory(lv -> addressCell());
        startPointComboBox.setButtonCell(addressCell());
        endPointComboBox.setCellFactory(lv -> addressCell());
        endPointComboBox.setButtonCell(addressCell());
    }

    private ListCell<Address> addressCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Address item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getTown() + ", " + item.getStreet() + ", " + item.getHouse());
            }
        };
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                driverComboBox.valueProperty().isNull()
                        .or(carComboBox.valueProperty().isNull())
                        .or(startPointComboBox.valueProperty().isNull())
                        .or(endPointComboBox.valueProperty().isNull())
                        .or(startDatePicker.valueProperty().isNull())
        );
    }

    /**
     * Заполняет форму данными выезда для редактирования.
     *
     * @param trip выезд для редактирования
     */
    public void setForm(Trip trip) {
        this.currentTrip = trip;
        titleLabel.setText("Редактирование выезда");
        if (trip.getStartDatetime() != null) {
            startDatePicker.setValue(trip.getStartDatetime().toLocalDate());
        }
        if (trip.getFuelBefore() != null) fuelBeforeSpinner.getValueFactory().setValue(trip.getFuelBefore());
        if (trip.getFuelAfter() != null) fuelAfterSpinner.getValueFactory().setValue(trip.getFuelAfter());
        if (trip.getMileageBefore() != null) mileageBeforeSpinner.getValueFactory().setValue(trip.getMileageBefore());
        if (trip.getMileageAfter() != null) mileageAfterSpinner.getValueFactory().setValue(trip.getMileageAfter());

        if (trip.getDriverId() != null) {
            driverComboBox.getItems().stream()
                    .filter(d -> d.getId().equals(trip.getDriverId().getId()))
                    .findFirst().ifPresent(driverComboBox::setValue);
        }
        if (trip.getCarId() != null) {
            carComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(trip.getCarId().getId()))
                    .findFirst().ifPresent(carComboBox::setValue);
        }
        if (trip.getStartPoint() != null) {
            startPointComboBox.getItems().stream()
                    .filter(a -> a.getId().equals(trip.getStartPoint().getId()))
                    .findFirst().ifPresent(startPointComboBox::setValue);
        }
        if (trip.getEndPoint() != null) {
            endPointComboBox.getItems().stream()
                    .filter(a -> a.getId().equals(trip.getEndPoint().getId()))
                    .findFirst().ifPresent(endPointComboBox::setValue);
        }
        logger.debug("Форма выезда открыта для редактирования: id={}", trip.getId());
    }

    @FXML
    private void save() {
        if (driverComboBox.getValue() == null || carComboBox.getValue() == null
                || startPointComboBox.getValue() == null || endPointComboBox.getValue() == null
                || startDatePicker.getValue() == null) {
            showAlert("Ошибка", "Заполните все обязательные поля!", Alert.AlertType.ERROR);
            return;
        }

        int mileageBefore = mileageBeforeSpinner.getValue();
        int mileageAfter = mileageAfterSpinner.getValue();
        if (mileageAfter > 0 && mileageAfter < mileageBefore) {
            showAlert("Ошибка", "Пробег после выезда не может быть меньше пробега до!", Alert.AlertType.ERROR);
            return;
        }

        Trip trip = (currentTrip != null)
                ? currentTrip
                : new Trip(null, null, null, null, null, null, null, null, null, null, null);
        trip.setDriverId(driverComboBox.getValue());
        trip.setCarId(carComboBox.getValue());
        trip.setStartPoint(startPointComboBox.getValue());
        trip.setEndPoint(endPointComboBox.getValue());
        trip.setFuelBefore(fuelBeforeSpinner.getValue());
        trip.setFuelAfter(fuelAfterSpinner.getValue());
        trip.setMileageBefore(mileageBefore);
        trip.setMileageAfter(mileageAfter);
        trip.setStartDatetime(startDatePicker.getValue().atStartOfDay());

        try {
            if (currentTrip == null) {
                tripDao.add(trip);
                logger.info("Выезд создан: водитель={}, авто={}", trip.getDriverId().getId(), trip.getCarId().getId());
            } else {
                tripDao.update(trip);
                logger.info("Выезд обновлён: id={}", trip.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения выезда: {}", e.getMessage(), e);
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
