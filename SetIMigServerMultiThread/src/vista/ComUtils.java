package vista;

import java.net.*;
import java.io.*;
import model.Card;
import model.Game;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class ComUtils
{
    
  /* Objectes per escriure i llegir dades */
  private DataInputStream dis;
  private DataOutputStream dos;
  private Game game;
  private Socket socket;
  private File logFile;
  private FileWriter fw;
  private BufferedWriter bw;

 
  public ComUtils(Socket socket, Game game) throws IOException
  {
    this.game = game;
    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());
    socket = this.socket;
  }
  
  public void setLogFitxer() throws IOException{      
       fw = new FileWriter("Server"+Thread.currentThread().getName()+".log");
       bw = new BufferedWriter(fw);
       
  }
  
  public void closeFitxer() throws IOException{
      bw.close();
  }
    /* Llegir un enter de 32 bits */
  public int read_int32() throws IOException
  {
    byte bytes[] = new byte[4];
    bytes  = read_bytes(4);
 
    return bytesToInt32(bytes,"be");
  }
 
  /* Escriure un enter de 32 bits */
  public void write_int32(int number) throws IOException
  {
    byte bytes[]=new byte[4];
 
    int32ToBytes(number,bytes,"be");
    dos.write(bytes, 0, 4);
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
  
  /* Llegir Bytes */
  private byte[] read_bytes(int numBytes) throws IOException{
    int len=0 ;
    byte bStr[] = new byte[numBytes];
    int bytesread = 0;
    do {
        bytesread = dis.read(bStr, len, numBytes-len);
        if(bytesread == -1) throw new IOException("Broken pipe");
      len += bytesread;
    } while (len < numBytes);
    return bStr;
  }
		
    /* Escriure COMMAND */
    public void writeCommand(String command) throws IOException {
        byte bStr[] = new byte[4];
        for (int i = 0; i < 4; i++) {
            bStr[i] = (byte) command.charAt(i);
        }   
        try{
        dos.write(bStr, 0, 4);
        }catch(Exception e){
            socket.close();
        }
    }
    /* Escriure SP */
    public void writeSP() throws IOException{
        byte bChar[] = new byte[1];
        bChar[0] = (byte) ' ';
        try{
        dos.write(bChar,0,1);
        }catch(IOException e){
            socket.close();
        }
    }
    
    /* Llegir COMMAND */ 
    public String readCommand() throws IOException{
        
        String str;
        byte bStr[] = new byte[4];
        char cStr[] = new char[4];

        bStr = read_bytes(4);

        for (int i = 0; i < 4; i++) {
            cStr[i] = (char) bStr[i];
        }

        str = String.valueOf(cStr);

        return str;
    }
    
    /* Llegir SP */
    public String readSP() throws IOException{
        String str;
        byte bStr[] = new byte[1];
        char cStr;

        bStr = read_bytes(1);
        cStr = (char) bStr[0];

        str = String.valueOf(cStr);
        return str;
    }
    
    /* Escriure D */
    public void writeD(char d) throws IOException{
        byte bChar[] = new byte[1];
        bChar[0] = (byte) d;
        dos.write(bChar, 0, 1);
    }
    
    /* Escriure P */
    public void writeP(char d) throws IOException {
        byte bChar[] = new byte[1];
        bChar[0] = (byte) d;
        dos.write(bChar, 0, 1);
    }
    
    /* Llegir D */ 
    public String readD() throws IOException{
        String str;
        byte bStr[] = new byte[1];
        char cStr;
        
        bStr = read_bytes(1);
        cStr = (char) bStr[0];
        
        str = String.valueOf(cStr);
        return str;
    }
    
    /* Llegir D */ 
    public String readP() throws IOException{
        String str;
        byte bStr[] = new byte[1];
        char cStr;
        
        bStr = read_bytes(1);
        cStr = (char) bStr[0];
        
        str = String.valueOf(cStr);
        return str;
    }
    
    /* Escriure Char */
    //metodo aux
    public void writeChar(char d) throws IOException {
        byte bChar[] = new byte[1];
        bChar[0] = (byte) d;
        dos.write(bChar, 0, 1);
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
    
    public String readScore() throws IOException{
        String str;
        byte bStr[] = new byte[4];
        char cStr[] = new char[4];

        bStr = read_bytes(4);
        for (int i = 0; i < 4; i++) {
            cStr[i] = (char) bStr[i];
        }

        str = String.valueOf(cStr);
        return str;
    }
    
    
    public void readGeneralCommandClient() throws IOException{
        String command = "";
        try{
            command = readCommand();
        }catch(Exception e){
            socket.close();
        }
        System.out.println("Llegit: "+ command);
        
        //Segons el command llegit, haurem de fer coses diferents.
        switch(command){
            case "STBT":
                //Starting bet
                try{
                    startingBet();
                }catch(IOException e){
                 socket.close();              
                }
                break;
                
            case "CARD":
                //Receive a card
                receiveCard();
                break;
                
            case "BSTG":
                //Busting!!
                busting();
                break;
                
            case "BKSC":
                //BankScore
                bankScore();
                break;
                
            case "GAIN":
                //Gains
                readGain();
                break;
            default:
                System.out.println("ERR-Command invalid.");
                socket.close();
                
        }
    }
    //Si llegim STBT
    private void startingBet() throws IOException{
        readSP();
        int bet = read_int32();
        game.setBet(bet);
        writeCommand("DRAW");
    }
    
    //Si llegim CARD
    private void receiveCard() throws IOException{
        readSP();
        String d,p;
        d = readD();
        p = readP();
        Card card = new Card(d+p);
        game.addCard(card);
        System.out.println("Carta: " + d+p);
    }
    
    //Si llegim Busting
    private void busting() throws IOException{
        readGeneralCommandClient();
    }
    
    //llegim bankScore
    private void bankScore() throws IOException{
        readSP();
        int number = read_int32();
        for (int i=0; i < number; i++){
            String d, p;
            d = readD();
            p = readP();
            System.out.println("'"+d+p+"'");
        }
        readSP();
        String score = readScore();
        System.out.println(score);
        readGeneralCommandClient();
    }
    
    //Llegir el Gain
    public void readGain() throws IOException{
        readSP();
        int number = read_int32();
        System.out.println("GAIN " + number);
    }
    
    /* Funcio per al Servidor de LECTURA */
    public void readGeneralCommandServidor() throws IOException {
        
        try{
        String command = readCommand();

        command = command.toUpperCase();
        
        System.out.println("Llegit: " + command);
        switch(command){
            //si llegim STRT
            case "STRT":
                writeSTBT();
                break;
            
            case "DRAW":
                bw.write("C: DRAW\n");
                sendCard();
                break;
            
            case "ANTE":
                readAnte();
                break;
                
            case "PASS":
                bw.write("C: PASS\n");
                
                readPass();
                break;
            
            case "ERRO":
                bw.write("C: ERRO ");
                readError();
                break;
            
            default:
                writeError("SYNTAX ERROR");
                break;
        }
        }catch(IOException e){

        }
    }
    
    /* Escriure Start beting */
    private void writeSTBT() throws IOException{
       try{
            bw.write("C: STRT\n");
            bw.write("S: STBT "+game.getBet()+"\n"); 
            
            writeCommand("STBT");
            writeSP();
            write_int32(game.getBet());
            readGeneralCommandServidor();
       }catch(IOException e){
           socket.close();
       }
    }
    
    /* Enviar carta */
    private void sendCard() throws IOException{
        try{
            writeCommand("CARD");
            writeSP();
            Card card = game.getNextCard();
            game.addCard(card);
            String carta = card.getCard();
            
            bw.write("S: CARD " + carta + "\n");
            
            writeD(carta.charAt(0));
            writeP(carta.charAt(1));
            System.out.println("Client score" + game.getScore());
        
            if(game.getScore() > 7.5){
                System.out.println("El client s'ha passat de score");
                writeBusting();
        }
        
            else{
                readGeneralCommandServidor();
            }
        }catch(IOException e){
            socket.close();
        }
    }
    
    /* guardar Ante del client */
    private void readAnte() throws IOException{
        try{
            //If the space between command and bet its ok, we still playing.
            if(readSP().equals(" ")){
                int ante = read_int32();

                if(ante <= 0){
                    writeError("ANTE INVALID!");
                }
                else{
                    game.addBet(ante);
                    bw.write("C: ANTE " + ante + "\n");

                    readGeneralCommandServidor();
                }
            }
            
            //If the client ante its not like the protocol says, 
            //"ANTE<SP><D>" we send error message and close the client socket.
            else{
                writeError("NO SPACE BETWEEN ANTE COMMAND AND BET");
                System.out.println("Syntax error. Closing game and client socket");
                socket.close();
                System.exit(0);
            }
            
        }catch(Exception e){
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
                bw.write(String.valueOf(game.getBet()*2));
                write_int32(game.getBet()*2);
            default: break;
        }
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
        bw.write(String.valueOf(cartesJugades));
        System.out.println(cartesJugades);
        write_int32(cartesJugades);
        for(int i= 0; i < cartesJugades; i++){
            Card card = gameServer.getNCard(i);
            String carta = card.getCard();
            System.out.println(carta);
            bw.write(String.valueOf(carta.charAt(0))+ String.valueOf(carta.charAt(1)));
            writeD(carta.charAt(0));
            writeP(carta.charAt(1));
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
    
    private void readError() throws IOException{
        if(readSP().equals(" ")){
            
            String nOne = readD();
            String nTwo = readD();
            int nCaracteres = Integer.parseInt((nOne+nTwo));
            bw.write(nOne);
            bw.write(nTwo);

            System.out.println(nCaracteres);
            for(int i= 0; i < nCaracteres; i++){
                String letra = readD();
                bw.write(letra);
                System.out.print(letra);
            }
            System.out.println("\n");
            bw.write("\n");
        }
        
        else {
            writeError("EXPECTED SP ON ERRO");
            
        }
    }
    
    private void writeError(String message) throws IOException{
        bw.write("S: ERRO ");
        writeCommand("ERRO");
        writeSP();
        int longMessage = message.length();
        
        //write longMessage
        if(longMessage < 0 || longMessage > 99) System.out.println("Error on message to send");
        
        else if(longMessage < 10){
            //log
            bw.write("0" + String.valueOf(longMessage).charAt(0));
            
            writeD('0');
            writeD(String.valueOf(longMessage).charAt(0));
        }
        
        else if(longMessage >= 10){
            
            bw.write(String.valueOf(longMessage));
            String intParsed = String.valueOf(longMessage);
            writeD(intParsed.charAt(0));
            writeD(intParsed.charAt(1));
        }
        
        else{
            System.out.println("Error on message to send");
        }
        
        //write message
        bw.write(message);
        for(int i = 0; i < longMessage; i++){
            writeD(message.charAt(i));
        }
        
    }
}