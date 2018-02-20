//นาย ศักย์ชัย ลิ้มสุขนิรันดร์ 5810451063    Sakchai Limsuknirund 5810451063
package controllers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Controller_Chat {
    @FXML
    Button SENDmsg;
    @FXML
    ComboBox person_kick = new ComboBox();
    @FXML
    Button AdminBtn;
    @FXML
    private Button ON_OFF;
    @FXML
    private TextArea chatView;
    @FXML
    private TextField msgIn;
    @FXML
    private TextArea person;
    @FXML
    private Label ClientName;
    @FXML
    private ComboBox<String> listplaygame = new ComboBox<>();
    ObservableList<String> QuickText = FXCollections.observableArrayList();
    ObservableList<String> personKick = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> zip = new ComboBox<>();
    @FXML
    private Label size;
    ObservableList<String> zipuser = FXCollections.observableArrayList("ALL");
    private boolean StatusOnOff = true;
    String username, address = "localhost";
    ObservableList<String> users = FXCollections.observableArrayList();
    private int port = 23513;
    Boolean isConnected = false;
    Socket sock;
    BufferedReader reader;
    PrintWriter writer;


    @FXML
    public void initialize() {
        System.out.println("Jay");
        setUserToKick();
        addQuickChat();
        msgIn.requestFocus();


    }

    public Controller_Chat() {
        startRunning();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatView.setEditable(false);
                person.setEditable(false);
            }
        });
        //chatView.setEditable(false);
    }


    //	send
    @FXML
    private void sendAction(ActionEvent event) {
        try {
            if (zip.getValue().equals("ALL") || zip.getValue().equals("เลือกกระซิบ")) {
                String nothing = "";
                if ((msgIn.getText()).equals(nothing)) {
                    msgIn.clear();
                    msgIn.requestFocus();
                } else {
                    try {
                        writer.println(username + ":" + msgIn.getText() + ":" + "Chat" + ":" + "20000");
                        writer.flush(); // flushes the buffer

                    } catch (Exception ex) {
                        msgIn.appendText("Message was not sent. \n");
                    }
                    msgIn.clear();
                    msgIn.requestFocus();
                }
                msgIn.clear();
                msgIn.requestFocus();
            } else {
                String nothing = "";
                if ((msgIn.getText()).equals(nothing)) {
                    msgIn.clear();
                    msgIn.requestFocus();
                } else {
                    try {
                        writer.println(zip.getValue() + ":" + msgIn.getText() + ":" + "Zip" + ":" + username + ":" + "19999");
                        writer.flush(); // flushes the buffer

                    } catch (Exception ex) {
                        msgIn.appendText("Message was not sent Private. \n");
                    }
                    msgIn.clear();
                    msgIn.requestFocus();
                }
                msgIn.clear();
                msgIn.requestFocus();

            }


        } finally {
            if (StatusOnOff) {
                try {
                    String nothing = "";
                    if ((msgIn.getText()).equals(nothing)) {
                        msgIn.setText("");
                        msgIn.requestFocus();
                    } else {
                        try {
                            writer.println(username + ":" + msgIn.getText() + ":" + "Chat" + ":" + "20000");
                            writer.flush(); // flushes the buffer

                        } catch (Exception ex) {
                            msgIn.appendText("Message was not sent. \n");
                        }
                        msgIn.setText("");
                        msgIn.requestFocus();
                    }
                    msgIn.setText("");
                    msgIn.requestFocus();

                } catch (Exception err) {
                    err.printStackTrace();
                }
            } else {
                chatView.appendText("You is Offline \n");
            }
        }
    }


    /// connect
    @FXML
    public void startRunning() {
        if (isConnected == false) {
            username = Controller_Login.name;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ClientName.setText("Client Name: " + username);

                }
            });
            try {
                this.address = Controller_Login.address;
                //System.out.println(address);
                sock = new Socket(address, port);
                InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(sock.getOutputStream());
                writer.println(username + ":has connected.:Connect");
                writer.flush();
                //System.out.println(sock.getInetAddress());   //localhost/127.0.0.1
                isConnected = true;
            } catch (Exception ex) {
                appendText("Cannot Connect! Try Again. \n");
                chatView.setStyle("-fx-text-fill: red;");

            }

            ListenThread();

        } else if (isConnected == true) {
            appendText("You are already connected. \n");
        }
    }

    public void ListenThread() {
        Thread IncomingReader = new Thread(new IncomingReader());
        IncomingReader.start();
    }

    public void appendText(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chatView.appendText(text + "\n");
            }
        });
    }

    public void setText(String text) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                person.setText(text + "\n");
                //size.setText("ออนไลน์ทั้งหมด: "+(sizePERSON+1));
            }
        });
    }


    public class IncomingReader implements Runnable {
        @Override
        public void run() {
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat", promote = "promote",
                    zip = "Zip", kick = "kick", demote = "demote";

            try {

                while ((stream = reader.readLine()) != null) {
                    System.out.println(stream);
                    data = stream.split(":");
                    System.out.println(stream);

                    if (data[2].equals(chat)) {
                        appendText(data[0] + ": " + data[1]);

                    } else if (data[2].equals(kick)) {
                        if (data[0].equals(username)) {
                            StatusOnOff = false;
                            person.clear();
                            chatView.clear();
                            sendDisconnect();
                            Disconnect();
                            chatView.appendText("You have been kicked by ADMIN " + data[3] + "[ADMIN] \n");
                            chatView.setStyle("-fx-text-fill: red;");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    ON_OFF.setText("ONLINE");
                                }
                            });
                            System.out.println(data[2]);
                        }
                    } else if (data[2].equals(connect)) {
                        userAdd(data[0]);
                        String s = "";

                        for (String a : users) {
                            s += a + " \n";
                            if (zipuser.contains(a) == false && a.equals(username) == false) {
                                zipuser.add(a);
                                personKick.add(a);
                            }
                        }
                        setText(s);


                    } else if (data[2].equals(zip)) {
                        if (data[0].equals(username)) {
                            chatView.appendText(data[3] + " ได้กระซิบคุณ :" + data[1] + "\n");

                        } else if (data[3].equals(username)) {
                            chatView.appendText("คุณกระซิบหา  " + data[0] + " :  " + data[1] + "\n");
                        }

                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);

                    } else if (data[2].equals(done)) {
                        writeUsers();
                        users.clear();

                    } else if (data[2].equals(promote)) {
                        if (data[0].equals(username)) {
                            AdminBtn.setVisible(true);
                            person_kick.setVisible(true);
                            String newName = username + " [ADMIN]";
                            //resetPersonlist();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    chatView.appendText("You have been Promote by ADMIN SERVER\n");
                                    ClientName.setText("Client Name: " + newName);
                                }
                            });

                            //resetPersonlist();
                        }

                    } else if (data[2].equals(demote)) {
                        if (data[0].equals(username)) {
                            AdminBtn.setVisible(false);
                            person_kick.setVisible(false);
                            //resetPersonlist();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    chatView.appendText("You have been Demote by ADMIN SERVER\n");
                                    ClientName.setText("Client Name: " + username);
                                }
                            });

                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }

    public void resetPersonlist() {
        users.set(username.indexOf(username), username + "  [ADMIN]");
        String s = "";
        for (String a : users) {

            s += a + "\n";
        }

        setText(s);
        users.clear();

    }


    public void userAdd(String data) {
        users.add(data);

    }

    public void userRemove(String data) {
        appendText(data + " is now offline.\n");
    }

    public void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);


    }

    @FXML
    private void disAction(ActionEvent e) {
        if (StatusOnOff) {
            ON_OFF.setText("ONLINE");
            StatusOnOff = false;
            sendDisconnect();
            Disconnect();
        } else {
            StatusOnOff = true;
            ON_OFF.setText("ONLINE");
            Button b = (Button) e.getSource();
            Stage stage = (Stage) b.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Chat_UI.fxml"));
            try {
                stage.setScene(new Scene(loader.load(), 437, 435));
                stage.setResizable(false);
                stage.show();

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }


    }

    public void sendDisconnect() {

        String bye = (username + ": :Disconnect");

        try {
            writer.println(bye);
            writer.flush();
        } catch (Exception e) {
            appendText("Could not send Disconnect message.\n");
        }
    }

    public void Disconnect() {
        try {
            appendText("Disconnected.\n");
            sock.close();
        } catch (Exception ex) {
            appendText("Failed to disconnect. \n");

        }
        isConnected = false;
    }


    public void addQuickChat() {
        setComzip();
        QuickText.add("--select--");
        QuickText.add("สวัสดีครับ ตอนนี้ผมไม่ว่าง");
        QuickText.add("พรุ่งนี้เจอกัน ตอนบ่ายๆ");
        QuickText.add("ROVกัน พวกเรา!!!");

        listplaygame.setItems(QuickText);

    }

    @FXML
    public void selectQuickChat(ActionEvent e) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                msgIn.setText(listplaygame.getValue());
            }
        });
    }

    @FXML
    public void addNEWQC(ActionEvent event) {
        QuickText.add(msgIn.getText());
        msgIn.clear();
    }

    @FXML
    public void deQC(ActionEvent event) {
        QuickText.remove(listplaygame.getValue());
        msgIn.clear();
    }

    public void setComzip() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                zip.setItems(zipuser);
            }
        });
    }

    public void setUserToKick() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                person_kick.setItems(personKick);
            }
        });
    }

    @FXML
    public void SendKicktoServer(ActionEvent event) {
        try {
            if (person_kick.getValue() != null) {
                writer.println(username + ":" + person_kick.getValue() + "" + ":" + "senduserkick"+":"+"2305");
                writer.flush();
                chatView.appendText("Kick Successful \n");
            } else chatView.appendText("Plase Select User\n");

        } catch (Exception ex) {
            msgIn.appendText("Cannot Kick User\n");
        }
    }

}








