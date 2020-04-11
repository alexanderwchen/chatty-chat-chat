import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChattyChatChatServer{

    public static ArrayList<ClientRunnable> clientRunnables;

    public static void main(String[] args){
        try {
            System.out.println("Start ChattyChatChatServer");
            if (args.length != 1) {
                throw new Exception("Invalid number of arguments");
            }

            int port = Integer.parseInt(args[0]);
            ServerSocket listener = null;
            Socket socket = null;
            boolean runServer = true;
            int clientNumber = 0;
            clientRunnables = new ArrayList<ClientRunnable>();

            try{
                listener = new ServerSocket( port );

                while(runServer){
                    try {
                        socket = null;
                        socket = listener.accept();
                        System.out.println("New Connection Made: " + socket.toString());
                        ClientRunnable tempClientRunnable = new ClientRunnable(socket,clientNumber);
                        clientRunnables.add(tempClientRunnable);
                        new Thread(tempClientRunnable).start();
                        clientNumber++;
                    } catch (IOException  e) {
                        System.out.println("Error connecting to client " + clientNumber++);
                    }
                }
                listener.close();
            }
            catch(IOException e){
                System.out.println("Error establishing listener");
            }

            System.out.println("End ChattyChatChatServer");
        }
        catch(Exception e){
            System.out.println("Unknown Error Occurred");
            e.printStackTrace();
        }
    }

    public static class ClientRunnable implements Runnable{
        private Socket socket;
        private String username;
        private int clientNumber;
        private boolean isRunning;
        private BufferedReader in;
        private PrintWriter out;

        public String getUsername(){return username;};
        public int getClientNumber(){return clientNumber;};
        public boolean getIsRunning(){return isRunning;};



        public ClientRunnable(Socket socket, int clientNumber){
            this.socket = socket;
            this.clientNumber = clientNumber;
            username = "user" + Integer.toString(clientNumber);
            isRunning = true;
            in = null;
            out = null;
        }

        @Override
        public void run() {

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Hello client " + clientNumber);
                out.println("You have been connected to ChattyChatChatServer");

                while(isRunning){
                    String input = in.readLine();
                    String response = "";

                    if ( input != null ) {
                        out.println("SERVER: You've entered " + input);
                    }

                    String[] inputArray = input.split(" ", 3);
                    switch(inputArray[0]){
                        case "/nick":
                            changeNick(inputArray);
                            break;
                        case "/quit":
                            quit();
                            break;
                        case "/name":
                            out.println(username);
                            break;
                        case "/dm":
                            dm(inputArray);
                            break;
                        default:
                            response = "SERVER: DEFAULT";
                            message(input);
                            break;
                    }

                    System.out.println("Received \"" + input + "\" from client " + clientNumber + " (" + username + ")");
                    System.out.println("Sent \"" + response + "\" to client " + clientNumber + " (" + username + ")");
                }
            }
            catch(IOException e){
                System.out.println("Error while communicating with client");
            }
            catch(Exception e){
                System.out.println("Unknown Error Occurred");
                e.printStackTrace();
            }
            finally{
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket with client " + clientNumber);
                } finally {
                    System.out.println("Connection to client " + clientNumber + " closed");
                }
            }
        }

        private void message(String input){
            for( ClientRunnable cr : ChattyChatChatServer.clientRunnables ){
                if(cr.getIsRunning()){
                    cr.send(username + ": " + input);
                }
            }
        }
        private void changeNick(String[] inputArray){
            if(inputArray.length < 2){
                send("SERVER: No new username provided");
            }
            else {
                send("SERVER: Username changed from " + username + " to " + inputArray[1]);
                username = inputArray[1];
            }
        }
        private void dm(String[] inputArray){
            if(inputArray.length < 3){
                send("SERVER: No message provided");
            }
            else {
                send("searching for:" + inputArray[1]);
                boolean isFound = false;
                for( ClientRunnable cr : ChattyChatChatServer.clientRunnables){
                    if(cr.getUsername().equals(inputArray[1])){
                        isFound = true;
                        cr.send(username + " to you (private): " + inputArray[2]);
                    }
                }
                if(isFound) {
                    send(username + " to " + inputArray[1] + " (private): " + inputArray[2]);
                }
                else{
                    send("SERVER: " + inputArray[1] + " not found");
                }
            }
        }

        public void send( String message ){
            out.println(message);
        }

        private void quit(){
            isRunning = false;
        }

    }
}