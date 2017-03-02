import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame{
	/**
	 * 增加客户端id
	 */
	private static final long serialVersionUID = 1L;
	private JTextField jtf = new JTextField();
	private JTextArea jta = new JTextArea();
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private boolean connectFlag = false; 
	private Socket client = null;
	private static int id = 0;
	public static void main(String[] args) {
		new ChatClient();
	}
	
	public ChatClient() {
		jtf.addActionListener(new TFListenner());
		this.setSize(400, 300);
		
		this.add(jta,BorderLayout.CENTER);
		this.add(jtf,BorderLayout.SOUTH);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("ChatClient");		
		this.setVisible(true);
		connectToServer();
		new Thread(new DisListenner()).start();
	}
	
	private void connectToServer() {
		try {
			this.client = new Socket("127.0.0.1",8888);
			this.id++;
			System.out.println("Connected!");
			this.dos = new DataOutputStream(client.getOutputStream());
			this.dis = new DataInputStream(client.getInputStream());
			this.connectFlag = true;
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private class DisListenner implements Runnable{
		public void run() {
			while(true){
				if(dis!=null){
					try {
						jta.append(dis.readUTF()+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private class TFListenner implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(connectFlag){
				String str = jtf.getText();	
				try {
					dos.writeUTF("client"+id+":"+str);
					dos.flush();
				} catch (IOException e2) {
					e2.printStackTrace();
				}		
				jtf.setText("");
			}			
		}
	}
}
