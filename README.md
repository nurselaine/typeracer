# Roomiesplit

Typeracer is a game where users race to type a random string the fastest. Users start by logging into the game and are given a random userID. Each user will enter the waiting queue). As soon as 4 players are in the queue, their status will change to playing and the game will begin. Once the game begins, the server will provide all users with a string that they must type as fast as they can. As the users are typing, each keystroke will be validated with the original string in the server. As soon as a client makes an error, the server will send the error to the client. The client must correctly type ALL of the characters in the original string (case sensitive) in order to finish the game. Once all players finish typing the string, the game will end and the player with the fastest time to finish typing is the winner! Players that disconnect or take longer than a maximum time limit will automatically lose the game (timeout is set by the server).
This will be a 4 player game. Users that finish one game will once again be put into a waiting queue until a group of 4 players are available. This will continue until the user disconnects from the socket.

## Description

Typeracer is a command line game (for now) that allows up to 4 users to join a competitive game of typing. Users will be given
a paragraph in which they must type word for word without any mistakes in order to finish the game. The user with the most words
per minute wins!

## Set Up
1. This program has been set up to run in one window
2. Run the server.java file then start the client
3. It is currently a single threaded server that will run one client at a time while the server will be in a continuous loop until you terminate the process

## Key Features
Key Features:

1. Users choose a unique username and login using an encrypted password for authentication
2. Speed of each user's typing will be tracked and used to decide who wins
3. Mistakes will be tracked and user will be notified if a mistake in typing is detected

## Stretch Goals

1. Highlighting errors in the user string and displaying to client
2. Make this a 4 player game, where groups of 4 players can play simultaneously. 
3. Players from different games do not interfere with each other.

## Authors

Contributors: Michael Torkamen, Junxian Li, Elaine Huynh, Junwen Zheng
