import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//the starting point for the server

public class ChatServer {
	//stores a hashmap of online users
	static HashMap<String,User> activeUsers = new HashMap<String, User>();
	
	//stores a hashmap of printWriters that connects the server to clients
	static HashMap<String, PrintWriter> printWriters = new HashMap<String, PrintWriter>();
	
	//stores a hashmap of active rooms
	static HashMap<String, ChatRoom> activeRooms = new HashMap<String, ChatRoom>();
	
	
	public static void main(String[] args) throws Exception{
		System.out.println("Waiting for clients...");
				//initialize our server socket with specified port number
				ServerSocket ss = new ServerSocket(9806);
				while(true) {
					//the server keeps accepting requests from clients
					Socket soc = ss.accept();
					System.out.println("Connection Established");
					//run new thread for the corresponding client and start the thread
					DirectClient handler = new DirectClient(soc);
					handler.start();
				}
		}
}
		
