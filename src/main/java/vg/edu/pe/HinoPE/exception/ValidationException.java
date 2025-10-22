package vg.edu.pe.HinoPE.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {
    
    private Map<String, String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public ValidationException(String field, String error) {
        super("Error de validación");
        this.errors = new HashMap<>();
        this.errors.put(field, error);
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}
