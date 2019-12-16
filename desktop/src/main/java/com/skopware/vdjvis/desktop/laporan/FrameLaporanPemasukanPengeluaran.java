package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.dto.laporan.DtoOutputLaporanPemasukanPengeluaran;
import com.skopware.vdjvis.desktop.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameLaporanPemasukanPengeluaran extends JInternalFrame {
    private List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "tahun";
                x.label = "Tahun";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "bulan";
                x.label = "Bulan";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "pemasukan";
                x.label = "Pemasukan";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "pengeluaran";
                x.label = "Pengeluaran";
            })
    );

    private JDatePicker edTglStart;
    private JDatePicker edTglEnd;
    private JTable table;
    private BaseCrudTableModel<DtoOutputLaporanPemasukanPengeluaran> tableModel;

    public FrameLaporanPemasukanPengeluaran() {
        super("Laporan pemasukan & pengeluaran", true, true, true, true);

        edTglStart = new JDatePicker();
        edTglStart.setDate(LocalDate.now());

        edTglEnd = new JDatePicker();
        edTglEnd.setDate(LocalDate.now());

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Bulan awal"));
        top.add(edTglStart);
        top.add(new JLabel("Bulan akhir"));
        top.add(edTglEnd);
        top.add(btnRefresh);

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanPemasukanPengeluaran.class);

        table = new JTable(tableModel);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void onRefresh(ActionEvent event) {
        LocalDate startDate = edTglStart.getDate();
        LocalDate endDate = edTglEnd.getDate();

        if (!SwingHelper.validateFormFields(App.mainFrame,
                new Tuple2<>(startDate == null, "Tanggal awal tidak boleh kosong"),
                new Tuple2<>(endDate == null, "Tanggal akhir tidak boleh kosong")
        )) {
            return;
        }

        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        if (startMonth.isAfter(endMonth)) {
            SwingHelper.showErrorMessage(App.mainFrame, "Tanggal awal harus lebih kecil dari tanggal akhir");
            return;
        }

        List<DtoOutputLaporanPemasukanPengeluaran> result = App.jdbi.withHandle(handle -> {
            List<DtoOutputLaporanPemasukanPengeluaran> result2 = new ArrayList<>();

            YearMonth curr = startMonth;
            while (!curr.isAfter(endMonth)) {
                int ymCurr = DateTimeHelper.computeMySQLYearMonth(curr);
                int jmlPengeluaran = handle.select("select sum(nominal) from pengeluaran where active=1 and extract(year_month from tgl_trx) = ?", ymCurr)
                        .mapTo(int.class)
                        .findOnly();
                int jmlPendapatanNonRutin = handle.select("select sum(nominal) from pendapatan where active=1 and extract(year_month from tgl_trx) = ?", ymCurr)
                        .mapTo(int.class)
                        .findOnly();
                int jmlPendapatanRutin = handle.select("select sum(total_nominal) from pembayaran_samanagara_sosial_tetap where active=1 and extract(year_month from tgl) = ?", ymCurr)
                        .mapTo(int.class)
                        .findOnly();
                int jmlPendapatan = jmlPendapatanNonRutin + jmlPendapatanRutin;

                DtoOutputLaporanPemasukanPengeluaran x = new DtoOutputLaporanPemasukanPengeluaran();
                x.tahun = curr.getYear();
                x.bulan = curr.getMonthValue();
                x.pemasukan = jmlPendapatan;
                x.pengeluaran = jmlPengeluaran;
                result2.add(x);

                curr = curr.plusMonths(1);
            }

            return result2;
        });
        tableModel.setData(result);
    }
}
