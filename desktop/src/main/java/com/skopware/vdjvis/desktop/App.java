package com.skopware.vdjvis.desktop;

import com.skopware.javautils.swing.BaseCrudAppConfig;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.vdjvis.api.entities.User;

import javax.swing.*;
import java.awt.*;

public class App {
    public static boolean loggedIn = false;
    public static User currentUser;
    
    public static BaseCrudAppConfig config;
    public static MainFrame mainFrame;
    public static DialogLogin formLogin;

    public static void main(String[] args) {
        config = new BaseCrudAppConfig();

        SwingUtilities.invokeLater(() -> {
            formLogin = new DialogLogin();
            mainFrame = new MainFrame();
            JDatePicker.popupOwner = mainFrame;

            formLogin.setVisible(true);
            formLogin.pack();
        });
    }
    
    public static void login(User u) {
        currentUser = u;

        formLogin.setVisible(false);

        mainFrame.setVisible(true);
        mainFrame.pack();
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        
        loggedIn = true;
    }

    public static void logout() {
        formLogin.setVisible(true);

        mainFrame.setVisible(false);

        loggedIn = false;
        currentUser = null;
    }
}
