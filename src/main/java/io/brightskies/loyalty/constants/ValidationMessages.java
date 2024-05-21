package io.brightskies.loyalty.constants;

/* 
 * Variables without the word SCHEMA (Except INVALID_PHONE_NUMBER) in them
 * are meant to be appended to a variable, e.g someVariable + NOT_BLANK 
 * Variables with the word SCHEMA in them are made to be used by the swagger API
 */
public class ValidationMessages {
    public static final String NOT_BLANK_SCHEMA = "Cannot be empty or null";
    public static final String NOT_BLANK = " cannot be empty or null";

    public static final String NOT_NULL_SCHEMA = "Cannot be null";
    public static final String NOT_NULL = " cannot be null";

    public static final String POSITIVE = " must be more than 0";

    public static final String POSITIVE_OR_ZERO = " must be 0 or more";

    public static final String LOE_TO_FIVE = " must be less than or equal to 5";

    public static final String PAST_OR_PRESENT = " must be in the past or present";
    public static final String PAST_OR_PRESENT_SCHEMA = "Must be in the past or present";
    public static final String FUTURE = " must be in the future";
    public static final String FUTURE_SCHEMA = "Must be in the future";

    public static final String INVALID_EMAIL = "Email must be valid";
    public static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public static final String INVALID_PHONE_NUMBER = "Phone number must be valid";
    public static final String PHONE_NUMBER_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";
}
