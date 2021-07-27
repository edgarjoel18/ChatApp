package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
     static List<String> userNames = new ArrayList<>();
     static List<PrintWriter> printWriters = new ArrayList<>(); // whenever a client sends a message
    // to our server, the server has to send this message to all the clients. So we are getting the printwriters of all
    // the clients

    public static void main(String[] args) throws IOException {
        System.out.println("Waiting for Clients");
        ServerSocket ss = new ServerSocket(9806);
        while(true){
            Socket soc = ss.accept(); // accepts incoming client connections
            System.out.println("Connection established");
            ConversionHandler handler = new ConversionHandler(soc);
            // start this thread
            handler.start();
        } // end of while

        // making this multithreaded, so it can handle multiple client connections simultaneously

    }
}

class ConversionHandler extends Thread {
    Socket socket;
    BufferedReader in;
    PrintWriter out; // used to write data to the socket's output stream
    String name;
    PrintWriter pw; // write data to our file
    static FileWriter fw;
    static BufferedWriter bw;

    public ConversionHandler(Socket socket) throws IOException{
        this.socket = socket;
        fw = new FileWriter("/Users/edgar/Documents/chatlogs/chatlogs.txt");
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw, true);
    }

    @Override
    public void run() {
        // read from the socket's input stream
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // now send data to the socket's output stream
            out = new PrintWriter(socket.getOutputStream(), true);
            int count = 0;
            while(true){
                if(count > 0){
                    out.println("Name exists");
                }
                else{
                    out.println("Name required");
                }
                name = in.readLine();
                if(name == null)
                    return;
                if(!ChatServer.userNames.contains(name)){
                    ChatServer.userNames.add(name);
                    break; // get out of the loop
                }
                count++;
            } //

            out.println("Name accepted: " + name);
            ChatServer.printWriters.add(out);

            // read messages from the client and send it to all the clients
            while(true){
                String message = in.readLine();
                if(message == null)
                    return;
                pw.println(name + ": " + message);

                for(PrintWriter printWriter: ChatServer.printWriters){
                    printWriter.println(name + ": " + message);
                }


            } // end of while


        } catch (Exception e) {
            System.out.println(e);
        }

    } // end of run


}
