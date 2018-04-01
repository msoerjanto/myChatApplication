import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class User {
	private String username;
	private String ip;
	private String port;
	private int id;
	
	public void setName(String username) {this.username = username;}
	
	public void setId(int id) {this.id = id;}
	
	public String getName() {return username;}
	
	public String getIp() {return ip;}
	
	public String getPort() {return port;}
	
	public int getId(){return id;}
	
	
}
