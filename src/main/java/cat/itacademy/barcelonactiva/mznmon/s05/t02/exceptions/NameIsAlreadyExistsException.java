package cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions;

public class NameIsAlreadyExistsException extends RuntimeException {
    public NameIsAlreadyExistsException(String message) {
        super(message);
    }
}
