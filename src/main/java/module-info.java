module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.io;

    opens org.example to javafx.fxml;
    exports org.example;
}