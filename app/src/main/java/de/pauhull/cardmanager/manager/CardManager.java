package de.pauhull.cardmanager.manager;

import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CardManager {

    private static CardManager instance;

    private Card cardToEdit;
    private Card displayedCard;
    private LinkedList<Card> cards;

    private CardManager() {

        this.cards = new LinkedList<>();
    }

    public void readCards(File dir) {

        File file = new File(dir, "cards.json");

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            Gson gson = new Gson();

            for (int i = 0; i < array.size(); i++) {
                cards.add(gson.fromJson(array.get(i), Card.class));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCards(File dir) {

        File file = new File(dir, "cards.json");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();

                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String json = new Gson().toJson(cards);

        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.append(json);
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Card getLastAddedCard() {

        if (cards.size() == 0) {
            return null;
        }

        return cards.get(cards.size() - 1);
    }

    public List<Card> getCards() {
        return cards;
    }

    public static void create() {

        if (instance == null) {
            instance = new CardManager();
        }
    }

    public static CardManager getInstance() {
        return instance;
    }

    public Card getDisplayedCard() {
        return displayedCard;
    }

    public void setDisplayedCard(Card displayedCard) {
        this.displayedCard = displayedCard;
    }

    public Card getCardToEdit() {
        return cardToEdit;
    }

    public void setCardToEdit(Card cardToEdit) {
        this.cardToEdit = cardToEdit;
    }

    public static class Card {

        private transient Button button;
        private String cardName;
        private String customerName;
        private int color;
        private int barcodeFormat;
        private String barcodeValue;

        public Card(String cardName, String customerName, int color, int barcodeFormat, String barcodeValue) {

            this.cardName = cardName;
            this.customerName = customerName;
            this.color = color;
            this.barcodeFormat = barcodeFormat;
            this.barcodeValue = barcodeValue;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public int getBarcodeFormat() {
            return barcodeFormat;
        }

        public void setBarcodeFormat(int barcodeFormat) {
            this.barcodeFormat = barcodeFormat;
        }

        public String getBarcodeValue() {
            return barcodeValue;
        }

        public void setBarcodeValue(String barcodeValue) {
            this.barcodeValue = barcodeValue;
        }

        public Button getButton() {
            return button;
        }

        public void setButton(Button button) {
            this.button = button;
        }
    }

}
