package com.skopware.vdjvis.desktop.samanagara;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.jdbi.v3.core.Handle;

import javax.swing.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public class DialogPemutihanSamanagara extends JDialog {
    private Umat umat;
    private JSpinner txtTahun;
    private JSpinner txtBulan;

    public DialogPemutihanSamanagara(Umat umat) {
        super(App.mainFrame, "Input mau diputihkan sampai bulan apa", false);
        this.umat = umat;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        YearMonth now = YearMonth.now();
        txtTahun = new JSpinner(new SpinnerNumberModel(now.getYear(), 2000, Integer.MAX_VALUE, 1));
        txtBulan = new JSpinner(new SpinnerNumberModel(now.getMonthValue(), 1, 12, 1));

        FormLayout layout = new FormLayout("right:pref, 4dlu, left:pref:grow");
        DefaultFormBuilder formBuilder = new DefaultFormBuilder(layout);
        formBuilder.append("Tahun", txtTahun);
        formBuilder.nextLine();
        formBuilder.append("Bulan", txtBulan);
        JPanel pnlContent = formBuilder.build();

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            int tahun = (int) txtTahun.getValue();
            int bulan = (int) txtBulan.getValue();

            try (Handle handle = App.jdbi.open()) {
                handle.useTransaction(handle1 -> {
                    int param = 0;
                    String parentUuid = UUID.randomUUID().toString();
                    handle1.createUpdate("insert into pembayaran_samanagara_sosial_tetap(uuid, umat_id, tipe, tgl," +
                            "total_nominal, channel, keterangan) values(?, ?, ?, ?," +
                            "?, ?, ?)")
                            .bind(param++, parentUuid)
                            .bind(param++, umat.getUuid())
                            .bind(param++, PembayaranDanaRutin.Type.samanagara.name())
                            .bind(param++, LocalDate.now())
                            .bind(param++, 0)
                            .bind(param++, "-")
                            .bind(param++, "Pemutihan")
                            .execute();

                    handle1.select("select uuid from leluhur_smngr where umat_id=?", umat.getUuid())
                            .mapTo(String.class)
                            .stream()
                            .forEach(leluhurId -> {
                                int param2 = 0;
                                handle1.createUpdate("insert into detil_pembayaran_dana_rutin(uuid, trx_id, jenis, leluhur_id," +
                                        "ut_thn_bln, nominal) values(?, ?, ?, ?," +
                                        "?, ?)")
                                        .bind(param2++, UUID.randomUUID().toString())
                                        .bind(param2++, parentUuid)
                                        .bind(param2++, DetilPembayaranDanaRutin.Type.samanagara)
                                        .bind(param2++, leluhurId)
                                        .bind(param2++, LocalDate.of(tahun, bulan, 1))
                                        .bind(param2++, 0)
                                        .execute();
                            });
                });
            }

            this.dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(pnlContent, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
