package server.commands;

import server.Server;

public class HelpCommand implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientHandler client) {
        client.sendMessageToUser("""
            List of available commands:
            /list -> gets you the list of connected clients
            /shout <message> -> lets you shout a message to all connected clients
            /whisper <username> <message> -> lets you whisper a message to a single connected client
            /name <new name> -> lets you change your name
            /quit -> exits the server""");
    }
}
