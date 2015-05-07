package controlador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Game;
import vista.ComUtils;

/**
 *
 * @author marc
 */
public class ServidorThread extends Thread implements Runnable {
    
    private Socket socket;
    private Game game;
    private ComUtils comUtils;
    private File log;
    
    public ServidorThread(Socket socket,Game game) throws IOException{
        this.socket = socket;
        this.game = game;
        comUtils = new ComUtils(this.socket, this.game);
    }
    
    @Override
    public void run(){
        boolean fi = false;
        
        try {
            comUtils.setLogFitxer();
        } catch (IOException ex) {
            Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (!fi) {
            System.out.println("\tEsperant...");
            
            try {
                comUtils.readGeneralCommandServidor();
            } catch(SocketTimeoutException e){
                System.out.println("Client TimeOUT");
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                fi = true;
                System.out.println("Socket tancat - " + Thread.currentThread().getName());
            }
            
            try {
                comUtils.closeFitxer();
            } catch (IOException ex) {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
            System.out.println("Fi partida nº" + Thread.currentThread().getName() );
            try {
            socket.close();
            } catch (IOException ex) {
            Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

