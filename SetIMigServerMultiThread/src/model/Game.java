package model;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * SD 2015
 * @author Marc
 */
public class Game {
    
    private double score;
    private int bet;
    private ArrayList<Card> cards; 
    private Deck deck;
    
    public Game(int bet) {
        this.score = 0.0;
        this.bet = bet;
        this.cards = new ArrayList();
        this.deck = new Deck();
    }
    public boolean omplirDeck(String deckfile) throws FileNotFoundException{
        
        return deck.omplirDeck(deckfile);
    }
    
    public void addScore(Card card) {
        this.score += card.getPuntuacio();
    }

    public void addBet(int ante) {
        this.bet += ante;
    }

    public boolean addCard(Card card) {
        boolean found = false;
        int i = 0;
        while(!found && i < cards.size()){
            if(cards.get(i) == card) found = true;
            else{
                i++;
            }
        }
        
        if(!found){
            addScore(card);
            this.cards.add(card);
            return true;
        }
        else{
            return false;
        }
    }
    
    
    public int getBet() {
        return this.bet;
    }
    
    public double getScore() {
        return this.score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    public void setBet(int bet) {
        this.bet = bet;
    }
    
    public Card getNextCard(){
        return deck.getNextCard();
    }
    
    //funcio per el servidor
    public int getNCards(){
        return cards.size();
    }
    
    //funcio per el servidor
    public Card getNCard(int i){
        return cards.get(i);
    }
}