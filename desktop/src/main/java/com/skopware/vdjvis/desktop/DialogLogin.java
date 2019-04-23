package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.entities.User;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.event.ActionListener;

public class DialogLogin extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public DialogLogin() {
        super("Login");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        txtUsername = new JTextField(45);
        txtPassword = new JPasswordField(45);

        FormLayout layout = new FormLayout("right:pref, 4dlu, left:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.border(Borders.DIALOG);

        builder.append("Username", txtUsername);
        builder.nextLine();
        builder.append("Password", txtPassword);

        JPanel pnlMain = builder.build();

        ActionListener onOk = event -> {
            User requestBody = new User();
            requestBody.username = txtUsername.getText();
            requestBody.password = new String(txtPassword.getPassword());

            try {
                User user = HttpHelper.makeHttpRequest(App.config.url("/user/login"), HttpPost::new, requestBody, User.class);
                if (user == null) {
                    SwingHelper.showErrorMessage(this, "Username / password tidak ditemukan");
                    return;
                }

                App.login(user);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                SwingHelper.showErrorMessage(DialogLogin.this, "Terjadi error saat login");
            }
        };


        ActionListener onCancel = e -> this.dispose();

        JButton btnOk = new JButton("Login");
        btnOk.addActionListener(onOk);
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(onCancel);

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
