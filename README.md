# simple-chat-rest
### About
This is the realization of REST api for simple chat application. It uses Spring Framework + Postgre SQL + STOMP and
produses/consumes JSON.
In this app users can create contact list by finding other users by the phone number. Then, users can exchange messages
with their contacts.

After passing the basic authentication, the user must be registered in the database (/api/user/register).

URLs responds to (begin with /api):
- `/user/register` - POST for adding user's info into DB
- `/user/{id}` - GET for getting info about user; PUT for updating status (online/offline)
- `/user/{id}/contacts` - GET for getting user's contacts; POST for adding contact to user
- `/user/{id}/contact/{phone}` - DELETE for deleting contact
- `/user/{id}/contact/{contactPhone}/messages` - GET for getting all messages with contact; POST for sending message to contact (also it sends STOMP message to receiver on `/queue/message/notifications`); DELETE for deleting all messages with contact.

<hr>

### To run app
Import the project as a maven project into your IDE of choice. You also should configure server configuration.
