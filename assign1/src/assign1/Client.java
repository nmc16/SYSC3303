package assign1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

public class Client {
	private static final Logger LOG = Logger.getLogger("client");
	private static final String FILE_NAME = "test.txt";
	private static final String MODE = "netascii";
	private static final int BUFFER_SIZE = 1024, HOST_PORT = 68;
	private DatagramSocket clientSocket;
	private int port;
	private InetAddress address;
	
	public Client(String ip, int port) {
		try {
			InetAddress address = InetAddress.getByName(ip);
			this.address = address;
			this.port = port;
			clientSocket = new DatagramSocket(port, address);
		} catch (IOException e) {
			LOG.severe("Could not create client socket: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}
	
	public DatagramPacket createPacket() {
		Random r = new Random();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		// Randomize either a read or write request
		if (r.nextBoolean()) {
			buffer.write(0);
			buffer.write(1);

		} else {
			buffer.write(0);
			buffer.write(2);
		}
		
		buffer.write(FILE_NAME.getBytes(), 0, FILE_NAME.length());
		buffer.write(0);
		buffer.write(MODE.getBytes(), 0, MODE.length());
		buffer.write(0);
		
		String msg = Arrays.toString(buffer.toByteArray());
		LOG.info("Byte array being sent to server: " + msg);
		
		return new DatagramPacket(buffer.toByteArray(), buffer.toByteArray().length, address, HOST_PORT);
	}
	
	public void run() {
		LOG.info("Starting client...");
		for (int i = 0; i <= 10; i++) {
			DatagramPacket datagramPacket;
			if (i == 10) {
				byte msg[] = new byte[10];
				datagramPacket = new DatagramPacket(msg, msg.length, address, HOST_PORT);
				
			} else {
				datagramPacket = createPacket();
			}
			
			try {
				clientSocket.send(datagramPacket);
			} catch (IOException e) {
				LOG.severe("Could not send data packet: " + e.getLocalizedMessage());
				System.exit(1);
			}
			
			byte[] buffer = new byte[100];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
			
			try {
		    	clientSocket.receive(receivePacket);
		    } catch(IOException e) {
		    	LOG.severe("Could not receive data packet: " + e.getLocalizedMessage());
		    	System.exit(1);
		    }
			
			LOG.info(Arrays.toString(receivePacket.getData()));
		}
		
		LOG.info("Client shutting down...");
	}
	
	public static void main(String args[]) {
		Client client = new Client("localhost", 67);
		client.run();
	}
}
