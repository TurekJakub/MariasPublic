/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.marias.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.marias.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jakub
 */
public class TrumphDialogController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    ChoiceBox trumphBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        trumphBox.getItems().addAll("Listy", "Kule", "Žaludy", "Srdce");
    }

    public void handelClickOk(MouseEvent e) throws IOException {
        String trumph = (String) trumphBox.getSelectionModel().getSelectedItem();
        Client.getClientInstance().sendData(trumph);
        Button b = (Button) e.getSource();
        Stage stage = (Stage) b.getScene().getWindow();
        stage.close();
    }

}
