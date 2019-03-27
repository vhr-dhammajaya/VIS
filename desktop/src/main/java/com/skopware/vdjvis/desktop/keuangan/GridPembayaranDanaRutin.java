package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.jasperreports.JasperHelper;
import com.skopware.javautils.swing.BaseCrudForm;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.JForeignKeyPicker;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.api.entities.User;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.DialogInputAlasanMintaPembetulan;
import com.skopware.vdjvis.desktop.MainFrame;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

        o.recordType = PembayaranDanaRutin.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/pembayaran_dana_rutin";

        o.enableAdd = false;
        o.enableEdit = false;
//        o.fnShowEditForm = (rec, idx) -> new FormPembayaranDanaRutin(App.mainFrame, rec, idx);

        JButton btnCetakKuitansi = new JButton("Cetak tanda terima");
        btnCetakKuitansi.addActionListener(e -> {
            PembayaranDanaRutin sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }

            Map<String, String> keperluanDana = HttpHelper.makeHttpRequest(App.config.url("/pembayaran_dana_rutin/get_keperluan"), HttpGetWithBody::new, sel, Map.class, String.class, String.class);

            DialogPrepareTandaTerima dialog = new DialogPrepareTandaTerima(App.mainFrame, sel.umat.nama, sel.totalNominal, keperluanDana.get("keperluanDana"), sel.keterangan);
            dialog.setVisible(true);
            dialog.pack();

//            Map<String, Object> params = new HashMap<>();
//            params.put("NamaUmat", sel.umat.nama);
//            params.put("NominalDana", sel.totalNominal);
//            params.put("KeperluanDana", keperluanDana.get("keperluanDana"));
//            params.put("KeteranganTambahan", sel.keterangan);
//
//            JasperReport jasperReport = JasperHelper.loadJasperFileFromResource(GridPembayaranDanaRutin.class, "tanda_terima_dana.jasper");
//            JasperPrint jasperPrint = JasperHelper.fillReport(jasperReport, params, new JREmptyDataSource());
//            JasperHelper.showReportPreview(jasperPrint);
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
                HttpHelper.makeHttpRequest(App.config.url("/pembayaran_dana_rutin/request_koreksi"), HttpPost::new, sel, boolean.class);
                o.grid.tableModel.fireTableRowsUpdated(selIdx, selIdx);
            });
            dialogInput.setVisible(true);
            dialogInput.pack();
        });

        o.additionalToolbarButtons.add(btnMintaPembetulan);

        return o;
    }

//    public static class FormPembayaranDanaRutin extends BaseCrudForm<PembayaranDanaRutin> {
//        private JForeignKeyPicker<Umat> edUmat;
//        private JDatePicker edTgl;
//        private JComboBox<String> edChannel;
//        private JTextArea edKeterangan;
//
//
//        public FormPembayaranDanaRutin(Frame owner, PembayaranDanaRutin record, int modelIdx) {
//            super(owner, "Edit pembayaran iuran samanagara / dana sosial / dana tetap", PembayaranDanaRutin.class, record, modelIdx);
//        }
//
//        @Override
//        protected void initFormFields() {
//
//        }
//
//        @Override
//        protected void syncModelToGui() {
//
//        }
//
//        @Override
//        protected boolean validateFormFields() {
//            return false;
//        }
//
//        @Override
//        protected void syncGuiToModel() {
//
//        }
//    }
}
