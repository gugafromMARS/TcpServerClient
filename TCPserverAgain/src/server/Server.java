package server;

import server.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server {

    private ServerSocket serverSocket;
    private List<ClientHandler> list;

    public static void main(String[] args) {
       Server server = new Server();
       server.startServer(1010);
       server.acceptClient();
    }

    private void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            list = new ArrayList<>();
        } catch (IOException e) {
            System.exit(1);
        }
        System.out.println("Sever is running!");
    }

    private void acceptClient(){
        System.out.println("Server is accepting clients");
        try {
            Socket socket = serverSocket.accept();
            ClientHandler client = new ClientHandler(socket);
            list.add(client);
            new Thread(client).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            acceptClient();
        }
    }

    public void removeClient(ClientHandler client){
        list.remove(client);
    }

    public ClientHandler getClientByName(String nickname) {
        for(ClientHandler client : list){
            if(client.getUsername().equals(nickname)){
                return client;
            }
        }
        return null;
    }

    public void printListOfUsers(ClientHandler handler) {
        List<String> users = new ArrayList<>();
        for(ClientHandler client : list){
            users.add(client.getUsername());
        }
        handler.sendMessageToUser(users.toString());
    }

    public void broadcast(String message, ClientHandler clientHandler) {
        list.stream()
                .filter(client -> !client.equals(clientHandler))
                .forEach(client -> client.sendMessageToUser(clientHandler.getUsername() + " : " + message));
    }

    private void dealsWithCommands(String message, ClientHandler handler) {
            String description = message.split(" ")[0];
            Command command = Command.getCommandFromDescription(description);

            command.getCommandHandler().execute(this, handler);
    }

    public class ClientHandler implements Runnable {

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private String message;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                startBuffers();
                greetClient();
                handleClient();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }

        private void startBuffers() throws IOException {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        private void greetClient() {
            System.out.println("New client connected!");
            sendMessageToUser("Welcome to the chat!");
            try {
                username = readUsername();
            } catch (IOException e) {
                System.out.println("not inserted");
            }
        }

        private String readUsername() throws IOException {
            sendMessageToUser("What is your username? ");
            String username = in.readLine();

            for(ClientHandler client : list){
                if(client.getUsername() != null) {
                    if(client.getUsername().equals(username)){
                        sendMessageToUser("User already exists");
                        readUsername();
                    }
                }
            }
            return username;
        }

        public void sendMessageToUser(String message) {

            out.println(message);
        }

        private void handleClient() throws IOException {
            if(this.socket.isClosed()){
                return;
            }
            sendMessageToUser("Write something!");
            readMessageFromUser();
            handleClient();

        }

        private void readMessageFromUser() {
                try {
                    message = in.readLine();
                    if (message == null) {
                        socket.close();
                        return;
                    }
                    if(message.contains("/")) {
                        dealsWithCommands(message, this);
                    } else {
                        broadcast(message, this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }

        public void close(){
            try {
                this.socket.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.out.println("Disconnected!");
            }
        }
    }

}
