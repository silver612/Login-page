# Description

This is the description for the frontend of this project
This section of the project was coded using vanilla JS and HTML. No CSS styling has been added.
The UI is only basic and functional enough to demonstrate that the system works. 

## Pages

The front-end consists of 4 pages:

1. home.html : The landing page.
2. login.html : Page for logging in.
3. signup.html : Page for creating a new account.
4. profile.html : A page available only to a logged-in user. Contains serveral actions such as logout, deleting account. Also displays info which can be set by user and is unaccessible by other users.

# Authentication

All user authentication is done by sending information to server. Sensitive data such as password and private data is not sent to client-side unless user has been logged-in or signed-up. Access to endpoints containing '/secure' in the path requires 2 cookies, sessionId and username. These cookies are marked Secure and HttpOnly for additional security. Authenticity of these cookie values are verified on every call to such endpoints and in case of mismatch, user is directed to login again.