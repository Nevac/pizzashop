package ch.cagatay.pizzashop.exception;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String errorMessage) {
        super(errorMessage + " Not Found");
    }
}
