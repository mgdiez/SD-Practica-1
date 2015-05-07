package vista;

import java.net.*;
import java.io.*;
import model.Card;
import model.Game;
 
public class ComUtils
{
    
  /* Objectes per escriure i llegir dades */
  private DataInputStream dis;
  private DataOutputStream dos;
  private Game game;
  private Socket socket;
 
  public ComUtils(Socket socket, Game game) throws IOException
  {
    this.game = game;
    dis = new DataInputStream(socket.getInputStream());
    dos = new DataOutputStream(socket.getOutputStream());
    this.socket = socket;
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
        dos.write(bStr, 0, 4);
    }
    /* Escriure SP */
    public void writeSP() throws IOException{
        byte bChar[] = new byte[1];
        bChar[0] = (byte) ' ';
        dos.write(bChar,0,1);
    }
    
    /* Llegir COMMAND */ 
    public String readCommand() throws IOException{
        String str;
        byte bStr[] = new byte[4];
        char cStr[] = new char[4];

        bStr = read_bytes(4);

        for (int i = 0; i < 4; i++) {
            cStr[i] = (char) bStr[i];
            System.out.println(cStr[i]);
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
    public void writeP(char p) throws IOException {
        byte bChar[] = new byte[1];
        bChar[0] = (byte) p;
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
        bChar[1] = (byte) d;
        dos.write(bChar, 0, 1);
    }
    
    /* Escriure Score */
    public void writeScore (double score) throws IOException{
        String scoreS = String.valueOf(score);
        if(score < 10.0){
            writeChar((char)0);
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
        
        String command = readCommand();
        command = command.toUpperCase();
        System.out.println("Llegit: "+ command);
        
        //Segons el command llegit, haurem de fer coses diferents.
        switch(command){
            case "STBT":
                //Starting bet
                startingBet();
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
            case "ERRO":
                //errors
                readError();
                break;
            default:
                writeError("SYNTAX ERROR");
                socket.close();
                System.exit(0);
                System.out.println("ERR-Command invalid.");
                
        }
    }
    //Si llegim STBT
    private void startingBet() throws IOException{
        if(readSP().equals(" ")){
            int bet = read_int32();
            game.setBet(bet);
            writeCommand("DRAW");
            readGeneralCommandClient();
        }else{
            System.out.println("Error reading SYNT on STBT_INT32");
            writeError("ERROR EXPECTED SP BETWEEN COMMAND AND INT");
            socket.close();
            System.exit(0);
        }
    }
    
    //Si llegim CARD
    private void receiveCard() throws IOException{
        
        if(readSP().equals(" ")){
            String d,p;
            d = readD();
            p = readP();

            if(("1".equals(d) || "2".equals(d) || "3".equals(d) || "4".equals(d) || "5".equals(d) || "6".equals(d) || "7".equals(d) || "s".equals(d) || "c".equals(d) || "r".equals(d)) && ("o".equals(p) || "c".equals(p) || "b".equals(p) || "e".equals(p)   )) {
                Card card = new Card(d+p);

                if(!game.addCard(card)){
                    System.out.println("CARTA REPETIDA!");
                    writeError("INVALID CARD, REPETIDA!");
                    socket.close();
                    System.exit(0);
                }
                else{
                System.out.println("Carta: " + d+p);
                }
            }

            else{
                writeError("CARTA INVALIDA!");
                System.out.println("Exiting client. ERROR RETRIEVING CARD");
                socket.close();
                System.exit(0);
            }
        }
        
        else {
            System.out.println("Error syntax incorrect cmd CARD");
            writeError("SYNTAX CARD ERROR");
            socket.close();
            System.exit(0);
        }
    }
    
    //Si llegim Busting
    private void busting() throws IOException{
        readGeneralCommandClient();
    }
    
    //llegim bankScore
    private void bankScore() throws IOException{
        
        if(readSP().equals(" ")){
            int number = read_int32();
            for (int i=0; i < number; i++){
                String d, p;
                d = readD();
                p = readP();
                System.out.println("'"+d+p+"'");
            }
            if(readSP().equals(" ")){
                String score = readScore();
                System.out.println(score);
                readGeneralCommandClient();
            } else {
                System.out.println("ERR - 2nd SP BKSC");
                socket.close();
                System.exit(0);
            }
        } else {
            System.out.println("ERR - 1st SP BKSC");
            socket.close();
            System.exit(0);
        }
    }
    
    //Llegir el Gain
    private void readGain() throws IOException{
        if(readSP().equals(" ")){
            int number = read_int32();
            System.out.println(number);
            
        }else{
            System.out.println("Error on GAIN SYNTAX");
            writeError("Error reading GAIN (SYNTAX ERR)");
            socket.close();
            System.exit(0);
        }
    }
    
    private void readError() throws IOException {
        if(readSP().equals(" ")){
            String nOne = readD();
            String nTwo = readD();
            System.out.println("a leer el error");
            int nCaracteres = Integer.parseInt((nOne + nTwo));
            System.out.println("tengo que leer "+nOne + nTwo);
            System.out.println(nCaracteres);
            for (int i = 0; i < nCaracteres; i++) {
                System.out.print(readD());
            }
            System.out.println("");
            System.out.println("Cerrando conexión.");
            socket.close();
            System.exit(0);
        } else{
            System.out.println("Error SP ERR CMD");
            socket.close();
            System.exit(0);
        }
    }

    public void writeError(String message) throws IOException{
        writeCommand("ERRO");
        writeSP();
        int longMessage = message.length();
        
        //write longMessage
        if(longMessage < 0 || longMessage > 99) System.out.println("Error on message to send");
        
        else if(longMessage < 10){
            writeD('0');
            writeD(String.valueOf(longMessage).charAt(0));
        }
        
        else if(longMessage >= 10){
            String intParsed = String.valueOf(longMessage);
            writeD(intParsed.charAt(0));
            writeD(intParsed.charAt(1));
        }
        
        else{
            System.out.println("Error on message to send");
        }
        
        //write message
        for(int i = 0; i < longMessage; i++){
            writeD(message.charAt(i));
        }
        
    }
}