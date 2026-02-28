module com.example.jurajcernickaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.jurajcernickaproject to javafx.fxml;
    exports com.jurajcernickaproject.pieces;
    opens com.jurajcernickaproject.pieces to javafx.fxml;
    exports com.jurajcernickaproject.move;
    opens com.jurajcernickaproject.move to javafx.fxml;
    exports com.jurajcernickaproject.exceptions;
    opens com.jurajcernickaproject.exceptions to javafx.fxml;
    exports com.jurajcernickaproject.gui;
    opens com.jurajcernickaproject.gui to javafx.fxml;
    exports com.jurajcernickaproject.launcher;
    opens com.jurajcernickaproject.launcher to javafx.fxml;
    exports com.jurajcernickaproject.gamelogic;
    opens com.jurajcernickaproject.gamelogic to javafx.fxml;
    exports com.jurajcernickaproject.stringifier;
    opens com.jurajcernickaproject.stringifier to javafx.fxml;
}