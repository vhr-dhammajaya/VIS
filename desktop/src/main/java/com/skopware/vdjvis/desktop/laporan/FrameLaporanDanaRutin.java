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
    private List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaUmat";
                x.label = "Nama umat";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "noTelpon";
                x.label = "No. telpon";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "alamat";
                x.label = "Alamat";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "jenisDana";
                x.label = "Jenis dana";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaLeluhur";
                x.label = "Nama leluhur (ut Samanagara)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "statusBayar.strStatus";
                x.label = "Status bayar";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "statusBayar.countBulan";
                x.label = "Berapa bulan";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "statusBayar.nominal";
                x.label = "Kurang bayar (Rp)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "statusBayar.lastPaidMonth";
                x.label = "Pembayaran terakhir di bulan";
            })
    );

    private JForeignKeyPicker<Umat> edUmat;
    private JTable table;
    private BaseCrudTableModel<DtoOutputLaporanStatusDanaRutin> tableModel;

    public FrameLaporanDanaRutin() {
        super("Laporan status iuran samanagara, dana sosial & tetap", true, true, true, true);

        edUmat = new JForeignKeyPicker<>(App.mainFrame, GridUmat.createNoAddEditDelete());

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlFilter.add(new JLabel("Pilih umat"));
        pnlFilter.add(edUmat);
        pnlFilter.add(btnRefresh);

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanStatusDanaRutin.class);

        table = new JTable(tableModel);

        JScrollPane pnlTable = new JScrollPane(table);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(pnlFilter, BorderLayout.NORTH);
        contentPane.add(pnlTable, BorderLayout.CENTER);

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
}
