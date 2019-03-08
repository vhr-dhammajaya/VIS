package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.vdjvis.api.dto.DtoOutputLaporanAbsensi;
import com.skopware.vdjvis.desktop.App;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameLaporanAbsensiUmat extends JInternalFrame {
    private JTable table;
    private TableModel tableModel;

    public FrameLaporanAbsensiUmat() {
        super("Laporan absensi", true, true, true, true);

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        tableModel = new TableModel();
        table = new JTable(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(btnRefresh);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void onRefresh(ActionEvent event) {
        List<DtoOutputLaporanAbsensi> result = HttpHelper.makeHttpRequest(App.config.url("/laporan/absensi"), HttpGetWithBody::new, null, List.class, DtoOutputLaporanAbsensi.class);
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
                    x.fieldName = "sdhBerapaLamaAbsen.years";
                    x.label = "Sdh berapa lama tidak hadir (tahun)";
                    x.dataType = Integer.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "sdhBerapaLamaAbsen.months";
                    x.label = "(bulan)";
                    x.dataType = Integer.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "sdhBerapaLamaAbsen.days";
                    x.label = "(hari)";
                    x.dataType = Integer.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglTerakhirHadir";
                    x.label = "Tgl terakhir hadir";
                    x.dataType = LocalDate.class;
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
                })
        );

        private List<DtoOutputLaporanAbsensi> data = new ArrayList<>();

        public void setData(List<DtoOutputLaporanAbsensi> data) {
            this.data = data;
            fireTableDataChanged();
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
            DtoOutputLaporanAbsensi row = data.get(rowIndex);
            String fieldName = columnConfigs.get(columnIndex).fieldName;
            return ObjectHelper.getFieldValue(row, fieldName);
        }
    }
}
