package client;

import java.io.*;
import java.net.Socket;

public class Client {

   private Socket socket;
   //BUFFERED READER DA CONSOLA
   private BufferedReader in;
   private PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.startReader();
        client.handleServer();
    }

    private void startReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void handleServer() {
        connectToServer();
        startListeningServer();
        communicateWithServer();
    }

    private void connectToServer() {
        String hostName = "localhost";
        int port = 1010;
        try {
            socket = new Socket(hostName, port);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Maybe server is dead!");
            connectToServer();
        }
    }

    private void startListeningServer() {
        try {
            new Thread(new ServerListener(socket.getInputStream())).start();
        } catch (IOException e) {
            handleServer();
        }
    }

    private void communicateWithServer() {
        try {
            sendMessages();
            communicateWithServer();
        } catch (IOException e) {
            System.out.println("maybe server is dead");
            handleServer();
        }
    }

    private void sendMessages() throws IOException {
        String message = readFromConsole();
        out.println(message);
    }

    private String readFromConsole() {
        String message;
        try {
            message = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    class ServerListener implements Runnable {
        //BUFFERED READER DO SERVER
        private BufferedReader in;
        public ServerListener(InputStream inputStream) {
            this.in = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            try {
                readMessage();
            } catch (IOException e) {

            }
        }

        private void readMessage() throws IOException {
            String message = in.readLine();
            if(message == null) {
                System.out.println("Disconnected");
                System.exit(1);
            }
            System.out.println(message);
            readMessage();
        }
    }

}

