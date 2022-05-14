module io.pal.spell {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.seleniumhq.selenium.api;
    requires org.seleniumhq.selenium.support;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.jsoup;
    requires io.github.bonigarcia.webdrivermanager;
    opens io.pal.spell to javafx.fxml;
    exports io.pal.spell ;
}