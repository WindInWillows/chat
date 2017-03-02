import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	/**
	 * 增加客户端id
	 */
	private boolean started = false;
	private ServerSocket ss = null;
	private ArrayList<ClientThread> ClientList = new ArrayList<ClientThread>();
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	public void start(){
		try {
			ss =new ServerSocket(8888);
			started = true;
			while(started){
				ClientThread c = new ClientThread(ss.accept());
				ClientList.add(c);
				c.hashCode();
				new Thread(c).start();
				System.out.println("A client connected!");
			}

		}catch(BindException ee){
			System.out.println("端口已被占用！");
			System.exit(1);
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}finally{
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ClientThread implements Runnable{
		private Socket cc = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		public boolean connected = false;
		
		
		public ClientThread(Socket c) {
			this.cc = c;
			try {
				dis = new DataInputStream(cc.getInputStream());
				dos = new DataOutputStream(cc.getOutputStream());
				connected = true;
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		public void send(String str){
			try {
				if(this.connected)dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ClientThread ct;
		@Override
		public void run() {
			try{
				while(connected){
					String str = dis.readUTF();
//					System.out.println("client said:"+str);
					for(int i=0;i<ClientList.size();i++){
						ct = ClientList.get(i);
						ct.send(str);
					}
				}
				dis.close();
			}catch(IOException ee){
				try {
					dis.close();
					dos.close();
					cc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}finally{
				ClientList.remove(this);
				System.out.println("A client quit!");
				this.connected = false;
			}
		}
	}
}
