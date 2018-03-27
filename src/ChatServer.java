import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChatServer {
	
	//mock arrayList that contains taken userNames
	static ArrayList<String> userNames = new ArrayList<String>();
	
	/*contains all the printWriters corresponding to all users in chat
	 we need this since we want sent messages to be displayed in the
	 textArea of all participating users */
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	
	static HashMap<String, ChatRoom> activeRooms = new HashMap<String, ChatRoom>();
	
	public static void main(String[] args) throws Exception{
		System.out.println("Waiting for clients...");
				
				//initialize our server socket
				ServerSocket ss = new ServerSocket(9806);
				while(true) {
					Socket soc = ss.accept();
					System.out.println("Connection Established");
					BufferedReader check = new BufferedReader(new InputStreamReader(soc.getInputStream()));
					PrintWriter test = new PrintWriter(soc.getOutputStream(), true);
					
					test.println("Press Enter to Continue...");
					String mode = check.readLine();
					
					if(mode.equals("CLIENT")) {
						System.out.println("Used client mode");
						ConversationHandler handler = new ConversationHandler(soc);
						handler.start();
					}else
					{
						System.out.println("Used non-client mode");
						DirectClient handler = new DirectClient(soc);
						handler.start();
					}
				}
			}
}
		
