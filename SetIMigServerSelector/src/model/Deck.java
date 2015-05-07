package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marc
 */
public class Deck {
    
    private ArrayList<Card> deck;
    private int i;
    
    public Deck(){
        deck = new ArrayList<>();
        i = 0;
    }
    
    public Card getNextCard(){
        System.out.println(i);
        Card card = deck.get(i);
        i++;
        return card;
    }
    //Funcio per guardar a memoria totes les cartes del deckfile
public boolean omplirDeck(String path) throws FileNotFoundException {

        File file = new File(path);
        BufferedReader reader = null;
        if (file.isFile()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;

                while ((text = reader.readLine()) != null) {
                    System.out.println(text);
                    Card card = new Card(text);
                    deck.add(card);
                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                }
            }
            return true;
        } else {
            return false;
        }

    }
}
