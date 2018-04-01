import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.Statement;

public class DirectClient extends Thread{
	Socket socket; 																		//socket belonging to the corresponding client
	BufferedReader in; 																	//the client input stream, where server receives input from client
	PrintWriter out;																	//the client output stream, where server writes output to client
	String name;																		//username of corresponding client
	ChatRoom currRoom;																	//chat room user is associated with
	User user;
	
	
	public DirectClient(Socket socket) throws IOException{ //constructor simply links the thread's socket to the client's socket
		this.socket = socket;
	}
	
	//mysql functions
	public void createUser(String username, String password) {
		int rs = 0;
		Connection connection = null;
		Statement statement = null;
		
		String query = "INSERT INTO users(user_name, password) VALUES('" 
						+ username + "','" + password + "')";
		try {
			connection = JDBCMySQLConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeUpdate(query);
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean authenticateUser(String username, String password) {
		ResultSet rs = null;
		Connection connection = null;
		Statement statement = null;
		
		User user = null;
		String query = "SELECT * FROM users WHERE user_name ='" + username + "'";
		String m_password;
		try {
			connection = JDBCMySQLConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			
			if (rs.next()) {
				m_password = rs.getString("password");
				if(m_password.equals(password))
				{
					return true;
				}else
				{
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public User getUser(String username) {
		ResultSet rs = null;
		Connection connection = null;
		Statement statement = null;
		
		User user = null;
		String query = "SELECT * FROM users WHERE user_name='" + username + "'";
		try {
			connection = JDBCMySQLConnection.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			
			if (rs.next()) {
				user = new User();
				user.setName(rs.getString("user_name"));
				user.setId(rs.getInt("user_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return user;
	}
	
	public void handleRooms() {
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
	}
	
	public void handleJoin(String message) {
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
	}
	
	public void handleLeave() {
		if(currRoom == null) {
			out.println("You are not in a room");
			return;
		}
		
		for(PrintWriter writer : currRoom.getPw())
		{
			writer.println("User has left chat: " + this.name);
		}
		this.currRoom.removeParticipant(this.name);
		this.currRoom = null;
	}
	
	public void handleWhisper(String message)
	{
		int i = 3;
		while(i < message.length() && message.charAt(i) != ' ') {
			i++;
		}
		//get the username for our private chat
		String wArg = message.substring(3,i);

		//get the id of the user
		//int sendToIndex = ChatServer.activeUsers.containsKey(wArg);
		if(!ChatServer.activeUsers.containsKey(wArg)) {
			out.println("The user " + wArg + " is not online");
		}else {
			//we create a private connection for the two users
			System.out.println("Private chat with " + wArg);
			String m_message = (i + 1 <= message.length()) ? message.substring(i+1) : "";
			System.out.println("Message: " + m_message);
			PrintWriter writeTo = ChatServer.printWriters.get(wArg);
			writeTo.println("(" + this.name + ")" + m_message);
		}
	}
	
	public void handleList() {
		if(this.currRoom == null) {
			out.println("You are not in a room");
		}else {
			out.println("Participants of " + this.currRoom.getName());
			for(String name : this.currRoom.getParticipants()) {
				out.println("- " + name);
			}
		}
	}
	
	public void handleHelp() {
		out.println("Here are the list of options:\n\r\n"
				+ "\t/rooms                         : displays the list of active rooms\n\r"
				+ "\t/join roomName                 : joins the room called roomName or creates it if it does not exist\n\r"
				+ "\t/w userName messageContent     : sends a private message to user userName with message messageContent\n\r"
				+ "\t/leave                         : leave a chat room\n\r"
				+ "\t/quit                          : disconnects from the server\n\r"
				+ "\t/help                          : displays this list of options");
	}
	
			
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	//initialize the value for in now that we have the socket available
			out = new PrintWriter(socket.getOutputStream(), true);						//initialize the value for out now that we have the socket available
			
			//int count = 0;																//flag for user name input validation
			while(true) {
//				if(count > 0) {
//					out.println("Sorry, Name taken.");
//				}else {
//					//count initially 0 so always goes here first
//					out.println("Welcome to GungHo Test Chat Server!\n\rLogin name?");
//				}
				out.println("Welcome to GungHo Test Chat Server!\n\rLogin name?");
				//get the client name input
				name = in.readLine();
				
				if(name == null) //checks for null input
					return;
				
				//check to see if the username exists in the database
				if((user = getUser(name)) != null) {
					//the user exists, so now we authenticate
					out.println("Hi again " + name);
					boolean notAuthenticated = true;
					String password;
					while(notAuthenticated)
					{
						out.println("Please enter password:");
						password = in.readLine();
						if(authenticateUser(name, password))
						{
							//correct password
							ChatServer.printWriters.put(name, out);
							ChatServer.activeUsers.put(name, user);
							notAuthenticated = false;
						}else
						{
							out.println("Incorrect password!");
						}
					}
					break;
				}else {
					//it doesnt exist, create entry in database
					out.println("Creating user " + name);
					out.println("Create your password: ");
					String password = in.readLine();
					createUser(name, password);
					ChatServer.printWriters.put(name, out);
					ChatServer.activeUsers.put(name, getUser(name));
					break;
				}
				
				
//				if(!ChatServer.activeUsers.containsKey(name)) {
//					//checks if the name is taken, if its not add it
//					ChatServer.printWriters.put(name, out);
//					ChatServer.activeUsers.put(name, user);
//					break;
//				}
				//if the name exists we enter the count > 0 block which
				// lets the client process know that the name is taken
				
//				count++;
			}
			//after this point user would have inputed a valid name
			out.println("Welcome " + name + "!\n\r");
			handleHelp();
			
			while(true) {
				String message = in.readLine();
				if(message == null)return;
				
				//send the message to all participating users
				if(message.startsWith("/")) {
					//handles options
					if(message.equals("/rooms")) {
						handleRooms();
					}else if(message.startsWith("/join")) {
						handleJoin(message);
					}else if(message.equals("/leave")) {
						handleLeave();
					}else if(message.startsWith("/w ")){
						handleWhisper(message);
					}else if(message.equals("/help")) {
						handleHelp();
					}else if(message.equals("/list")) {
						handleList();
					}else if(message.equals("/quit")){
						out.println("BYE");
						ChatServer.activeUsers.remove(this.name);
						ChatServer.printWriters.remove(this.name);
						if(this.currRoom != null) {
							handleLeave();
						}
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
				
			}
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
}
