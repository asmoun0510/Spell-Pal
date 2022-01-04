module beyn {
    requires javafx.controls;
    requires javafx.fxml;

    opens beyn to javafx.fxml;
    exports beyn;
}
