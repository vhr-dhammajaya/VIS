package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.*;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.api.Pendapatan;
import com.skopware.vdjvis.api.Umat;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridPendapatan {
    public static JDataGrid<Pendapatan> create() {
        JDataGridOptions<Pendapatan> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Umat yg berdana";
                    x.fieldName = "umat.nama";
                    x.dbColumnName = "umat_nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Tgl dana";
                    x.fieldName = "tglTransaksi";
                    x.dbColumnName = "tgl_trx";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Nominal";
                    x.fieldName = x.dbColumnName = "nominal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Cara dana";
                    x.fieldName = x.dbColumnName = "channel";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Jenis dana";
                    x.fieldName = "jenisDana";
                    x.dbColumnName = "jenis_dana";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Untuk acara";
                    x.fieldName = "acara.nama";
                    x.dbColumnName = "acara_nama";
                })
        );

        o.recordType = Pendapatan.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/pendapatan";

        o.fnShowCreateForm = () -> new FormPendapatan(App.mainFrame);
        o.fnShowEditForm = (rec, idx) -> new FormPendapatan(App.mainFrame, rec, idx);

        return new JDataGrid<>(o);
    }

    public static class FormPendapatan extends BaseCrudForm<Pendapatan> {

        private JForeignKeyPicker<Umat> edUmat;
        private JDatePicker edTglTrans;
        private JSpinner edNominal;
        private JComboBox<String> edChannel;
        private JComboBox<Pendapatan.JenisDana> edJenisDana;
        private JTextArea edKeterangan;
        private JForeignKeyPicker<Acara> edAcara;

        public FormPendapatan(Frame owner) {
            super(owner, "Input pendapatan", Pendapatan.class);
        }

        public FormPendapatan(Frame owner, Pendapatan record, int modelIdx) {
            super(owner, "Edit pendapatan", Pendapatan.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            edUmat = new JForeignKeyPicker<>(App.mainFrame, GridUmat.createNoAddEditDelete());
            edTglTrans = new JDatePicker();
            edNominal = new JSpinner(new SpinnerNumberModel(1000, 1000, Integer.MAX_VALUE, 1000));
            edChannel = new JComboBox<>(new String[]{"Tunai", "EDC", "Transfer ke rek. BCA"});
            edJenisDana = new JComboBox<>(new Pendapatan.JenisDana[] {
                Pendapatan.JenisDana.IURAN_SAMANAGARA,
                Pendapatan.JenisDana.DANA_SOSIAL,
                Pendapatan.JenisDana.DANA_TETAP,
                Pendapatan.JenisDana.DANA_SUKARELA,
                Pendapatan.JenisDana.DANA_PARAMITTA,
                Pendapatan.JenisDana.HAPPY_SUNDAY,
                Pendapatan.JenisDana.KOTAK_DANA,
                Pendapatan.JenisDana.LAIN_2
            });
            edKeterangan = new JTextArea(10, 20);
            edAcara = new JForeignKeyPicker<>(App.mainFrame, GridAcara.createNoAddEditDelete());

            pnlFormFields = SwingHelper.buildForm(Arrays.asList(
                    Arrays.asList(new Tuple2<>("Umat yg berdana", edUmat)),
                    Arrays.asList(new Tuple2<>("Tgl dana", edTglTrans), new Tuple2<>("Nominal", edNominal)),
                    Arrays.asList(new Tuple2<>("Cara dana", edChannel), new Tuple2<>("Jenis dana", edJenisDana)),
                    Arrays.asList(new Tuple2<>("Keterangan", edKeterangan)),
                    Arrays.asList(new Tuple2<>("Untuk acara", edAcara))
            ));
        }

        @Override
        protected void syncModelToGui() {
            Pendapatan r = this.editedRecord;

            edUmat.setRecord(r.umat);
            edTglTrans.setDate(r.tglTransaksi);
            edNominal.setValue(r.nominal);
            edChannel.setSelectedItem(r.channel);
            edJenisDana.setSelectedItem(r.jenisDana);
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
            Pendapatan r = this.editedRecord;

            r.setUmat(edUmat.getRecord());

            r.tglTransaksi = edTglTrans.getDate();
            r.nominal = (int) edNominal.getValue();
            r.channel = (String) edChannel.getSelectedItem();
            r.jenisDana = (Pendapatan.JenisDana) edJenisDana.getSelectedItem();
            r.keterangan = edKeterangan.getText();

            r.setAcara(edAcara.getRecord());
        }
    }
}
