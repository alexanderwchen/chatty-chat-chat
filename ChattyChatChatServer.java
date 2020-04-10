import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChattyChatChatServer{

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
            ArrayList<ClientRunnable> clientRunnables = new ArrayList<ClientRunnable>();

            try{
                listener = new ServerSocket( port );

                while(runServer){
                    try {
                        socket = listener.accept();
                        ClientRunnable tempClientRunnable = new ClientRunnable(socket,clientNumber);
                        clientRunnables.add(tempClientRunnable);
                        new Thread(tempClientRunnable).start();
                        clientNumber++;
                    } catch (IOException  e) {
                        System.out.println("Error connecting to client " + clientNumber++);
                    }
                    runServer = false;
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

        public ClientRunnable(Socket socket, int clientNumber){
            this.socket = socket;
            this.clientNumber = clientNumber;
            username = "User" + Integer.toString(clientNumber);
            isRunning = true;
        }

        @Override
        public void run() {
            BufferedReader in = null;
            PrintWriter out = null;

            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Hello client " + clientNumber);
                out.println("You have been connected to ChattyChatChatServer");

                while(isRunning){
                    String input = in.readLine();
                    if ( input != null ) {
                        out.println("You've entered " + input);
                    }



                    System.out.println("Sent \"" + input + "\" to client " + clientNumber);
                }
            }
            catch(IOException e){
                System.out.println("Error while communicating with client");
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

        private void message(){

        }
        private void changeNick(){

        }
        private void dm(){

        }
        private void quit(){
            isRunning = false;
        }

    }
}