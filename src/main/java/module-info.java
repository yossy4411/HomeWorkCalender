module com.okayu.homework {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.okayu.homework to javafx.fxml;
    exports com.okayu.homework;
}