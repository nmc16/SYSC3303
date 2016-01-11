package assign1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.logging.Logger;

public class Client {
	private static final Logger LOG = Logger.getLogger("client");
	private static final String FILE_NAME = "test.txt";
	private static final int BUFFER_SIZE = 1024;
	private DatagramSocket clientSocket;
	private int requests;
	
	public Client(String ip, int port) {
		requests = 0;
		try {
			InetAddress address = InetAddress.getByName(ip);
			clientSocket = new DatagramSocket(port, address);
		} catch (IOException e) {
			LOG.severe("Could not create client socket: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}
	
	public DatagramPacket createPacket() {
		Random r = new Random();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		if (requests == 10) {
			// Send invalid
		}
		
		if (requests > 10) {
			System.exit(0);
		}
		
		// Randomize either a read or write request
		if (r.nextBoolean()) {
			buffer.write(0);
			buffer.write(1);
			
			byte[] b = FILE_NAME.getBytes();
			buffer.write(b, 0, b.length);
			buffer.write(0);
			
			
			
		}
		return null;
	}
	public void run() {
		
	}
}
