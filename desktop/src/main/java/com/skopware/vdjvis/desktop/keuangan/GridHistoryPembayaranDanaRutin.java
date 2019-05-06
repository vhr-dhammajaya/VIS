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

        JButton btnCetakKuitansi = new JButton("Cetak tanda terima");
        btnCetakKuitansi.addActionListener(e -> {
            PembayaranDanaRutin sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }

            Map<String, String> keperluanDana = HttpHelper.makeHttpRequest(App.config.url("/pembayaran_dana_rutin/get_keperluan"), HttpGetWithBody::new, sel, Map.class, String.class, String.class);

            DialogPrepareTandaTerima.Input jasperParams = new DialogPrepareTandaTerima.Input();
            jasperParams.set(sel, keperluanDana.get("keperluanDana"));

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
                HttpHelper.makeHttpRequest(App.config.url("/pembayaran_dana_rutin/request_koreksi"), HttpPost::new, sel, boolean.class);
                o.grid.tableModel.fireTableRowsUpdated(selIdx, selIdx);
            });
            dialogInput.setVisible(true);
            dialogInput.pack();
        });

        o.additionalToolbarButtons.add(btnMintaPembetulan);

        return o;
    }
}
