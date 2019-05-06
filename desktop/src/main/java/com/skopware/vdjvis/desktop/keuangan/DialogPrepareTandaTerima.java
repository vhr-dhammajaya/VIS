package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.NumberHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.jasperreports.JasperHelper;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Pendapatan;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DialogPrepareTandaTerima extends JDialog {
//    private JTextField edNama;
//    private JSpinner edNominal;
    private Input input;
    private JTextArea edKeperluanDana;
    private JTextArea edKeteranganTambahan;

    public DialogPrepareTandaTerima(Frame owner, Input input) {
        super(owner, "Persiapan cetak tanda terima", false);

        this.input = input;
//        edNama = new JTextField(45);
//        edNama.setText(input.namaUmat);
//        edNama.setEditable(false);
//
//        edNominal = new JSpinner(new SpinnerNumberModel(input.nominal, 0, Integer.MAX_VALUE, 1));
//        edNominal.setEnabled(false);

        edKeperluanDana = new JTextArea(10, 40);
        edKeperluanDana.setText(input.keperluanDana);

        edKeteranganTambahan = new JTextArea(10, 40);
        edKeteranganTambahan.setText(input.keteranganTambahan);

        JPanel pnlMain = new JPanel(new GridBagLayout());
//        pnlMain.add(new JLabel("Nama umat"), ObjectHelper.apply(new GridBagConstraints(), x -> {
//            x.gridx = 0;
//            x.gridy = 0;
//            x.anchor = GridBagConstraints.LINE_END;
//            x.insets = new Insets(0, 0, 10, 0);
//        }));
//        pnlMain.add(edNama, ObjectHelper.apply(new GridBagConstraints(), x -> {
//            x.gridx = 1;
//            x.gridy = 0;
//            x.fill = GridBagConstraints.HORIZONTAL;
//            x.weightx = 1.0;
//        }));
//
//        pnlMain.add(new JLabel("Nominal dana"), ObjectHelper.apply(new GridBagConstraints(), x -> {
//            x.gridx = 0;
//            x.gridy = 1;
//            x.anchor = GridBagConstraints.LINE_END;
//            x.insets = new Insets(10, 0, 10, 0);
//        }));
//        pnlMain.add(edNominal, ObjectHelper.apply(new GridBagConstraints(), x -> {
//            x.gridx = 1;
//            x.gridy = 1;
//            x.fill = GridBagConstraints.HORIZONTAL;
//            x.weightx = 1.0;
//        }));

        pnlMain.add(new JLabel("Keperluan dana"), ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 0;
            x.gridy = 0;
            x.anchor = GridBagConstraints.FIRST_LINE_END;
        }));
        pnlMain.add(edKeperluanDana, ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 1;
            x.gridy = 0;
            x.fill = GridBagConstraints.BOTH;
            x.weightx = 1.0;
            x.weighty = 1.0;
            x.insets = new Insets(10, 0, 10, 0);
        }));

        pnlMain.add(new JLabel("Keterangan tambahan"), ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 0;
            x.gridy = 1;
            x.anchor = GridBagConstraints.FIRST_LINE_END;
        }));
        pnlMain.add(edKeteranganTambahan, ObjectHelper.apply(new GridBagConstraints(), x -> {
            x.gridx = 1;
            x.gridy = 1;
            x.fill = GridBagConstraints.BOTH;
            x.weightx = 1.0;
            x.weighty = 1.0;
            x.insets = new Insets(10, 0, 0, 0);
        }));

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            Map<String, Object> params = new HashMap<>();
            params.put("IdTransaksi", input.idTransaksi);
            params.put("StrTglTransaksi", input.strTglTransaksi);
            params.put("NamaUmat", input.namaUmat);
            params.put("NominalDana", input.nominal);
            params.put("NominalDanaKata2", input.nominalKata2);
            params.put("KeperluanDana", edKeperluanDana.getText());
            params.put("KeteranganTambahan", edKeteranganTambahan.getText());

            JasperReport jasperReport = JasperHelper.loadJasperFileFromResource(GridHistoryPembayaranDanaRutin.class, "tanda_terima_dana.jasper");
            JasperPrint jasperPrint = JasperHelper.fillReport(jasperReport, params, new JREmptyDataSource());
            JasperHelper.showReportPreview(jasperPrint);

            this.dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
        setContentPane(contentPane);
    }

    public static class Input {
        public String idTransaksi;

        private LocalDate tglTransaksi;
        public String strTglTransaksi;

        public String namaUmat;

        private int nominal;
        public String nominalKata2;

        public String keperluanDana;
        public String keteranganTambahan;

        public void set(PembayaranDanaRutin x, String keperluanDana) {
            idTransaksi = x.getIdTransaksi();
            setTglTransaksi(x.tgl);
            namaUmat = x.umat.nama;
            setNominal(x.totalNominal);
            this.keperluanDana = keperluanDana;
            keteranganTambahan = x.keterangan;
        }

        public void set(Pendapatan x) {
            idTransaksi = x.getIdTransaksi();
            setTglTransaksi(x.tglTransaksi);
            namaUmat = x.umat == null? "" : x.umat.nama;
            setNominal(x.nominal);
            keperluanDana = x.getKeperluanDana();
            keteranganTambahan = x.keterangan;
        }

        public void setTglTransaksi(LocalDate tglTransaksi) {
            this.tglTransaksi = tglTransaksi;
            this.strTglTransaksi = tglTransaksi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public int getNominal() {
            return nominal;
        }

        public void setNominal(int nominal) {
            this.nominal = nominal;
            this.nominalKata2 = NumberHelper.toIndonesianWords(new BigInteger(String.valueOf(nominal)));
        }
    }
}
