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
	private static final int BUFFER_SIZE = 128;
	private DatagramSocket clientSocket;
	private InetAddress address;
    private int hostPort;
	
	public Client(String ip, int port, int hostPort) {
		try {
			address = InetAddress.getByName(ip);
			this.hostPort = hostPort;
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
		
		return new DatagramPacket(buffer.toByteArray(), buffer.toByteArray().length, address, hostPort);
	}
	
	public void run() {
		LOG.info("Starting client...");
		for (int i = 0; i <= 10; i++) {
			DatagramPacket datagramPacket;
			if (i == 10) {
                LOG.info("Sending invalid request to host...");
				byte msg[] = new byte[10];
				datagramPacket = new DatagramPacket(msg, msg.length, address, hostPort);
				
			} else {
				datagramPacket = createPacket();
			}
			
			try {
                LOG.info("Request " + (i + 1) + " being sent to server: " + new String(datagramPacket.getData()) +
                        " (Byte form: " + Arrays.toString(datagramPacket.getData())+ ")");
				clientSocket.send(datagramPacket);
			} catch (IOException e) {
				LOG.severe("Could not send data packet: " + e.getLocalizedMessage());
				System.exit(1);
			}
			
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
			
			try {
		    	clientSocket.receive(receivePacket);
                LOG.info("Received data: " + Arrays.toString(receivePacket.getData()));
		    } catch(IOException e) {
		    	LOG.severe("Could not receive data packet: " + e.getLocalizedMessage());
		    	System.exit(1);
		    }
		}
		
		LOG.info("Client shutting down...");
	}
	
	public static void main(String args[]) {
		Client client = new Client("localhost", 67, 68);
		client.run();
	}
}
