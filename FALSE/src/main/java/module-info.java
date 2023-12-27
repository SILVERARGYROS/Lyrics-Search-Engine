module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires lucene.queryparser;
    requires lucene.core;
    requires opencsv;
    requires org.jsoup;
    requires config;
    requires junit;
    requires hamcrest.core;
    requires org.json;

    opens com.example.FXControllers to javafx.fxml;
    exports com.example;
}
