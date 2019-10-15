/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trimostomachine;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

/**
 *
 * @author mats.andersson
 */
public class MainWindowFXMLController implements Initializable {
    
    @FXML
    private TextArea fromTrimosTextArea;
    
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


        
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
