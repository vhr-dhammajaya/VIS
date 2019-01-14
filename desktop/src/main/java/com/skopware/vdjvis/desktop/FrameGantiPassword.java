package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseSwingWorker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.User;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.event.ActionListener;

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

            User requestBody = new User();
            requestBody.uuid = App.currentUser.uuid;
            requestBody.password = password1;

            BaseSwingWorker<Boolean> worker = new BaseSwingWorker<>();
            worker.onDoInBackground = () -> {
                boolean dummy = HttpHelper.makeHttpRequest(App.config.url("/user/set_password"), HttpPost::new, requestBody, boolean.class);
                return dummy;
            };

            worker.onSuccess = dummy -> {
                this.dispose();
            };

            worker.onError = ex -> {
                SwingHelper.setEnabled(true, btnOk, btnCancel);

                ex.printStackTrace();
                SwingHelper.showErrorMessage(FrameGantiPassword.this, "Gagal mengganti password. Terjadi error");
            };

            SwingHelper.setEnabled(false, btnOk, btnCancel);
            worker.execute();
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
