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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author mats.andersson
 */
public class MainWindowFXMLController implements Initializable {

    @FXML
    private Label xLabel, zLabel;

    @FXML
    private TextField fanucToolNoTextField;

    @FXML
    private Button fanucSetToOneButton;

    @FXML
    private TextArea toolFileTextArea;

    @FXML
    private CheckBox fanucUseRadiusCheckBox, fanucSetRadiusToZeroCheckBox;

    @FXML
    private TabPane machineTabPane;

    @FXML
    private Tab fanucMachineTab;

    @FXML
    private TextField fanucExtraLengthOffset;

    @FXML
    private TextField fanucExtraRadiusOffset;

    private final EventBus eventBus = ProjectEventBus.getInstance();
    private TrimosDevice trimosDevice;
    private int fanucToolNo = 1;

    private enum SelectedTab {
        FANUC, UNKNOWN
    };
    private SelectedTab selectedTab;

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

    @FXML
    private void useRadiusCheckBoxChanged() {
        if (fanucUseRadiusCheckBox.isSelected()) {
            fanucSetRadiusToZeroCheckBox.setDisable(false);
        } else {
            fanucSetRadiusToZeroCheckBox.setDisable(true);
        }
    }

    @FXML
    private void fanucSetToOneButtonClicked() {
        fanucToolNo = 1;
        fanucToolNoTextField.setText(Integer.toString(fanucToolNo));
    }

    @FXML
    private void fanucIncToolNo() {
        fanucToolNo++;
        fanucToolNoTextField.setText(Integer.toString(fanucToolNo));
    }

    @FXML
    private void fanucDecToolNo() {
        fanucToolNo--;
        if (fanucToolNo <= 0) {
            fanucToolNo = 1;
        }
        fanucToolNoTextField.setText(Integer.toString(fanucToolNo));
    }

    @FXML
    private void fanucAddStartSection() {
        toolFileTextArea.appendText("%\nO555\n");
    }

    @FXML
    private void fanucAddEndSection() {
        toolFileTextArea.appendText("M30\n%\n");
    }

    @FXML
    private void machineTabChanged() {
        System.out.println("Machine tab changed");
        selectedTab = SelectedTab.UNKNOWN;
        if (machineTabPane.getSelectionModel().getSelectedItem().equals(fanucMachineTab)) {
            selectedTab = SelectedTab.FANUC;
        }
    }

    @FXML
    void clearTextArea() {
        toolFileTextArea.clear();
    }

    @FXML
    void removeLastReadLine() {
        toolFileTextArea.setText( removeLastLine(toolFileTextArea.getText()));
    }

    // Remove the last line from a string with lines delimited with \n and
    // a \n as the last character.
    private String removeLastLine(String textArea) {
        // First remove the last endline
        // Get the pos.
        int lastEndLinePos = textArea.lastIndexOf('\n');
        // Check that it exists.
        if (lastEndLinePos > 0) {
            textArea = textArea.substring(0, lastEndLinePos);

            // Now remove the text from the last endLine to the end.
            lastEndLinePos = textArea.lastIndexOf('\n');
            if (lastEndLinePos > 0) {
                textArea = textArea.substring(0, lastEndLinePos);
            } else {
                // No more endlines. Must be the first line so we empty the
                // textArea
                return "";
            }

            // And put a \n in at the end so everything is as it should be.
            textArea += '\n';

        }
        return textArea;
    }

    @Subscribe
    private void handlePositionMessage(PositionMessage message) {

        if (message != null) {
            xLabel.setText(String.format("%.3f", message.getXVal()));
            zLabel.setText(String.format("%.3f", message.getZVal()));
            switch (selectedTab) {
                case FANUC: {
                    double zVal = message.getZVal();
                    zVal += getFanucExtraLengthOffset();
                    toolFileTextArea.appendText(getFanucToolText(message.getXVal(), zVal));
                    setFanucToolNo(fanucToolNo + 1);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private double getFanucExtraLengthOffset() {
        String stringValue = fanucExtraLengthOffset.getText();
        stringValue = stringValue.replaceAll(",", ".");
        try {
            return Double.parseDouble(stringValue);
        } catch (Exception e) {
            return -99999.0;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventBus.register(this);
    }

    void setTrimosDevice(TrimosDevice trimosDevice) {
        this.trimosDevice = trimosDevice;
    }

    private String getFanucToolText(double xVal, double zVal) {
        String toolText = "G10 L10 P" + fanucToolNo
                + " R" + String.format("%.3f", zVal) + "\n";
        if (fanucUseRadiusCheckBox.isSelected()) {
            toolText += "G10 L10 P" + (fanucToolNo + 20);
            if (fanucSetRadiusToZeroCheckBox.isSelected()) {
                toolText += " R0\n";
            } else {
                toolText += " R" + String.format("%.3f", xVal) + "\n";
            }
        }
        return toolText;
    }

    private void setFanucToolNo(int toolNo) {
        fanucToolNo = toolNo;
        fanucToolNoTextField.setText(Integer.toString(fanucToolNo));
    }

}
