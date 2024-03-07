# Typeracer

## Developers
- Elaine Huynh
- Charles Li
- Michael Torkamen
- Junwen Zheng

## Purpose
Typeracer is a game where users race to type a random string the fastest. Users start by logging into the game and are given a random userID. Each user will enter the waiting queue). As soon as 4 players are in the queue, their status will change to playing and the game will begin. Once the game begins, the server will provide all users with a string that they must type as fast as they can. As the users are typing, each keystroke will be validated with the original string in the server. As soon as a client makes an error, the server will send the error to the client. The client must correctly type ALL of the characters in the original string (case sensitive) in order to finish the game. Once all players finish typing the string, the game will end and the player with the fastest time to finish typing is the winner! Players that disconnect or take longer than a maximum time limit will automatically lose the game (timeout is set by the server). 

This will be a 4 player game. Users that finish one game will once again be put into a waiting queue until a group of 4 players are available. This will continue until the user disconnects from the socket.

## Technologies
- Java
- Semaphors and Synchronization techniques
- RPC, HTTP, and TCP/IP
- This program is run in VScode : (if you attempt to run in Intellij or other IDE, it may not compile)

## Start Up Instructions
- Start Server Program in a new terminal
- Start Client Progam in a separate terminal
- Start multiple client programs
- Follow instructions in client terminal

## UML and Basic Program Architecture

### Client Side UML
### ![Client Side UML](utils\client_uml.png)
### Server Side UML
### ![Server Side UML](utils\server_uml.png)
