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
 * **menuDate** date of Menu which determines which LunchPlace objects will be added as items to the Poll,
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

---
Updates an Area name.
###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
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
        <td><strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
 * **name** a unique name for the area, must 2-50 characters long.
###### Response
<ul>
 <li>An HTTP Status 204 Created </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted name is not unique</li>
</ul>

---
<p>Returns a requested Area by its ID.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId} 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
  * **id** ID of existing <code>Area</code>
  * **summary** true causes the summary information to be included into a response instead of IDs
  *                of User, Poll and Lunch Place objects. <code>true</code> means that IDs
  *                are included, but summary info is not.

###### Response
<ul>
    <li>An HTTP Status 200 Ok</li>
    <li>JSON object with the following fields:
        <ul>
            <li>if <code>summary=true</code>, <code>id, name, created, userCount, placeCount, pollCount</code> are included</li>
            <li>if <code>summary=false</code>, <code>id, name, created, users, places, polls</code> are included</li>
        </ul>
    </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
</ul>

---
<p>Returns Area objects which name starts with a given string.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
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
 * **name** start of <code>EatingArea</code> name, must be 2-50 characters long
###### Response
<ul>
    <li>An HTTP Status 200 Ok</li>
    <li>JSON object with the following fields: <code>id, name, created</code> are included</li>
</ul>

###### Response on failure
 <ul>
     <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
 </ul>

---
<p>Deletes an Area objects and all its Lunch Place and Poll objects that area associated with it.
Users of the area does not belong to any area after this request is complete</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP DELETE /api/areas 204</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><<strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
None
###### Response
<ul>
    <li>An HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
No standard failure scenarios

#### JoinRequest

<p>Creates a new JoinRequest to join a given area.
Any user can submit a request to join an area no matter whether he/she currently has a member of a particular
 area. Certain restrictions applies to only approving the requests (check corresponding section below)</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/{areaId}/requests 201</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **areaId**  ID of the Area a current user wants to join

###### Response
<ul>
    <li>An HTTP Status 201 Created</li>
    <li>JSON object with a field <code>id</code> which contains the ID of the newly created JoinRequest</li>
</ul>

###### Response on failure
 * HTTP Status 404 Not_Found is returned if a <code>areaId</code> refers to a non-existent Area

---
<p>Retrieves a JoinRequest by its ID, previously made by a currently authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/{areaId}/requests/{requestId} 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{areaId}</b> existing JoinRequest ID<br>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **requestId**  ID of JoinRequest made in the area of a currently authenticated user
###### Response
<ul>
    <li>HTTP Status 200 Ok </li>
    <li>JSON object with fields <code>id, area, requester, status, created, decidedOn</code></li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found is returned if a {@link JoinAreaRequest} with the given ID does not exist
    in the user's {@link EatingArea}</li>
</ul>

---
<p>Retrieves all JoinRequest} objects in the area of a currently authenticated user
 with the given JoinStatus.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/{areaId}/requests 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>status</code></td>
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
 * **status** a request status. Can be: PENDING, APPROVED, REJECTED, CANCELLED
###### Response
<ul>
    <li>HTTP Status 200 Ok</li>
    <li>JSON array where each object has fields <code>id, area, requester, status, created, decidedOn</code></li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
</ul>

---
<p>Retrieves all JoinRequests of a currently authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/{areaId}/requests 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
None
###### Response
<ul>
    <li>HTTP Status 200 Ok</li>
    <li>JSON array where each object has fields <code>id, area, requester, status, created, decidedOn</code></li>
</ul>

###### Response on failure
No standard failure scenarios

---
<p>Approves a specified JoinRequest.
The request can be approved regardless of the membership or its absence in a certain area. The requester can be
a voter or an admin in an area. However, if the requester is the last user with <strong>ADMIN</strong> right in
his current area, approval for such request fails.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/{areaId}/requests/{requestId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{requestId}</b> existing JoinRequest ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>status</code></td>
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
 * **requestId** ID of JoinRequest in the area of approving user
###### Response
<ul>
    <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found, if a JoinRequest with the given ID does not exist
    in the approving user's Area </li>
    <li>HTTP Status 422 Unprocessable entity, if the requester has an area membership and
    is the only ADMIN in that area.</li>
</ul>

---
<p>Rejects a specified JoinRequest.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/{areaId}/requests/{requestId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{requestId}</b> existing JoinRequest ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>status</code></td>
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
 * **requestId** ID of JoinRequest in the area of approving user

###### Response
<ul>
    <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found is returned if a JoinRequest with the given ID does not exist
    in the approving user's Area</li>
</ul>

---
<p>Cancels a specified JoinRequest previously made by a currently authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/{areaId}/requests/{requestId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{requestId}</b> existing JoinRequest ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>status</code></td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>VOTER</strong></td>
    </tr>
</table>

###### Request Parameters
 * **requestId** ID of JoinRequest in the area of a currently authenticated user

###### Response
 <ul>
     <li>HTTP Status 204 No_Content</li>
 </ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found is returned if a JoinRequest with the given ID does not exist
    in the currently authenticated user's Area</li>
</ul>

#### User (Profile Management)
<p>Registers a new User. This endpoint should be used when a new user is creating his/her profile.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/profile/ 201</code></font><br></td>
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
        <td>does not require</td>
    </tr>
</table>

###### Request Parameters
<ul>
 <li><strong>name</strong>  name of the User, must be between 2 and 100 characters long</li>
 <li><strong> password</strong>  password of the User, must be between 6 and 30 characters long</li>
 <li><strong>email</strong>  email of the User. Must be unique.</li>
</ul>

###### Response
<ul>
    <li>An HTTP Status 201 Created </li>
    <li>A URL to access the created object in HTTP Location Header</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted email is not unique</li>
</ul>

---
<p>Updates an existing User. If a certain user property is not changed,
its current value should be included in the request.
This endpoint should be used when a user is updating his/her profile</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/profile/{userId} 204</code></font><br>
            <b>{userId}</b> existing User ID
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
        <td>any authenticated user</td>
    </tr>
</table>

###### Request Parameters
<ul>
    <li><b>userId</b> existing User ID</li>
    <li><strong>name</strong>  new name of the User, must be between 2 and 100 characters long</li>
    <li><strong> password</strong>  new password of the User, must be between 6 and 30 characters long</li>
    <li><strong>email</strong>  new email of the User. Must be unique</li>
</ul>

###### Response
<ul>
    <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted email value is not unique</li>
    <li>HTTP Status 404 Not_Found is returned if a user with the given ID does not exist</li>
</ul>

---
<p>Retrieves an authenticated User. This endpoint should be used when displaying a profile to a user</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/profile 200</code></font><br>
            </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td>any authenticated user</td>
    </tr>
</table>

###### Request Parameters
None
###### Response
<ul>
    <li>HTTP Status 200 Ok </li>
    <li>JSON object with fields <code>userId, name, email, roles, registeredDate, activated, areaId</code> </li>
</ul>

###### Response on failure
No standard failure scenarios

---
<p>Updates an existing User. This endpoint should be used for creating a user by an admin.
If a certain User property is not changed, its old value should be included in the request.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/members/{userId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{userId}</b> existing User ID
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
 <li><b>userId</b> existing User ID</li>
 <li><strong>name</strong>  new name of the User, must be between 2 and 100 characters long</li>
 <li><strong> password</strong>  new password of the User, must be between 6 and 30 characters long</li>
 <li><strong>email</strong>  new email of the User. Must be unique</li>
</ul>

###### Response
<ul>
    <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 409 Conflict is returned if the submitted email value is not unique</li>
    <li>HTTP Status 404 Not_Found is returned if a <code>userId</code>  refers to a non-existent User
    in the Area of an authenticated user/li>
</ul>

---
<p>Retrieves a User with a given ID.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/members/{userId} 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{userId}</b> existing User ID
       </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><none</td>
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
 * **userId** existing user ID in the area of an authenticated user
###### Response
<ul>
    <li>HTTP Status 200 Ok </li>
    <li>JSON object with fields <code>userId, name, email, roles, registeredDate, activated, areaId</code> </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
    of an authenticated user/li>
</ul>

---
<p>Retrieves all User objects in the Area of an authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/members 200</code></font><br>
            <b>{areaId}</b> existing Area ID
            </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><none</td>
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
None
###### Response
<ul>
    <li>HTTP Status 200 Ok </li>
    <li>JSON array where each object has fields {@code id, name, email, roles, registeredDate,
    activated, areaId} </li>
</ul>
###### Response on failure
No standard failure scenarios

---
<p>Deletes a User with a given ID in the Area of an authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP DELETE /api/areas/{areaId}/members/{userId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{userId}</b> existing User ID
            </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><none</td>
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
 * **userId** existing user ID in the Area of an authenticated user
###### Response
<ul>
    <li>HTTP Status 204 No_Content </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
    of an authenticated user/li>
</ul>

---
<p>Activates/deactivates a user account.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/members/{userId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{userId}</b> existing User ID
            </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>activated</code></td>
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
 * **userId** existing <code>User</code> ID in the Area of an authenticated User
 * **activated** <code>true</code> to activate the account, <code>false</code> to deactivate the account

###### Response
<ul>
    <li>HTTP Status 204 No_Content </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
    of an authenticated user/li>
</ul>

---
<p>Sets roles for a user account.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/members/{userId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{userId}</b> existing User ID
            </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>roles</code></td>
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
   <li><code>userId</code> existing user ID</li> in the area of an authenticated user
   <li><code>roles</code> comma-separated list of values. Valid values are: ADMIN, VOTER</li>
</ul>

###### Response
<ul>
    <li>HTTP Status 204 No_Content </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
    of an authenticated user/li>
</ul>

#### Lunch Place
<p>Updates an existing LunchPlace object.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP PUT /api/areas/{areaId}/places/{placeId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{placeId}</b> existing LunchPlace ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/json</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td><code>name<br>address<br>description<br>phones</code></td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
<ul>
    <li><b>name</b> (2-50 characters)</li>
    <li><b>address</b> string, up to 200 characters</li>
    <li><b>description</b> string, up to 1000 characters</li>
    <li><b>phones</b> an array of strings with length <=5, each string consists of 10 digits</li>
</ul>

###### Response
<ul>
     <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found is returned if <code>userId</code> refers to a non-existent object in the Area
    of an authenticated user/li>
</ul>

---
<p>Returns a LunchPlace with a given IDs in the area of an authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/places/{placeId} 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{placeId}</b> existing LunchPlace ID
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
        <td><code>fields<br>startDate<br>endDate</code></td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
<ul>
    <li><b>placeId</b> ID of a LunchPlace in the Area of an authenticated user</li>
   <li><strong>fields</strong>  fields to display in the returned JSON response. Available values:
<code>name, address, description, phones, menus</code>. Additionally, a field <code>id</code> is always contained
in the response.</li>
   <li><strong>startDate</strong>  instructs to include in the response LunchPlace objects which
have menus belonging to the time range starting with <strong>startDate</strong>. Only matching menus are
included to the list of LunchPlace menus.</li>
   <li><strong>endDate</strong>  instructs to include in the response LunchPlace objects which
have menus belonging to the time range ending with <strong>endDate</strong>. Only matching menus are
included to the list of LunchPlace menus.</li>
</ul>

###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON array of objects each containing fields <code>id, name</code> if <code>fields</code> parameter
     was not specified in the request. Otherwise each JSON object will contain a mandatory <code>id</code> field and
     fields that were specified by a user.</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found is returned if <code>placeId</code> refers to a non-existent object in the Area
    of an authenticated user/li>
</ul>

---
<p>Returns multiple LunchPlace objects from the area of an authenticated user.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/places 200</code></font><br>
            <b>{areaId}</b> existing Area ID
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
        <td><code>ids<br>fields<br>startDate<br>endDate</code></td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
<ul>
   <li><strong>ids</strong>  specifies which objects a user wants to request. If not provided, all objects
will be returned</li>
   <li><strong>fields</strong>  fields to display in the returned JSON response. Available values:
<code>name, address, description, phones, menus</code>. Additionally, a field <code>id</code> is always contained
in the response</li>
   <li><strong>startDate</strong>  instructs to include in the response LunchPlace objects which
have menus belonging to the time range starting with <strong>startDate</strong>. Only matching menus are
included to the list of LunchPlace menus</li>
   <li><strong>endDate</strong>  instructs to include in the response LunchPlace objects which
have menus belonging to the time range ending with <strong>endDate</strong>. Only matching menus are
included to the list of LunchPlace menus</li>
</ul>

###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON array of objects each containing fields <code>id, name</code> if <code>fields</code> parameter
     was not specified in the request. Otherwise each JSON object will contain a mandatory <code>id</code> field and
     fields that were specified by a user.</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found is returned if a <code>ids</code> refers to
    non-existing  LunchPlace in the authenticated user's Area.</li>
</ul>

---
<p>Deletes a {@link LunchPlace} with a given ID in the area of the authenticated user. </p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green">HTTP DELETE /api/areas/{areaId}/places/{placeId} 204</font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{placeId}</b> existing LunchPlace ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **id** ID of existing LunchPlace in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found is returned if a <code>ids</code> refers to
    non-existing  LunchPlace in the authenticated user's Area.</li>
</ul>

#### Menu
<p>Adds a new Menu to the LunchPlace specified by ID</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/places/{placeId}/menus 201 </code></font>
            <b>{areaId}</b> existing Area ID<br>
            <b>{placeId}</b> existing LunchPlace ID
         </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/json</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>effectiveDate<br>dishes : {name, price, position}</code></td>
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
<li><b>placeId</b> an ID of an existing LunchPlace in the area of an authenticated user</li>
<li><code>effectiveDate</code> date for which the created Menu will be valid</li>
<li><code>dishes</code> array of Dish objects with mandatory fields
   <ul>
       <li><code>name</code> dish name, must be 2-50 characters long</li>
       <li><code>price</code> floating value, must be >= 0 </li>
       <li><code>position</code> integer value, must be >= 0 </li>
   </ul>
   </li>
</ul>

###### Response
<ul>
     <li>HTTP Status 201 Created</li>
     <li>A URL to access the created object in HTTP Location Header</li>
     <li>A JSON object with a field <code>id</code> containing the ID of the newly created <code>Menu</code></li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400 Bad_Syntax is returned if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found, if <code>placeId</code> refers to a non-existent object
    in the area of an authenticated user</li>
</ul>

---
<p>Returns a Menu with a given ID.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/places/{placeId}/menus/{menuId} 200</code></font>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **placeId** an ID of an existing LunchPlace in the area of an authenticated user
 * **menuId** an ID of an existing Menu in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>a JSON object with fields <code>id, effectiveDate, lunchPlaceId, dishes</code></li>
     <li><code>dishes</code> is a JSON array of Dish object with fields <code>name, price, position</code>
     </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found, if <code>placeId</code> or <code>menuId</code> refers to a non-existent object
    in the area of an authenticated user</li>
</ul>

---
<p>Deletes a Menu by its ID</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP DELETE  /api/areas/{areaId}/places/{placeId}/menus/{menuId} 204</code></font>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **placeId** an ID of an existing LunchPlace in the area of an authenticated user
 * **menuId** an ID of an existing Menu in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found, if <code>placeId</code> or <code>menuId</code> refers to a non-existent object
    in the area of an authenticated user</li>
</ul>

#### Poll

<p>Returns a LunchPlacePoll object by its ID.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/{pollId} 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
            <b>{pollId}</b> existing Poll ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **pollId** ID of existing Poll in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON object with fields <code>id, menuDate, timeConstraint, pollItems</code><br>
         <code>timeConstraint</code> is an object with fields <code>startTime, endTime, voteChangeThreshold</code><br>
             <code>pollItems</code> is an array of objects with fields <code>id, position, itemId</code>, where <code>itemId</code>
             refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found is returned if <code>pollId</code> refers to non-existent object
    in the authenticated user's Area</li>
</ul>

---
<p>Returns a list of Poll objects in the area.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls 200</code></font><br>
            <b>{areaId}</b> existing Area ID<br>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
None
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON array where each object contains fields <code>id, menuDate, timeConstraint, pollItems</code><br>
         <code>timeConstraint</code> is an object with fields <code>startTime, endTime, voteChangeThreshold</code><br>
             <code>pollItems</code> is an array of objects with fields <code>id, position, itemId</code>, where <code>itemId</code>
             refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
</ul>

###### Response on failure
No standard failure scenarios

---
<p>Returns a list of Poll objects in the area filtered by their start or/and end time.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Optional Parameters</td>
        <td><code>start<br>end</code></td>
    </tr>
    <tr>
        <td>Requires role</td>
        <td><strong>VOTER</strong> or <strong>ADMIN</strong></td>
    </tr>
</table>

###### Request Parameters
 * **start** a start of filtering range for poll active time. If null, it's set to 1900/1/1 00:00:00
 * **end** an end of filtering range for poll active time. If null, it's set to 7777/1/1 00:00:00
###### Response
 <li>HTTP Status 200 Ok</li>
 <li>JSON array where each object contains fields <code>id, menuDate, timeConstraint, pollItems</code><br>
     <code>timeConstraint</code> is an object with fields <code>startTime, endTime, voteChangeThreshold</code><br>
     <code>pollItems</code> is an array of objects with fields <code>id, position, itemId</code>, where <code>itemId</code>
     refers to a LunchPlace object
</li>

###### Response on failure
No standard failure scenarios

---
<p>Returns a list of polls in the area which have ended.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/past 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
None
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON array where each object contains fields <code>id, menuDate, timeConstraint, pollItems</code><br>
         <code>timeConstraint</code> is an object with fields <code>startTime, endTime, voteChangeThreshold</code><br>
         <code>pollItems</code> is an array of objects with fields <code>id, position, itemId</code>, where <code>itemId</code>
         refers to a LunchPlace object</li>
</ul>

###### Response on failure
No standard failure scenarios

---
<p>Returns a list of polls in the area which are active for the moment of the request.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/active 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
None
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON array where each object contains fields <code>id, menuDate, timeConstraint, pollItems</code><br>
         <code>timeConstraint</code> is an object with fields <code>startTime, endTime, voteChangeThreshold</code><br>
         <code>pollItems</code> is an array of objects with fields <code>id, position, itemId</code>, where <code>itemId</code>
         refers to a LunchPlace object</li>
</ul>

###### Response on failure
No standard failure scenarios

---
<p>Returns a list of polls in the area which are scheduled for some time in the future.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/future 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
None
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>JSON array where each object contains fields <code>id, menuDate, timeConstraint, pollItems</code><br>
         <code>timeConstraint</code> is an object with fields <code>startTime, endTime, voteChangeThreshold</code><br>
         <code>pollItems</code> is an array of objects with fields <code>id, position, itemId</code>, where <code>itemId</code>
         refers to a {@link ua.belozorov.lunchvoting.model.lunchplace.LunchPlace} object</li>
 </ul>

###### Response on failure
No standard failure scenarios

---
<p>Provides an information whether a poll is active for the moment of the request.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/polls/active 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>pollId</code></td>
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
 * **pollId** ID of LunchPlacePoll in the area of authenticated user
###### Response
<ul>
     <li>HTTP Status 200 Ok</li>
     <li>a JSON object with a fields:
         <ul>
             <li><code>pollId</code> specifies requested poll ID</li>
             <li><code>active</code> specifies the poll state as <code>true</code> or <code>false</code></li>
         </ul>
     </li>
 </ul>

###### Response on failure
<ul>
    <li>HTTP Status 404, if <code>pollId</code> refers to a non-existent Poll in the area of
    authenticated user</li>
</ul>

---
<p>Deletes a poll with a given ID.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP DELETE /api/areas/{areaId}/polls/{pollId} 204</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **pollId** ID of LunchPlacePoll in the area of authenticated user
###### Response
<ul>
     <li>HTTP Status 204 Ok</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404, if <code>pollId</code> refers to a non-existent Poll in the area of
    authenticated user</li>
</ul>

---
<p>Accepts and review a vote from an authenticated user.</p>
<p>Voter can vote for a poll when the poll is active. Only one vote per poll is accepted.
Voter can change his/her mind before poll's voteChange time. In that case the previous vote is deleted and a new
vote is accepted</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP POST /api/areas/{areaId}/votes 201</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td><code>application/x-www-form-urlencoded</code></td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>pollId, pollItemId</code></td>
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
 * **pollId** ID of existing Poll in the area of an authenticated user
 * **pollItemId** ID of existing Poll in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 201 Created</li>
     <li>a JSON object with fields <code>id, voterId, pollId, itemId</code></li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found, if <code>pollId</code> or <code>pollItemId</code> refers to a non-existent object
    in the area of an authenticated user</li>
    <li>HTTP Status 422 Unprocessable_Entity, if:
        <ul>
            <li>the poll is not active</li>
            <li>vote change attempt is made after the
                corresponding time threshold</li>
            <li>2nd and subsequent attempt to vote for the same item is made</li>
        </ul>
    </li>
</ul>

---
<p>Returns all Vote objects made for a specified poll.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/votes 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>pollId</code></td>
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
 * **pollId** ID of existing Poll in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 200</li>
     <li>a JSON object with fields:
         <ul>
             <li><code>pollId</code></li>
             <li><code>votes</code> a JSON array of Vote objects with fields <code>id, voterId, itemId</code></li>
         </ul>
     </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found, if <code>pollId</code> refers to a non-existent object
    in the area of an authenticated user</li>
</ul>

---
<p>Returns the result of voting for a poll grouped by an item</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/votes 200</code></font><br>
            <b>{areaId}</b> existing Area ID
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>type</code></td>
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
 * **pollId** ID of an existing Poll in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status 200</li>
     <li>a JSON object with fields
         <ul>
             <li><code>pollId</code></li>
             <li><code>result</code> a JSON object representing a poll result.
             Contains fields <code>pollItemId, itemId, count</code>, where <code>itemId </code> refers to
             an ID of LunchPlace</li>
         </ul>
     </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400, if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found, if <code>pollId</code> refers to a non-existent object
    in the area of an authenticated user</li>
</ul>

---
<p>Returns a list of item ID that an authenticated user has voted for.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP GET /api/areas/{areaId}/votes 200</code></font>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td><code>filterBy, pollId</code></td>
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
 * **pollId** ID of an existing Poll in the area of an authenticated user
###### Response
<ul>
     <li>HTTP Status </li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 400, if parameter validation fails</li>
    <li>HTTP Status 404 Not_Found, if <code>pollId</code> refers to a non-existent object
    in the area of an authenticated user</li>
</ul>

---
<p>Revokes a user vote.</p>

###### Summary
<table summary="" rules="all" style="border:1px solid black; border-collapse:collapse; width:700px; padding:3px;">
    <tr>
        <td>HTTP Request</td>
        <td><font style="color:green"><code>HTTP DELETE /api/areas/{areaId}/votes 204</code></font>
        </td>
    </tr>
    <tr>
        <td>Request Content-Type</td>
        <td>none</td>
    </tr>
    <tr>
        <td>Required Request Parameters</td>
        <td>none</td>
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
 * **voteId** ID of an existing vote which was made by an authenticated user.

###### Response
<ul>
     <li>HTTP Status 204 No_Content</li>
</ul>

###### Response on failure
<ul>
    <li>HTTP Status 404 Not_Found, if <code>voteId</code> refers to a non-existent object or the vote has been
    made by another voter.</li>
</ul>
