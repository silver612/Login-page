# Description

This is the description for the backend section of this project. 
This section of this project was coded using java and spring boot.
This code can execute CRUD operations on an embedded MySQL database. The required link and credentials need to be set in [application.properties](src\main\resources\application.properties). 

## Endpoints

This code contains several endpoints with purpose as described below:

1. PATCH at "/auth" : Receive a [UserAuth](src\main\java\com\example\demo\data\payloads\UserAuth.java) as body and confirm that password sent matched password stored for username sent. Creates a random sessionId and sets it as cookie for future authentication. 

2. POST at "/signup" : Receive a UserAuth as body and create a record matching datails sent if username does not already exist. Also creates a random sessionId and sets it as cookie for future authentication. 

3. PUT at "/secure/auth" : Clears up all cookies (set by server) which were created for authentication purposes. Equivalent to logging out the user.

4. DELETE at "/secure/deleteAccount" : Deletes database record for user and clears all cookies set by server. 

5. PUT at "/secure/updateAccount/username" : Receives String as body and updates record if username not used by someone else. Also updates required cookies.

6. PUT at "/secure/updateAccount/password" : Receives a UserAuth as body, validates current password sent before updating password to new password. 

7. GET at "/secure/info" : Returns the info stored for username present in cookie.

8. PUT at "/secure/info" : Receives String as body and updates the info stored for username present in cookie.

## Cookies

There are 2 cookies which are set by server: sessionId and username. Both cookies are permanent cookies which will last for 1 hour. Cookies are marked Secure and HttpOnly. Both cookies are set on login or signup. Deleting the account or logging out (calling "/secure/auth") will delete both cookies. Successful username change results in change of username cookie.

The cookies are only sent to endpoints which contain /secure in their path. These cookies help in checking if user is logged in. sessionId cookie value is validated for username cookie value by the interceptor, [LoggedInCheckerInterceptor.java](src\main\java\com\example\demo\interceptor\LoggedInCheckerInterceptor.java). If sessionId is invalid or cookies are not found, server returns 401 Error code with message "Session Id Mismatch".

## CORS

Most calls made from Client-side will require cookies to be sent, which means that server needs to be configured to respond to the CORS preflight request with a response containing the headers, "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Access-Control-Allow-Methods", "Access-Control-Allow-Headers" and appropriate values for these headers. This is done by the interceptor, [CorsHeaderAdderInterceptor](src\main\java\com\example\demo\interceptor\CorsHeaderAdderInterceptor.java).

## Unit Tests

Unit Tests have been added for every function in [AuthServiceImpl](src\main\java\com\example\demo\service\AuthServiceImpl.java), which is the only service class. 