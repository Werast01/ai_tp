package ru.kpr.kyrs.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kpr.kyrs.Dao.Interfaces.*;
import ru.kpr.kyrs.MainApplication;
import ru.kpr.kyrs.Pogo.*;
import ru.kpr.kyrs.Utilites.LocaleManager;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // ===================== DAO =====================
    private final ClientDaoInt clientDao;
    private final ClientRequestDaoInt requestDao;
    private final WorkerDaoInt workerDao;
    private final DriverDaoInt driverDao;
    private final CarDaoInt carDao;
    private final TechnicalInspectionDaoInt inspectionDao;
    private final RepairDaoInt repairDao;
    private final TripDaoInt tripDao;
    private final AddressDaoInt addressDao;

    // ===================== Списки данных =====================
    private final ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private final ObservableList<ClientRequest> requestsList = FXCollections.observableArrayList();
    private final ObservableList<Worker> workersList = FXCollections.observableArrayList();
    private final ObservableList<Driver> driversList = FXCollections.observableArrayList();
    private final ObservableList<Car> carsList = FXCollections.observableArrayList();
    private final ObservableList<TechnicalInspection> inspectionsList = FXCollections.observableArrayList();
    private final ObservableList<Repair> repairsList = FXCollections.observableArrayList();
    private final ObservableList<Trip> tripsList = FXCollections.observableArrayList();
    private final ObservableList<Address> addressesList = FXCollections.observableArrayList();

    // ===================== Таблицы =====================
    @FXML private TableView<ClientRequest> tableRequests;
    @FXML private TableView<Client> tableClients;
    @FXML private TableView<Worker> tableWorkers;
    @FXML private TableView<Driver> tableDrivers;
    @FXML private TableView<Car> tableCars;
    @FXML private TableView<TechnicalInspection> tableInspections;
    @FXML private TableView<Repair> tableRepairs;
    @FXML private TableView<Trip> tableTrips;
    @FXML private TableView<Address> tableAddresses;

    // ===================== Колонки =====================
    // ===================== Заявки =====================
    @FXML private TableColumn<ClientRequest, Long> colRequestId;
    @FXML private TableColumn<ClientRequest, String> colClientName;
    @FXML private TableColumn<ClientRequest, String> colExecutionDate;
    @FXML private TableColumn<ClientRequest, String> colStartPoint;
    @FXML private TableColumn<ClientRequest, String> colEndPoint;
    @FXML private TableColumn<ClientRequest, Integer> colPersons;

    // ===================== Клиенты =====================
    @FXML private TableColumn<Client, Long> colClientId;
    @FXML private TableColumn<Client, String> colFullName;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private TableColumn<Client, String> colEmail;

    // ===================== Сотрудники =====================
    @FXML private TableColumn<Worker, Long> colWorkerId;
    @FXML private TableColumn<Worker, String> colWorkerFIO;
    @FXML private TableColumn<Worker, String> colPosition;
    @FXML private TableColumn<Worker, String> colWorkerPhone;
    @FXML private TableColumn<Worker, String> colWorkerEmail;

    // ===================== Водители =====================
    @FXML private TableColumn<Driver, Long> colDriverId;
    @FXML private TableColumn<Driver, String> colDriverFIO;
    @FXML private TableColumn<Driver, String> colLicense;
    @FXML private TableColumn<Driver, String> colDriverPhone;

    // ===================== Автомобили =====================
    @FXML private TableColumn<Car, Long> colCarId;
    @FXML private TableColumn<Car, String> colCarMarka;
    @FXML private TableColumn<Car, String> colCarModel;
    @FXML private TableColumn<Car, String> colCarGosNumber;
    @FXML private TableColumn<Car, Integer> colCarYear;
    @FXML private TableColumn<Car, String> colCarVin;
    @FXML private TableColumn<Car, String> colCarCondition;
    @FXML private TableColumn<Car, Integer> colCarCapacity;
    @FXML private TableColumn<Car, String> colCarFuelNorm;

    // ===================== Техосмотры =====================
    @FXML private TableColumn<TechnicalInspection, Long> colInspectionId;
    @FXML private TableColumn<TechnicalInspection, String> colInspectionCar;
    @FXML private TableColumn<TechnicalInspection, String> colInspectionDate;
    @FXML private TableColumn<TechnicalInspection, String> colInspectionMechanic;
    @FXML private TableColumn<TechnicalInspection, String> colInspectionResult;

    // ===================== Ремонты =====================
    @FXML private TableColumn<Repair, Long> colRepairId;
    @FXML private TableColumn<Repair, String> colRepairCar;
    @FXML private TableColumn<Repair, String> colRepairDate;
    @FXML private TableColumn<Repair, String> colRepairMechanic;
    @FXML private TableColumn<Repair, String> colRepairResult;

    // ===================== Выезды =====================
    @FXML private TableColumn<Trip, Long> colTripId;
    @FXML private TableColumn<Trip, String> colTripDriver;
    @FXML private TableColumn<Trip, String> colTripCar;
    @FXML private TableColumn<Trip, String> colTripStartPoint;
    @FXML private TableColumn<Trip, String> colTripEndPoint;
    @FXML private TableColumn<Trip, String> colTripStartDateTime;
    @FXML private TableColumn<Trip, String> colTripEndDateTime;
    @FXML private TableColumn<Trip, String> colTripMileage;
    @FXML private TableColumn<Trip, String> colTripFuel;

    // ===================== Адреса =====================
    @FXML private TableColumn<Address, Long> colAddressId;
    @FXML private TableColumn<Address, String> colAddressTown;
    @FXML private TableColumn<Address, String> colAddressStreet;
    @FXML private TableColumn<Address, String> colAddressHouse;

    // ===================== Кнопки =====================
    @FXML private Button btnCreateRequest, btnEditRequest, btnDeleteRequest;
    @FXML private Button btnCreateClient, btnEditClient, btnDeleteClient;
    @FXML private Button btnCreateWorker, btnEditWorker, btnDeleteWorker;
    @FXML private Button btnCreateDriver, btnEditDriver, btnDeleteDriver;
    @FXML private Button btnCreateCar, btnEditCar, btnDeleteCar;
    @FXML private Button btnCreateInspection, btnEditInspection, btnDeleteInspection;
    @FXML private Button btnCreateRepair, btnEditRepair, btnDeleteRepair;
    @FXML private Button btnCreateTrip, btnEditTrip, btnDeleteTrip, btnCompleteTrip;
    @FXML private Button btnCreateAddress, btnEditAddress, btnDeleteAddress;

    // ===================== Язык =====================
    @FXML private Button btnLangRu, btnLangEn, btnLangDe;

    // ===================== Поиск =====================
    @FXML private TextField searchRequests, searchClients, searchWorkers, searchDrivers;
    @FXML private TextField searchCars, searchInspections, searchRepairs, searchTrips, searchAddresses;

    // ===================== Отчёты =====================
    @FXML private DatePicker reportDateFrom;
    @FXML private DatePicker reportDateTo;
    @FXML private Button btnBuildReport;
    @FXML private TableView<FuelReportRow> tableFuelReport;
    @FXML private TableColumn<FuelReportRow, String> colReportCar;
    @FXML private TableColumn<FuelReportRow, String> colReportMileage;
    @FXML private TableColumn<FuelReportRow, String> colReportFuelUsed;
    @FXML private TableColumn<FuelReportRow, String> colReportFuelNorm;
    @FXML private TableColumn<FuelReportRow, String> colReportOverflow;

    @FXML private DatePicker reportDriverDateFrom;
    @FXML private DatePicker reportDriverDateTo;
    @FXML private Button btnBuildDriverReport;
    @FXML private TableView<DriverReportRow> tableDriverReport;
    @FXML private TableColumn<DriverReportRow, String> colDriverReportName;
    @FXML private TableColumn<DriverReportRow, String> colDriverReportTrips;
    @FXML private TableColumn<DriverReportRow, String> colDriverReportMileage;
    @FXML private TableColumn<DriverReportRow, String> colDriverReportFuel;

    public MainController(ClientDaoInt clientDao, ClientRequestDaoInt requestDao,
                          WorkerDaoInt workerDao, DriverDaoInt driverDao, CarDaoInt carDao,
                          TechnicalInspectionDaoInt inspectionDao, RepairDaoInt repairDao,
                          TripDaoInt tripDao, AddressDaoInt addressDao) {
        this.clientDao = clientDao;
        this.requestDao = requestDao;
        this.workerDao = workerDao;
        this.driverDao = driverDao;
        this.carDao = carDao;
        this.inspectionDao = inspectionDao;
        this.repairDao = repairDao;
        this.tripDao = tripDao;
        this.addressDao = addressDao;
    }

    @FXML
    public void initialize() {
        System.out.println("=== MainController.initialize() START ===");
        try {
            setupTableColumns();
            setupButtonHandlers();
            setupLanguageButtons();
            setupSearch();
            loadAllData();
            setupReports();
            System.out.println("=== MainController успешно инициализирован ===");
        } catch (Exception e) {
            System.err.println("ОШИБКА в initialize(): " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        // Заявки
        if (tableRequests != null) {
            colRequestId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colClientName.setCellValueFactory(data -> {
                Client client = data.getValue().getClientId();
                return new SimpleStringProperty(client != null ? client.toString() : "—");
            });
            colExecutionDate.setCellValueFactory(data -> {
                java.time.LocalDate date = data.getValue().getExecutionDate();
                return new SimpleStringProperty(date != null ? date.toString() : "—");
            });
            colStartPoint.setCellValueFactory(data -> {
                Address address = data.getValue().getStartPoint();
                return new SimpleStringProperty(address != null ? address.toString() : "—");
            });
            colEndPoint.setCellValueFactory(data -> {
                Address address = data.getValue().getEndPoint();
                return new SimpleStringProperty(address != null ? address.toString() : "—");
            });
            colPersons.setCellValueFactory(new PropertyValueFactory<>("numberOfPersons"));
            tableRequests.setItems(requestsList);
        }

        // Клиенты
        if (tableClients != null) {
            colClientId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colFullName.setCellValueFactory(data -> new SimpleStringProperty(getFullName(data.getValue())));
            colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            tableClients.setItems(clientsList);
        }

        // Сотрудники
        if (tableWorkers != null) {
            colWorkerId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colWorkerFIO.setCellValueFactory(data -> new SimpleStringProperty(getFullName(data.getValue())));
            colWorkerPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            colWorkerEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            TableColumn<Worker, String> colPosition = (TableColumn<Worker, String>) tableWorkers.getColumns().get(2);
            if (colPosition != null) {
                colPosition.setCellValueFactory(data -> {
                    Worker worker = data.getValue();
                    String position = worker.getPositionId() != null ? worker.getPositionId().getPosition() : "—";
                    return new SimpleStringProperty(position);
                });
            }
            tableWorkers.setItems(workersList);
        }

        // Водители
        if (tableDrivers != null) {
            colDriverId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colDriverFIO.setCellValueFactory(data -> {
                Driver driver = data.getValue();
                Worker worker = driver.getWorkerId();
                return new SimpleStringProperty(worker != null ? worker.toString() : "—");
            });
            colLicense.setCellValueFactory(new PropertyValueFactory<>("license"));
            colDriverPhone.setCellValueFactory(data -> {
                Driver driver = data.getValue();
                Worker worker = driver.getWorkerId();
                String phone = worker != null && worker.getPhoneNumber() != null ? worker.getPhoneNumber() : "—";
                return new SimpleStringProperty(phone);
            });
            tableDrivers.setItems(driversList);
        }

        // Автомобили
        if (tableCars != null) {
            colCarId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colCarMarka.setCellValueFactory(new PropertyValueFactory<>("marka"));
            colCarModel.setCellValueFactory(new PropertyValueFactory<>("model"));
            colCarGosNumber.setCellValueFactory(new PropertyValueFactory<>("gosNumber"));
            colCarYear.setCellValueFactory(new PropertyValueFactory<>("yearOfManufacture"));
            colCarVin.setCellValueFactory(new PropertyValueFactory<>("vinNumber"));
            colCarCondition.setCellValueFactory(data -> {
                Car car = data.getValue();
                String condition = car.getConditionId() != null
                        ? car.getConditionId().getConditionName()
                        : "—";
                return new SimpleStringProperty(condition);
            });
            colCarCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
            colCarFuelNorm.setCellValueFactory(data -> {
                Double norm = data.getValue().getFuelNorm();
                return new SimpleStringProperty(norm != null ? norm + " л/100км" : "—");
            });
            tableCars.setItems(carsList);
        }

        // Техосмотры
        if (tableInspections != null) {
            colInspectionId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colInspectionCar.setCellValueFactory(data -> {
                Car car = data.getValue().getCarId();
                return new SimpleStringProperty(car != null ? car.getMarka() + " " + car.getGosNumber() : "—");
            });
            colInspectionDate.setCellValueFactory(data -> {
                java.time.LocalDate date = data.getValue().getInspectionDate();
                return new SimpleStringProperty(date != null ? date.toString() : "—");
            });
            colInspectionMechanic.setCellValueFactory(data -> {
                Worker mechanic = data.getValue().getMechanicId();
                return new SimpleStringProperty(mechanic != null ? getFullName(mechanic) : "—");
            });
            colInspectionResult.setCellValueFactory(new PropertyValueFactory<>("result"));
            tableInspections.setItems(inspectionsList);
        }

        // Ремонты
        if (tableRepairs != null) {
            colRepairId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colRepairCar.setCellValueFactory(data -> {
                Car car = data.getValue().getCarId();
                return new SimpleStringProperty(car != null ? car.toString() : "—");
            });
            colRepairDate.setCellValueFactory(data -> {
                java.time.LocalDate date = data.getValue().getRepairDate();
                return new SimpleStringProperty(date != null ? date.toString() : "—");
            });

            colRepairMechanic.setCellValueFactory(data -> {
                Worker mechanic = data.getValue().getMechanicId();
                return new SimpleStringProperty(mechanic != null ? getFullName(mechanic) : "—");
            });
            colRepairResult.setCellValueFactory(new PropertyValueFactory<>("result"));
            tableRepairs.setItems(repairsList);
        }

        // Выезды
        if (tableTrips != null) {
            colTripId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colTripDriver.setCellValueFactory(data -> {
                Driver driver = data.getValue().getDriverId();
                Worker worker = driver != null ? driver.getWorkerId() : null;
                return new SimpleStringProperty(worker != null ? getFullName(worker) : "—");
            });
            colTripCar.setCellValueFactory(data -> {
                Car car = data.getValue().getCarId();
                return new SimpleStringProperty(car != null ? car.toString() : "—");
            });
            colTripStartPoint.setCellValueFactory(data -> {
                Address address = data.getValue().getStartPoint();
                return new SimpleStringProperty(address != null ? address.toString() : "—");
            });
            colTripEndPoint.setCellValueFactory(data -> {
                Address address = data.getValue().getEndPoint();
                return new SimpleStringProperty(address != null ? address.toString() : "—");
            });
            colTripStartDateTime.setCellValueFactory(data -> {
                java.time.LocalDateTime dateTime = data.getValue().getStartDatetime();
                return new SimpleStringProperty(dateTime != null ? dateTime.toString() : "—");
            });
            colTripEndDateTime.setCellValueFactory(data -> {
                java.time.LocalDateTime dateTime = data.getValue().getEndDatetime();
                return new SimpleStringProperty(dateTime != null ? dateTime.toString() : "—");
            });
            colTripMileage.setCellValueFactory(data -> {
                Trip trip = data.getValue();
                if (trip.getMileageBefore() != null && trip.getMileageAfter() != null) {
                    return new SimpleStringProperty(String.valueOf(trip.getMileageAfter() - trip.getMileageBefore()));
                }
                return new SimpleStringProperty("—");
            });
            colTripFuel.setCellValueFactory(data -> {
                Trip trip = data.getValue();
                if (trip.getFuelBefore() != null && trip.getFuelAfter() != null) {
                    return new SimpleStringProperty(trip.getFuelBefore() + " / " + trip.getFuelAfter());
                }
                return new SimpleStringProperty("—");
            });
            tableTrips.setItems(tripsList);
        }

        // Адреса
        if (tableAddresses != null) {
            ObservableList<TableColumn<Address, ?>> cols = tableAddresses.getColumns();
            if (cols.size() >= 4) {
                cols.get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
                cols.get(1).setCellValueFactory(new PropertyValueFactory<>("town"));
                cols.get(2).setCellValueFactory(new PropertyValueFactory<>("street"));
                cols.get(3).setCellValueFactory(new PropertyValueFactory<>("house"));
            }
            tableAddresses.setItems(addressesList);
        }
    }

    // Вспомогательные методы
    private String getFullName(Object obj) {
        if (obj == null) return "-";
        if (obj instanceof Client c) {
            String fio = (c.getFamilya() != null ? c.getFamilya() : "") + " " +
                    (c.getName() != null ? c.getName() : "");
            if (c.getLastName() != null && !c.getLastName().isBlank())
                fio += " " + c.getLastName();
            return fio.trim();
        }
        if (obj instanceof Worker w) {
            String fio = (w.getFamilya() != null ? w.getFamilya() : "") + " " +
                    (w.getName() != null ? w.getName() : "");
            if (w.getLastName() != null && !w.getLastName().isBlank())
                fio += " " + w.getLastName();
            return fio.trim();
        }
        return "-";
    }

    private String getClientName(Client c) { return c != null ? c.toString() : "-"; }

    // ===================== Кнопки =====================
    private void setupButtonHandlers() {
        // Заявки
        if (btnCreateRequest != null)
            btnCreateRequest.setOnAction(e -> openForm("/ru/kpr/kyrs/RequestForm.fxml",
                    "Новая заявка", new ClientRequestFormController(requestDao, clientDao, addressDao), null));

        if (btnEditRequest != null)
            btnEditRequest.setOnAction(e -> editItem(tableRequests, "/ru/kpr/kyrs/RequestForm.fxml",
                    "Редактирование заявки", new ClientRequestFormController(requestDao, clientDao, addressDao)));
        if (btnDeleteRequest != null)
            btnDeleteRequest.setOnAction(e -> deleteItem(tableRequests, requestDao));

        // Клиенты
        if (btnCreateClient != null)
            btnCreateClient.setOnAction(e -> openForm("/ru/kpr/kyrs/ClientForm.fxml",
                    "Новый клиент", new ClientFormController(clientDao), null));
        if (btnEditClient != null)
            btnEditClient.setOnAction(e -> editItem(tableClients, "/ru/kpr/kyrs/ClientForm.fxml",
                    "Редактирование клиента", new ClientFormController(clientDao)));
        if (btnDeleteClient != null)
            btnDeleteClient.setOnAction(e -> deleteItem(tableClients, clientDao));

        // Сотрудники
        if (btnCreateWorker != null)
            btnCreateWorker.setOnAction(e -> openForm("/ru/kpr/kyrs/WorkerForm.fxml",
                    "Новый сотрудник", new WorkerFormController(workerDao), null));
        if (btnEditWorker != null)
            btnEditWorker.setOnAction(e -> editItem(tableWorkers, "/ru/kpr/kyrs/WorkerForm.fxml",
                    "Редактирование сотрудника", new WorkerFormController(workerDao)));
        if (btnDeleteWorker != null)
            btnDeleteWorker.setOnAction(e -> deleteItem(tableWorkers, workerDao));

        // Водители
        if (btnCreateDriver != null)
            btnCreateDriver.setOnAction(e -> openForm("/ru/kpr/kyrs/DriverForm.fxml",
                    "Новый водитель", new DriverFormController(driverDao, workerDao), null));
        if (btnEditDriver != null)
            btnEditDriver.setOnAction(e -> editItem(tableDrivers, "/ru/kpr/kyrs/DriverForm.fxml",
                    "Редактирование водителя", new DriverFormController(driverDao, workerDao)));
        if (btnDeleteDriver != null)
            btnDeleteDriver.setOnAction(e -> deleteItem(tableDrivers, driverDao));
        // Машины
        if (btnCreateCar != null)
            btnCreateCar.setOnAction(e -> openForm("/ru/kpr/kyrs/CarForm.fxml",
                    "Новая машина", new CarFormController(carDao), null));
        if (btnEditCar != null)
            btnEditCar.setOnAction(e -> editItem(tableCars, "/ru/kpr/kyrs/CarForm.fxml",
                    "Редактирование машины", new CarFormController(carDao)));
        if (btnDeleteCar != null)
            btnDeleteCar.setOnAction(e -> deleteItem(tableCars, carDao));

        // Техосмотры
        if (btnCreateInspection != null)
            btnCreateInspection.setOnAction(e -> openForm("/ru/kpr/kyrs/InspectionForm.fxml",
                    "Новый техосмотр", new TechnicalInspectionFormController(inspectionDao, carDao, workerDao), null));
        if (btnEditInspection != null)
            btnEditInspection.setOnAction(e -> editItem(tableInspections, "/ru/kpr/kyrs/TechnicalInspectionForm.fxml",
                    "Редактирование техосмотра", new TechnicalInspectionFormController(inspectionDao, carDao, workerDao)));
        if (btnDeleteInspection != null)
            btnDeleteInspection.setOnAction(e -> deleteItem(tableInspections, inspectionDao));

        // Ремонты
        if (btnCreateRepair != null)
            btnCreateRepair.setOnAction(e -> openForm("/ru/kpr/kyrs/RepairForm.fxml",
                    "Новый ремонт", new RepairFormController(repairDao, carDao, workerDao), null));
        if (btnEditRepair != null)
            btnEditRepair.setOnAction(e -> editItem(tableRepairs, "/ru/kpr/kyrs/RepairForm.fxml",
                    "Редактирование ремонта", new RepairFormController(repairDao, carDao, workerDao)));
        if (btnDeleteRepair != null)
            btnDeleteRepair.setOnAction(e -> deleteItem(tableRepairs, repairDao));

        // Выезды
        if (btnCreateTrip != null)
            btnCreateTrip.setOnAction(e -> openForm("/ru/kpr/kyrs/TripForm.fxml",
                    "Новый выезд", new TripFormController(tripDao, driverDao, carDao, addressDao), null));
        if (btnEditTrip != null)
            btnEditTrip.setOnAction(e -> editItem(tableTrips, "/ru/kpr/kyrs/TripForm.fxml",
                    "Редактирование выезда", new TripFormController(tripDao, driverDao, carDao, addressDao)));
        if (btnDeleteTrip != null)
            btnDeleteTrip.setOnAction(e -> deleteItem(tableTrips, tripDao));
        if (btnCompleteTrip != null)
            btnCompleteTrip.setOnAction(e -> completeTrip());

        // Адреса
        if (btnCreateAddress != null)
            btnCreateAddress.setOnAction(e -> openForm("/ru/kpr/kyrs/AddressForm.fxml",
                    "Новый адрес", new AddressFormController(addressDao), null));
        if (btnEditAddress != null)
            btnEditAddress.setOnAction(e -> editItem(tableAddresses, "/ru/kpr/kyrs/AddressForm.fxml",
                    "Редактирование адреса", new AddressFormController(addressDao)));
        if (btnDeleteAddress != null)
            btnDeleteAddress.setOnAction(e -> deleteItem(tableAddresses, addressDao));
    }

    private void openForm(String fxmlPath, String title, Object controller, Object entity) {
        try {
            ResourceBundle bundle = LocaleManager.getBundle();
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath), bundle);
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            if (entity != null) {
                controller.getClass().getMethod("setForm", entity.getClass()).invoke(controller, entity);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            refreshAllTables();
        } catch (Exception e) {
            logger.error("Ошибка открытия формы", e);
        }
    }

    private void editItem(TableView<?> table, String fxmlPath, String title, Object controller) {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Внимание", "Сначала выберите запись!", Alert.AlertType.WARNING);
            return;
        }
        try {
            ResourceBundle bundle = LocaleManager.getBundle();
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlPath), bundle);
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            if (selected != null) {
                controller.getClass().getMethod("setForm", selected.getClass()).invoke(controller, selected);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            refreshAllTables();
        } catch (Exception e) {
            logger.error("Ошибка открытия формы", e);
        }
    }

    private void deleteItem(TableView<?> table, Object dao) {
        Object selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Внимание", "Выберите запись!", Alert.AlertType.WARNING);
            return;
        }
        if (new Alert(Alert.AlertType.CONFIRMATION, "Удалить запись?").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Long id = (Long) selected.getClass().getMethod("getId").invoke(selected);
                if (dao instanceof ClientDaoInt d) d.delete(id);
                else if (dao instanceof ClientRequestDaoInt d) d.delete(id);
                else if (dao instanceof WorkerDaoInt d) d.delete(id);
                else if (dao instanceof DriverDaoInt d) d.delete(id);
                else if (dao instanceof CarDaoInt d) d.delete(id);
                else if (dao instanceof TechnicalInspectionDaoInt d) d.delete(id);
                else if (dao instanceof RepairDaoInt d) d.delete(id);
                else if (dao instanceof TripDaoInt d) d.delete(id);
                else if (dao instanceof AddressDaoInt d) d.delete(id);
                refreshAllTables();
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось удалить запись", Alert.AlertType.ERROR);
            }
        }
    }

    private void completeTrip() {
        Trip selected = tableTrips.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Внимание", "Выберите выезд!", Alert.AlertType.WARNING);
            return;
        }
        if (selected.getEndDatetime() != null) {
            showAlert("Внимание", "Выезд уже завершён!", Alert.AlertType.WARNING);
            return;
        }
        selected.setEndDatetime(java.time.LocalDateTime.now());
        try {
            tripDao.update(selected);
            refreshAllTables();
            showAlert("Успех", "Выезд завершён", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Ошибка", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void refreshAllTables() {
        loadAllData();
    }

    private void loadAllData() {
        try {
            clientsList.setAll(clientDao.getAll());
            requestsList.setAll(requestDao.getAll());
            workersList.setAll(workerDao.getAll());
            driversList.setAll(driverDao.getAll());
            carsList.setAll(carDao.getAll());
            inspectionsList.setAll(inspectionDao.getAll());
            repairsList.setAll(repairDao.getAll());
            tripsList.setAll(tripDao.getAll());
            addressesList.setAll(addressDao.getAll());
        } catch (Exception e) {
            logger.error("Ошибка загрузки данных", e);
        }
    }

    private void setupLanguageButtons() {
        if (btnLangRu != null) btnLangRu.setOnAction(e -> changeLanguage(new Locale("ru")));
        if (btnLangEn != null) btnLangEn.setOnAction(e -> changeLanguage(Locale.ENGLISH));
        if (btnLangDe != null) btnLangDe.setOnAction(e -> changeLanguage(Locale.GERMAN));
    }

    private void changeLanguage(Locale locale) {
        LocaleManager.setLocale(locale);
        Stage stage = (Stage) btnCreateClient.getScene().getWindow();
        new MainApplication().loadMainWindow(stage);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Поиск
    private void setupSearch() {
        // Клиенты
        setupSearchField(searchClients, tableClients, clientsList,
                (item, q) -> getFullName(item).toLowerCase().contains(q));
        // Заявки по имени клиента
        setupSearchField(searchRequests, tableRequests, requestsList, (item, q) -> {
            Client c = item.getClientId();
            return c != null && getFullName(c).toLowerCase().contains(q);
        });
        // Сотрудники
        setupSearchField(searchWorkers, tableWorkers, workersList,
                (item, q) -> getFullName(item).toLowerCase().contains(q));
        // Водители по ФИО сотрудника и номеру прав
        setupSearchField(searchDrivers, tableDrivers, driversList, (item, q) -> {
            Worker w = item.getWorkerId();
            String fio = w != null ? getFullName(w).toLowerCase() : "";
            String lic = item.getLicense() != null ? item.getLicense().toLowerCase() : "";
            return fio.contains(q) || lic.contains(q);
        });

        // Машины по марке, модели, госномеру
        setupSearchField(searchCars, tableCars, carsList, (item, q) -> {
            String marka = item.getMarka() != null ? item.getMarka().toLowerCase() : "";
            String model = item.getModel() != null ? item.getModel().toLowerCase() : "";
            String gos = item.getGosNumber() != null ? item.getGosNumber().toLowerCase() : "";
            return marka.contains(q) || model.contains(q) || gos.contains(q);
        });
        // Техосмотры по госномеру машины
        setupSearchField(searchInspections, tableInspections, inspectionsList, (item, q) -> {
            Car car = item.getCarId();
            String gos = car != null && car.getGosNumber() != null ? car.getGosNumber().toLowerCase() : "";
            return gos.contains(q);
        });
        // Ремонты по госномеру машины
        setupSearchField(searchRepairs, tableRepairs, repairsList, (item, q) -> {
            Car car = item.getCarId();
            String gos = car != null && car.getGosNumber() != null ? car.getGosNumber().toLowerCase() : "";
            return gos.contains(q);
        });
        // Выезды по ФИО водителя и госномеру машиныost
        setupSearchField(searchTrips, tableTrips, tripsList, (item, q) -> {
            Driver d = item.getDriverId();
            Worker w = d != null ? d.getWorkerId() : null;
            String fio = w != null ? getFullName(w).toLowerCase() : "";
            Car car = item.getCarId();
            String gos = car != null && car.getGosNumber() != null ? car.getGosNumber().toLowerCase() : "";
            return fio.contains(q) || gos.contains(q);
        });
        // Адреса по городу и улице
        setupSearchField(searchAddresses, tableAddresses, addressesList, (item, q) -> {
            String town = item.getTown() != null ? item.getTown().toLowerCase() : "";
            String street = item.getStreet() != null ? item.getStreet().toLowerCase() : "";
            return town.contains(q) || street.contains(q);
        });
    }

    private <T> void setupSearchField(TextField field, TableView<T> table, ObservableList<T> list, SearchMatcher<T> matcher) {
        if (field == null || table == null) return;
        FilteredList<T> filtered = new FilteredList<>(list, p -> true);
        field.textProperty().addListener((obs, old, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase().trim();
            filtered.setPredicate(item -> q.isEmpty() || matcher.matches(item, q));
        });
        table.setItems(filtered);
    }

    @FunctionalInterface
    private interface SearchMatcher<T> {
        boolean matches(T item, String query);
    }
    // Строка отчёта по топливу
    private static class FuelReportRow {
        String car;
        int mileage;
        int fuelUsed;
        double fuelNorm;
        double overflow;

        FuelReportRow(String car, int mileage, int fuelUsed, double fuelNorm) {
            this.car = car;
            this.mileage = mileage;
            this.fuelUsed = fuelUsed;
            this.fuelNorm = fuelNorm;
            // перерасход: факт минус норма (отрицательное = экономия)
            this.overflow = fuelUsed - fuelNorm;
        }
    }

    // Строка отчёта по водителям
    private static class DriverReportRow {
        String driver;
        int trips;
        int mileage;
        int fuel;

        DriverReportRow(String driver, int trips, int mileage, int fuel) {
            this.driver = driver;
            this.trips = trips;
            this.mileage = mileage;
            this.fuel = fuel;
        }
    }
    private void setupReports() {
        // Настройка колонок отчёта по топливу
        colReportCar.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().car));
        colReportMileage.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().mileage)));
        colReportFuelUsed.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().fuelUsed)));
        colReportFuelNorm.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().fuelNorm)));
        colReportOverflow.setCellValueFactory(data -> {
            double val = data.getValue().overflow;
            String text = String.format("%.1f", val);
            return new SimpleStringProperty(val > 0 ? ("!! " + text) : text);
        });

        // Строки с перерасходом красим красным
        tableFuelReport.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(FuelReportRow row, boolean empty) {
                super.updateItem(row, empty);
                if (!empty && row != null && row.overflow > 0) {
                    setStyle("-fx-background-color: #ffe0e0;");
                } else {
                    setStyle("");
                }
            }
        });

        // Настройка колонок отчёта по водителям
        colDriverReportName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().driver));
        colDriverReportTrips.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().trips)));
        colDriverReportMileage.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().mileage)));
        colDriverReportFuel.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().fuel)));

        // Кнопки
        btnBuildReport.setOnAction(e -> buildFuelReport());
        btnBuildDriverReport.setOnAction(e -> buildDriverReport());
    }

    private void buildFuelReport() {
        if (reportDateFrom.getValue() == null || reportDateTo.getValue() == null) {
            showAlert("Внимание", "Укажите период!", Alert.AlertType.WARNING);
            return;
        }

        java.time.LocalDate from = reportDateFrom.getValue();
        java.time.LocalDate to = reportDateTo.getValue();

        // Группируем выезды по машине
        java.util.Map<Long, FuelReportRow> map = new java.util.LinkedHashMap<>();

        for (Trip trip : tripsList) {
            // Фильтр по периоду
            if (trip.getStartDatetime() == null) continue;
            java.time.LocalDate tripDate = trip.getStartDatetime().toLocalDate();
            if (tripDate.isBefore(from) || tripDate.isAfter(to)) continue;
            // Только завершённые выезды
            if (trip.getMileageBefore() == null || trip.getMileageAfter() == null) continue;
            if (trip.getFuelBefore() == null || trip.getFuelAfter() == null) continue;

            Car car = trip.getCarId();
            if (car == null) continue;

            int mileage = trip.getMileageAfter() - trip.getMileageBefore();
            int fuelUsed = trip.getFuelBefore() - trip.getFuelAfter(); // сколько сожгли

            if (map.containsKey(car.getId())) {
                FuelReportRow row = map.get(car.getId());
                row.mileage += mileage;
                row.fuelUsed += fuelUsed;
                // пересчитываем норму и перерасход
                double normPer100 = car.getFuelNorm() != null ? car.getFuelNorm() : 0;
                row.fuelNorm = normPer100 * row.mileage / 100.0;
                row.overflow = row.fuelUsed - row.fuelNorm;
            } else {
                double normPer100 = car.getFuelNorm() != null ? car.getFuelNorm() : 0;
                double normTotal = normPer100 * mileage / 100.0;
                String carName = car.getMarka() + " " + car.getModel() + " (" + car.getGosNumber() + ")";
                map.put(car.getId(), new FuelReportRow(carName, mileage, fuelUsed, normTotal));
            }
        }

        tableFuelReport.setItems(FXCollections.observableArrayList(map.values()));
        logger.info("Отчёт по топливу сформирован: {} строк", map.size());
    }

    private void buildDriverReport() {
        if (reportDriverDateFrom.getValue() == null || reportDriverDateTo.getValue() == null) {
            showAlert("Внимание", "Укажите период!", Alert.AlertType.WARNING);
            return;
        }

        java.time.LocalDate from = reportDriverDateFrom.getValue();
        java.time.LocalDate to = reportDriverDateTo.getValue();

        java.util.Map<Long, DriverReportRow> map = new java.util.LinkedHashMap<>();

        for (Trip trip : tripsList) {
            if (trip.getStartDatetime() == null) continue;
            java.time.LocalDate tripDate = trip.getStartDatetime().toLocalDate();
            if (tripDate.isBefore(from) || tripDate.isAfter(to)) continue;
            if (trip.getMileageBefore() == null || trip.getMileageAfter() == null) continue;

            Driver d = trip.getDriverId();
            if (d == null || d.getWorkerId() == null) continue;

            Worker w = d.getWorkerId();
            int mileage = trip.getMileageAfter() - trip.getMileageBefore();
            int fuel = (trip.getFuelBefore() != null && trip.getFuelAfter() != null)
                    ? trip.getFuelBefore() - trip.getFuelAfter() : 0;

            if (map.containsKey(d.getId())) {
                DriverReportRow row = map.get(d.getId());
                row.trips++;
                row.mileage += mileage;
                row.fuel += fuel;
            } else {
                String name = w.getFamilya() + " " + w.getName();
                map.put(d.getId(), new DriverReportRow(name, 1, mileage, fuel));
            }
        }

        tableDriverReport.setItems(FXCollections.observableArrayList(map.values()));
        logger.info("Отчёт по водителям сформирован: {} строк", map.size());
    }
}