import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChattyChatChatClient{

    public static void main(String[] args){
        try {

            if(args.length != 2)
            {
                throw new Exception("Invalid number of arguments");
            }

            String hostname = args[0];
            int port = Integer.parseInt(args[1]);
            Socket socket;
            ChatRunnable chatRunnable;

            try{
                socket = new Socket( hostname, port );

                new Thread(chatRunnable = new ChatRunnable(socket)).start();

                PrintWriter out = new PrintWriter( socket.getOutputStream(), true);
                BufferedReader userIn = new BufferedReader(
                        new InputStreamReader( System.in ) );

                boolean done = false;
                while ( !done ) {
                    String userInput = userIn.readLine();
                    out.println( userInput );

                    if ( userInput.equals("/quit") ) {
                        done = true;
                        chatRunnable.done = true;
                    }

                }
                socket.close();
            }
            catch(IOException e){
                System.out.println("Error connecting to server");
            }
        }

        catch(Exception e){
            System.out.print("Unknown Error Occurred: ");
            System.out.println(e.getMessage());
        }
    }

    public static class ChatRunnable implements Runnable {

        private Socket socket;
        BufferedReader in;
        boolean done;

        public ChatRunnable(Socket socket){
            this.socket = socket;
            done = false;
        }
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                for (int i = 0; i < 2; i++) {
                    System.out.println( in.readLine() );
                }
                while(!done){
                    String response;
                    try {
                        response = in.readLine();
                        if(response != null){
                            System.out.println(response);
                        }
                    } catch (IOException e) {
                        System.out.println("Error receiving from server");
                        done = true;
                    }
                }
            }
            catch(IOException e){
                System.out.println("Error while communicating to server");
            }
        }
    }
}