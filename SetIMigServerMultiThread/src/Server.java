

import controlador.ServidorThread;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import model.Game;

/**
 * SD 2015
 * @author marc
 */

public class Server {

    public static void main(String[] args) {        
        ServerSocket serverSocket = null;
        Socket socket = null;
        boolean fi = false;
        int port = 0;
    
        if (args.length != 6){
            System.out.println("Us: java Server -p <numPort> -b <starting_bet> -f <deckfile>");
            System.exit(1);
        }
        
        port = Integer.parseInt(args[1]);
        int stbt = Integer.parseInt(args[3]);
        String pathDeckfile = args[5];
        
        try{  
            serverSocket = new ServerSocket(port);

            //testeamos si hay file
            File file = new File(pathDeckfile);
            if(!file.isFile()){
                System.out.println("ERROR - " + pathDeckfile + "not found");
                System.exit(0);
            }
            
            System.out.println("Server preparat: "+port+"\n");
            while(true){
                System.out.println("Esperant client...\n");
                socket = serverSocket.accept();
                System.out.println("Nova conexio!\n");
                socket.setSoTimeout(600000);
                Game game = new Game(stbt);

                if (!game.omplirDeck(pathDeckfile)) {
                    System.out.println("ERROR! Cannot read deck.txt! Abort System.");
                    socket.close();
                    System.exit(0);
                }
                
                ServidorThread server = new ServidorThread(socket,game);
                server.start(); 
                
            }
        }catch(SocketTimeoutException e){
            System.out.println("Client timeOut");

        }catch (IOException ex) {
            System.out.println("ERR-Creant el socket\n");
            fi = true;        
        
        } finally{
            try {
                if(socket != null)
                    socket.close();
                if(serverSocket!=null)
                    serverSocket.close();
            } catch (IOException ex) {
                System.out.println("Socket no existeix, exit.\n");
            }
        }
    }
}
   
