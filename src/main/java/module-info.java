module ru.kpr.kyrs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires static lombok;

    opens ru.kpr.kyrs to javafx.fxml;
    opens ru.kpr.kyrs.Controllers to javafx.fxml;
    opens ru.kpr.kyrs.Pogo to javafx.base;

    exports ru.kpr.kyrs;
    exports ru.kpr.kyrs.Controllers;
    exports ru.kpr.kyrs.Pogo;
    exports ru.kpr.kyrs.Utilites;
}
