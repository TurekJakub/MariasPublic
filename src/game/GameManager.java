/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import shared.Card;

/**
 *
 * @author jakub
 */
public class GameManager {

    private final String[] VALUES_OF_CARDS;
    private final String[] COLORS_OF_CARDS;
    private final LinkedList<Card> CARDSET;
    private Random random;

    public GameManager() {                              // Konstruktor registru karet - vytvoří registr všech karet ve hře 
        VALUES_OF_CARDS = new String[]{"7", "8", "9", "1", "B", "T", "K", "A"};
        COLORS_OF_CARDS = new String[]{"L", "Z", "K", "S"};
        CARDSET = new LinkedList<>();

        for (String COLORS_OF_CARDS1 : COLORS_OF_CARDS) {
            for (String VALUES_OF_CARDS1 : VALUES_OF_CARDS) {
                CARDSET.add(new Card(VALUES_OF_CARDS1, COLORS_OF_CARDS1));
            }
        }

        shuffleCard();
    }

    private void shuffleCard() { // Náhodně promíchá výchozí seznam karet.
        random = new Random();
        Card c;
        int j;
        for (int i = CARDSET.size(); i > 0; i--) {
            j = random.nextInt(i);
            c = CARDSET.get(j);
            CARDSET.set(j, CARDSET.get(i - 1));
            CARDSET.set(i - 1, c);

        }

    }

    public LinkedList<Card> getPlayersCardsSet(int playerindex) { // Vrátí karyty rozdané danému hráči.
        LinkedList<Card> cards = new LinkedList<>();
        for (int i = 0; i < 8; i++) {

            cards.add(CARDSET.get(playerindex + i * 4));

        }
        return cards;
    }

    public int getWinerOfRound(Card[] playedCards, String trumpColor) { // Vrátí inddex vítězného hráče
        if (containsTrump(trumpColor, playedCards)) {

            return indexOfHigestValue(trumpCardsFilter(playedCards, trumpColor));

        }

        return indexOfHigestValue(playedCards);

    }

    private boolean containsTrump(String trumpColor, Card[] playedCards) { // Vrátí zda byly v daném kole zahrány trumfové karty
        boolean b;
        for (int i = 1; i < playedCards.length; i++) {
            b = playedCards[i].getColor().equals(trumpColor);
            if (b) {
                return b;
            }

        }
        return false;
    }

    private Card[] trumpCardsFilter(Card[] cards, String trumpColor) { // Vrátí kopii vstupního pole, ze které jsou odstraněny netrumfové karty.
        Card[] c = cards.clone();
        for (int i = 1; i < c.length; i++) {
            if (!c[i].getColor().equals(trumpColor)) {
                c[i] = null;
            }

        }
        return c;
    }

    public int getRoundPoints(Card[] cards) { // Vrátí počet bodů získaných vítězným hráčem za dané kolo.
        int points = 0;
        for (int i = 1; i < cards.length; i++) {
            if (cards[i].getValue().equals("1") || cards[i].getValue().equals("A")) {
                points = points + 10;

            }

        }
        return points;
    }

    private int indexOfHigestValue(Card[] cards) { // vrátí index karty s nejvyší hodnotou v poli.

        int index = 0;
        String value = "";
        for (int i = 1; i < cards.length; i++) {
            if (cards[i] != null) {
                if (Arrays.asList(VALUES_OF_CARDS).indexOf(cards[i].getValue()) > Arrays.asList(VALUES_OF_CARDS).indexOf(value)) {
                    index = i;
                    value = cards[i].getValue();
                }
            }
        }
        return index;
    }
      public int getGameWinner(Player players[]) {
        int index = -1;
        int max = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getPoints() > max) {
                index = i;
                max = players[i].getPoints();
            }
        }
        return index;
    }

}