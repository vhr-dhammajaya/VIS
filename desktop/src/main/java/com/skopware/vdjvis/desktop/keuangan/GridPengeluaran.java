package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.*;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.Acara;
import com.skopware.vdjvis.api.entities.Pengeluaran;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.master.GridAcara;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;

public class GridPengeluaran {
    public static JDataGrid<Pengeluaran> create() {
        JDataGridOptions<Pengeluaran> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Tgl";
                    x.fieldName = "tglTransaksi";
                    x.dbColumnName = "tgl_trx";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Nominal";
                    x.fieldName = x.dbColumnName = "nominal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Untuk acara";
                    x.fieldName = "acara.nama";
                    x.dbColumnName = "acara_nama";
                })
        );

        o.recordType = Pengeluaran.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/pengeluaran";

        o.fnShowCreateForm = () -> new FormPengeluaran(App.mainFrame);
        o.fnShowEditForm = (rec, idx) -> new FormPengeluaran(App.mainFrame, rec, idx);

        return new JDataGrid<>(o);
    }

    public static class FormPengeluaran extends BaseCrudForm<Pengeluaran> {
        private JDatePicker edTglTrans;
        private JSpinner edNominal;
        private JTextArea edKeterangan;
        private JForeignKeyPicker<Acara> edAcara;

        public FormPengeluaran(Frame owner) {
            super(owner, "Input pengeluaran", Pengeluaran.class);
        }

        public FormPengeluaran(Frame owner, Pengeluaran record, int modelIdx) {
            super(owner, "Edit pengeluaran", Pengeluaran.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            edTglTrans = new JDatePicker();
            edTglTrans.setDate(LocalDate.now());

            edNominal = new JSpinner(new SpinnerNumberModel(1000, 1000, Integer.MAX_VALUE, 1000));
            edKeterangan = new JTextArea(10, 20);
            edAcara = new JForeignKeyPicker<>(App.mainFrame, GridAcara.createNoAddEditDelete());

            pnlFormFields = SwingHelper.buildForm2(Arrays.asList(
                    Arrays.asList(new Tuple2<>("Tgl transaksi", edTglTrans), new Tuple2<>("Nominal", edNominal)),
                    Arrays.asList(new Tuple2<>("Keterangan", edKeterangan)),
                    Arrays.asList(new Tuple2<>("Untuk acara", edAcara))
            ));
        }

        @Override
        protected void syncModelToGui() {
            Pengeluaran r = this.editedRecord;

            edTglTrans.setDate(r.tglTransaksi);
            edNominal.setValue(r.nominal);
            edKeterangan.setText(r.keterangan);
            edAcara.setRecord(r.acara);
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(this,
                    new Tuple2<>(edTglTrans.getDate() == null, "Tanggal tidak boleh kosong"),
                    new Tuple2<>(edNominal.getValue() == null, "Nominal tidak boleh kosong")
            );
        }

        @Override
        protected void syncGuiToModel() {
            Pengeluaran r = this.editedRecord;

            r.tglTransaksi = edTglTrans.getDate();
            r.nominal = (int) edNominal.getValue();
            r.keterangan = edKeterangan.getText();

            Acara acara = edAcara.getRecord();
            r.acara = new Acara(); // reset uuid to null
            if (acara != null) {
                r.acara = acara;
            }
        }
    }
}
