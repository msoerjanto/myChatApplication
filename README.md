# My Chat Application
## made for GungHo Online Entertainments coding challenge

This repository contains my implementation of a chat server. 

### Preliminary Notes
- I have decided to ignore one required file for the project since it contains my MySQL database credentials (I had some issue creating an additional user) thus cloning this repository and running it won't correctly configure the application.
- I am currently developing an android mobile client for this server but it seems I do not have enough time so I might develop this at a later time. 

### List of Features Implemented:
- user creation and authentication, so that users may log in and log off to the server
- public room creation so that online users may create and join rooms
- view available active rooms
- view participants of room
- private chat feature

### Technologies used:
- Java (JDBC, java.net)
- MySQL

### Connecting to the Server:
I have decided to deploy my server in an AWS EC2 ubuntu virtual machine. The following are the required information to connect to the server.

IP address: 18.222.43.173
Port number: 9806

Since I used java.net sockets which are essentially bare TCP sockets we can directly interact with the server with telnet as follows:

  _telnet 18.222.43.173 9806_
  
### Interacting with the Server
1. Upon connecting to the server users will be prompted to login. Upon some string input the behavior of the server is as follows: if the user does not exist , then it will create that user on the spot and ask for a password; if the user exists it will ask for the user password.

![Initial server prompt](/samples/initial.png)
Format: ![Alt Text](url)

2. Once the user is logged in he/she will see a menu with available options which can be accessed at any time by entering '/help'

3. The user can then view available rooms via '/rooms' option, send private messages to online users via '/w username message'

4. The user can also join or create rooms via the '/join roomName' option. Note that this command creates a room if the specified roomName does not exist. Also rooms will be destoryed if there are no participants and a user can only participate in a single room at a given time, thus the user will need to leave a room before joining a new one. It is definitely possible to participate in multiple rooms but I decided to stick to this simpler implementation for now.

5. Once in a room, the user can then talk to other participants in the room, view participants via '/list' or leave via the '/leave' option
  
6. Finally the user may log out via the '/quit' command 

### Additional notes:
- I spent a lot of time experimenting with other frameworks but they were not used because it was not feasible with regards to communicating with telnet. These include:
    - using node.js and socket.io with a web client
    - using php server with android client
