module com.digitized {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens com.digitized.controller to javafx.fxml;
    opens com.digitized.model to javafx.base;
    exports com.digitized;
}