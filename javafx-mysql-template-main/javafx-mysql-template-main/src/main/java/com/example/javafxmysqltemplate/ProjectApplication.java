package com.example.javafxmysqltemplate;

import com.example.database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectApplication extends Application {
    private static final String CONFIG_FILE_PATH = "config.properties";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ProjectApplication.class.getResource("welcomePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Inhouse database");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Database.initialize(CONFIG_FILE_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize database", e);
        }
        launch();
    }
}