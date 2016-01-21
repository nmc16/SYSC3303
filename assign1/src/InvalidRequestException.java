/**
 * Exception to handle invalid requests being sent to the server
 *
 * @author Nicolas McCallum #100936816
 */
public class InvalidRequestException extends Exception {
    public InvalidRequestException(String message) {
        super(message);
    }
}
