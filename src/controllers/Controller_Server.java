//นาย ศักย์ชัย ลิ้มสุขนิรันดร์ 5810451063    Sakchai Limsuknirund 5810451063
package controllers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Controller_Server {
    @FXML
    TextArea CHAT_AREA;
    @FXML
    TextArea personServer;
    @FXML
    Label size;
    private ServerSocket serverSock;
    private InetAddress IP = null;
    @FXML
    private TextArea ipSHOWs;
    @FXML
    ComboBox kick = new ComboBox();
    ArrayList clientOutputStreams;
    ObservableList<String> users = FXCollections.observableArrayList();
    private PrintWriter writer;

    public Controller_Server() {
        startRunning();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                CHAT_AREA.setEditable(false);
                personServer.setEditable(false);
                personServer.setStyle("-fx-text-fill: blue");
            }
        });

    }

    @FXML
    public void initialize() {
        appendText("Start Sever...\nGot a connection... \n");
        kick.setItems(users);


    }

    public void appendText(String text) {
        CHAT_AREA.setText(text);
        CHAT_AREA.setStyle("-fx-text-fill: green;");
    }
    // run

    public void startRunning() {
        Thread starter = new Thread(new ServerStart());

        try {
            IP = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String str = IP.toString();
        String[] data = str.split("/");
        //System.out.println(str);
//        lblLocalHost.setText(data[1]);
        System.out.println(IP.getHostAddress());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ipSHOWs.setText("IP : " + data[1] +"  HostName : "+data[0]);
            }
        });
        starter.start();

        //CHAT_AREA.appendText("Server started...\n");
    }


    public class ServerStart implements Runnable {
        @Override
        public void run() {
            clientOutputStreams = new ArrayList();
            //users = new ArrayList();

            try {
                serverSock = new ServerSocket(23513);
                IP = serverSock.getInetAddress();


                while (true) {
                    Socket clientSock = serverSock.accept();

                    writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(writer);

                    Thread listener = new Thread(new ClientHandler(clientSock, writer));
                    listener.start();


                }

            } catch (Exception ex) {
                CHAT_AREA.appendText("Error making a connection. \n");
            }
        }
    }


    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                sock = clientSocket;

                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                CHAT_AREA.appendText("Unexpected error... \n");
            }

        }

        public void setText(String text) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    personServer.setText(text + "\n");
                }
            });
        }

        @FXML
        public void setSIZEONLINE(String text) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    size.setText(text);
                    size.setStyle("-fx-text-fill: red");
                }
            });
        }

        @Override
        public void run() {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat", kick = "kick", Send_userkick = "senduserkick";
            String[] data;

            try {

                while ((message = reader.readLine()) != null) {

                    data = message.split(":");

                    if (data[2].equals(connect)) {
                        CHAT_AREA.appendText(message + "\n");
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat+":"+"20001"));
                        //tellEveryone((data[0] + ":" + sock.getLocalAddress()+"" + ":" + chat));
                        userAdd(data[0]);
                        String s = "";
                        for (String a : users) {
                            s += a + "\n";
                        }
                        setText(s);
                        //sizePERSON = users.size()+1;
                        setSIZEONLINE("จำนวน USER " + users.size() + " คน");

                    } else if (data[2].equals(Send_userkick)) {
                        Kick_now(data[1], data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat+":"+"99999"));
                        userRemove(data[0]);


                        CHAT_AREA.appendText(message + "\n");
                        String s = "";
                        for (String a : users) {
                            s += a + "\n";
                        }

                        setText(s);
                        setSIZEONLINE("จำนวน USER" + users.size() + "คน");

                    } else if (data[2].equals(chat)) {
                        CHAT_AREA.appendText(message + "\n");
                        tellEveryone(message);
                    } else if (data[2].equals(kick)) {
                        CHAT_AREA.appendText("kick");
                    } else if (data[2].equals("Zip")) {
                        SendZip(message);

                    } else {
                        CHAT_AREA.appendText("No Conditions were met. \n");
                    }
                }
            } catch (Exception ex) {
                CHAT_AREA.appendText("Lost a connection. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            }
        }
    }

    public void userAdd(String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        users.add(name);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void userRemove(String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        users.remove(name);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();

            } catch (Exception ex) {
                CHAT_AREA.appendText("Error telling everyone. \n");
            }
        }
    }

    @FXML
    public void kickBtn(ActionEvent event) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(kick.getValue() + ":" + "JAY" + ":" + "kick"+":"+"Server"+":"+"2304");
                users.remove(kick.getValue());
                writer.flush();
                CHAT_AREA.appendText(kick.getValue() + ":" + "JAY" + ":" + "kick"+":"+"Server"+":"+"2304 \n");
                CHAT_AREA.appendText(kick.getValue() + ": have been kicked by ADMIN SERVER \n");


            } catch (Exception ex) {
                CHAT_AREA.appendText("Error Kick User. \n");
            }
        }
        kick.setValue("--Select--");

        //userRemove(kick.getValue());
    }

    public void SendZip(String text) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(text);
                CHAT_AREA.appendText(text+"\n");
                writer.flush();

            } catch (Exception ex) {
                CHAT_AREA.appendText("Error Private Chat. \n");
            }
        }
    }

    @FXML
    public void PromoteBtn(ActionEvent event) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                if (kick.getValue()!= null) {
                    PrintWriter writer = (PrintWriter) it.next();
                    writer.println(kick.getValue() + ":" + "promote" + ":" + "promote"+":"+"2302");
                    writer.flush();
                    CHAT_AREA.appendText(kick.getValue() + ":" + "promote" + ":" + "promote"+":"+"2302 \n");
                    CHAT_AREA.appendText(kick.getValue() + ": have been Promote to admin by ADMIN SERVER \n");
                }
                else CHAT_AREA.appendText("Plase Select User to Kick");
            } catch (Exception ex) {
                CHAT_AREA.appendText("Error Private Chat. \n");
            }

        }
    }

    @FXML
    public void Kick_now(String name, String adminName) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(name + ":" + "JAY" + ":" + "kick" +":"+adminName +":"+"2305");
                writer.flush();
                CHAT_AREA.appendText(name + ":" + "JAY" + ":" + "kick" +":"+adminName +":"+"2305 \n");
                CHAT_AREA.appendText(name + ": have been kicked by ADMIN " + adminName + "[ADMIN] \n");


            } catch (Exception ex) {
                CHAT_AREA.appendText("Error Kick User. \n");
            }
        }

    }public void DemoteBtn(ActionEvent event) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(kick.getValue() + ":" + "JAY" + ":" + "demote" + ":" + "Server" +":" + "2301");
                writer.flush();
                CHAT_AREA.appendText(kick.getValue() + ":" + "JAY" + ":" + "demote" + ":" + "Server" +":" + "2301 \n");
                CHAT_AREA.appendText(kick.getValue() + ": have been Demote by ADMIN Server\n");


            } catch (Exception ex) {
                CHAT_AREA.appendText("Error Demote User. \n");
            }
        }
    }
    public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
