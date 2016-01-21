import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Server that waits for requests from clients and handles them.
 *
 * Creates a new response socket for each request from clients. Sends
 * a response code for write and read requests if they are valid.
 *
 * Throws an exception and exits if the request is invalid.
 *
 * @author Nicolas McCallum #100936816
 */
public class Server {
    private static final Logger LOG = Logger.getLogger("server");
    private static final int BUFFER_SIZE = 128;
    private DatagramSocket serverSocket;
    private InetAddress address;

    public Server(String ip, int port) {
        // Store the ip address and create the socket
        try {
            address = InetAddress.getByName(ip);
            serverSocket = new DatagramSocket(port, address);
        } catch (IOException e) {
            LOG.severe("Could not create server socket: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    /**
     * Continuously waits for requests from clients and sends a response for read
     * and write requests.
     *
     * Sends back 0301 for read requests and 0400 for write requests.
     *
     * @throws InvalidRequestException Thrown if the request was invalid
     */
    public void run() throws InvalidRequestException {
        while(true) {

            // Create data packet to receive from the clients
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            // Receive the packets from the client
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                LOG.severe("Could not receive data packet from host: " + e.getLocalizedMessage());
                System.exit(1);
            }

            // Print the data received
            LOG.info("Received from " + receivePacket.getAddress().toString() + " data: " +
                    new String(receivePacket.getData()) + " (Byte form " + Arrays.toString(receivePacket.getData()) + ")");

            // Check that the request is a valid request
            checkValid(receivePacket.getData());

            // Create the response
            buffer = new byte[BUFFER_SIZE];
            if (receivePacket.getData()[1] == 1) {
                buffer[0] = 0;
                buffer[1] = 3;
                buffer[2] = 0;
                buffer[3] = 1;
            } else {
                buffer[0] = 0;
                buffer[1] = 4;
                buffer[2] = 0;
                buffer[3] = 0;
            }

            LOG.info("Data being sent back to client: " + Arrays.toString(buffer));

            // Send the response back to the client on the new socket created
            try {
                DatagramSocket responseSocket = new DatagramSocket();
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length, address, receivePacket.getPort());
                responseSocket.send(responsePacket);
                responseSocket.close();
            } catch (IOException e) {
                LOG.severe("Could not send response data: " + e.getLocalizedMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Checks the request passed in buffer is valid.
     *
     * A request is valid if it starts with 01 or 02, followed by data, another 0,
     * more data and a final 0 with no data afterwards.
     *
     * @param buffer Byte data received from client request
     * @throws InvalidRequestException Thrown if request is invalid
     */
    public void checkValid(byte buffer[]) throws InvalidRequestException {
        // The first byte must be a 0
        if (buffer[0] != 0) {
            throw new InvalidRequestException("Starting byte did not start with 0!");
        }

        // Check the op code must either be 01 or 02
        if (buffer[1] != 1 && buffer[1] != 2) {
            throw new InvalidRequestException("OP Code " + buffer[0] + "" + buffer[1] + " (must be 01 or 02)!");
        }

        // Check there is data after the op code
        if (buffer[2] == 0) {
            throw new InvalidRequestException("Request did not contain a file name!");
        }

        // Find the next index with a zero (should be the 0 separating the file name and encoding)
        int i;
        for (i = 3; i < buffer.length; i++) {
            // Check the encoding is not empty
            if (buffer[i] == 0) {
                if (buffer[i + 1] == 0) {
                    throw new InvalidRequestException("Request did not contain and encoding!");
                } else {
                    break;
                }
            }
        }

        // If the next 0 is at the end of the buffer, then there is no encoding
        if (i == buffer.length - 1) {
            throw new InvalidRequestException("Request did not contain and encoding!");
        }

        // Check that after the encoding the text is empty
        for (i = i + 1; i < buffer.length; i++) {
            if (buffer[i] == 0) {
                for (; i < buffer.length; i++) {
                    if (buffer[i] != 0) {
                        throw new InvalidRequestException("Request had data after encoding!");
                    }
                }
            }
        }
    }

    public static void main(String args[]) throws InvalidRequestException {
        Server server = new Server("localhost", 69);
        server.run();
    }
}