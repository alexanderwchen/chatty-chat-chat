# Chatty Chat Chat Protocol

- [About](#about)
- [Tasks](#tasks)
- [Description](#description)
- [Setup](#setup)
- [Chat Protocols and commands](#chat-protocol-and-commands)

## About
- COSC 150 - Advanced Programming, Project 4
- Made by Alex Chen, April 2020

## Tasks

- Understand how to model network connections using sequence diagrams
  - Describe the operations required for a complete network interaction.
  - Model the communications between client and server processes, and between the threads supporting those processes.
- Understand how to use Java sockets to create and sustain network connnections.
  - Create a server that accepts multiple simultaneous connections.
  - Implement a design to handle interactions with many clients at once.
- Understand how to use Java threads to allow for nondeterministic execution.
  - Allow for both client and server processes to handle multiple simultaneous tasks.
  - Use proper object locks and/or wait-notify signaling to collaborate on a shared object.

## Description

Implementing an internet chat protocol -- the ChattyChatChat protocol (CCC). The protocol governs how a single ChattyChatChat server mediates connections between any number of ChattyChatChat clients as they communicate with each other.

This protocol will contain two primary Java classes:
- The ChattyChatChatServer, which is run on a single computer at a specified port and receives connections from clients.
- The ChattyChatChatClient, which is run by each client computer and connects to the server.

## Setup

- `ChattyChatChatServer.java`, which contains a `main()` method and serves as the program to run on the server.
- `ChattyChatChatClient.java`, which contains a `main()` method and serves as the program to run on the clients.
- No other source code files are necessary.

#### ChattyChatChatServer

The server program is a class named `ChattyChatChatServer`; this program accepts a single command-line argument describing the port for the server to listen on. For example, to start the server and have it listen to port `9876`, the command-line invocation would be:
```
java ChattyChatChatServer 9876
```
A single instance of the ChattyChatChat server will serve as the common point of connection for all clients wanting to interact with the chat server.

#### ChattyChatChatClient

The client program is a class named `ChattyChatChatClient`; this program accepts two command-line arguments describing the server name and port to connect to. For example, to start a client and connect to a server running on port `9876` on `localhost`, the command-line invocation would be:
```
java ChattyChatChatClient localhost 9876
```
Note that the server must be running in order for any client to successfully connect.

## Chat Protocol and Commands

The communication protocol for the chat clients and server includes the following commands and rules:

#### Normal Message
- A "normal" message is text sent by one client to the server; this message will be relayed to all other clients.

#### Set Nickname
- `/nick <name>` : Set this client's nickname to be the string `<name>`. For example:
```
/nick johnsmith
```
would set the user's nickname to `johnsmith`.
- The nickname command may be used more than once per session by any user; the current nickname is retained unless/until a subsequent `/nick` command is received.
- Nicknames do *not* need to be unique on the server.
- Nicknames are single-words and do not contain spaces.
- Any additional characters beyond the first word may be ignored; that is, the above and below commands would have identical effect:
```
/nick johnsmith these words may be discarded
```

#### Private Message
- `/dm <name> <msg>` : Send a message to user(s) with the specified nickname. For example:
```
/dm johnsmith This is a "secret" message
```
will deliver the message "This is a "secret" message" only to user(s) who have the nickname "johnsmith".
- Only clients with the correct nickname will receive this message; nothing will be sent to any other clients.
- If no client has the specified nickname, this message may be ignored.
- If multiple clients have the specified nickname, *all* of them will receive the message.

#### List All Users
- `/list`: List out all users currently connected to the server. For example:
```
/list
```
will provide a list of all users using their original or renamed nicknames.

#### Quit
- `/quit` : Disconnect from the server and end the client program.
- When a client enters this message, it will still be sent to the server as a notice that the client will disconnect; the server may then safely close this socket connection and clean-up details related to the client.
- The client program will disconnect, clean up, and end when this string is entered.
- Any additional characters after the `/quit` may be safely ignored.

#### Other
- Any other input (including one beginning with a slash, but not exactly matching the above) will be considered a regular message.      