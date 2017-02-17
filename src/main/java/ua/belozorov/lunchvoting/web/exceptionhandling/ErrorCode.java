package ua.belozorov.lunchvoting.web.exceptionhandling;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public enum ErrorCode {
    DUPLICATE_EMAIL("error.model.user.duplicate_email"),
    DUPLICATE_AREA_NAME("error.model.eatingarea.duplicate_name"),
    DUPLICATE_PLACE_NAME("error.model.lunchplace.duplicate_name"),
    ENTITY_NOT_FOUND("error.service.entity_not_found"),
    AREA_HAS_ONE_ADMIN("error.service.eatingareaservice.one_admin"),
    PARAMS_VALIDATION_FAILED("");

    private final String messageCode;

    ErrorCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
