package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.poi.excel.ExcelHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.dto.laporan.DtoOutputLaporanAbsensiSiswa;
import com.skopware.vdjvis.desktop.App;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
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
        btnRefresh.addActionListener(e -> {
            tableModel.setData(computeReportData());
        });

        JButton btnExcel = new JButton("Download laporan (Excel)");
        btnExcel.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                List<DtoOutputLaporanAbsensiSiswa> data = computeReportData();

                ExcelHelper.saveListToXlsx(data, jfc.getSelectedFile(),
                        (data2, workbook) -> {
                            Sheet sheet1 = workbook.createSheet();
                            Row headerRow = sheet1.createRow(0);
                            CellStyle headerStyle = workbook.createCellStyle();
                            XSSFFont headerFont = workbook.createFont();
                            headerFont.setBold(true);
                            headerStyle.setFont(headerFont);

                            Cell headerCell = headerRow.createCell(3);
                            headerCell.setCellValue("Sdh berapa lama tidak hadir");
                            headerCell.setCellStyle(headerStyle);

                            headerRow = sheet1.createRow(1);
                            headerCell = headerRow.createCell(0);
                            headerCell.setCellValue("Nama siswa");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(1);
                            headerCell.setCellValue("Nama ayah");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(2);
                            headerCell.setCellValue("Nama ibu");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(3);
                            headerCell.setCellValue("Tahun");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(4);
                            headerCell.setCellValue("Bulan");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(5);
                            headerCell.setCellValue("Hari");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(6);
                            headerCell.setCellValue("Tgl terakhir hadir");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(7);
                            headerCell.setCellValue("No. telpon");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(8);
                            headerCell.setCellValue("Alamat");
                            headerCell.setCellStyle(headerStyle);

                            CellStyle dateCellStyle = workbook.createCellStyle();
                            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));

                            for (int i=1; i <= data2.size(); i++) {
                                DtoOutputLaporanAbsensiSiswa record = data2.get(i-1);
                                Row row = sheet1.createRow(i+1);
                                Cell cell = row.createCell(0);
                                cell.setCellValue(record.namaSiswa);
                                cell = row.createCell(1);
                                cell.setCellValue(record.namaAyah);
                                cell = row.createCell(2);
                                cell.setCellValue(record.namaIbu);

                                if (record.sdhBerapaLamaAbsen != null) {
                                    cell = row.createCell(3);
                                    cell.setCellValue(record.sdhBerapaLamaAbsen.getYears());
                                    cell = row.createCell(4);
                                    cell.setCellValue(record.sdhBerapaLamaAbsen.getMonths());
                                    cell = row.createCell(5);
                                    cell.setCellValue(record.sdhBerapaLamaAbsen.getDays());
                                }

                                cell = row.createCell(6);
                                cell.setCellValue(DateTimeHelper.toCalendar(record.tglTerakhirHadir));
                                cell.setCellStyle(dateCellStyle);

                                cell = row.createCell(7);
                                cell.setCellValue(record.noTelpon);
                                cell = row.createCell(8);
                                cell.setCellValue(record.alamat);
                            }
                        },
                        ex -> SwingHelper.showErrorMessage(this, "Error: gagal menyimpan laporan"));
            }
        });

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanAbsensiSiswa.class);

        table = new JTable(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(btnRefresh);
        top.add(btnExcel);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private List<DtoOutputLaporanAbsensiSiswa> computeReportData() {
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

        return data;
    }
}
