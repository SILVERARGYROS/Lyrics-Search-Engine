module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires lucene.queryparser;
    requires lucene.core;
    requires opencsv;

    opens com.example.FXControllers to javafx.fxml;
    exports com.example;
}
