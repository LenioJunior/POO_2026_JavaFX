package br.edu.ifsuldeminas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class App extends Application {

    private static ConfigurableApplicationContext context;
    private static Scene scene;

    @Override
    public void init() throws Exception {
        context = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 800, 540);
        stage.setScene(scene);
        stage.setTitle("Sistema de Biblioteca");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        context.close();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
