import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChatServer {
	
	static HashMap<String,User> activeUsers = new HashMap<String, User>();
	
	static HashMap<String, PrintWriter> printWriters = new HashMap<String, PrintWriter>();
	
	static HashMap<String, ChatRoom> activeRooms = new HashMap<String, ChatRoom>();
	
	
	public static void main(String[] args) throws Exception{
		System.out.println("Waiting for clients...");
				
				//initialize our server socket
				ServerSocket ss = new ServerSocket(9806	);
				while(true) {
					Socket soc = ss.accept();
					System.out.println("Connection Established");
					DirectClient handler = new DirectClient(soc);
					handler.start();
				}
		}
}
		
