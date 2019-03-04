package com.skopware.vdjvis.desktop;

import com.skopware.javautils.Tuple2;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.requestparams.RqBayarDanaSosialDanTetap;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Arrays;

public class DialogBayarDanaSosialTetap extends JDialog {
    private PendaftaranDanaRutin danaRutin;

    private JDatePicker edTglTrans;
    private JSpinner edCountBulan;
    private JFormattedTextField edNominal;
    private JComboBox<String> edChannel;
    private JTextArea edKeterangan;

    public DialogBayarDanaSosialTetap(PendaftaranDanaRutin danaRutin) {
        super(App.mainFrame, "Pembayaran dana sosial/tetap", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.danaRutin = danaRutin;

        edTglTrans = new JDatePicker();
        edTglTrans.setDate(LocalDate.now());

        edCountBulan = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        edCountBulan.addChangeListener(e -> {
            int count = (int) edCountBulan.getValue();
            int nominal = count * danaRutin.nominal;
            edNominal.setValue(nominal);
        });

        edNominal = new JFormattedTextField(new DecimalFormat("###,###,##0"));
        edNominal.setValue(danaRutin.nominal);
        edNominal.setEditable(false);
        edNominal.setColumns(11);

        edChannel = new JComboBox<>(new String[]{"Tunai", "EDC", "Transfer ke rek. BCA"});
        edKeterangan = new JTextArea(10, 20);

        JPanel pnlFormFields = SwingHelper.buildForm1(Arrays.asList(
                new Tuple2<>("Tgl transaksi", edTglTrans),
                new Tuple2<>("Bayar ut berapa bulan?", edCountBulan),
                new Tuple2<>("Nominal", edNominal),
                new Tuple2<>("Cara bayar", edChannel),
                new Tuple2<>("Keterangan", edKeterangan)
        ));

        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");

        btnOk.addActionListener(e -> {
            // fixme don't block ui thread
            RqBayarDanaSosialDanTetap requestParam = new RqBayarDanaSosialDanTetap();
            requestParam.idPendaftaran = danaRutin.uuid;
            requestParam.tglTrans = edTglTrans.getDate();
            requestParam.countBulan = (int) edCountBulan.getValue();
            requestParam.channel = (String) edChannel.getSelectedItem();
            requestParam.keterangan = edKeterangan.getText();

            HttpHelper.makeHttpRequest(App.config.url("/pendaftaran_dana_rutin/bayar_dana_sosial_tetap"), HttpPost::new, requestParam, boolean.class);

            this.dispose();
        });

        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlFormFields, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
