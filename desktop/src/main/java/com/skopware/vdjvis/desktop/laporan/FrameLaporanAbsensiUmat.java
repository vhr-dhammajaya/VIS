package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.jasperreports.JRListOfPublicsFieldObjectDataSource;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.vdjvis.api.dto.DtoOutputLaporanAbsensiUmat;
import com.skopware.vdjvis.desktop.App;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FrameLaporanAbsensiUmat extends JInternalFrame {
    private List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaUmat";
                x.label = "Nama umat";
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
    private BaseCrudTableModel<DtoOutputLaporanAbsensiUmat> tableModel;

    public FrameLaporanAbsensiUmat() {
        super("Laporan absensi", true, true, true, true);

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        JButton btnPrintLaporan = new JButton("Print laporan");
        btnPrintLaporan.addActionListener(this::onPrintLaporan);

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanAbsensiUmat.class);

        table = new JTable(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(btnRefresh);
        top.add(btnPrintLaporan);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void onRefresh(ActionEvent event) {
        List<DtoOutputLaporanAbsensiUmat> data = HttpHelper.makeHttpRequest(App.config.url("/laporan/absensi_umat"), HttpGetWithBody::new, null, List.class, DtoOutputLaporanAbsensiUmat.class);
        tableModel.setData(data);
    }

    private void onPrintLaporan(ActionEvent event) {
        List<DtoOutputLaporanAbsensiUmat> data = HttpHelper.makeHttpRequest(App.config.url("/laporan/absensi_umat"), HttpGetWithBody::new, null, List.class, DtoOutputLaporanAbsensiUmat.class);

        try {
            InputStream compiledReportFile = getClass().getResourceAsStream("laporan_absensi_umat.jasper");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(compiledReportFile);
            JRListOfPublicsFieldObjectDataSource ds = new JRListOfPublicsFieldObjectDataSource(data);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), ds);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
            viewer.pack();
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }
}
