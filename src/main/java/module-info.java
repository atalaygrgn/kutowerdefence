module com.canja.kutowerdefence {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.canja.kutowerdefence to javafx.fxml;
    exports com.canja.kutowerdefence;
    exports com.canja.kutowerdefence.ui;
    opens com.canja.kutowerdefence.ui to javafx.fxml;
    exports com.canja.kutowerdefence.domain;
    opens com.canja.kutowerdefence.domain to javafx.fxml;
}