package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.NumberHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.jasperreports.JasperHelper;
import com.skopware.javautils.swing.*;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.datasource.JdbiDataSource;
import com.skopware.vdjvis.api.entities.Acara;
import com.skopware.vdjvis.api.entities.Pengeluaran;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.master.GridAcara;
import com.skopware.vdjvis.jdbi.dao.PengeluaranDAO;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import javax.swing.*;
import java.awt.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
                    x.label = "Dibayarkan kepada";
                    x.fieldName = x.dbColumnName = "penerima";
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

        o.appConfig = App.config;
        o.dataSource = new JdbiDataSource<>(Pengeluaran.class, App.jdbi, "v_pengeluaran", PengeluaranDAO.class);

        o.fnShowCreateForm = () -> new FormPengeluaran(App.mainFrame);
        o.fnShowEditForm = (rec, idx) -> new FormPengeluaran(App.mainFrame, rec, idx);

        JButton btnCetakBukti = new JButton("Cetak bukti kas keluar");
        btnCetakBukti.addActionListener(e -> {
            Pengeluaran sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }

            Map<String, Object> params = computeJasperParams(sel);

            JasperReport jasperReport = JasperHelper.loadJasperFileFromResource(GridPengeluaran.class, "bukti_kas_keluar.jasper");
            JasperPrint jasperPrint = JasperHelper.fillReport(jasperReport, params, new JREmptyDataSource());
            JasperHelper.showReportPreview(jasperPrint);
        });

        o.additionalToolbarButtons.add(btnCetakBukti);

        return new JDataGrid<>(o);
    }

    private static Map<String, Object> computeJasperParams(Pengeluaran x) {
        Map<String, Object> params = new HashMap<>();
        params.put("IdTransaksi", x.getIdTransaksi());
        params.put("TglTransaksi", x.tglTransaksi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        params.put("Penerima", x.penerima);
        params.put("Nominal", x.nominal);
        params.put("NominalKata2", NumberHelper.toIndonesianWords(new BigInteger(String.valueOf(x.nominal))));
        params.put("Acara", x.acara == null? "" : x.acara.nama);
        params.put("Keterangan", x.keterangan);
        return params;
    }

    public static class FormPengeluaran extends BaseCrudForm<Pengeluaran> {
        private JDatePicker edTglTrans;
        private JTextField edPenerima;
        private JSpinner edNominal;
        private JTextArea edKeterangan;
        private JForeignKeyPicker<Acara> edAcara;

        public FormPengeluaran(Frame owner) {
            super(owner, "Input pengeluaran", Pengeluaran.class);

            onBackendSuccess = (createdRecord) -> {
                this.dispose();

                Map<String, Object> params = computeJasperParams(createdRecord);

                JasperReport jasperReport = JasperHelper.loadJasperFileFromResource(GridPengeluaran.class, "bukti_kas_keluar.jasper");
                JasperPrint jasperPrint = JasperHelper.fillReport(jasperReport, params, new JREmptyDataSource());
                JasperHelper.showReportPreview(jasperPrint);
            };
        }

        public FormPengeluaran(Frame owner, Pengeluaran record, int modelIdx) {
            super(owner, "Edit pengeluaran", Pengeluaran.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            edTglTrans = new JDatePicker();
            edTglTrans.setDate(LocalDate.now());

            edPenerima = new JTextField(20);
            edNominal = new JSpinner(new SpinnerNumberModel(1000, 1000, Integer.MAX_VALUE, 1000));
            edKeterangan = new JTextArea(10, 20);
            edAcara = new JForeignKeyPicker<>(App.mainFrame, GridAcara.createNoAddEditDelete());

            pnlFormFields = SwingHelper.buildForm1(Arrays.asList(
                    new Tuple2<>("Tgl transaksi", edTglTrans),
                    new Tuple2<>("Dibayarkan kepada", edPenerima),
                    new Tuple2<>("Nominal", edNominal),
                    new Tuple2<>("Keterangan", edKeterangan),
                    new Tuple2<>("Untuk acara", edAcara)
            ));
        }

        @Override
        protected void syncModelToGui() {
            Pengeluaran r = this.editedRecord;

            edTglTrans.setDate(r.tglTransaksi);
            edPenerima.setText(r.penerima);
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
            r.penerima = edPenerima.getText();
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
