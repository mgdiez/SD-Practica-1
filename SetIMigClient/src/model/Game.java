package model;

import java.util.ArrayList;

/**
 * SD 2015
 * @author Marc
 */
public class Game {
    
    private double score;
    private int bet;
    private ArrayList<Card> cards; 
    
    public Game() {
        this.score = 0.0;
        this.bet = 0;
        this.cards = new ArrayList();
    }
    
    public void addScore(Card card) {
        this.score += card.getPuntuacio();
    }

    public void addBet(int inc) {
        this.bet += inc;
    }

    public boolean addCard(Card card) {
        
        boolean found = false;
        int i = 0;
        while(!found && i < cards.size()){
            
            if(cards.get(i).getCard().equals(card.getCard())) found = true;
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
}