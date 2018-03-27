import java.io.PrintWriter;
import java.util.ArrayList;

public class ChatRoom {
	String roomName;
	private ArrayList<String> participants = new ArrayList<String>();
	private ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	public ChatRoom(String name, String hostName, PrintWriter pw) {
		this.roomName = name;
		participants.add(hostName);
		printWriters.add(pw);
	}
	public void addParticipant(String pName, PrintWriter pw) {
		this.participants.add(pName);
		this.printWriters.add(pw);
	}
	
	public void removeParticipant(String pName)
	{
		int index = participants.indexOf(pName);
		participants.remove(index);
		printWriters.remove(index);
	}
	public ArrayList<PrintWriter> getPw(){
		return printWriters;
	}
	
	public ArrayList<String> getParticipants()
	{
		return participants;
	}
	public int getNumParticipant()
	{
		return this.participants.size();
	}
	public String getName() {
		return this.roomName;
	}
}
