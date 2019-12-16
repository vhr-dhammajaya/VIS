package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.vdjvis.api.dto.laporan.DtoOutputLaporanAbsensiSiswa;
import com.skopware.vdjvis.desktop.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Period;
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
        List<DtoOutputLaporanAbsensiSiswa> data = App.jdbi.withHandle(handle -> {
            LocalDate today = LocalDate.now();
            return handle.select("select s.nama, s.nama_ayah, s.nama_ibu, s.alamat, s.no_telpon, max(k.tgl) as tgl_terakhir_hadir" +
                    " from siswa s" +
                    " left join kehadiran_siswa k on k.siswa_uuid = s.uuid" +
                    " group by s.uuid, s.nama, s.nama_ayah, s.nama_ibu, s.alamat, s.no_telpon" +
                    " order by if(max(k.tgl) is not null, 1, 2), tgl_terakhir_hadir")
                    .map((rs, ctx) -> {
                        DtoOutputLaporanAbsensiSiswa x = new DtoOutputLaporanAbsensiSiswa();
                        x.namaSiswa = rs.getString("nama");
                        x.namaAyah = rs.getString("nama_ayah");
                        x.namaIbu = rs.getString("nama_ibu");
                        x.alamat = rs.getString("alamat");
                        x.noTelpon = rs.getString("no_telpon");
                        x.tglTerakhirHadir = DateTimeHelper.toLocalDate(rs.getDate("tgl_terakhir_hadir"));
                        if (x.tglTerakhirHadir != null) {
                            x.sdhBerapaLamaAbsen = Period.between(x.tglTerakhirHadir, today);
                        } else {
                            x.sdhBerapaLamaAbsen = null;
                        }
                        return x;
                    })
                    .list();
        });
        tableModel.setData(data);
    }
}
