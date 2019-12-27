package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.jasperreports.JRListOfPublicsFieldObjectDataSource;
import com.skopware.javautils.poi.excel.ExcelHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.dto.laporan.DtoOutputLaporanAbsensiUmat;
import com.skopware.vdjvis.desktop.App;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
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
        super("Laporan absensi umat", true, true, true, true);

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(this::onRefresh);

        JButton btnExcel = new JButton("Download laporan (Excel)");
        btnExcel.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                List<DtoOutputLaporanAbsensiUmat> data = computeReportData();

                ExcelHelper.saveListToXlsx(data, jfc.getSelectedFile(),
                        (data2, workbook) -> {
                            Sheet sheet1 = workbook.createSheet();
                            Row headerRow = sheet1.createRow(0);
                            CellStyle headerStyle = workbook.createCellStyle();
                            XSSFFont headerFont = workbook.createFont();
                            headerFont.setBold(true);
                            headerStyle.setFont(headerFont);

                            Cell headerCell = headerRow.createCell(1);
                            headerCell.setCellValue("Sdh berapa lama tidak hadir");
                            headerCell.setCellStyle(headerStyle);

                            headerRow = sheet1.createRow(1);
                            headerCell = headerRow.createCell(0);
                            headerCell.setCellValue("Nama umat");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(1);
                            headerCell.setCellValue("Tahun");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(2);
                            headerCell.setCellValue("Bulan");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(3);
                            headerCell.setCellValue("Hari");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(4);
                            headerCell.setCellValue("Tgl terakhir hadir");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(5);
                            headerCell.setCellValue("No. telpon");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(6);
                            headerCell.setCellValue("Alamat");
                            headerCell.setCellStyle(headerStyle);

                            CellStyle dateCellStyle = workbook.createCellStyle();
                            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));

                            for (int i=1; i <= data2.size(); i++) {
                                DtoOutputLaporanAbsensiUmat record = data2.get(i-1);
                                Row row = sheet1.createRow(i+1);
                                Cell cell = row.createCell(0);
                                cell.setCellValue(record.namaUmat);

                                if (record.sdhBerapaLamaAbsen != null) {
                                    cell = row.createCell(1);
                                    cell.setCellValue(record.sdhBerapaLamaAbsen.getYears());

                                    cell = row.createCell(2);
                                    cell.setCellValue(record.sdhBerapaLamaAbsen.getMonths());

                                    cell = row.createCell(3);
                                    cell.setCellValue(record.sdhBerapaLamaAbsen.getDays());
                                }

                                cell = row.createCell(4);
                                cell.setCellValue(DateTimeHelper.toCalendar(record.tglTerakhirHadir));
                                cell.setCellStyle(dateCellStyle);

                                cell = row.createCell(5);
                                cell.setCellValue(record.noTelpon);

                                cell = row.createCell(6);
                                cell.setCellValue(record.alamat);
                            }
                        },
                        ex -> SwingHelper.showErrorMessage(this, "Error: gagal menyimpan laporan"));
            }
        });

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanAbsensiUmat.class);

        table = new JTable(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(btnRefresh);
        top.add(btnExcel);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private void onRefresh(ActionEvent event) {
        List<DtoOutputLaporanAbsensiUmat> data = computeReportData();
        tableModel.setData(data);
    }

    private List<DtoOutputLaporanAbsensiUmat> computeReportData() {
        return App.jdbi.withHandle(handle -> {
            LocalDate today = LocalDate.now();

            return handle.select("select u.uuid, u.nama, u.alamat, u.no_telpon, max(k.tgl) as tgl_terakhir_hadir" +
                    " from umat u" +
                    " left join kehadiran k on k.umat_id = u.uuid" +
                    " group by u.uuid, u.nama, u.alamat, u.no_telpon" +
                    " order by if(max(k.tgl) is not null, 1, 2), tgl_terakhir_hadir")
                    .map((rs, ctx) -> {
                        DtoOutputLaporanAbsensiUmat x = new DtoOutputLaporanAbsensiUmat();
                        x.namaUmat = rs.getString("nama");
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
    }

}
