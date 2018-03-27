import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ChatServer {
	
	//mock arrayList that contains taken userNames
	static ArrayList<String> userNames = new ArrayList<String>();
	
	/*contains all the printWriters corresponding to all users in chat
	 we need this since we want sent messages to be displayed in the
	 textArea of all participating users */
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	
	public static void main(String[] args) throws Exception{
		System.out.println("Waiting for clients...");
				
				//initialize our server socket
				ServerSocket ss = new ServerSocket(9806);
				while(true) {
					Socket soc = ss.accept();
					System.out.println("Connection Established");
					ConversationHandler handler = new ConversationHandler(soc);
					handler.start();
					
				}
			}
}
		
class ConversationHandler extends Thread{
	Socket socket; //socket belonging to the corresponding client
	BufferedReader in;
	PrintWriter out;
	String name;
			
	public ConversationHandler(Socket socket) throws IOException{
		this.socket = socket;
	}
			
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			int count = 0;
			while(true) {
				if(count > 0) {
					out.println("NAMEALREADYEXISTS");
				}else {
					//count initially 0 so always goes here first
					out.println("NAMEREQUIRED");
				}
				
				//get the client name input
				name = in.readLine();
				
				if(name == null) //checks for null input
					return;
				if(!ChatServer.userNames.contains(name)) {
					//checks if the name is taken, if its not add it
					ChatServer.userNames.add(name);
					break;
				}
				//if the name exists we enter the count > 0 block which
				// lets the client process know that the name is taken
				count++;
			}
			//after this point user would have inputed a valid name
			//we send the following message to let the client process know
			out.println("NAMEACCEPTED" + name);
			
			//add the client's out socket so that they receive the chat messages
			ChatServer.printWriters.add(out);
			
			while(true) {
				String message = in.readLine();
				if(message == null)return;
				
				//send the message to all participating users
				for(PrintWriter writer : ChatServer.printWriters)
				{
					writer.println(name + ": " + message);
				}
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
