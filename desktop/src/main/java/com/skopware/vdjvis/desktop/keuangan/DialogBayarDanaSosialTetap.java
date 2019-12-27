package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.Holder;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PendaftaranDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.jdbi.v3.core.Handle;

import javax.swing.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.UUID;

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
            PembayaranDanaRutin pembayaran = new PembayaranDanaRutin();
            Holder<String> keperluanDana = new Holder<>();

            try (Handle handle = App.jdbi.open()) {
                handle.useTransaction(handle1 -> {
                    pembayaran.uuid = UUID.randomUUID().toString();

                    pembayaran.umat = new Umat();
                    pembayaran.umat.uuid = danaRutin.umat.uuid;
                    pembayaran.umat.nama = handle1.select("select nama from umat where uuid=?", danaRutin.umat.uuid)
                            .mapTo(String.class)
                            .findOnly();

                    pembayaran.tipe = PembayaranDanaRutin.Type.sosial_tetap;
                    pembayaran.tgl = edTglTrans.getDate();
                    pembayaran.totalNominal = ((int) edCountBulan.getValue()) * danaRutin.nominal;
                    pembayaran.channel = (String) edChannel.getSelectedItem();
                    pembayaran.keterangan = edKeterangan.getText();

                    handle1.createUpdate("insert into pembayaran_samanagara_sosial_tetap(uuid, umat_id, tipe, tgl, total_nominal, channel, keterangan)" +
                            " values(?, ?, ?, ?, ?, ?, ?)")
                            .bind(0, pembayaran.uuid)
                            .bind(1, pembayaran.umat.uuid)
                            .bind(2, pembayaran.tipe.name())
                            .bind(3, pembayaran.tgl)
                            .bind(4, pembayaran.totalNominal)
                            .bind(5, pembayaran.channel)
                            .bind(6, pembayaran.keterangan)
                            .execute();
                    pembayaran.noSeq = handle1.select("select no_seq from pembayaran_samanagara_sosial_tetap where uuid=?", pembayaran.uuid)
                            .mapTo(int.class)
                            .findOnly();

                    YearMonth lastPaidMonth = PendaftaranDanaRutin.fetchLastPaidMonth(handle1, danaRutin.umat.uuid, danaRutin.uuid, danaRutin.tglDaftar);
                    YearMonth currentMonth = lastPaidMonth.plusMonths(1);
                    YearMonth smallestMonth = currentMonth;
                    YearMonth largestMonth = null;

                    int countBulan = (int) edCountBulan.getValue();
                    for (int i = 0; i < countBulan; i++) {
                        DetilPembayaranDanaRutin detil = new DetilPembayaranDanaRutin();
                        detil.uuid = UUID.randomUUID().toString();
                        detil.parentTrx = pembayaran;
                        detil.jenis = danaRutin.tipe;
                        detil.danaRutin = danaRutin;
                        detil.untukBulan = currentMonth;
                        detil.nominal = danaRutin.nominal;

                        handle1.createUpdate("insert into detil_pembayaran_dana_rutin(uuid, trx_id, jenis, dana_rutin_id, ut_thn_bln, nominal)" +
                                " values(?, ?, ?, ?, ?, ?)")
                                .bind(0, detil.uuid)
                                .bind(1, pembayaran.uuid)
                                .bind(2, detil.jenis.name())
                                .bind(3, detil.danaRutin.uuid)
                                .bind(4, LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), 1))
                                .bind(5, detil.nominal)
                                .execute();

                        pembayaran.mapDetilPembayaran.put(detil.uuid, detil);

                        if (i == countBulan-1) {
                            largestMonth = currentMonth;
                        }
                        currentMonth = currentMonth.plusMonths(1);
                    }

                    keperluanDana.value = String.format("Bayar dana %s dari %s s/d %s", danaRutin.tipe, smallestMonth, largestMonth);
                });
            }

            DialogPrepareTandaTerima.Input jasperParams = new DialogPrepareTandaTerima.Input();
            jasperParams.set(pembayaran, keperluanDana.value);

            DialogPrepareTandaTerima dialog = new DialogPrepareTandaTerima(App.mainFrame, jasperParams);
            dialog.setVisible(true);
            dialog.pack();

            this.dispose();
        });

        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlFormFields, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
