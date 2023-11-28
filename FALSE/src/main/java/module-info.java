module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires lucene.queryparser;
    requires lucene.core;

    opens com.example to javafx.fxml;
    exports com.example;
}
