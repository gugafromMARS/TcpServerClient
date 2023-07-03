package server.commands;

import server.Server;

public class QuitCommand implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientHandler client) {
        client.close();
        server.removeClient(client);
        server.broadcast("has been disconnected!", client);
    }
}
