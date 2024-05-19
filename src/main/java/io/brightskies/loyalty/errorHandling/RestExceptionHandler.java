package io.brightskies.loyalty.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.brightskies.loyalty.pointsEntry.exception.PointsEntryException;
import io.brightskies.loyalty.product.exception.ProductException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleProductException(ProductException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(PointsEntryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handlePointsEntryException(PointsEntryException ex) {
        return new ErrorResponse(ex.getMessage());
    }

}
