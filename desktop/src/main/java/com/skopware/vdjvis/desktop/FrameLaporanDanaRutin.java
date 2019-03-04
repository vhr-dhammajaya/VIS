package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.JForeignKeyPicker;
import com.skopware.vdjvis.api.dto.DtoInputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.dto.DtoOutputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;

public class FrameLaporanDanaRutin extends JInternalFrame {
    private static final String TABLE_CL_ID = "TABLE_CL_ID";

    private JForeignKeyPicker<Umat> edUmat;
    private JTable table;
    private TableModel tableModel;

    public FrameLaporanDanaRutin() {
        super("Laporan status iuran samanagara, dana sosial & tetap", true, true, true, true);

        edUmat = new JForeignKeyPicker<>(App.mainFrame, GridUmat.createNoAddEditDelete());

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlFilter.add(new JLabel("Pilih umat"));
        pnlFilter.add(edUmat);
        pnlFilter.add(btnRefresh);

        tableModel = new TableModel();
        table = new JTable(tableModel);

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

        String idUmat = record == null? null : record.uuid;
        DtoInputLaporanStatusDanaRutin rq = new DtoInputLaporanStatusDanaRutin();
        rq.idUmat = idUmat;

        List<DtoOutputLaporanStatusDanaRutin> result = HttpHelper.makeHttpRequest(App.config.url("/laporan/status_dana_rutin"), HttpGetWithBody::new, rq, List.class, DtoOutputLaporanStatusDanaRutin.class);
        tableModel.setData(result);
    }

    public static class TableModel extends AbstractTableModel {
        private static String[] columnLabels = {"Nama umat", "No. telpon", "Alamat", "Jenis dana", "Nama leluhur (ut Samanagara)", "Status bayar", "Berapa bulan", "Kurang bayar (Rp)"};
        private static Class[] columnClasses = {String.class, String.class, String.class, String.class, String.class, String.class, Long.class, Long.class};

        private List<DtoOutputLaporanStatusDanaRutin> data = new ArrayList<>();

        public void setData(List<DtoOutputLaporanStatusDanaRutin> data) {
            this.data = data;
            this.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnLabels.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnLabels[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClasses[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DtoOutputLaporanStatusDanaRutin row = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return row.namaUmat;
                case 1:
                    return row.noTelpon;
                case 2:
                    return row.alamat;
                case 3:
                    return row.jenisDana;
                case 4:
                    return row.namaLeluhur;
                case 5:
                    return row.strStatusBayar;
                case 6:
                    return row.diffInMonths;
                case 7:
                    return row.nominal;
                default:
                    throw new IllegalArgumentException("columnIndex: " + columnIndex);
            }
        }
    }
}
