package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.*;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.PendaftaranDanaRutin;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridPendaftaranDanaRutin {
    public static JDataGrid<PendaftaranDanaRutin> createDefault() {
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

        o.fnShowCreateForm = () -> new FormPendaftaranDanaRutin(App.mainFrame);
        o.fnShowEditForm = (record, modelIdx) -> new FormPendaftaranDanaRutin(App.mainFrame, record, modelIdx);

        return new JDataGrid<>(o);
    }

    public static class FormPendaftaranDanaRutin extends BaseCrudForm<PendaftaranDanaRutin> {
        private JDatePicker txtTglDaftar;
        private JSpinner txtNominal;
        private JRadioButton btnTipeSosial;
        private JRadioButton btnTipeTetap;
        private EnumFormFieldMapper<PendaftaranDanaRutin.Type> mapperTipe;

        public FormPendaftaranDanaRutin(Frame owner) {
            super(owner, "Pendaftaran dana sosial/tetap", PendaftaranDanaRutin.class);
        }

        public FormPendaftaranDanaRutin(Frame owner, PendaftaranDanaRutin record, int modelIdx) {
            super(owner, "", PendaftaranDanaRutin.class, record, modelIdx);
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
                    new Tuple2<>(PendaftaranDanaRutin.Type.sosial, btnTipeSosial),
                    new Tuple2<>(PendaftaranDanaRutin.Type.tetap, btnTipeTetap));

            pnlFormFields = SwingHelper.buildForm1(Arrays.asList(
                    new Tuple2<>("Jenis", pnlTipe),
                    new Tuple2<>("Nominal", txtNominal),
                    new Tuple2<>("Tgl daftar", txtTglDaftar)
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
            r.umatId = jDataGrid.parentRecordId;
        }
    }
}
