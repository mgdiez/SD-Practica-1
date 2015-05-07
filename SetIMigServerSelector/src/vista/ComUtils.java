package vista;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import model.Card;
import model.Game;

 
public class ComUtils
{
    
  /* Objectes per escriure i llegir dades */
    //Cambios respecto al MultiThread para el selector.
    private final Charset charset;
    private CharsetEncoder encoder;
    private CharsetDecoder decoder;
    private Game game;
    private SocketChannel socket;
    private ByteBuffer buffer;
    private int id;
    private File logFile;
    private FileWriter fw;
    private BufferedWriter bw;
    private String request;


  public ComUtils(SocketChannel socket, Game game, int nClient, ByteBuffer buffer) throws IOException
  {
    this.game = game;
    this.charset = Charset.forName("ISO-8859-1");
    this.decoder = charset.newDecoder();
    this.encoder = charset.newEncoder();
    this.socket = socket;
    this.buffer = buffer;
    this.id = nClient;
    this.request = "";
  }
 
    public void setLogFitxer() throws IOException{      
       fw = new FileWriter("ServerGame-"+id+".log");
       bw = new BufferedWriter(fw);
       
    } 
  
    public void closeFitxer() throws IOException {
        bw.close();
    }
 
  /* Escriure un enter de 32 bits */
  public void write_int32(int number) throws IOException
  {
    byte bytes[]=new byte[4];
    int32ToBytes(number,bytes,"be");
    socket.write(ByteBuffer.wrap(bytes));
  }
  
  /* Passar d'enters a bytes */
  private int int32ToBytes(int number,byte bytes[], String endianess)
  {
    if("be".equals(endianess.toLowerCase()))
    {
      bytes[0] = (byte)((number >> 24) & 0xFF);
      bytes[1] = (byte)((number >> 16) & 0xFF);
      bytes[2] = (byte)((number >> 8) & 0xFF);
      bytes[3] = (byte)(number & 0xFF);
    }
    else
    {
      bytes[0] = (byte)(number & 0xFF);
      bytes[1] = (byte)((number >> 8) & 0xFF);
      bytes[2] = (byte)((number >> 16) & 0xFF);
      bytes[3] = (byte)((number >> 24) & 0xFF);
    }
    return 4;
  }
 
  /* Passar de bytes a enters */
  private int bytesToInt32(byte bytes[], String endianess)
  {
    int number;
 
    if("be".equals(endianess.toLowerCase()))
    {
      number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
        ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
    else
    {
      number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
        ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
    }
    return number;
  }
 
		
    /* Escriure COMMAND */
    public void writeCommand(String command) throws IOException {
        socket.write(encoder.encode(CharBuffer.wrap(command)));
    }
    
    /* Escriure SP */
    public void writeSP() throws IOException{
        socket.write(encoder.encode(CharBuffer.wrap(" ")));
    }
    
    /* Llegir SP */
    public char readSP(String request) throws IOException{
        return request.charAt(4);
    }
    
    /* Escriure D */
    
    public void writeD(char d) throws IOException{ 
        socket.write(encoder.encode(CharBuffer.wrap(String.valueOf(d))));
    }
    
    /* Escriure P */
    
    public void writeP(char p) throws IOException {
        socket.write(encoder.encode(CharBuffer.wrap(String.valueOf(p))));
    }

    
    /* Escriure Char */
    public void writeChar(char d) throws IOException {
        socket.write(encoder.encode(CharBuffer.wrap(String.valueOf(d))));
    }
    
    /* Escriure Score */
    public void writeScore (double score) throws IOException{
        String scoreS = String.valueOf(score);
        if(score < 10.0){
            bw.write(" 0"+ String.valueOf(score) +"\n");
            writeChar('0');
            for (int i = 0; i < 3; i++) {
                writeChar(scoreS.charAt(i));
            }
        }
        
        else{
            
            for(int i=0; i < 4; i++){
                writeChar(scoreS.charAt(i));
            }
        }    
    }
        
    /* Funcio per al Servidor de LECTURA */
    public void readGeneralCommandServidor() throws IOException {
        
        buffer.flip();
        request += decoder.decode(buffer).toString();
        buffer.clear();
        
        if(request.length() < 4) System.out.println("Estoy esperando a que llegue un command entero.");
        
        else{
            String command = request.substring(0,4);
            command = command.toUpperCase();
            System.out.println("Buffer: " + request + " Command:" + command);
        

            switch(command){

                //si llegim STRT
                case "STRT":
                    bw.write("C: STRT\n");
                    writeSTBT();
                    break;

                case "DRAW":
                    bw.write("C: DRAW\n");
                    sendCard();
                    break;

                case "ANTE":
                    readAnte(request);
                    break;

                case "PASS":
                    bw.write("C: PASS\n");
                    readPass();
                    break;

                case "ERRO":
                    readError(request);
                    break;

                default:
                    writeError("FATAL ERROR. COMMAND SYNTAX ERROR.");
                    tancarSocket();
                    System.out.println("ERR-Recibiendo datos cliente");
                    break;
            }
        }
        
    }
    
    /* Escriure Start beting */
    private void writeSTBT() throws IOException{
        bw.write("S: STBT "+game.getBet()+"\n");
        writeCommand("STBT");
        writeSP();
        write_int32(game.getBet());
        request = "";
    }
    
    /* Enviar carta */
    private void sendCard() throws IOException{
        bw.write("S: CARD ");
        writeCommand("CARD");
        writeSP();
        Card card = game.getNextCard();
        game.addCard(card);
        String carta = card.getCard();
        writeD(carta.charAt(0));
        writeP(carta.charAt(1));
        
        bw.write(carta+"\n");
        System.out.println("Client score" + game.getScore());
        if(game.getScore() > 7.5){
            System.out.println("El client s'ha passat de score");
            writeBusting();
        }
        
        request = "";
    }
    
    /* guardar Ante del client */
    private void readAnte(String request) throws IOException{
        
        System.out.println("Esto dentro de readAnte, me ha llegado: " + request);
        if(request.length() != 13){
            System.out.println("He ido a leer ante pero no esta completo, me espero.");
        }
        
        else{
            bw.write("C: ANTE ");
            if(!(' ' == readSP(request))){
                System.out.println("He leido en SP: *" + readSP(request) + "*");
                writeError("ERROR SYNTAX ANTE");
                socket.close();
                System.out.println("Cliente " + id + " exit.");
            }
            else{
                String anteString = request.substring(5,9);
                int ante = bytesToInt32(anteString.getBytes(charset),"be");
                System.out.println("La apuesta es: *" + ante + "*");
                if (ante < 0){
                    writeError("Ante negatiu!");
                    tancarSocket();
                }
                
                else{
                    game.addBet((ante));
                    bw.write(String.valueOf(ante)+"\n");
                    this.request = request.substring(9,13);
                    System.out.println("le vuelvo a pasar: " + request);
                    readGeneralCommandServidor();
                }
            }
        }
        
    }
    
    /* Llegir el pass i jugar + Bank Score i gain */
    private void readPass() throws IOException{
        jugaServer(false);
    }
    
    //1 si guanya server, -1 si guanya client, 0 empat
    private void writeGain(int guanyador) throws IOException{
        bw.write("S: GAIN ");
        writeCommand("GAIN");
        writeSP();
        switch(guanyador){
            case -1:
                bw.write(String.valueOf(game.getBet()));
                write_int32(game.getBet());
                break;
            case 0:
                bw.write(String.valueOf(0));
                write_int32(0);
                break;
            case 1:
                bw.write(String.valueOf(game.getBet()*(-1)));
                write_int32(game.getBet()*(-1));
                break;
            case 2:
                bw.write(String.valueOf((game.getBet()*2)));
                write_int32(game.getBet()*2);
            default: break;
        }
        
        closeFitxer();
        socket.close();
    }
    
    private void jugaServer(boolean bstg) throws IOException{
        Game gameServer = new Game(0);
        double puntuacioClient = game.getScore();
        
        if(bstg){
            System.out.println("Score client: "+game.getScore());
            System.out.println("Score server: "+gameServer.getScore());
            Card card = game.getNextCard();
            gameServer.addCard(card);
        }
        
        else{
            while(gameServer.getScore() <= puntuacioClient && gameServer.getScore() != 7.5){
                Card card = game.getNextCard();
                gameServer.addCard(card);
                System.out.println("Score client: " + game.getScore());
                System.out.println("Score server: " + gameServer.getScore());
            }
        }
        
        writeBankScore(gameServer);
        
        if(game.getScore() > 7.5){
            writeGain(1);
        }
        
        else if(gameServer.getScore() > game.getScore() && gameServer.getScore() <= 7.5 ){
            writeGain(1);
        }
        
        else if(gameServer.getScore() ==  game.getScore()){
            writeGain(0);
        }
        else if(game.getScore() == 7.5){
            writeGain(2);
        }
        else{
            writeGain(-1);
        }   
    }
    
    private void writeBankScore(Game gameServer) throws IOException{
        bw.write("S: BKSC ");
        writeCommand("BKSC");
        writeSP();
        int cartesJugades = gameServer.getNCards();
        write_int32(cartesJugades);
        for(int i= 0; i < cartesJugades; i++){
            Card card = gameServer.getNCard(i);
            String carta = card.getCard();
            System.out.println(carta);
            writeD(carta.charAt(0));
            writeP(carta.charAt(1));
            bw.write(carta);
            System.out.println(carta.charAt(0));
            System.out.println(carta.charAt(1));
            
        }
        
        writeSP();
        writeScore(gameServer.getScore());
    }
    
    private void writeBusting() throws IOException{
        bw.write("S: BSTG\n");
        writeCommand("BSTG");
        jugaServer(true);
    }
    
    private void writeError(String message) throws IOException {
        bw.write("S: ERRO ");
        writeCommand("ERRO");
        writeSP();
        int longMessage = message.length();

        //write longMessage
        if (longMessage < 0 || longMessage > 99) {
            System.out.println("Error on message to send");
        } else if (longMessage < 10) {
            //log
            bw.write("0" + String.valueOf(longMessage).charAt(0));

            writeD('0');
            writeD(String.valueOf(longMessage).charAt(0));
        } else if (longMessage >= 10) {

            bw.write(String.valueOf(longMessage));
            String intParsed = String.valueOf(longMessage);
            writeD(intParsed.charAt(0));
            writeD(intParsed.charAt(1));
        } else {
            System.out.println("Error on message to send");
        }

        //write message
        bw.write(message);
        for (int i = 0; i < longMessage; i++) {
            writeD(message.charAt(i));
        }
        closeFitxer();
        socket.close();
    }
    
    private void readError(String request) throws IOException {
        
        if(request.length() >= 7){
            String nOne = String.valueOf(request.charAt(5));
            String nTwo = String.valueOf(request.charAt(6));
            

            int nCaracteres = Integer.parseInt((nOne + nTwo));
            
            if(request.length() >= 7 + nCaracteres){
                bw.write("C: ERRO ");
                bw.write(nOne);
                bw.write(nTwo);
                System.out.println(nCaracteres);
                for (int i = 7; i < nCaracteres + 7; i++) {
                    String letra = String.valueOf(request.charAt(i));
                    bw.write(letra);
                    System.out.print(letra);
                }
                System.out.println("\n");
                bw.write("\n");
                
                this.request = "";
                socket.close();
                closeFitxer();
            }
        }
        
        else{
            System.out.println("Estoy recibiendo un mensaje ERRO, espero a que este completo.");
        }
        
    }
    
    private void tancarSocket() throws IOException{
        socket.close();
        System.out.println("Client " + id + " closed.");
    }
}
