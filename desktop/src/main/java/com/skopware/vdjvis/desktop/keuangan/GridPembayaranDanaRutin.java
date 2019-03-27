package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudForm;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.User;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridPembayaranDanaRutin {
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
                })
        );

        o.recordType = PembayaranDanaRutin.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/pembayaran_dana_rutin";

        o.enableAdd = false;

        o.fnShowEditForm = (rec, idx) -> new FormPembayaranDanaRutin(App.mainFrame, rec, idx);

        JButton btnCetakKuitansi = new JButton("Cetak tanda terima");
        btnCetakKuitansi.addActionListener(e -> {
            // todo
        });

        o.additionalToolbarButtons.add(btnCetakKuitansi);

        return o;
    }

    public static JDataGridOptions<PembayaranDanaRutin> createOptionsForOperator() {
        JDataGridOptions<PembayaranDanaRutin> o = createBaseOptions();
        o.enableEdit = o.enableDelete = false;

        JButton btnMintaPembetulan = new JButton("Minta pembetulan");
        btnMintaPembetulan.addActionListener(e -> {
            // todo
        });

        o.additionalToolbarButtons.add(btnMintaPembetulan);

        return o;
    }

    public static class FormPembayaranDanaRutin extends BaseCrudForm<PembayaranDanaRutin> {

        public FormPembayaranDanaRutin(Frame owner, PembayaranDanaRutin record, int modelIdx) {
            super(owner, "Edit pembayaran iuran samanagara / dana sosial / dana tetap", PembayaranDanaRutin.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {

        }

        @Override
        protected void syncModelToGui() {

        }

        @Override
        protected boolean validateFormFields() {
            return false;
        }

        @Override
        protected void syncGuiToModel() {

        }
    }
}
