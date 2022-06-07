import java.io.*;
import java.net.*;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class Client extends JFrame {

    Socket socket;

    BufferedReader br;  //for reading data
    PrintWriter out;    //for writting data

    //GUI 
    private JLabel heading = new JLabel("Conversations");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Matura MT Script Capitals",Font.PLAIN,25);
    private Font font1 = new Font("Lucida Sans Unicode",Font.PLAIN,15);

    //creating constructor
    public Client() {
        try {
            System.out.println("Establishing Connection with Server");
            socket = new Socket("127.0.0.1",7777);   //Server ip and port number   
            System.out.println("Connection Established");  

            br = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
            //startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == 10) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("You : "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                } 
            }
            
        });
    }

    public void createGUI() {
        this.setTitle("Client Chat Application");
        this.setSize(500,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Setting Font
        heading.setFont(font);
        messageArea.setFont(font1);
        messageInput.setFont(font1);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);

        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        this.setLayout(new BorderLayout()); //Setting Frame Layout
        
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void startReading() {
        //thread for reading
        Runnable r1 = ()->{
            //System.out.println("Reader is Being Procced");

            try {
                while(true) {
                    String msg = br.readLine();
                    
                    if(msg.equals("exit")) {
                        System.out.println("Server has terminated the communication"); //print on console
                        JOptionPane.showMessageDialog(this, "Server has terminated the communication");
                        messageArea.setEnabled(false); //disable the input 
                        socket.close();
                        break;
                    }
                    //System.out.println("Server : "+msg); //print on console
                    messageArea.append("Server : "+msg+ "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection is Closed");
            }
        };
        new Thread(r1).start();     //starting the thread
    }

    //In GUI we won't be using startWriting Method b'coz createGUI method is doing the same
    public void startWriting() {
        //thread for writing and sending
        Runnable r2 = ()->{
            //System.out.println("Writter has been started");
        
            try {
                while(!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();

                    out.println(content);
                    out.flush();

                    if(content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection is Closed");
            }
        };
        new Thread(r2).start();     //starting the thread
    }
    public static void main(String[] args) {
        System.out.println("Client is Here");
        new Client();
    }
}
