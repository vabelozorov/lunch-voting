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

Almost 9000 lines of Java source code, 160+ tests, 40+ REST API endpoints, 30+ direct Maven dependencies

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
2. User creates a new area and becomes and its admin.
3. Other users register and search for an area of their interest by its name
4. User selects an area and makes a request to join an area.
5. The area admin approves the request.
6. The area admin inputs lunch places along with its menus and dishes.
7. The area admin creates a poll to start voting for the best lunch place.
8. Users vote in the poll.
9. Poll results are displayed.

### API Reference
#### Area
Creates an Area, adds the creator to the lists of area members and
 sets his rights to <strong>ADMIN</strong>
###### Summary
 <table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
     <tr>
         <td>HTTP Request</td>
         <td><font style="color:green"><code>HTTP POST /api/areas 201</code></font></td>
     </tr>
     <tr>
         <td>Request Content-Type</td>
         <td><code>application/x-www-form-urlencoded</code></td>
     </tr>
     <tr>
         <td>Required Request Parameters</td>
         <td><code>name</code></td>
     </tr>
     <tr>
         <td>Optional Parameters</td>
         <td>none</td>
     </tr>
     <tr>
         <td>Requires role</td>
         <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
     </tr>
 </table>

###### Request Parameters
 * **name** a unique name for the area, must 2-50 characters long. Must be unique within area

###### Response
<ul>
    <li>An HTTP Status 201 Created </li>
    <li>A URL to access the created object in HTTP Location Header</li>
    <li>A JSON object with a field <code>id</code> containing the ID of the newly
    created area</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted name is not unique</li>
</ul>

---
Registers a new User in the area of a currently authenticated user
###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/members/ 201</code></font><br>
             <b>{areaId}</b> existing area ID
       </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/json</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>name<br>email<br>password<br></code></td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
<ul>
 <li><strong>name</strong>  name of the User, must be between 2 and 100 characters long</li>
 <li><strong> password</strong>  password of the User, must be between 6 and 30 characters long</li>
 <li><strong>email</strong>  email of the User. Must be unique</li>
</ul>

###### Response
<ul>
    <li>An HTTP Status 201 Created </li>
    <li>A URL to access the created object in HTTP Location Header</li>
    <li>A JSON object with a field <code>id</code> containing the ID of the newly created user</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted email is not unique</li>
</ul>

---
Creates a new Lunch Place in the Area of an authenticated user
###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/places 201</code></font><br>
            <b>{areaId}</b> existing area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/json</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>name</code></td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td><code>address<br>description<br>phones</code></td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
<ul>
 <li><strong>name</strong>  name of a new Lunch Place, should be unique and be between 2 and 50 characters long</li>
 <li><strong>address</strong>  address of the Lunch Place, must not exceed 200 characters, optional field</li>
 <li><strong>description</strong>  description of the Lunch Place, must not exceed 1000 characters, optional field</li>
 <li><strong>phones</strong> phones of the Lunch Place, comma-separated list of strings, each string is 10-digit value</li>
</ul>

###### Response
<ul>
    <li>HTTP Status 201 Created</li>
    <li>A URL to access the created object in HTTP Location Header</li>
    <li>A JSON object with a field <code>id</code> containing the ID of the newly created <code>Lunch Place</code></li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted name value is not unique</li>
</ul>

---
Creates a new Poll in the area of an authenticated <code>User</code>
<p>Items of the poll are Lunch Place objects with menus for given date or a current day, if not given.</p>
<p>
    The poll starts at <code>start</code> time or will be 09-00 of a current day.<br>
    The poll ends at <code>end</code> time or will be 12-00 of a current day.<br>
    The poll final time to change a vote is <code>change</code> time or will be 11-00 of a current day.
</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/polls 201</code></font><br>
            <b>{areaId}</b> existing area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td><code>menuDate<br>start<br>end<br>change</code></td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
 * **menuDate** date of Menu which determines which LunchPlace objects will be added as items to the Poll
 optional. Default value is 09-00 of a current day
 * **start** time when the poll starts, optional. Default value is 12-00 of a current day
 * **end** time when the poll ends, optional. Default value is 11-00 of a current day
 * **change** time before which user can change a vote, optional. Default value is 11-00 of a current day.
The following constraint applies: start <= change <= end

###### Response
<ul>
    <li>HTTP Status 201 Created</li>
    <li>A URL to access the created object in HTTP Location Header</li>
    <li>A JSON object with a field <code>id</code> containing the ID of the newly created <code>Poll</code></li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 422 Unprocessable_entity is returned if no <code>LunchPlace</code> objects
    with <code>menudate</code></li>
</ul>

#### Voter

#### Lunch Place

#### Menu

#### Poll