package server.commands;

import server.Server;

public class WhisperCommand implements CommandHandler{
    @Override
    public void execute(Server server, Server.ClientHandler client) {
        /*
         * /whisper username message
         */
        String[] input = client.getMessage().split(" ");
        String nickname = input[1];
        String message = "";
        for (int i = 2; i < input.length; i++) {
            message += input[i] + " ";
        }

        Server.ClientHandler clientHandler = server.getClientByName(nickname);

        clientHandler.sendMessageToUser(client.getUsername() + " whisper you : " + message.trim());

    }
}
