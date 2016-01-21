import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Client that connects to the intermediate host and sends 11 requests that
 * randomize between read and write requests.
 *
 * The 11th request is an invalid request that the server will throw an exception on.
 *
 * Connects to the remote host over localhost and hosts a socket on port 67 and sends
 * to port 68.
 *
 * @author Nicolas McCallum #100936816
 */
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
            // Try to obtain the IP address and store it
			address = InetAddress.getByName(ip);
			this.hostPort = hostPort;

            // Create the client socket
			clientSocket = new DatagramSocket(port, address);
		} catch (IOException e) {
			LOG.severe("Could not create client socket: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

    /**
     * Creates a random data packet that contains either a read (01) or write (02) request
     * followed by the file name to read from, another 0, the encoding, and a final 0.
     *
     * The format is: [0 1 | 0 2] File_name 0 encoding 0
     *
     * Creates the datagram packet to send to the intermediate host IP and port.
     *
     * @return DatagramPacket generated with request information stored in data
     */
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

        // Add the file name to the byte array
		buffer.write(FILE_NAME.getBytes(), 0, FILE_NAME.length());
		buffer.write(0);

        // Add the encoding to the byte array
		buffer.write(MODE.getBytes(), 0, MODE.length());
		buffer.write(0);

		return new DatagramPacket(buffer.toByteArray(), buffer.toByteArray().length, address, hostPort);
	}

    /**
     * Connects the client to the host and sends 11 requests using the data packets generated
     * by {@link #createPacket()}.
     */
	public void run() {
		LOG.info("Starting client...");

        // Create and send 11 packets
		for (int i = 0; i <= 10; i++) {
			DatagramPacket datagramPacket;

            // If on request 11, then send an invalid request
			if (i == 10) {
                LOG.info("Sending invalid request to host...");
				byte msg[] = new byte[10];
				datagramPacket = new DatagramPacket(msg, msg.length, address, hostPort);
				
			} else {
                // Otherwise send the generated packet
				datagramPacket = createPacket();
			}

            // Send the created packet to the host
			try {
                LOG.info("Request " + (i + 1) + " being sent to server: " + new String(datagramPacket.getData()) +
                        " (Byte form: " + Arrays.toString(datagramPacket.getData())+ ")");
				clientSocket.send(datagramPacket);
			} catch (IOException e) {
				LOG.severe("Could not send data packet: " + e.getLocalizedMessage());
				System.exit(1);
			}

            // Create a packet to receive the response from the host
			byte[] buffer = new byte[BUFFER_SIZE];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            // Block until the packet is received from the host
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
