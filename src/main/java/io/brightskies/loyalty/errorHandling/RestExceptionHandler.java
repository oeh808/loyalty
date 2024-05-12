package io.brightskies.loyalty.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.brightskies.loyalty.pointsEntry.exception.PointsEntryException;
import io.brightskies.loyalty.product.exception.ProductException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ProductException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProductException handleProductException(ProductException ex) {
        return ex;
    }

    @ExceptionHandler(PointsEntryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public PointsEntryException handlePointsEntryException(PointsEntryException ex) {
        return ex;
    }

}
