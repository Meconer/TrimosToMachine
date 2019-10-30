/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trimostomachine;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 *
 * @author mats.andersson
 */
public class MainWindowFXMLController implements Initializable {
    
    private final EventBus eventBus = ProjectEventBus.getInstance();

    
    @FXML
    private TextArea fromTrimosTextArea;
    private TrimosDevice trimosDevice;
    
    @FXML
    private void onMenuSettingsClicked() {
        Configuration.getConfiguration().showConfigurationDialog();
    }
    
    @FXML
    private void showAboutBox() {
        Alert aboutBox = new Alert(Alert.AlertType.INFORMATION);
        aboutBox.setHeaderText("Om");
        aboutBox.setContentText("Trimos To Machine\nVersion 0.1");
        aboutBox.showAndWait();
    }


    @Subscribe
    private void handleTrimosMessageEvent( TrimosMessageEvent trimosMEvent ) {
        Platform.runLater( () -> {
            String message = trimosMEvent.getStatusMessage();
            if ( message!=null ) {
                fromTrimosTextArea.setText(trimosMEvent.getStatusMessage());
            }
        });
    }

        
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventBus.register(this);
    }    

    void setTrimosDevice(TrimosDevice trimosDevice) {
        this.trimosDevice = trimosDevice;
    }
    
}
