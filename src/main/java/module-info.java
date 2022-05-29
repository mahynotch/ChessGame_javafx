module com.sustech.chess {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires javafx.media;
    requires org.json;

    exports com.sustech.chess;

    opens com.sustech.chess to javafx.fxml;
}