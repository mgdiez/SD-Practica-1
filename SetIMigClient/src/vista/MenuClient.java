package vista;

import java.util.Scanner;

/**
 *
 * @author marc
 */
public class MenuClient {
    private Scanner sc;
    
    public MenuClient (Scanner sc){
        this.sc = sc;
    }
    
    public int menuPrincipal() {
        System.out.println("Benvingut al joc del 7.5.\nSelecciona l'opció:");
        System.out.println("1.Nova partida.");
        System.out.println("2.Exit.");
        return sc.nextInt();   
    }
    
    public int menuPartida() {
        System.out.println("1.Demana carta");
        System.out.println("2.Ante");
        System.out.println("3.Pasa\n");
        return sc.nextInt();
    }
    
    public int demanaAnte(){
        System.out.println("Introdueix la quantitat: ");
        return sc.nextInt();
    }
}
