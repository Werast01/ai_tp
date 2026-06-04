package ru.kpr.kyrs;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import ru.kpr.kyrs.Dao.Classes.AddressDao;
import ru.kpr.kyrs.Dao.Classes.ClientDao;
import ru.kpr.kyrs.Pogo.Address;
import ru.kpr.kyrs.Pogo.Client;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

public class MainApplicationTest extends ApplicationTest {

    private final ClientDao clientDao = new ClientDao();
    private final AddressDao addressDao = new AddressDao();

    private final String testClientName = "ТестовоеИмя";
    private final String testClientFamilya = "ТестоваяФамилия";
    private final String testClientPhone = "+79991234567";

    private final List<Client> clientsToClean = new ArrayList<>();
    private final List<Address> addressesToClean = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {

        String testUser = System.getProperty("test.db.user", "postgres");
        String testPass = System.getProperty("test.db.password", "postgres");

        try {
            DatabaseConnection.initConnection(testUser, testPass);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к тестовой БД", e);
        }

        MainApplication app = new MainApplication();
        app.loadMainWindow(stage);
    }

    @AfterEach
    void cleanUp() {

        for (Client c : clientsToClean) {
            try {
                clientDao.delete(c.getId());
            } catch (Exception ignored) {
            }
        }

        clientsToClean.clear();

        for (Address a : addressesToClean) {
            try {
                addressDao.delete(a.getId());
            } catch (Exception ignored) {
            }
        }

        addressesToClean.clear();
    }

    // ====================================================
    // POSITIVE
    // ====================================================

    @Test
    @DisplayName("Добавление клиента")
    void testAddClient() {

        try {

            clickOn("#btnCreateClient");

            waitFor(5, TimeUnit.SECONDS,
                    () -> lookup("#nameField").tryQuery().isPresent());

            clickOn("#nameField").write(testClientName);
            clickOn("#familyaField").write(testClientFamilya);
            clickOn("#phoneField").write(testClientPhone);

            clickOn("#btnSave");

            sleep(1000);

            TableView<Client> table =
                    lookup("#tableClients").queryAs(TableView.class);

            boolean found = table.getItems().stream()
                    .anyMatch(c ->
                            testClientPhone.equals(c.getPhoneNumber()));

            assertThat(found).isTrue();

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Добавление заказа")
    void testAddRequest() {

        try {

            Client client = new Client(
                    null,
                    testClientName,
                    testClientFamilya,
                    null,
                    testClientPhone,
                    null
            );

            clientDao.add(client);
            clientsToClean.add(client);

            Address start = new Address(
                    null,
                    "Москва",
                    "Ленина",
                    "1"
            );

            addressDao.add(start);
            addressesToClean.add(start);

            Address end = new Address(
                    null,
                    "Москва",
                    "Пушкина",
                    "10"
            );

            addressDao.add(end);
            addressesToClean.add(end);

            clickOn("#btnCreateRequest");

            waitFor(10, TimeUnit.SECONDS,
                    () -> lookup("#btnSave").tryQuery().isPresent());

            clickOn("#clientComboBox");
            clickOn(testClientFamilya);

            clickOn("#startPointComboBox");
            clickOn("Москва");

            clickOn("#endPointComboBox");
            clickOn("Пушкина");

            DatePicker datePicker =
                    lookup("#executionDatePicker")
                            .queryAs(DatePicker.class);

            interact(() ->
                    datePicker.setValue(LocalDate.now().plusDays(1)));

            clickOn("#btnSave");

            sleep(3000);

            TableView<?> table =
                    lookup("#tableRequests")
                            .queryAs(TableView.class);

            assertThat(table.getItems().size())
                    .isGreaterThan(0);

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    // ====================================================
    // NEGATIVE
    // ====================================================

    @Test
    @DisplayName("Кнопка сохранения клиента disabled при пустых полях")
    void testClientSaveButtonDisabledWhenIncomplete() {

        try {

            clickOn("#btnCreateClient");

            waitFor(5, TimeUnit.SECONDS,
                    () -> lookup("#nameField").tryQuery().isPresent());

            clickOn("#nameField").write("OnlyName");

            Button saveButton =
                    lookup("#btnSave").queryButton();

            sleep(200);

            assertThat(saveButton.isDisabled()).isTrue();

            clickOn("#btnCancel");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Кнопка сохранения заказа disabled без клиента")
    void testRequestSaveButtonDisabledWithoutClient() {

        try {

            clickOn("#btnCreateRequest");

            waitFor(5, TimeUnit.SECONDS,
                    () -> lookup("#btnSave").tryQuery().isPresent());

            Button saveButton =
                    lookup("#btnSave").queryButton();

            sleep(200);

            assertThat(saveButton.isDisabled()).isTrue();

            clickOn("#btnCancel");

        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
