Service for user authentication and authorization, user management and logging activities.

Includes:
  A. Authentication and authorization with access and refresh JWT tokens.
  B. User management service (with e-mail services to restore forgotten password).
  C. Logging user activity service (with attendance functionality).

Don't forget to fill application.properties.

_All /api/ endpoints need authorization with bearer token._

REST Endpoints for authorization:
1. POST /register - register user
2. POST /auth - login user; return access token + refresh token
3. POST /token - return new access token using refresh token
4. GET /me - check user auth; return role
5. POST /api/refreshToken - return new refresh token using old refresh token
6. POST /forgottenPassword - send restoring token for resetting password on user's email
7. POST /resetPassword - takes restoring token with new password and changes in db

REST Endpoints for user management:
1. GET /api/users/getUser/{id} - get user via id (only for admin and moderator)
2. GET /api/users/getAllUsers - get all users in short info form (only for admin and moderator)
3. DELETE /api/users/deleteUser - delete user (only for admin and moderator)
4. PUT /api/users/editUser - edit user fields (only for user with greater role + only for admin and moderator)
5. PUT /api/changeOwnPassword - change password of authenticated user (only for user itself)
6. GET /api/getUserInfo - get user fields (only for user itself)
7. PUT /api/editOwnInfo - edit user fields (only for user itself)

REST Endpoints for logging activity:
1. GET /api/allActivities - get all users activities
2. GET /api/getUserActivities/{login} - get all activities of user with {login}

REST Endpoints for system information:
1. GET /api/getApplicationStart - get time the app started
2. GET /api/getApplicationWorkingTime - calculates the period the app is working

REST Endpoint for user online status management:
1. GET /api/pingAlive - request from client to ping that user is active
2. GET /api/showUserStats - get all active users for the last N minutes
