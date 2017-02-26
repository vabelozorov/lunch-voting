package ua.belozorov.lunchvoting.model.voting.polling.votedecisions;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Set;

/**

 *
 * Created on 19.01.17.
 */
public interface VotePolicyDecision {
    /**
     * Defines whether check should by continued by another policy checking instance
     * if this method is true, {@code getAcceptedVote} method and {@code votesToBeRemoved} method must raise {@link sun.reflect.generics.reflectiveObjects.NotImplementedException}
     * and {@code isAccept}, {@code isUpdate} methods must return false, as it follows a common sense
     * @return Returns true if the result of policy compliance check was to continue checking, false otherwise
     */
    boolean isContinue();

    /**
     * Defines that a policy checking instance came to conclusion that Vote meets requirements for accepting it
     * if this method is true, {@code getAcceptedVote} method must return a Vote instance to be added to a set of accepted votes
     * and {@code votesToBeRemoved} method must raise {@link sun.reflect.generics.reflectiveObjects.NotImplementedException}.
     * {@code isContinue}, {@code isUpdate} methods must return false, as it follows a common sense
     * @return Returns {@code true} if the result of policy compliance check was to accept (add) vote to accepted set of votes
     * or false otherwise
     */
    boolean isAccept();

    /**
     * Defines that a policy checking instance came to conclusion that Vote meets requirements for updating it
     * (that is, a set of votes have to be removed and the current vote must be added). Such set of votes must be provided by {@code votesToBeRemoved} method
     * {@code isContinue}, {@code isAccept} methods must return false, as it follows a common sense.
     * @return Returns {@code true} if the result of policy compliance check was to update (replace) defined set of votes for the current Vote
     * or false otherwise
     */
    boolean isUpdate();

    /**
     * If {@code isAccept()} or {@code isUpdate()} method returns true, this method should return a Vote to be added to the set of accepted Votes
     * Otherwise {@code sun.reflect.generics.reflectiveObjects.NotImplementedException} must be raised
     * @return
     */
    Vote getAcceptedVote();

    /**
     If {@code isUpdate()} method returns true, this method should return a Set<Vote> to be removed before adding the current Vote
     * Otherwise {@code sun.reflect.generics.reflectiveObjects.NotImplementedException} must be raised
     * @return
     */
    Set<Vote> votesToBeRemoved();
}
