package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.jasperreports.JasperHelper;
import com.skopware.javautils.swing.BaseCrudForm;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.SortConfig;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.DialogInputAlasanMintaPembetulan;
import com.skopware.vdjvis.desktop.MainFrame;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GridDetilPembayaranDanaRutin {
    public static JDataGridOptions<DetilPembayaranDanaRutin> createDefaultOptions() {
        JDataGridOptions<DetilPembayaranDanaRutin> o = new JDataGridOptions<>();

        o.enableAdd = false; // admin can only edit & delete

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "umat.nama";
                    x.dbColumnName = "umat_nama";
                    x.label = "Nama umat";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "jenis";
                    x.dbColumnName = "jenis";
                    x.label = "Jenis (samanagara / sosial / tetap)";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "leluhurSamanagara.nama";
                    x.dbColumnName = "leluhur_nama";
                    x.label = "Nama leluhur (ut Samanagara)";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "untukBulan.year";
                    x.dbColumnName = "ut_thn";
                    x.label = "Untuk tahun";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "untukBulan.month";
                    x.dbColumnName = "ut_bln";
                    x.label = "Untuk bulan";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nominal";
                    x.dbColumnName = "nominal";
                    x.label = "Nominal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglTrans";
                    x.dbColumnName = "tgl";
                    x.label = "Tgl transaksi";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "channel";
                    x.dbColumnName = "channel";
                    x.label = "Cara dana";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "keterangan";
                    x.dbColumnName = "keterangan";
                    x.label = "Keterangan";
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

        o.recordType = DetilPembayaranDanaRutin.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/detil_pembayaran_dana_rutin";
        o.initialSortConfig.add(ObjectHelper.apply(new SortConfig(), x -> {
            x.field = "tgl";
            x.dir = SortConfig.SortDir.DESC;
        }));

        o.fnShowEditForm = (rec, idx) -> new FormDetilPembayaranDanaRutin(App.mainFrame, rec, idx);

        JButton btnCetakKuitansi = new JButton("Cetak tanda terima");
        btnCetakKuitansi.addActionListener(e -> {
            DetilPembayaranDanaRutin sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("NamaUmat", sel.umat.nama);
            params.put("NominalDana", sel.nominal);
            params.put("KeperluanDana", sel.getKeperluanDana());
            params.put("KeteranganTambahan", sel.keterangan);

            JasperReport jasperReport = JasperHelper.loadJasperFileFromResource(GridPendapatan.class, "tanda_terima_dana.jasper");
            JasperPrint jasperPrint = JasperHelper.fillReport(jasperReport, params, new JREmptyDataSource());
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
        });

        o.additionalToolbarButtons.add(btnCetakKuitansi);

        return o;
    }

    public static JDataGridOptions<DetilPembayaranDanaRutin> createForOperator() {
        JDataGridOptions<DetilPembayaranDanaRutin> o = createDefaultOptions();
        o.enableEdit = o.enableDelete = false;

        JButton btnMintaPembetulan = new JButton("Minta pembetulan");
        btnMintaPembetulan.addActionListener(e -> {
            DetilPembayaranDanaRutin sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }
            int selIdx = o.grid.getSelectedRecordIdx();

            DialogInputAlasanMintaPembetulan dialogInput = new DialogInputAlasanMintaPembetulan(reason -> {
                sel.correctionStatus = true;
                sel.correctionRequestReason = reason;
                HttpHelper.makeHttpRequest(App.config.url("/detil_pembayaran_dana_rutin/request_koreksi"), HttpPost::new, sel, boolean.class);
                o.grid.tableModel.fireTableRowsUpdated(selIdx, selIdx);
            });
            dialogInput.setVisible(true);
            dialogInput.pack();
        });

        o.additionalToolbarButtons.add(btnMintaPembetulan);

        return o;
    }

    public static class FormDetilPembayaranDanaRutin extends BaseCrudForm<DetilPembayaranDanaRutin> {
        private JSpinner edNominal;
        private JDatePicker edTglTrans;
        private JComboBox<String> edChannel;
        private JTextArea edKeterangan;
        private JCheckBox edButuhPembetulan;
        private JTextArea edAlasanButuhPembetulan;

        public FormDetilPembayaranDanaRutin(Frame owner, DetilPembayaranDanaRutin rec, int idx) {
            super(owner, "Edit detil pembayaran", DetilPembayaranDanaRutin.class, rec, idx);
        }

        @Override
        protected void initFormFields() {
            edNominal = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1000));
            edTglTrans = new JDatePicker();
            edChannel = new JComboBox<>(new String[]{"Tunai", "EDC", "Transfer ke rek. BCA"});
            edKeterangan = new JTextArea(10, 20);

            edButuhPembetulan = new JCheckBox();

            edAlasanButuhPembetulan = new JTextArea(10, 20);
            edAlasanButuhPembetulan.setEditable(false);

            pnlFormFields = SwingHelper.buildForm1(Arrays.asList(
                    new Tuple2<>("Nominal", edNominal),
                    new Tuple2<>("Tgl transaksi", edTglTrans),
                    new Tuple2<>("Cara dana", edChannel),
                    new Tuple2<>("Keterangan", edKeterangan),
                    new Tuple2<>("Butuh pembetulan?", edButuhPembetulan),
                    new Tuple2<>("Alasan butuh pembetulan", edAlasanButuhPembetulan)
            ));
        }

        @Override
        protected void syncModelToGui() {
            DetilPembayaranDanaRutin r = this.editedRecord;

            edNominal.setValue(r.nominal);
            edTglTrans.setDate(r.tglTrans);
            edChannel.setSelectedItem(r.channel);
            edKeterangan.setText(r.keterangan);
            edButuhPembetulan.setSelected(r.correctionStatus);
            edAlasanButuhPembetulan.setText(r.correctionRequestReason);
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(this,
                    new Tuple2<>(edTglTrans.getDate() == null, "Tgl transaksi tidak boleh kosong")
            );
        }

        @Override
        protected void syncGuiToModel() {
            DetilPembayaranDanaRutin r = this.editedRecord;

            r.nominal = (int) edNominal.getValue();
            r.tglTrans = edTglTrans.getDate();
            r.channel = (String) edChannel.getSelectedItem();
            r.keterangan = edKeterangan.getText();
            r.correctionStatus = edButuhPembetulan.isSelected();
            if (!r.correctionStatus) {
                r.correctionRequestReason = "";
            }
        }
    }
}
