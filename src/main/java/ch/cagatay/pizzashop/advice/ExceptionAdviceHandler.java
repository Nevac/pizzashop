package ch.cagatay.pizzashop.advice;

import ch.cagatay.pizzashop.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdviceHandler {

    private final Logger logger = LoggerFactory.getLogger(ExceptionAdviceHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleRessourceNotFoundException(ResourceNotFoundException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({JsonProcessingException.class, HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleUnprocessableEntityException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseEntity<String>(
                "Unprocessable Body Entity", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
