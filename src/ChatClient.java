import java.net.Socket;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ChatClient {
	//Note that all swing components will be made static since they are shared by all threads
	//the outer container on which we are going to add components
	static JFrame chatWindow = new JFrame("Chat Application");
	
	//where the messages will be displayed
	static JTextArea chatArea = new JTextArea(22,40);
	
	//where the messages will be input
	static JTextField textField = new JTextField(40);
	
	//a blank label to separate chatArea and textField
	static JLabel blank = new JLabel("             ");
	
	//button to send our message
	static JButton sendButton = new JButton("send");
	
	//our streams to communicate with the server
	static BufferedReader in;
	static PrintWriter out;
	
	static JLabel nameLabel = new JLabel("     ");
	
	ChatClient(){
		//layout used for arranging our component
		chatWindow.setLayout(new FlowLayout());
		
		//label to display the client username
		chatWindow.add(nameLabel);
		
		//add scrollPane to scroll through chatArea when it exceeds size
		chatWindow.add(new JScrollPane(chatArea));
		
		chatWindow.add(blank);
		chatWindow.add(textField);
		chatWindow.add(sendButton);
		
		//ensure that our window is closed when user clicks close
		//otherwise it just hides it
		chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatWindow.setSize(475, 500);
		//reveal our window
		chatWindow.setVisible(true);
		// we don't want users to be able to type anything before the user 
		// has established connection with our server 
		textField.setEditable(false);
		//we dont want users entering text to chatArea
		chatArea.setEditable(false);
		
		//bind sendButton to our listener
		sendButton.addActionListener(new Listener());
		
		//to handle case where user presses enter
		textField.addActionListener(new Listener());
	}
	
	void startChat() throws Exception{
		//add dialog box to allow user to input IP address
		String ipAddress = JOptionPane.showInputDialog(
				chatWindow, //the component on which to display the dialog box
				"Enter IP Address: ", //the message displayed in the box
				"IP Address Required!", //the title of the dialog box
				JOptionPane.PLAIN_MESSAGE); //the type of message
		Socket soc = new Socket(ipAddress, 9806);
		in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
		out = new PrintWriter(soc.getOutputStream(), true);
		
		while(true) {
			//get server message to determine data flow
			String str = in.readLine();
			if(str.equals("NAMEREQUIRED")) {
				//prompt user for a unique username
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter a Unique Name: ",
						"Name Required!", 
						JOptionPane.PLAIN_MESSAGE);
				//send the input to the server to determine next action
				out.println(name);
			}else if(str.equals("NAMEALREADYEXISTS"))
			{
				String name = JOptionPane.showInputDialog(
						chatWindow,
						"Enter Another Name: ",
						"Name Already Exists!", 
						JOptionPane.WARNING_MESSAGE);
				//send the input to the server to determine next action
				out.println(name);
			}else if(str.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
				nameLabel.setText("You are logged in as: " + str.substring(12));
				
			}else {
				/*	if the message received is none of the above, then it is
					a message from another client, thus we display in our 
					chatArea
				*/
				chatArea.append(str + "\n");
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception{
		ChatClient client = new ChatClient();
		client.startChat();
		
	}
}

class Listener implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent e) //send button listener
	{
		//send the message to the server 
		ChatClient.out.println(ChatClient.textField.getText());
		//clear the textField
		ChatClient.textField.setText("");
	}
}