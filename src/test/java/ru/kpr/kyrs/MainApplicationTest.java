package ru.kpr.kyrs;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.function.Try;
import org.testfx.framework.junit5.ApplicationTest;
import ru.kpr.kyrs.Dao.Classes.AddressDao;
import ru.kpr.kyrs.Dao.Classes.ClientDao;
import ru.kpr.kyrs.Pogo.Address;
import ru.kpr.kyrs.Pogo.Client;
import ru.kpr.kyrs.Utilites.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

class MainApplicationTest extends ApplicationTest {

    private final ClientDao clientDao = new ClientDao();
    private final AddressDao addressDao = new AddressDao();

    private final String testClientName = "ТестовоеИмя";
    private final String testClientFamilya = "ТестоваяФамилия";
    private final String testClientPhone = "+79991234567";

    private final String testTown = "ТестовыйГород";
    private final String testStreet = "ТестоваяУлица";
    private final String testHouse = "999";

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
                clientDao.getAll().stream()
                        .filter(cl -> testClientPhone.equals(cl.getPhoneNumber()))
                        .findFirst()
                        .ifPresent(cl -> clientDao.delete(cl.getId()));
            } catch (Exception ignored) {}
        }
        clientsToClean.clear();

        for (Address a : addressesToClean) {
            try {
                addressDao.getAll().stream()
                        .filter(addr -> testTown.equals(addr.getTown())
                                && testStreet.equals(addr.getStreet()))
                        .findFirst()
                        .ifPresent(addr -> addressDao.delete(addr.getId()));
            } catch (Exception ignored) {}
        }
        addressesToClean.clear();
    }

    // ===================== POSITIVE =====================

    @Test
    @DisplayName("Добавление клиента")
    void testAddClient() {
        try {
        clientsToClean.add(new Client(null, testClientName, testClientFamilya, null, testClientPhone, null));

        clickOn("#btnCreateClient");

        waitFor(2, TimeUnit.SECONDS,
                () -> lookup("#nameField").tryQuery().isPresent());

        clickOn("#nameField").write(testClientName);
        clickOn("#familyaField").write(testClientFamilya);
        clickOn("#phoneField").write(testClientPhone);

        clickOn("#btnSave");

        sleep(500);

        TableView<Client> table = lookup("#tableClients").queryAs(TableView.class);

        boolean found = table.getItems().stream()
                .anyMatch(c -> testClientPhone.equals(c.getPhoneNumber()));

        assertThat(found).isTrue();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Добавление адреса")
    void testAddAddress() {
        try {
            addressesToClean.add(new Address(null, testTown, testStreet, testHouse));

            clickOn("#btnCreateAddress");

            waitFor(5, TimeUnit.SECONDS,
                    () -> lookup("#townField").tryQuery().isPresent());

            clickOn("#townField").write(testTown);
            clickOn("#streetField").write(testStreet);
            clickOn("#houseField").write(testHouse);

            clickOn("#btnSave");

            sleep(500);

            TableView<Address> table = lookup("#tableAddresses").queryAs(TableView.class);

            boolean found = table.getItems().stream()
                    .anyMatch(a -> testTown.equals(a.getTown())
                            && testStreet.equals(a.getStreet())
                            && testHouse.equals(a.getHouse()));

            assertThat(found).isTrue();
        } catch (TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }


    // ===================== NEGATIVE =====================

    @Test
    @DisplayName("Кнопка сохранения клиента disabled при пустых полях")
    void testClientSaveButtonDisabledWhenIncomplete() {
        try {
            clickOn("#btnCreateClient");
            waitFor(5, TimeUnit.SECONDS,
                    () -> lookup("#nameField").tryQuery().isPresent());

            clickOn("#nameField").write("OnlyName");

            Button saveButton = lookup("#btnSave").queryButton();

            sleep(200);

            assertThat(saveButton.isDisabled()).isTrue();

            clickOn("#btnCancel");
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @DisplayName("Кнопка сохранения адреса disabled без дома")
    void testAddressSaveButtonDisabledWithoutHouse() {
        try {
        clickOn("#btnCreateAddress");

        waitFor(5, TimeUnit.SECONDS,
                () -> lookup("#townField").tryQuery().isPresent());

        clickOn("#townField").write("City");
        clickOn("#streetField").write("Street");

        Button saveButton = lookup("#btnSave").queryButton();

        sleep(200);

        assertThat(saveButton.isDisabled()).isTrue();

        clickOn("#btnCancel");
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}