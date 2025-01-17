/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.marias.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.marias.shared.Card;
import com.example.marias.shared.CardManager;
import com.example.marias.shared.Receiver;
import com.example.marias.shared.Sender;
import javafx.application.Platform;


/**
 *
 * @author jakub
 */
public class Client extends Thread {

    private static Client client;
    private Socket clientSocket;
    private List<String> playersNames;
    private final String name;
    private final Sender sender;
    private final Receiver receiver;
    private final GameScreen gameScreen;
    private final ScreenManager screenManager;
    private Card lastCard;

    private Card playWith;
    private List<Card> cards;
    private String trumphColor;

    GameScreenController controller;

    private Client(String address, int port, String name, GameScreenController cont) {
        try {
            this.clientSocket = new Socket(address, port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.name = name;
        receiver = new Receiver();
        sender = new Sender();
        controller = cont;
        playersNames = new ArrayList<>();
        lastCard = null;
        gameScreen = new GameScreen();
        screenManager = new ScreenManager();

    }

    public static Client getClientInstance(String address, int port, String name, GameScreenController cont) {
        if (client == null) {
            client = new Client(address, port, name, cont);
        }
        return client;

    }

    public static Client getClientInstance() {
        return client;
    }

    public void sendData(Object Data) throws IOException {

        sender.SingelsendData(Data, clientSocket);

    }

    private void initialize() throws IOException {
        sender.SingelsendData(name, clientSocket);
        playersNames = (List<String>) receiver.read(clientSocket);
        System.out.println(playersNames.size());
        cards = (List<Card>) receiver.read(clientSocket);

        Platform.runLater(
                () -> {
                    controller.initializePlayersInfo(playersNames, playersNames.indexOf(name));
                    controller.dealCards(gameScreen.getImagesStreams(cards), cards);
                }
        );

    }

    @Override
    public void run() {
        try {
            initialize();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        int playedRound = 0;
        String roundColor = null;
        while (playedRound < 8) {

            try {
                Object data = receiver.read(clientSocket);
                if (data.getClass() == Class.forName("java.lang.Boolean")) {
                    if (trumphColor == null) {
                        Platform.runLater(
                                () -> {
                                    try {
                                        screenManager.showDialog("playWithDialog.fxml");
                                        screenManager.showDialog("trumphDialog.fxml");
                                    } catch (IOException ex) {
                                        screenManager.showExceptio(ex);
                                    }
                                }
                        );
                    }
                    else{
                        controller.activateAll();
                    }
                  
                }
                if (data.getClass() == Class.forName("com.example.marias.shared.Card")) {
                    Card c = (Card) data;
                    if (Arrays.asList(CardManager.COLORS_OF_CARDS).contains(c.getColor())) {
                        lastCard = c;
                        if (roundColor == null) {
                            roundColor = c.getColor();
                        }
                        Platform.runLater(
                                () -> {
                                    showPlayedCard(c);

                                }
                        );

                    } else {
                        Platform.runLater(
                                () -> {
                                    setPlayWith(c, playersNames.get(4));

                                });
                    }

                }
                if (data.getClass() == Class.forName("java.lang.Integer")) {
                    int points = (int) data;
                    String name = (String) receiver.read(clientSocket);
                    Platform.runLater(
                            () -> {
                                updatePoints(name, points);
                                controller.reset();
                                
                            }
                    );
                    roundColor = null;
                    playedRound++;
                }
                if (data.getClass() == Class.forName("java.lang.String")) {
                    if (trumphColor != null) {
                        Platform.runLater(
                                () -> {
                                    controller.updateSateLabel((String) data);
                                }
                        );
                    } else {
                        trumphColor = (String) data;
                        Platform.runLater(
                                () -> {
                                    controller.setTrumphColor(trumphColor);
                                }
                        );

                    }
                }

            } catch (ClassNotFoundException ex) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        screenManager.showExceptio(ex);
                    }
                });

            }

        }

    }

    public void activatePlayableCards(String roundColor, String lastColor, String lastValue, List<Card> cards) {
        boolean[] indexes = null;
        boolean oneActivate = false;

        if (lastColor.equals(roundColor)) {
            indexes = gameScreen.findPlayableCards(roundColor, lastValue, cards);
        }
        if (Arrays.asList(indexes).indexOf(true) != -1) {
            if (lastColor.equals(trumphColor)) {
                indexes = gameScreen.findPlayableCards(trumphColor, lastValue, cards);
            } else {
                indexes = gameScreen.findPlayableCards(trumphColor, "O", cards);
            }
        }
        if (!oneActivate) {
            Arrays.fill(indexes, true);
        }
        controller.activatePlayable(indexes);
    }

    public void waitUntilNextTurn() {
        controller.deactivateAllCards();
    }

    public void showPlayedCard(Card playedCard) {
        controller.showPlayedCard(gameScreen.getImageInputStream(playedCard));

    }

    public void updatePoints(String playerName, int points) {
        controller.updatePoints(playerName, points);
    }

    public void setPlayWith(Card playWith, String playerName) {
        controller.setPlayWith(playWith, playerName);
    }

}
