import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConversationHandler extends Thread{
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
				
				//log the message
				//pw.println(name + ":" + message);
				
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
