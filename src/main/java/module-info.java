module com.okayu.homework {
    requires javafx.controls;
    requires javafx.fxml;
    requires one.cafebabe.businessCalendar4j;
    requires com.fasterxml.jackson.databind;
    requires annotations;

    opens com.okayu.homework to javafx.fxml;
    exports com.okayu.homework;
    exports com.okayu.homework.schedule;
    opens com.okayu.homework.schedule to javafx.fxml;
    exports com.okayu.homework.test;
    opens com.okayu.homework.test to javafx.fxml;
    exports com.okayu.homework.FXLib;
    opens com.okayu.homework.FXLib to javafx.fxml;
}