import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DirectClient extends Thread{
	Socket socket; 																		//socket belonging to the corresponding client
	BufferedReader in; 																	//the client input stream, where server receives input from client
	PrintWriter out;																	//the client output stream, where server writes output to client
	String name;																		//username of corresponding client
	ChatRoom currRoom;																	//chat room user is associated with
	
	
	public DirectClient(Socket socket) throws IOException{ //constructor simply links the thread's socket to the client's socket
		this.socket = socket;
	}
			
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	//initialize the value for in now that we have the socket available
			out = new PrintWriter(socket.getOutputStream(), true);						//initialize the value for out now that we have the socket available
			
			int count = 0;																//flag for user name input validation
			while(true) {
				if(count > 0) {
					out.println("Sorry, Name taken.");
				}else {
					//count initially 0 so always goes here first
					out.println("Welcome to GungHo Test Chat Server!\n\rLogin name?");
				}
				
				//get the client name input
				name = in.readLine();
				
				if(name == null) //checks for null input
					return;
				if(!ChatServer.userNames.contains(name)) {
					//checks if the name is taken, if its not add it
					ChatServer.printWriters.add(out);
					ChatServer.userNames.add(name);
					break;
				}
				//if the name exists we enter the count > 0 block which
				// lets the client process know that the name is taken
				count++;
			}
			//after this point user would have inputed a valid name
			out.println("Welcome " + name + "!\n\r"
					+ "Here are the list of options:\n\r\n"
					+ "\t/rooms                         : displays the list of active rooms\n\r"
					+ "\t/join roomName                 : joins the room called roomName or creates it if it does not exist\n\r"
					+ "\t/w userName messageContent     : sends a private message to user userName with message messageContent\n\r"
					+ "\t/leave                         : leave a chat room\n\r"
					+ "\t/quit                          : disconnects from the server\n\r");
			
			while(true) {
				String message = in.readLine();
				if(message == null)return;
				
				//send the message to all participating users
				if(message.startsWith("/")) {
					//handles options
					if(message.equals("/rooms")) {
						System.out.println("room option selected");
						if(ChatServer.activeRooms.isEmpty()) {
							out.println("There are no active rooms");
						}else {
							for(Map.Entry<String, ChatRoom> entry : ChatServer.activeRooms.entrySet()) {
								String rname =  entry.getKey();
								ChatRoom temp = entry.getValue();
								out.println(rname + "(" + temp.getNumParticipant() + ")");
							}
							out.println("ENDOFLIST");
						}
					}else if(message.startsWith("/join")) {
						if(message.length() < 7 || message.charAt(5) != ' ')
						{
							out.println("Invalid option argument!");
						}else {
							System.out.println("Join option selected");
							String rname = message.substring(6);
							if(ChatServer.activeRooms.containsKey(rname)) {
								System.out.println("Joining room " + rname);
								out.println("Joining Room " + rname);
								ChatServer.activeRooms.get(rname).addParticipant(this.name, out);
								this.currRoom = ChatServer.activeRooms.get(rname);
							}else {
								System.out.println("New room " + rname + " created");
								out.println("Creating room " + rname);
								ChatServer.activeRooms.put(rname,new ChatRoom(rname, this.name, out));
								this.currRoom = ChatServer.activeRooms.get(rname);
							}
						}
					}else if(message.equals("/leave")) {
						if(currRoom == null) {
							out.println("You are not in a room");
							continue;
						}
						
						for(PrintWriter writer : currRoom.getPw())
						{
							writer.println("User has left chat: " + this.name);
						}
						this.currRoom.removeParticipant(this.name);
						this.currRoom = null;
					}else if(message.startsWith("/w ")){
						int i = 3;
						while(i < message.length() && message.charAt(i) != ' ') {
							i++;
						}
						//get the username for our private chat
						String wArg = message.substring(3,i);
	
						//get the id of the user
						int sendToIndex = ChatServer.userNames.indexOf(wArg);
						
						if(sendToIndex == -1) {
							out.println("The user " + wArg + " does not exist");
						}else {
							//we create a private connection for the two users
							System.out.println("Private chat with " + wArg);
							String m_message = (i + 1 <= message.length()) ? message.substring(i+1) : "";
							System.out.println("Message: " + m_message);
							PrintWriter writeTo = ChatServer.printWriters.get(sendToIndex);
							writeTo.println("(" + this.name + ")" + m_message);
						}
					}else if(message.equals("/quit")){
						out.println("BYE");
						this.socket.close();
						break;
					}
				}else {
					//send message to all participants of room
					if(this.currRoom != null) {
						String roomName = currRoom.getName();
						for(PrintWriter writer : currRoom.getPw())
						{
							writer.println("(" + roomName + ") " + name + ": " + message);
						}
					}
				}
				//flush the input stream
	
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
