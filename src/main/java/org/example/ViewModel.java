package org.example;

import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

/** UI model
 * This script will act as a main controller and viewer
 * Since there is only two screen inside program,
 * there will be two method to represent two screen loginScreen() and composeEmail()
 */

public class ViewModel implements EventHandler<ActionEvent> {
    private Stage window;
    private Button fileButton, sendMultiple;
    private Hyperlink hyperlink;
    private TextField toInput;
    private String filename;
    private JavaMailUtil util = new JavaMailUtil();

    public ViewModel(Stage primary){
        this.window = primary;
        this.filename = "";
        window.setTitle("Viral Email");

        //Select which screen to start first
        window.setScene(loginScreen());
    }

    private Scene loginScreen(){
        Label welcome = new Label("Viral Your Email"); //Main Title
        welcome.setAlignment(Pos.CENTER);
        welcome.setFont(Font.font("Arial", FontWeight.BOLD,60));
        welcome.setTextFill(Color.web("#3498db"));
        welcome.setMaxSize(630,70);
        welcome.setPadding(new Insets(20));

        TextField username = new TextField("Username");
        username = textFieldStyle(username);
        TextField password = new TextField("Password");
        password = textFieldStyle(password);

        Button login = new Button("Login");
        login.setMaxSize(360,30);
        TextField finalUsername = username;
        TextField finalPassword = password;
        login.setOnAction(e -> {
            //start login process
            login.setText("Please Wait");
            if(util.isLogin(finalUsername.getText(), finalPassword.getText())) {
                //if login were success it will redirect to compose email
                window.setScene(composeEmail());
            } else {
                //if login failed, a dialog box will shown
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Login Failed! Try again.");
                alert.showAndWait();
            }
        });

        //Put all element inside the vertical order
        VBox layout = new VBox();
        layout.getChildren().addAll(welcome,username,password,login);
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 800,600);
    }

    private Scene composeEmail(){
        //Two textfield at the top were wrap inside these two HBox
        HBox toField = recipientLayout();
        HBox subField = subjectLayout();

        //For content area it just pass the message, wouldn't need complex setup
        TextArea content = new TextArea();
        content.setMinHeight(450);

        //This is just hyperlink for file
        hyperlink = hyperlink();

        //Set button to attach file
        fileButton = new Button("File Attachment");
        fileButton.setMinWidth(100);
        fileButton.setOnAction(this);

        //Set submit@send button and bind it with send method from JavaMailUtil
        Button send = new Button("Send");
        send.setMinWidth(100);
        toInput = (TextField) toField.getChildren().get(1);
        TextField subInput = (TextField) subField.getChildren().get(1);
        send.setOnAction(event -> {
            //Dialog panel will shown only after send button clicked
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            try {
                util.sendMail(toInput.getText(), subInput.getText(), content.getText());
                alert.setContentText("Message sent successfully");
            } catch (Exception e) {
                //Error will throw from JavaMailUtil and shown as "Message unable to sent" to user
                alert.setContentText("Message unable to sent");
                e.printStackTrace();
            }
            alert.showAndWait();
            alert.setContentText("");
        });

        //This is for button below content part
        HBox buttonBelow = new HBox();
        buttonBelow.getChildren().addAll(fileButton,send);

        //For quick tips, it will more interesting if could be integrate with RSS feed
        Label quickTips = new Label("Did you know? Most of people will read their emails at 10 a.m. ;)");
        quickTips.setPadding(new Insets(10));

        VBox layout = new VBox();
        layout.getChildren().addAll(toField,subField,content,hyperlink,buttonBelow,quickTips); //Blend all element together
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(5));
        return new Scene(layout, 800,600);
    }

    //Here is the container for recipient field
    private HBox recipientLayout() {
        Label label = new Label("To: ");
        label.setPadding(new Insets(0,0,0,5));
        label.setMinWidth(50);
        toInput = new TextField();
        toInput.setPrefWidth(740);
        toInput.setOnAction(this);
        sendMultiple = new Button("Send to Multiple");
        sendMultiple.setMinWidth(150);
        sendMultiple.setOnAction(this); //do an action if user want send to multiple address

        HBox toField = new HBox();
        toField.getChildren().addAll(label, toInput, sendMultiple);
        return toField;
    }

    //Here is the container for subject@title field
    private HBox subjectLayout(){
        Label label = new Label("Subject: ");
        label.setPadding(new Insets(0,0,0,5));
        label.setMinWidth(50);
        TextField textField = new TextField();
        textField.setPrefWidth(740);
        HBox toField = new HBox();
        toField.getChildren().addAll(label,textField);
        return toField;
    }

    //This is for login text field
    private TextField textFieldStyle(TextField text){
        text.setMaxSize(360,45);
        return text;
    }

    //Hyperlink to show attachment location
    private Hyperlink hyperlink(){
        hyperlink = new Hyperlink();
        hyperlink.setText("No file choosen");
        hyperlink.setContentDisplay(ContentDisplay.LEFT);
        hyperlink.setMaxWidth(Double.MAX_VALUE);
        hyperlink.setPadding(new Insets(2));
        return hyperlink;
    }

    private void setFilename(String input) {
        this.filename = input;
        this.hyperlink.setText(input);
    }

    private void setRecipients(String input) {
        this.toInput.setText(input);
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File response = fileChooser.showOpenDialog(null);

        if (event.getSource() == fileButton){
            //print out the path selected on below
            if (response != null){
                setFilename(response.getAbsoluteFile().getAbsolutePath());
                util.hasAttachment(filename); //enable the message to combine with pref file in JavaMailUtil
            }
        }
        else if (event.getSource() == sendMultiple){
            if (response != null){
                //Extracting from txt file contains email address list
                //This can be varied with diff type of file as long as extractor(new class file)
                // could return "arg1,arg2,...,argn"
                String filename = response.getAbsoluteFile().getAbsolutePath();
                extractTxt getList =  new extractTxt(filename);
                setRecipients(getList.strRecipients); //reassign the extracted value to recipient text field
            }
        }
    }
}
