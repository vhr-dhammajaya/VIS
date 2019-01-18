package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.Leluhur;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;

public class GridLeluhur extends JDataGrid<Leluhur> {
    public static GridLeluhur create() {
        JDataGridOptions<Leluhur> options = new JDataGridOptions<>();

        options.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nama";
                    x.label = "Nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatLahir";
                    x.dbColumnName = "tempat_lahir";
                    x.label = "Tempat Lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglLahir";
                    x.dbColumnName = "tgl_lahir";
                    x.label = "Tgl Lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatMati";
                    x.dbColumnName = "tempat_mati";
                    x.label = "Meninggal di";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglMati";
                    x.dbColumnName = "tgl_mati";
                    x.label = "Tgl Meninggal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "hubunganDgnUmat";
                    x.dbColumnName = "hubungan_dgn_umat";
                    x.label = "Hubungan dgn penanggung jawab";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglDaftar";
                    x.dbColumnName = "tgl_daftar";
                    x.label = "Tgl daftar";
                })
        );

        options.recordType = Leluhur.class;
        options.appConfig = App.config;
        options.shortControllerUrl = "/leluhur";

        return new GridLeluhur(options);
    }

    public GridLeluhur(JDataGridOptions<Leluhur> options) {
        super(options);
    }

    @Override
    protected void showCreateForm() {
        new FormLeluhur(App.mainFrame, "Daftarkan leluhur");
    }

    @Override
    protected void showEditForm(Leluhur record, int modelIdx) {
        new FormLeluhur(App.mainFrame, "Edit leluhur", record, modelIdx);
    }

    public class FormLeluhur extends BaseCrudForm {
        private JTextField txtNama;
        private JTextField txtTempatLahir;
        private JDatePicker txtTglLahir;
        private JTextField txtTempatMati;
        private JDatePicker txtTglMati;
        private JComboBox<String> txtHubunganDgnUmat;

        public FormLeluhur(Frame owner, String title) {
            super(owner, title);
        }

        public FormLeluhur(Frame owner, String title, Leluhur record, int modelIdx) {
            super(owner, title, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            txtNama = new JTextField(45);
            txtTempatLahir = new JTextField(45);
            txtTglLahir = new JDatePicker();
            txtTempatMati = new JTextField(45);
            txtTglMati = new JDatePicker();

            String[] hubungan = {
                    "Orang tua",
                    "Suami / istri",
                    "Anak",
                    "Saudara"
            };
            txtHubunganDgnUmat = new JComboBox<>(hubungan);
            txtHubunganDgnUmat.setEditable(true);

            pnlFormFields = SwingHelper.buildForm(2, Arrays.asList(
                    Arrays.asList(new Tuple2<>("Nama Mendiang", txtNama)),
                    Arrays.asList(new Tuple2<>("Tempat Lahir", txtTempatLahir), new Tuple2<>("Tgl Lahir", txtTglLahir)),
                    Arrays.asList(new Tuple2<>("Meninggal di", txtTempatMati), new Tuple2<>("Tgl Meninggal", txtTglMati)),
                    Arrays.asList(new Tuple2<>("Hubungan dgn penanggung jawab", txtHubunganDgnUmat))
            ));
        }

        @Override
        protected void syncModelToGui() {
            Leluhur r = this.editedRecord;
            txtNama.setText(r.nama);
            txtTempatLahir.setText(r.tempatLahir);
            txtTglLahir.setDate(r.tglLahir);
            txtTempatMati.setText(r.tempatMati);
            txtTglMati.setDate(r.tglMati);
            txtHubunganDgnUmat.setSelectedItem(r.hubunganDgnUmat);
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(
                    this,
                    new Tuple2<>(txtNama.getText().isEmpty(), "Nama tidak boleh kosong"));
        }

        @Override
        protected void syncGuiToModel() {
            Leluhur r = this.editedRecord;
            r.nama = txtNama.getText();
            r.tempatLahir = txtTempatLahir.getText();
            r.tglLahir = txtTglLahir.getDate();
            r.tempatMati = txtTempatMati.getText();
            r.tglMati = txtTglMati.getDate();
            r.hubunganDgnUmat = (String) txtHubunganDgnUmat.getSelectedItem();
            r.tglDaftar = LocalDate.now();
            r.penanggungJawabId = GridLeluhur.this.parentRecordId;
        }
    }
}
