package ua.belozorov.lunchvoting.web.exceptionhandling;

/**

 *
 * Created on 07.02.17.
 */
public enum ErrorCode {
    DUPLICATE_EMAIL("error.model.user.duplicate_email"),
    DUPLICATE_AREA_NAME("error.model.eatingarea.duplicate_name"),
    DUPLICATE_PLACE_NAME("error.model.lunchplace.duplicate_name"),
    ENTITY_NOT_FOUND("error.service.entity_not_found"),
    AREA_HAS_ONE_ADMIN("error.service.eatingareaservice.one_admin"),
    PARAMS_VALIDATION_FAILED(""),
    MULTIPLE_VOTE_PER_SAME_ITEM("error.model.commonpolicy.multiple_vote_per_item"),
    NO_MENUS_FOR_MENU_DATE("error.model.lunchplacepoll.no_menus"),
    NO_POLL_ITEMS("error.model.lunchplacepoll.no_pollitems"),
    VOTE_CHANGE_NOT_ALLOWED("error.model.VoteForAnotherUpdatePolicy.no_vote_chnage"),
    NO_VOTE_POLICY_MATCH("error.model.lunchplacepoll.no_policy_match"),
    POLL_NOT_ACTIVE("error.model.commonpolicy.poll_not_active"),
    TIMECONSTRAINT_END_BEFORE_START("error.model.timecontraint.end_before_start"),
    VOTECHANGETHRESHOLD_INVALID("error.model.timecontraint.vote_change_threshold_invalid"),
    JAR_REQUESTER_ALREADY_IN_AREA("error.model.jar.requester_already_in_area");

    private final String messageCode;

    ErrorCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
