

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import model.Game;
import vista.ComUtils;
import vista.MenuClient;

/**
 * SD 2015
 * @author marc
 */

public class Client {
    
    public static void main(String[] args) throws IOException {
    
    boolean fi = false;
    String nomMaquina, str, respuesta;
    int numPort, value, mode;
    double topcard = 0.0;
    Game game = new Game();
    Scanner sc = new Scanner(System.in);
    int opcio;
    MenuClient menu = new MenuClient(sc);
    //mode = 1 means automatic, 0 manual.
    InetAddress maquinaServidora;
    Socket socket = null;
    ComUtils comUtils;
 
    //Podem tenir 2 o 3 args segons mode automatic o manual
    if (args.length != 4 && args.length != 6){
      System.out.println("Us: java Client -s <maquina_servidora> -p <port> [-a topcard]");
      System.exit(1);
    }
    
    //Si tenim -s i -p sera el mode manual
    if (args.length == 4) {
        System.out.println("MODE MANUAL ACTIVAT");
        mode = 1;
    }
    
    //Si tenim -s -p i -a sera el mode automatic
    else {
        System.out.println("MODE AUTOMATIC ACTIVAT");
        topcard = Double.parseDouble(args[5]);
        System.out.println("Maxpuntuacio: " + topcard);
        mode = 0;
    }
    
    if(mode == 1){
        opcio = menu.menuPrincipal();
    }
    
    else{
        opcio = 1;
    }
    
    if (opcio != 1) {
        System.out.println("Adeu!");
        System.exit(1);

    } else {
        
        try{
            nomMaquina = args[1];
            numPort    = Integer.parseInt(args[3]); 
            /* INICIO PARTIDA, COMUN MANUAL Y AUTOMATICO */
            
            /* Obtenim la IP de la maquina servidora */
            maquinaServidora = InetAddress.getByName(nomMaquina);
            System.out.println("IP: "+maquinaServidora + "IP 2: "+ nomMaquina + "" + numPort);
            /* Obrim una connexio amb el servidor */
            socket = new Socket(maquinaServidora, numPort);
            socket.setSoTimeout(10000);
            /* Obrim un flux d'entrada/sortida amb el servidor */
            comUtils = new ComUtils(socket, game); 

            /* Enviem STRT al Servidor */
            comUtils.writeCommand("STRT");
            
            /* Leemos la respuesta */
            respuesta = comUtils.readCommand();
            if(!"STBT".equals(respuesta)) comUtils.writeError("Syntax Error - STBT");
            respuesta += comUtils.readSP();
            
            /* Guardamos STBT value*/
            int bet = comUtils.read_int32();
            game.setBet(bet);
            respuesta += bet;

            /* test */
            System.out.println("He enviado STRT, y recibo: " + respuesta);
            
            /* Si recibo lo esperado */
            comUtils.writeCommand("DRAW");

            /* Recibo carta */
            comUtils.readGeneralCommandClient();

            /* INICIO MODO AUTOMATICO */
            if (mode == 0){
                System.out.println("Jugant automatic...");
                
                while(game.getScore() < topcard && !fi){
                    comUtils.writeCommand("DRAW");
                    comUtils.readGeneralCommandClient();
                    
                    if(game.getScore() > 7.5){
                        comUtils.readGeneralCommandClient();
                        fi = true;
                    }
                    
                    if(game.getScore() == 7.5){
                        comUtils.writeCommand("PASS");
                        comUtils.readGeneralCommandClient();
                        fi = true;
                    }
                }
                comUtils.writeCommand("PASS");
                comUtils.readGeneralCommandClient();
                socket.close();
            }

            /* INICIO MODO MANUAL */
            if (mode == 1){
                while (!fi){
                    opcio = menu.menuPartida();
                    switch(opcio){
                        case 1: //Draw
                            comUtils.writeCommand("DRAW");    
                            comUtils.readGeneralCommandClient();
                            
                            if(game.getScore() > 7.5){
                                System.out.println("M'he pasat de score");
                                comUtils.readGeneralCommandClient();
                                fi = true;
                            }
                            break;
                            
                        case 2: //Ante
                            int ante = menu.demanaAnte();
                            comUtils.writeCommand("ANTE");
                            comUtils.writeSP();
                            comUtils.write_int32(ante);
                            comUtils.writeCommand("DRAW");
                            comUtils.readGeneralCommandClient();
                            
                            if(game.getScore() > 7.5){
                                System.out.println("M'he pasat de score");
                                comUtils.readGeneralCommandClient();
                                fi = true;
                            }
                            break;
                            
                        case 3: //Pass
                            comUtils.writeCommand("PASS");
                            comUtils.readGeneralCommandClient();
                            fi = true;
                            break;
                    }
                }
            }
        }catch(Exception e){
            fi = true;
        }finally{
            System.out.println("Adeu!");
            try {
                if (socket != null && fi) {
                    socket.close();
                }
            }catch (IOException ex) {
                //Socket no existeix...
            }
        }
    }

    }
}