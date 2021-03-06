package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.swing.SwingHelper;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.concurrent.CompletableFuture;

public class FrameGantiPassword extends JInternalFrame {
    private JPasswordField txtPassword1;
    private JPasswordField txtPassword2;
    private JButton btnOk;
    private JButton btnCancel;

    public FrameGantiPassword() {
        super("Ganti password", true, true, false, false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        txtPassword1 = new JPasswordField(50);
        txtPassword2 = new JPasswordField(50);

        FormLayout layout = new FormLayout("right:pref, 4dlu, left:pref:grow");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);

        builder.border(Borders.DIALOG);

        builder.append("Password baru", txtPassword1);
        builder.nextLine();
        builder.append("Ulangi password baru", txtPassword2);

        JPanel pnlMain = builder.build();

        ActionListener onOk = event -> {
            String password1 = new String(txtPassword1.getPassword());
            String password2 = new String(txtPassword2.getPassword());

            if (password1.isEmpty() || password2.isEmpty()) {
                SwingHelper.showErrorMessage(this, "Password tidak boleh kosong!");
                return;
            }

            if (!password1.equals(password2)) {
                SwingHelper.showErrorMessage(this, "Password tidak sama");
                return;
            }

            SwingHelper.setEnabled(false, btnOk, btnCancel);
            CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
                App.jdbi.useHandle(h -> h.createUpdate("update user set password=md5(?) where id=?")
                        .bind(0, password1)
                        .bind(1, App.currentUser.uuid)
                        .execute()
                );
            });

            cf.thenRun(() -> this.dispose());
            cf.exceptionally(ex -> {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    SwingHelper.setEnabled(true, btnOk, btnCancel);
                    SwingHelper.showErrorMessage(FrameGantiPassword.this, "Gagal mengganti password. Terjadi error");
                });
                return null;
            });
        };

        ActionListener onCancel = e -> {
            this.dispose();
        };

        btnOk = new JButton("Ok");
        btnOk.addActionListener(onOk);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(onCancel);

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
