import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;
import model.Game;
import vista.ComUtils;

/**
 * SD 2015
 * @author marc
 */

public class Server {

    public static void main(String[] args) throws IOException {        
    
        if (args.length != 6){
            System.out.println("Us: java Server -p <numPort> -b <starting_bet> -f <deckfile>");
            System.exit(1);
        }
        
        int port = 1212;
        if(Integer.parseInt(args[1]) != 1212) port = Integer.parseInt(args[1]);
        int stbt = Integer.parseInt(args[3]);
        String pathDeckfile = args[5];
        
        
        try{
            Charset charset = Charset.forName("ISO-8859-1");
            ByteBuffer buffer = ByteBuffer.allocate(512);
            Selector selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new java.net.InetSocketAddress(port));
            server.configureBlocking(false);
            SelectionKey serverkey = server.register(selector, SelectionKey.OP_ACCEPT);
            CharsetEncoder encoder = charset.newEncoder();
            CharsetDecoder decoder = charset.newDecoder();
            int nClient = 0;
            
            System.out.println("Server preparat: "+ port +"\n");
            
            for(;;){
                
                selector.select();
                Set keys = selector.selectedKeys();
                
                for (Iterator i = keys.iterator(); i.hasNext();) {
                    SelectionKey key = (SelectionKey) i.next();
                    i.remove();
                    
                    if (key == serverkey) {
                        if (key.isAcceptable()) {
                            SocketChannel client = server.accept();
                            System.out.println("Client nou");
                            client.configureBlocking(false);
                            SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ);
                            
                            client.socket().setSoTimeout(10000);
                            
                            Game game = new Game(stbt);
                            
                            if (!game.omplirDeck(pathDeckfile)) {
                                System.out.println("ERROR! Cannot read deck.txt! Abort System.");
                                System.exit(0);
                            }
                            
                            ComUtils comUtilsClient = new ComUtils(client,game,nClient,buffer);
                            comUtilsClient.setLogFitxer();
                            nClient++;
                            
                            clientkey.attach(comUtilsClient);
			}
                        
                    } else {
                        
                        SocketChannel client = (SocketChannel) key.channel();
                        if (!key.isReadable()) {
                            continue;
                        }
                        
                        //bytes leidos
                        int bytesread = client.read(buffer);
                        
                        if (bytesread == -1) {
                            key.cancel();
                            client.close();
                            System.out.println("ERROR-Broken Pipe");
                            continue;
                        }
                        
                        ComUtils comUtils = (ComUtils) key.attachment();
                        comUtils.readGeneralCommandServidor();
                    }
		}
            }
	}catch(IOException e){   
            
        }
    }
}
   
