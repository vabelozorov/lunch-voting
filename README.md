# Lunch Place Voting system with REST API

Ever wasted a valuable time arguing with your friends or colleagues
where all of you are having a little chow today? That is what this project is for.
It allows its users to handle this matter in an honest and
democracy-approved way - by voting.
This is a backend with a REST API which provides the following functionality:
 * 2 types of users: admin and regular mortal voters
 * Admin inputs a restaurant and its lunch menu of the day
 * Menus are updated daily by admins
 * Voters vote for a restaurant/pub/cafe they want to have lunch at
 * Only one vote is accepted per voter
 * Repetitive votes are handled:
    - before 11:00, it is assumed that a voter changed his/her mind
    - after 11:00, a vote can't be changed
