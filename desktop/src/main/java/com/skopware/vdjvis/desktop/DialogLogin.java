package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.entities.User;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
            CompletableFuture<Optional<User>> cf = CompletableFuture.supplyAsync(() -> {
                Optional<User> user = App.jdbi.withHandle(h -> h.createQuery("select id, username, nama, tipe from user where username=? and password=md5(?)")
                        .bind(0, txtUsername.getText())
                        .bind(1, new String(txtPassword.getPassword()))
                        .mapTo(User.class)
                        .findFirst()
                );
                return user;
            });

            cf.thenAccept(user -> {
                SwingUtilities.invokeLater(() -> {
                    if (!user.isPresent()) {
                        SwingHelper.showErrorMessage(this, "Username / password tidak ditemukan");
                        return;
                    }

                    App.login(user.get());
                });
            });
            cf.exceptionally(ex -> {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> SwingHelper.showErrorMessage(this, "Terjadi error. Gagal login"));
                return Optional.empty();
            });
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
