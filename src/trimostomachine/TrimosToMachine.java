/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trimostomachine;

import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author mats.andersson
 */
public class TrimosToMachine extends Application {
    
    MainWindowFXMLController controller;
    TrimosDevice trimosDevice;
    EventBus eventBus;

    
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindowFXMLDocument.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        
        controller = loader.<MainWindowFXMLController>getController();
        eventBus = ProjectEventBus.getInstance();
        trimosDevice = new TrimosDevice(eventBus);
        trimosDevice.startTrimosConnection();
        controller.setTrimosDevice(trimosDevice);

        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        trimosDevice.stopThreads();
    }
}
