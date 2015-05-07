package model;

/**
 * SD 2015
 * @author Marc
 */

public class Card {
    
    private String card;
    private double puntuacio;
    
    public Card(String card) {
        this.card = card;
        
        switch (card.substring(0, 1)) {
            case "1":
                this.puntuacio = 1;
                break;
            case "2":
                this.puntuacio = 2;
                break;
            case "3":
                this.puntuacio = 3;
                break;
            case "4":
                this.puntuacio = 4;
                break;
            case "5":
                this.puntuacio = 5;
                break;
            case "6":
                this.puntuacio = 6;
                break;
            case "7":
                this.puntuacio = 7;
                break;
            
            default:
                this.puntuacio = 0.5;
                break;
        }
    }
    
    public double getPuntuacio() {
        return puntuacio;
    }
    
    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }


}