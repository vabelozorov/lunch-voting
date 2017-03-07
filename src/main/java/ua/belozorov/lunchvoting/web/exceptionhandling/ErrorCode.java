package ua.belozorov.lunchvoting.web.exceptionhandling;

/**

 *
 * Created on 07.02.17.
 */
public enum ErrorCode {
    AREA_DUPLICATE_NAME("error.model.areaservice.duplicate_name"),
    AREA_HAS_ONE_ADMIN("error.service.eatingareaservice.one_admin"),
    DUPLICATE_EMAIL("error.model.user.duplicate_email"),
    DUPLICATE_PLACE_NAME("error.model.lunchplace.duplicate_name"),
    ENTITY_NOT_FOUND("error.service.entity_not_found"),
    PARAMS_VALIDATION_FAILED("error.model.area"),
    MULTIPLE_VOTE_PER_SAME_ITEM("error.model.commonpolicy.multiple_vote_per_item"),
    NO_MENUS_FOR_MENU_DATE("error.model.lunchplacepoll.no_menus"),
    NO_POLL_ITEMS("error.model.lunchplacepoll.no_pollitems"),
    VOTE_CHANGE_NOT_ALLOWED("error.model.VoteForAnotherUpdatePolicy.no_vote_chnage"),
    NO_VOTE_POLICY_MATCH("error.model.lunchplacepoll.no_policy_match"),
    POLL_NOT_ACTIVE("error.model.commonpolicy.poll_not_active"),
    TIMECONSTRAINT_END_BEFORE_START("error.model.timecontraint.end_before_start"),
    VOTECHANGETHRESHOLD_INVALID("error.model.timecontraint.vote_change_threshold_invalid"),
    JAR_REQUESTER_ALREADY_IN_AREA("error.model.jar.requester_already_in_area"),
    AUTH_CREDENTIALS_NOT_FOUND("error.auth.credentials_not_found"),
    AUTH_AREA_NOT_ASSIGNED("error.auth.area_not_assigned"),
    AUTH_NO_PERMISSIONS(""),
    AUTH_BAD_CREDENTIALS(""),
    URL_NOT_FOUND(""),
    METHOD_NOT_ALLOWED(""),
    UNEXPECTED_CONTENT("");

    private final String messageCode;

    ErrorCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
