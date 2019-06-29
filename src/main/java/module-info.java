module YToolsFX {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.datatransfer;
    requires java.desktop;
    requires com.jfoenix;
    requires com.gluonhq.charm.glisten;

    opens com.yedhrab.ytoolsfx.controllers to javafx.fxml;

    exports com.yedhrab.ytoolsfx.applications;
//    exports com.yedhrab.ytoolsfx.controllers;
//    exports com.yedhrab.ytoolsfx.interfaces;
//    exports com.yedhrab.ytoolsfx.services;
}