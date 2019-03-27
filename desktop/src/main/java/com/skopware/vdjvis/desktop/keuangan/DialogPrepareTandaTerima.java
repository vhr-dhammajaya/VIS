package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.jasperreports.JasperHelper;
import com.skopware.javautils.swing.SwingHelper;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DialogPrepareTandaTerima extends JDialog {
    private JTextField edNama;
    private JSpinner edNominal;
    private JTextArea edKeperluanDana;
    private JTextArea edKeteranganTambahan;

    public DialogPrepareTandaTerima(Frame owner, String namaUmat, int nominalDana, String keperluanDana, String keteranganTambahan) {
        super(owner, "Persiapan cetak tanda terima", false);

        edNama = new JTextField(45);
        edNama.setText(namaUmat);
        edNama.setEditable(false);

        edNominal = new JSpinner(new SpinnerNumberModel(nominalDana, 0, Integer.MAX_VALUE, 1));
        edNominal.setEnabled(false);

        edKeperluanDana = new JTextArea(10, 40);
        edKeperluanDana.setText(keperluanDana);

        edKeteranganTambahan = new JTextArea(10, 40);
        edKeteranganTambahan.setText(keteranganTambahan);

        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.add(new JLabel("Nama umat"), ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 0;
            x.gridy = 0;
            x.anchor = GridBagConstraints.LINE_END;
            x.insets = new Insets(0, 0, 10, 0);
        }));
        pnlMain.add(edNama, ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 1;
            x.gridy = 0;
            x.fill = GridBagConstraints.HORIZONTAL;
            x.weightx = 1.0;
        }));

        pnlMain.add(new JLabel("Nominal dana"), ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 0;
            x.gridy = 1;
            x.anchor = GridBagConstraints.LINE_END;
            x.insets = new Insets(10, 0, 10, 0);
        }));
        pnlMain.add(edNominal, ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 1;
            x.gridy = 1;
            x.fill = GridBagConstraints.HORIZONTAL;
            x.weightx = 1.0;
        }));

        pnlMain.add(new JLabel("Keperluan dana"), ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 0;
            x.gridy = 2;
            x.anchor = GridBagConstraints.FIRST_LINE_END;
        }));
        pnlMain.add(edKeperluanDana, ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 1;
            x.gridy = 2;
            x.fill = GridBagConstraints.BOTH;
            x.weightx = 1.0;
            x.weighty = 1.0;
            x.insets = new Insets(10, 0, 10, 0);
        }));

        pnlMain.add(new JLabel("Keterangan tambahan"), ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 0;
            x.gridy = 3;
            x.anchor = GridBagConstraints.FIRST_LINE_END;
        }));
        pnlMain.add(edKeteranganTambahan, ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 1;
            x.gridy = 3;
            x.fill = GridBagConstraints.BOTH;
            x.weightx = 1.0;
            x.weighty = 1.0;
            x.insets = new Insets(10, 0, 0, 0);
        }));

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            Map<String, Object> params = new HashMap<>();
            params.put("NamaUmat", edNama.getText());
            params.put("NominalDana", edNominal.getValue());
            params.put("KeperluanDana", edKeperluanDana.getText());
            params.put("KeteranganTambahan", edKeteranganTambahan.getText());

            JasperReport jasperReport = JasperHelper.loadJasperFileFromResource(GridPembayaranDanaRutin.class, "tanda_terima_dana.jasper");
            JasperPrint jasperPrint = JasperHelper.fillReport(jasperReport, params, new JREmptyDataSource());
            JasperHelper.showReportPreview(jasperPrint);

            this.dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
