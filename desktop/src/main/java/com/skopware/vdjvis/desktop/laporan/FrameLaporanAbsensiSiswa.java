package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.vdjvis.api.dto.DtoOutputLaporanAbsensiSiswa;
import com.skopware.vdjvis.desktop.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class FrameLaporanAbsensiSiswa extends JInternalFrame {
    private List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaSiswa";
                x.label = "Nama siswa";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaAyah";
                x.label = "Nama ayah";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaIbu";
                x.label = "Nama ibu";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "sdhBerapaLamaAbsen.years";
                x.label = "Sdh berapa lama tidak hadir (tahun)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "sdhBerapaLamaAbsen.months";
                x.label = "(bulan)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "sdhBerapaLamaAbsen.days";
                x.label = "(hari)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "tglTerakhirHadir";
                x.label = "Tgl terakhir hadir";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "noTelpon";
                x.label = "No. telpon";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "alamat";
                x.label = "Alamat";
            })
    );

    private JTable table;
    private BaseCrudTableModel<DtoOutputLaporanAbsensiSiswa> tableModel;

    public FrameLaporanAbsensiSiswa() {
        super("Laporan absensi siswa", true, true, true, true);

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanAbsensiSiswa.class);

        table = new JTable(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(btnRefresh);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void onRefresh(ActionEvent event) {
        List<DtoOutputLaporanAbsensiSiswa> data = HttpHelper.makeHttpRequest(App.config.url("/laporan/absensi_siswa"), HttpGetWithBody::new, null, List.class, DtoOutputLaporanAbsensiSiswa.class);
        tableModel.setData(data);
    }
}
