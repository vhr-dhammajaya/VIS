package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.JForeignKeyPicker;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.api.requestparams.RqLaporanStatusDanaRutin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FrameLaporanDanaRutin extends JInternalFrame {
    private static final String TABLE_CL_ID = "TABLE_CL_ID";

    private JForeignKeyPicker<Umat> edUmat;
    private JTable table;
    private DefaultTableModel tableModel;

    public FrameLaporanDanaRutin() {
        super("Laporan status iuran samanagara, dana sosial & tetap", true, true, true, true);

        edUmat = new JForeignKeyPicker<>(App.mainFrame, GridUmat.createNoAddEditDelete());

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlFilter.add(new JLabel("Pilih umat"));
        pnlFilter.add(edUmat);
        pnlFilter.add(btnRefresh);

        table = new JTable(new Vector(), ObjectHelper.apply(new Vector<String>(), columnNames -> {
            columnNames.add("Nama umat");
            columnNames.add("No. telpon");
            columnNames.add("Alamat");

            columnNames.add("Jenis dana");
            columnNames.add("Status");
            columnNames.add("Berapa bulan");
            columnNames.add("Jumlah (Rp)");
        }));
        tableModel = (DefaultTableModel) table.getModel();

        JScrollPane pnlTable = new JScrollPane(table);

        JPanel pnlMain = new JPanel(new CardLayout());
        pnlMain.add(pnlTable, TABLE_CL_ID);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(pnlFilter, BorderLayout.NORTH);
        contentPane.add(pnlMain, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    private void onRefresh(ActionEvent event) {
        Umat record = edUmat.getRecord();

        RqLaporanStatusDanaRutin rq = new RqLaporanStatusDanaRutin();
        rq.idUmat = record == null? null : record.uuid;

        List<Map<String, Object>> result = HttpHelper.makeHttpRequest(App.config.url("/laporan/status_dana_rutin"), HttpGetWithBody::new, rq, List.class);

        tableModel.setRowCount(0);
        for (Map<String, Object> row : result) {
            tableModel.addRow(new Object[] {
                    row.get("namaUmat"),
                    row.get("noTelpon"),
                    row.get("alamat"),
                    row.get("jenisDana"),
                    row.get("status"),
                    row.get("months"),
                    row.get("totalRp"),
            });
        }
        tableModel.fireTableDataChanged();
    }
}
