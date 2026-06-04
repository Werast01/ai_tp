package ru.kpr.kyrs.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.AddressDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.ClientDaoInt;
import ru.kpr.kyrs.Dao.Interfaces.ClientRequestDaoInt;
import ru.kpr.kyrs.Pogo.Address;
import ru.kpr.kyrs.Pogo.Client;
import ru.kpr.kyrs.Pogo.ClientRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Контроллер формы создания и редактирования заявки клиента.
 */
public class ClientRequestFormController {

    private static final Logger logger = LoggerFactory.getLogger(ClientRequestFormController.class);

    private final ClientRequestDaoInt requestDao;
    private final ClientDaoInt clientDao;
    private final AddressDaoInt addressDao;
    private ClientRequest currentRequest;

    @FXML private Label titleLabel;
    @FXML private ComboBox<Client> clientComboBox;
    @FXML private ComboBox<Address> startPointComboBox;
    @FXML private ComboBox<Address> endPointComboBox;
    @FXML private DatePicker executionDatePicker;
    @FXML private Spinner<Integer> personsSpinner;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    /**
     * Создаёт контроллер формы заявки.
     *
     * @param requestDao DAO для работы с заявками
     * @param clientDao  DAO для работы с клиентами
     * @param addressDao DAO для работы с адресами
     */
    public ClientRequestFormController(ClientRequestDaoInt requestDao,
                                        ClientDaoInt clientDao,
                                        AddressDaoInt addressDao) {
        this.requestDao = requestDao;
        this.clientDao = clientDao;
        this.addressDao = addressDao;
    }

    @FXML
    public void initialize() {
        personsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        loadClients();
        loadAddresses();
        setupCellFactories();
        setupValidation();
    }

    private void loadClients() {
        try {
            clientComboBox.setItems(FXCollections.observableArrayList(clientDao.getAll()));
            logger.debug("Загружено {} клиентов", clientComboBox.getItems().size());
        } catch (Exception e) {
            logger.error("Ошибка загрузки клиентов: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить клиентов: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAddresses() {
        try {
            var addresses = addressDao.getAll();
            startPointComboBox.setItems(FXCollections.observableArrayList(addresses));
            endPointComboBox.setItems(FXCollections.observableArrayList(addresses));
            logger.debug("Загружено {} адресов", addresses.size());
        } catch (Exception e) {
            logger.error("Ошибка загрузки адресов: {}", e.getMessage(), e);
            showAlert("Ошибка", "Не удалось загрузить адреса: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupCellFactories() {
        clientComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.getFamilya() + " " + item.getName() + " (" + item.getPhoneNumber() + ")");
            }
        });
        clientComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFamilya() + " " + item.getName());
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
                clientComboBox.valueProperty().isNull()
                        .or(startPointComboBox.valueProperty().isNull())
                        .or(endPointComboBox.valueProperty().isNull())
                        .or(executionDatePicker.valueProperty().isNull())
        );
    }

    /**
     * Заполняет форму данными заявки для редактирования.
     *
     * @param request заявка для редактирования
     */
    public void setForm(ClientRequest request) {
        this.currentRequest = request;
        titleLabel.setText("Редактирование заявки");
        executionDatePicker.setValue(request.getExecutionDate());
        if (request.getNumberOfPersons() != null) {
            personsSpinner.getValueFactory().setValue(request.getNumberOfPersons());
        }
        if (request.getClientId() != null) {
            clientComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(request.getClientId().getId()))
                    .findFirst().ifPresent(clientComboBox::setValue);
        }
        if (request.getStartPoint() != null) {
            startPointComboBox.getItems().stream()
                    .filter(a -> a.getId().equals(request.getStartPoint().getId()))
                    .findFirst().ifPresent(startPointComboBox::setValue);
        }
        if (request.getEndPoint() != null) {
            endPointComboBox.getItems().stream()
                    .filter(a -> a.getId().equals(request.getEndPoint().getId()))
                    .findFirst().ifPresent(endPointComboBox::setValue);
        }
        logger.debug("Форма заявки открыта для редактирования: id={}", request.getId());
    }

    @FXML
    private void save() {
        if (clientComboBox.getValue() == null || startPointComboBox.getValue() == null
                || endPointComboBox.getValue() == null || executionDatePicker.getValue() == null) {
            showAlert("Ошибка", "Заполните все обязательные поля!", Alert.AlertType.ERROR);
            return;
        }

        LocalDate execDate = executionDatePicker.getValue();
        if (currentRequest == null && execDate.isBefore(LocalDate.now())) {
            showAlert("Ошибка", "Дата выполнения не может быть в прошлом!", Alert.AlertType.ERROR);
            return;
        }

        ClientRequest request = (currentRequest != null)
                ? currentRequest
                : new ClientRequest(null, LocalDateTime.now(), null, null, null, null, null);
        request.setClientId(clientComboBox.getValue());
        request.setStartPoint(startPointComboBox.getValue());
        request.setEndPoint(endPointComboBox.getValue());
        request.setExecutionDate(execDate);
        request.setNumberOfPersons(personsSpinner.getValue());

        try {
            if (currentRequest == null) {
                requestDao.add(request);
                logger.info("Заявка создана: клиент={}", request.getClientId().getId());
            } else {
                requestDao.update(request);
                logger.info("Заявка обновлена: id={}", request.getId());
            }
            close();
        } catch (Exception e) {
            logger.error("Ошибка сохранения заявки: {}", e.getMessage(), e);
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
