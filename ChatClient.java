package chat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    // these will be the same for all our ChatClient instances
    static JFrame chatWindow = new JFrame("Chat Application");
    static JTextArea chatArea = new JTextArea(22, 40);
    static JLabel blankLabel = new JLabel("          ");
    static JButton sendButton = new JButton("Send");
    static JTextField textField = new JTextField(40);
    static JLabel nameLabel = new JLabel("          ");
    static BufferedReader in;
    static PrintWriter out;

    public ChatClient() {
        chatWindow.setLayout(new FlowLayout());
        chatWindow.add(new JScrollPane(chatArea));
        chatWindow.add(blankLabel);
        chatWindow.add(textField);
        chatWindow.add(sendButton);

        chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatWindow.setSize(475, 500);
        chatWindow.setVisible(true);
        
        textField.setEditable(false);

        sendButton.addActionListener(new Listener());
        textField.addActionListener(new Listener());


    }

     void startChat() throws Exception {
         String ipAddress = JOptionPane.showInputDialog(
                 chatWindow,
                 "Enter IP Address: ",
                 "IP Address Required",
                 JOptionPane.PLAIN_MESSAGE
         );
         Socket soc = new Socket(ipAddress, 9806);
         in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
         out = new PrintWriter(soc.getOutputStream(), true);
         // wait for the server's response
         while(true){
            // read the data from the server
             String str = in.readLine();
             if(str.equals("Name required")){
                 String name = JOptionPane.showInputDialog(
                         chatWindow,
                         "Enter a unique name: ",
                         "Name required",
                         JOptionPane.PLAIN_MESSAGE
                 );
                 out.println(name); // send to server
             } // end of if

             else if(str.equals("Name exists")){
                 String name = JOptionPane.showInputDialog(
                         chatWindow,
                         "Enter another name: ",
                         "Name already exists",
                         JOptionPane.WARNING_MESSAGE
                 );
                 out.println(name); // send to server
             } // end of else if

             else if(str.startsWith("Name accepted")){
                 textField.setEditable(true);
                 nameLabel.setText("You are logged in as");
             } // true
             else{
                 // our chat notices it is another message from the clients
                 chatArea.append(str + "\n");

             }


         }


    }


    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.startChat();

    }
} // end of class

class Listener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent event){
        // send the value of the text field to the socket which is listening on port 9806. Sending to server
        ChatClient.out.println(ChatClient.textField.getText());
        ChatClient.textField.setText("");


    }
}


