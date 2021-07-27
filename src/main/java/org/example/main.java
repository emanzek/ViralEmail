package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

/** Viral Email Program
 * @author : emanzek
 *
 * This program will let you send your email into multiple address instantly using SMTP protocol.
 * For testing purpose, we'll be using this test account
 *
 *
 * Since we're using smtp protocol, which means less secure, you need to lower the account's security
 * by creating an App Password from your account settings.
 * For now, this program can be use with account from gmail and yahoo.
 */
public class main extends Application{
    Stage window;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        //initialize the program
        new ViewModel(window);
        window.show();
    }
}
