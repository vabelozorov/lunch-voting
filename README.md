# Lunch Place Voting system with a REST API

Ever wasted time arguing with your friends or colleagues
where all of you are having a chow today? That is what this project is for.
It allows its users to handle this matter in an honest and
democracy-approved way - by voting.
This is a backend with a REST API which provides the following functionality:
 * 2 types of users: admin and regular mortal voters
 * Admins input a restaurant and its lunch menu of the day
 * Menus are updated daily by admins
 * Voters... well, vote for a restaurant/pub/cafe they want to have lunch at
 * Only one vote is accepted per voter
 * Repetitive votes are handled:
    - before 11:00, it is assumed that a voter changed his/her mind
    - after 11:00, a vote can't be changed

## Project technical information

**Main technology stack**

Spring, Spring MVC, Spring Security, Hibernate, PostgreSQL/HSQLDB, Jackson, Lombok

**Testing**

Spring Testing, JUnit, Mockito, Db Utilities, P6Spy, datasource-proxy

**Numbers**

Almost 9000 lines of Java source code, 160+ tests, 40+ REST API endpoints, 30+ direct Maven dependencies

## API
### Components Overview
###### Area
A logical entity that groups together a community of users and their activity, such as creating
places and menus, creating polls to select the best place and voting for the best place.
 Activity in one area is not seen by users of other areas.

 Area can be created by any user regardless of whether they belong to an area or not. If a user belongs to
 an area, creating a new one makes him/her join this area with **ADMIN** role and leave a previous one.

###### Voter
A subject that creates or joins a particular area of the choice and performs activities in this area.

###### Lunch Place
Represents any place where users are interested to have a meal together. Its main components are
 menus.

###### Menu
Describes a lunch place's menu for a particular day and are created on a regular basis. Consists of
 a number of dishes.

###### Poll
An entity that manages a voting process for the best lunch place. Its items are lunch places which have
menus for the particular date for which the poll is created.

### Typical Workflow
1. User registers.
2. User creates a new area and becomes and its admin.
3. Other users register and search for an area of their interest by its name
4. User selects an area and makes a request to join an area.
5. The area admin approves the request.
6. The area admin inputs lunch places along with its menus and dishes.
7. The area admin creates a poll to start voting for the best lunch place.
8. Users vote in the poll.
9. Poll results are displayed.

### API Reference
