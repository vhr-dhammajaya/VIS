package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.CollectionHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.datasource.JdbiDataSource;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.DialogInputAlasanMintaPembetulan;
import com.skopware.vdjvis.jdbi.dao.PembayaranDanaRutinDAO;

import javax.swing.*;
import java.util.*;

public class GridHistoryPembayaranDanaRutin {
    public static JDataGridOptions<PembayaranDanaRutin> createForUser(User user) {
        switch (user.tipe) {
            case PENGURUS:
                return createBaseOptions();
            case OPERATOR:
                return createOptionsForOperator();
            default:
                throw new IllegalStateException();
        }
    }

    public static JDataGridOptions<PembayaranDanaRutin> createBaseOptions() {
        JDataGridOptions<PembayaranDanaRutin> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "ID Transaksi";
                    x.fieldName = "idTrx";
                    x.dbColumnName = "id_trx";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Tanggal transaksi";
                    x.fieldName = "tgl";
                    x.dbColumnName = "tgl";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Nama Umat";
                    x.fieldName = "umat.nama";
                    x.dbColumnName = "umat_nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Total Rp";
                    x.fieldName = "totalNominal";
                    x.dbColumnName = "total_nominal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Cara dana";
                    x.fieldName = "channel";
                    x.dbColumnName = "channel";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Keterangan";
                    x.fieldName = "keterangan";
                    x.dbColumnName = "keterangan";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "correctionStatus";
                    x.dbColumnName = "correction_status";
                    x.label = "Butuh pembetulan?";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "correctionRequestReason";
                    x.dbColumnName = "corr_req_reason";
                    x.label = "Alasan";
                })
        );

        o.appConfig = App.config;
        o.dataSource = new JdbiDataSource<>(PembayaranDanaRutin.class, App.jdbi, "v_pembayaran_samanagara_sosial_tetap", PembayaranDanaRutinDAO.class);

        o.enableAdd = false;
        o.enableEdit = false;

        JButton btnCetakKuitansi = new JButton("Cetak tanda terima");
        btnCetakKuitansi.addActionListener(e -> {
            PembayaranDanaRutin sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }

            String keperluanDana = App.jdbi.withHandle(h -> sel.computeKeperluanDana(h));

            DialogPrepareTandaTerima.Input jasperParams = new DialogPrepareTandaTerima.Input();
            jasperParams.set(sel, keperluanDana);

            DialogPrepareTandaTerima dialog = new DialogPrepareTandaTerima(App.mainFrame, jasperParams);
            dialog.setVisible(true);
            dialog.pack();
        });

        o.additionalToolbarButtons.add(btnCetakKuitansi);

        return o;
    }

    public static JDataGridOptions<PembayaranDanaRutin> createOptionsForOperator() {
        JDataGridOptions<PembayaranDanaRutin> o = createBaseOptions();
        o.enableEdit = o.enableDelete = false;

        JButton btnMintaPembetulan = new JButton("Minta pembetulan");
        btnMintaPembetulan.addActionListener(e -> {
            PembayaranDanaRutin sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }
            int selIdx = o.grid.getSelectedRecordIdx();

            DialogInputAlasanMintaPembetulan dialogInput = new DialogInputAlasanMintaPembetulan(reason -> {
                sel.correctionStatus = true;
                sel.correctionRequestReason = reason;

                App.jdbi.useHandle(h -> h.createUpdate("update pembayaran_samanagara_sosial_tetap set correction_status=1, corr_req_reason=:reason" +
                        " where uuid=:uuid")
                        .bind("reason", sel.correctionRequestReason)
                        .bind("uuid", sel.uuid)
                        .execute()
                );

                o.grid.tableModel.fireTableRowsUpdated(selIdx, selIdx);
            });
            dialogInput.setVisible(true);
            dialogInput.pack();
        });

        o.additionalToolbarButtons.add(btnMintaPembetulan);

        return o;
    }
}
