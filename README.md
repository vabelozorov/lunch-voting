# Lunch Place Voting system with a REST API

Ever wasted time arguing with your friends or colleagues
where all of you are having a chow today? That is what this project is for.
It allows its users to handle this matter in an honest and
democracy-approved way - by voting.
This is a backend with a REST API which provides the following functionality:
 * 2 types of users: admin and regular users
 * admins input a restaurant and its lunch menu of the day
 * menus are updated daily by admins
 * users vote for a restaurant/pub/cafe they want to have lunch at
 * only one vote is accepted per user
 * Repetitive votes are handled:
    - before 11:00, it is assumed that a voter changed his/her mind
    - after 11:00, a vote can't be changed

## Project technical information

**Main technology stack**

Spring, Spring MVC, Spring Security, Hibernate, PostgreSQL/HSQLDB, Jackson, Lombok

**Testing**

Spring Testing, JUnit, Mockito, Db Utilities, P6Spy, datasource-proxy

**Numbers**

9000+ lines of Java source code, 160+ tests, 40+ REST API endpoints, 30+ direct Maven dependencies

## API
### Components Overview
###### Area
A logical entity that groups together a community of users and their activity, such as creating
places and menus, creating polls to select the best place and voting for the best place.
 Activity in one area is not seen by users of other areas.

 Area can be created by any user regardless of whether they belong to an area or not. If a user belongs to
 an area, creating a new one makes him/her join this area with **ADMIN** role and leave a previous one.

###### User
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
    * let's use a name `cleis`, email (username) `cleis@email.com` and password `thenes`.
    ```
    curl -i -X POST -H "Content-Type: application/json" -d '{
      "name" : "cleis",
      "email" : "cleis@email.com",
      "password" : "thenes"
    }' "http://localhost:8080/lunch-voting/api/profile"
    ```
    * now he can request his own profile with
    ```
    curl -X GET -H "Authorization: Basic Y2xlaXNAZW1haWwuY29tOnRoZW5lcw==" \
    -H "Accept: application/json" "http://localhost:8080/lunch-voting/api/profile"
    ```


2. User creates a new area which makes this user its admin.
    * let's create an area with name `Athenes`
    ```
    curl -i -X POST -H "Authorization: Basic Y2xlaXNAZW1haWwuY29tOnRoZW5lcw==" \
    -H "Accept: application/json" -H "Content-Type: application/x-www-form-urlencoded" \
    -d 'name=Athenes' "http://localhost:8080/lunch-voting/api/areas"
    ```

3. Other users register and search for an area of their interest by its name.
    * another user registers
    ```
    curl -i -X POST -H "Content-Type: application/json" -d '{
      "name" : "voter1",
      "email" : "voter1@email.com",
      "password" : "voter1pass"
    }' "http://localhost:8080/lunch-voting/api/profile"
    ```
    * finds area
    ```
    curl -X GET -H "Authorization: Basic dm90ZXIxQGVtYWlsLmNvbTp2b3RlcjFwYXNz" \
    -H "Accept: application/json" "http://localhost:8080/lunch-voting/api/areas?name=Ath"
    ```
4. User selects an area and makes a request to join it.
    * make a Join Request using an ID from from the previous request
    ```
    curl -i -X POST -H "Authorization: Basic dm90ZXIxQGVtYWlsLmNvbTp2b3RlcjFwYXNz" \
    -H "Accept: application/json" "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/requests"
    ```
5. The area admin approves the request, which allows the user to browse through Area's lunch places and polls

    ```
    curl -i -X PUT -H "Authorization: Basic Y2xlaXNAZW1haWwuY29tOnRoZW5lcw==" \
    -H "Content-Type: application/x-www-form-urlencoded" -d 'status=APPROVED' \
    "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/requests/{YOUR_REQUEST_ID}"
    ```
6. The area admin inputs lunch places along with its menus and dishes.
    * add a nice restaurant
    ```
    curl -i -X POST -H "Authorization: Basic Y2xlaXNAZW1haWwuY29tOnRoZW5lcw==" -H "Accept: application/json" \
    -H "Content-Type: application/json" -d '{
      "name" : "Дача",
      "address" : "Французский бул., 85",
      "description" : "Приходите к закату",
      "phones" : [ "0674811430", "0661234567" ]
    }' "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/places"
    ```
    * ...and its menu which is effective for a certain date, for example, 2017-03-02
    ```
    curl -i -X POST -H "Authorization: Basic Y2xlaXNAZW1haWwuY29tOnRoZW5lcw==" -H "Accept: application/json" \
    -H "Content-Type: application/json" -d '{
      "effectiveDate" : "2017-03-02",
      "dishes" : [ {
        "name" : "First Dish",
        "price" : 20.21,
        "position" : 0
      }, {
        "name" : "Second Dish",
        "price" : 10.12,
        "position" : 1
      } ]
    }' "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/places/{YOUR_PLACE_ID}/menus"
    ```
    * by the way, let's check that everything has been created successfully
    ```
    curl -X GET -H "Authorization: Basic dm90ZXIxQGVtYWlsLmNvbTp2b3RlcjFwYXNz" \
    "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/places/{YOUR_PLACE_ID}?fields=name,menus"
    ```
7. The area admin creates a poll to start voting for the best lunch place.
    * let's create a poll for a specific date, the same date as `menuDate` from the previous request.
    Set `start` and `end` of the poll to some distant time in the past and in the future respectively.
     Doesn't make much sense, but simplifies the usage of this example=)
     ```
     curl -X POST -H "Authorization: Basic Y2xlaXNAZW1haWwuY29tOnRoZW5lcw==" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d 'menuDate=2017-03-02&start=2017-01-01 00:00:00&end=2100-12-12 12:00:00' \
     "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/polls"
     ```
8. Users vote in the poll.
    * firstly, let's get a detail view of our poll
    ```
    curl -X GET -H "Authorization: Basic dm90ZXIxQGVtYWlsLmNvbTp2b3RlcjFwYXNz" \
    "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/polls/{YOUR_POLL_ID}"
    ```
    * then vote!
    ```
    curl -X POST -H "Authorization: Basic dm90ZXIxQGVtYWlsLmNvbTp2b3RlcjFwYXNz" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d 'pollId={YOUR_POLL_ID}&pollItemId={YOUR_POLLITEM_ID}'
    "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/votes"
    ```
9. Poll results are displayed.

    ```
    curl -X GET -H "Authorization: Basic dm90ZXIxQGVtYWlsLmNvbTp2b3RlcjFwYXNz" \
    "http://localhost:8080/lunch-voting/api/areas/{YOUR_AREA_ID}/votes/results?type=item&pollId={YOUR_POLL_ID}
    ```

### API Reference

Refer to the [Wiki](https://github.com/vabelozorov/lunch-voting/wiki)
