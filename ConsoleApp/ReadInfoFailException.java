import java.io.IOException;

//This exception is when reading complex inputs fail
public class ReadInfoFailException extends IOException {
    public ReadInfoFailException() {
        super("ReadInfoFailException occurred!");
    }

    // Constructor that accepts a custom message
    public ReadInfoFailException(String message) {
        super(message);
    }

    public void printStackTrace() {
        super.printStackTrace();
        System.out.println("---ReadInfoFailException: Could not properly read the info from the input fields---");
    }

}
