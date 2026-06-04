package ru.kpr.kyrs.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.AddressDaoInt;
import ru.kpr.kyrs.Pogo.Address;

/**
 * Контроллер формы создания и редактирования адреса.
 */
public class AddressFormController {

    private static final Logger logger = LoggerFactory.getLogger(AddressFormController.class);

    private final AddressDaoInt addressDao;
    private Address currentAddress;

    @FXML private Label titleLabel;
    @FXML private TextField townField;
    @FXML private TextField streetField;
    @FXML private TextField houseField;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы адреса.
     *
     * @param addressDao DAO для работы с адресами
     */
    public AddressFormController(AddressDaoInt addressDao) {
        this.addressDao = addressDao;
    }

    @FXML
    public void initialize() {
        setupValidation();
    }

    private void setupValidation() {
        btnSave.disableProperty().bind(
                townField.textProperty().isEmpty()
                        .or(streetField.textProperty().isEmpty())
                        .or(houseField.textProperty().isEmpty())
        );
    }

    /**
     * Заполняет форму данными адреса для редактирования.
     *
     * @param address адрес для редактирования
     */
    public void setForm(Address address) {
        this.currentAddress = address;
        titleLabel.setText("Редактирование адреса");
        townField.setText(address.getTown() != null ? address.getTown() : "");
        streetField.setText(address.getStreet() != null ? address.getStreet() : "");
        houseField.setText(address.getHouse() != null ? address.getHouse() : "");
        logger.debug("Форма адреса открыта для редактирования: id={}", address.getId());
    }

    @FXML
    private void save() {
        String town = townField.getText().trim();
        String street = streetField.getText().trim();
        String house = houseField.getText().trim();

        if (town.isEmpty() || street.isEmpty() || house.isEmpty()) {
            showAlert("Ошибка", "Все поля обязательны для заполнения!", Alert.AlertType.ERROR);
            return;
        }

        Address address = (currentAddress != null)
                ? currentAddress
                : new Address(null, null, null, null);
        address.setTown(town);
        address.setStreet(street);
        address.setHouse(house);

        try {
            if (currentAddress == null) {
                addressDao.add(address);
                logger.info("Адрес создан: {}, {}, {}", town, street, house);
            } else {
                addressDao.update(address);
                logger.info("Адрес обновлён: id={}", address.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения адреса: {}", e.getMessage(), e);
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
