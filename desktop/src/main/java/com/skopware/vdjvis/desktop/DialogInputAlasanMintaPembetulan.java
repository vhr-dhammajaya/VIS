package com.skopware.vdjvis.desktop;

import com.skopware.javautils.swing.SwingHelper;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DialogInputAlasanMintaPembetulan extends JDialog {
    private Consumer<String> fnOnOk;

    public DialogInputAlasanMintaPembetulan(Consumer<String> fnOnOk) {
        super(App.mainFrame, "Jelaskan alasan minta pembetulan", false);
        this.fnOnOk = fnOnOk;

        JLabel labelTextarea = new JLabel("Jelaskan alasan minta pembetulan");
        JTextArea txtAlasan = new JTextArea(10, 20);

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.add(labelTextarea, BorderLayout.NORTH);
        pnlMain.add(txtAlasan, BorderLayout.CENTER);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            if (txtAlasan.getText() == null || txtAlasan.getText().isEmpty()) {
                SwingHelper.showErrorMessage(App.mainFrame, "Alasan tidak boleh kosong");
                return;
            }

            fnOnOk.accept(txtAlasan.getText());
            this.dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
