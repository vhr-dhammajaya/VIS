package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.EnumFormFieldMapper;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.PendaftaranDanaRutin;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridPendaftaranDanaRutin extends JDataGrid<PendaftaranDanaRutin> {
    public static JDataGridOptions<PendaftaranDanaRutin> createDefaultOptions() {
        JDataGridOptions<PendaftaranDanaRutin> o = new JDataGridOptions<>();

        o.enableEdit = false;
        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nominal";
                    x.label = "Nominal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "tipe";
                    x.label = "Jenis";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglDaftar";
                    x.dbColumnName = "tgl_daftar";
                    x.label = "Tgl daftar";
                })
        );

        o.recordType = PendaftaranDanaRutin.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/pendaftaran_dana_rutin";

        return o;
    }

    public GridPendaftaranDanaRutin(JDataGridOptions<PendaftaranDanaRutin> options) {
        super(options);
    }

    @Override
    protected void showCreateForm() {
        new FormPendaftaranDanaRutin(App.mainFrame, "Pendaftaran dana sosial / tetap baru");
    }

    @Override
    protected void showEditForm(PendaftaranDanaRutin record, int modelIdx) {

    }

    public class FormPendaftaranDanaRutin extends BaseCrudForm {
        private JDatePicker txtTglDaftar;
        private JSpinner txtNominal;
        private JRadioButton btnTipeSosial;
        private JRadioButton btnTipeTetap;
        private EnumFormFieldMapper<String> mapperTipe;

        public FormPendaftaranDanaRutin(Frame owner, String title) {
            super(owner, title);
        }

        public FormPendaftaranDanaRutin(Frame owner, String title, PendaftaranDanaRutin record, int modelIdx) {
            super(owner, title, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            txtTglDaftar = new JDatePicker();
            txtNominal = new JSpinner(new SpinnerNumberModel(1000, 1000, Integer.MAX_VALUE, 1000));

            btnTipeSosial = new JRadioButton("Sosial");
            btnTipeTetap = new JRadioButton("Tetap");

            ButtonGroup grpTipe = new ButtonGroup();
            grpTipe.add(btnTipeSosial);
            grpTipe.add(btnTipeTetap);

            JPanel pnlTipe = new JPanel(new FlowLayout());
            pnlTipe.add(btnTipeSosial);
            pnlTipe.add(btnTipeTetap);

            mapperTipe = new EnumFormFieldMapper<>(
                    new Tuple2<>("sosial", btnTipeSosial),
                    new Tuple2<>("tetap", btnTipeTetap));

            pnlFormFields = SwingHelper.buildForm(1, Arrays.asList(
                    Arrays.asList(new Tuple2<>("Jenis", pnlTipe)),
                    Arrays.asList(new Tuple2<>("Nominal", txtNominal)),
                    Arrays.asList(new Tuple2<>("Tgl daftar", txtTglDaftar))
            ));
        }

        @Override
        protected void syncModelToGui() {
            PendaftaranDanaRutin r = this.editedRecord;
            txtNominal.setValue(r.nominal);
            txtTglDaftar.setDate(r.tglDaftar);
            mapperTipe.modelToGui(r.tipe);
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(this,
                    new Tuple2<>(!btnTipeSosial.isSelected() && !btnTipeTetap.isSelected(), "Anda harus memilih jenis dana"));
        }

        @Override
        protected void syncGuiToModel() {
            PendaftaranDanaRutin r = this.editedRecord;
            r.nominal = (int) txtNominal.getValue();
            r.tglDaftar = txtTglDaftar.getDate();
            r.tipe = mapperTipe.guiToModel();
            r.umatId = GridPendaftaranDanaRutin.this.parentRecordId;
        }
    }
}
