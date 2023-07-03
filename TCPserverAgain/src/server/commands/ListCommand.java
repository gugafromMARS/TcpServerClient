package server.commands;

import server.Server;

public class ListCommand implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientHandler client) {
        server.printListOfUsers(client);
    }
}
