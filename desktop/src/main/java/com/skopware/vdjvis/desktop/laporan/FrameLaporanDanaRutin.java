package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JForeignKeyPicker;
import com.skopware.vdjvis.api.dto.DtoInputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.dto.DtoOutputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.master.GridUmat;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static class TableModel extends AbstractTableModel {
        private static List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "namaUmat";
                    x.label = "Nama umat";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "noTelpon";
                    x.label = "No. telpon";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "alamat";
                    x.label = "Alamat";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "jenisDana";
                    x.label = "Jenis dana";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "namaLeluhur";
                    x.label = "Nama leluhur (ut Samanagara)";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "strStatusBayar";
                    x.label = "Status bayar";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "diffInMonths";
                    x.label = "Berapa bulan";
                    x.dataType = Long.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nominal";
                    x.label = "Kurang bayar (Rp)";
                    x.dataType = Long.class;
                })
        );

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
            return columnConfigs.size();
        }

        @Override
        public String getColumnName(int column) {
            return columnConfigs.get(column).label;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnConfigs.get(columnIndex).dataType;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DtoOutputLaporanStatusDanaRutin row = data.get(rowIndex);
            String fieldName = columnConfigs.get(columnIndex).fieldName;
            return ObjectHelper.getFieldValue(row, fieldName);
        }
    }
}
