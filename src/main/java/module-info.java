module com.canja.kutowerdefence {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.canja.kutowerdefence to javafx.fxml;
    exports com.canja.kutowerdefence;
}