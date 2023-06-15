package cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions;

public class EmailIsAlreadyExistsException extends RuntimeException {
    public EmailIsAlreadyExistsException(String message) {
        super(message);
    }
}
