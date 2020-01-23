package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.poi.excel.ExcelHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.dto.laporan.DtoOutputLaporanPemasukanPengeluaranHarian;
import com.skopware.vdjvis.desktop.App;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameLaporanPemasukanPengeluaranHarian extends JInternalFrame {
    private List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "nominalMasuk";
                x.label = "Masuk (Rp)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "nominalKeluar";
                x.label = "Keluar (Rp)";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "keterangan";
                x.label = "Keterangan";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaAcara";
                x.label = "Untuk acara";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaUser";
                x.label = "User yg input";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "jenisDanaMasuk";
                x.label = "Jenis dana masuk";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "channel";
                x.label = "Cara dana";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "namaDonatur";
                x.label = "Nama umat yg berdana";
            }),
            ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                x.fieldName = "dibayarkanKepada";
                x.label = "Dibayarkan kepada";
            })
    );

    private JDatePicker edTgl;
    private JTable table;
    private BaseCrudTableModel<DtoOutputLaporanPemasukanPengeluaranHarian> tableModel;

    public FrameLaporanPemasukanPengeluaranHarian() {
        super("Laporan pemasukan & pengeluaran harian", true, true, true, true);

        edTgl = new JDatePicker();
        edTgl.setDate(LocalDate.now());

        JButton btnRefresh = new JButton("Lihat laporan");
        btnRefresh.addActionListener(e -> {
            List<DtoOutputLaporanPemasukanPengeluaranHarian> data = fetchReportData(edTgl.getDate());
            tableModel.setData(data);
        });

        JButton btnExcel = new JButton("Download laporan (Excel)");
        btnExcel.addActionListener(e -> {
            LocalDate date = edTgl.getDate();
            List<DtoOutputLaporanPemasukanPengeluaranHarian> data = fetchReportData(date);

            JFileChooser jfc = new JFileChooser();
            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                ExcelHelper.saveListToXlsx(data, jfc.getSelectedFile(),
                        (data2, workbook) -> {
                            Sheet sheet1 = workbook.createSheet();

                            CellStyle headerStyle = workbook.createCellStyle();
                            XSSFFont headerFont = workbook.createFont();
                            headerFont.setBold(true);
                            headerStyle.setFont(headerFont);

                            CellStyle dateCellStyle = workbook.createCellStyle();
                            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));

                            Row headerRow = sheet1.createRow(0);
                            Cell headerCell = headerRow.createCell(0);
                            headerCell.setCellStyle(headerStyle);
                            headerCell.setCellValue("Tanggal");

                            headerCell = headerRow.createCell(1);
                            headerCell.setCellStyle(dateCellStyle);
                            headerCell.setCellValue(DateTimeHelper.toCalendar(date));


                            headerRow = sheet1.createRow(1);
                            headerCell = headerRow.createCell(5);
                            headerCell.setCellValue("Dana masuk");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(8);
                            headerCell.setCellValue("Dana keluar");
                            headerCell.setCellStyle(headerStyle);

                            int colNum = 0;
                            headerRow = sheet1.createRow(2);
                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Masuk (Rp)");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Keluar (Rp)");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Keterangan");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Untuk acara");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("User yg input");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Jenis dana masuk");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Cara dana");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Nama umat yg berdana");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(colNum++);
                            headerCell.setCellValue("Dibayarkan kepada");
                            headerCell.setCellStyle(headerStyle);

                            for (int i = 1; i <= data2.size(); i++) {
                                DtoOutputLaporanPemasukanPengeluaranHarian record = data2.get(i-1);
                                Row row = sheet1.createRow(i+2);

                                colNum = 0;
                                Cell cell = row.createCell(colNum++);
                                cell.setCellValue(record.nominalMasuk);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.nominalKeluar);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.keterangan);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.namaAcara);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.namaUser);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.jenisDanaMasuk);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.channel);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.namaDonatur);

                                cell = row.createCell(colNum++);
                                cell.setCellValue(record.dibayarkanKepada);
                            }
                        },
                        ex -> SwingHelper.showErrorMessage(this, "Error: gagal menyimpan laporan"));
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
        top.add(new JLabel("Tanggal"));
        top.add(edTgl);
        top.add(btnRefresh);
        top.add(btnExcel);

        tableModel = new BaseCrudTableModel<>();
        tableModel.setColumnConfigs(columnConfigs);
        tableModel.setRecordType(DtoOutputLaporanPemasukanPengeluaranHarian.class);

        table = new JTable(tableModel);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(contentPane);
    }

    private List<DtoOutputLaporanPemasukanPengeluaranHarian> fetchReportData(LocalDate date) {
        List<DtoOutputLaporanPemasukanPengeluaranHarian> result = App.jdbi.withHandle(handle -> {
            List<DtoOutputLaporanPemasukanPengeluaranHarian> result2 = new ArrayList<>();

            handle.select("select p.tipe, p.channel, u.nama as nama_umat, p.total_nominal, p.keterangan, usr.nama as nama_user" +
                    " from pembayaran_samanagara_sosial_tetap p" +
                    " join umat u on u.uuid=p.umat_id" +
                    " join `user` usr on usr.id=p.user_id" +
                    " where p.active=1 and p.tgl=?", date)
                    .map((rs, ctx) -> {
                        DtoOutputLaporanPemasukanPengeluaranHarian x = new DtoOutputLaporanPemasukanPengeluaranHarian();
                        x.nominalMasuk = rs.getInt("total_nominal");
                        x.keterangan = rs.getString("keterangan");
                        x.namaAcara = null;
                        x.namaUser = rs.getString("nama_user");
                        x.jenisDanaMasuk = rs.getString("tipe");
                        x.channel = rs.getString("channel");
                        x.namaDonatur = rs.getString("nama_umat");
                        return x;
                    })
                    .stream()
                    .forEach(result2::add);

            handle.select("select p.jenis_dana, p.channel, u.nama as nama_umat, p.nominal, p.keterangan, a.nama as nama_acara, usr.nama as nama_user" +
                    " from pendapatan p" +
                    " left join umat u on u.uuid=p.umat_id" +
                    " left join acara a on a.id=p.acara_id" +
                    " join `user` usr on usr.id=p.user_id" +
                    " where p.active=1 and p.tgl_trx=?", date)
                    .map((rs, ctx) -> {
                        DtoOutputLaporanPemasukanPengeluaranHarian x = new DtoOutputLaporanPemasukanPengeluaranHarian();
                        x.nominalMasuk = rs.getInt("nominal");
                        x.keterangan = rs.getString("keterangan");
                        x.namaAcara = rs.getString("nama_acara");
                        x.namaUser = rs.getString("nama_user");
                        x.jenisDanaMasuk = rs.getString("jenis_dana");
                        x.channel = rs.getString("channel");
                        x.namaDonatur = rs.getString("nama_umat");
                        return x;
                    })
                    .stream()
                    .forEach(result2::add);

            handle.select("select p.nominal, p.keterangan, a.nama as nama_acara, p.penerima, usr.nama as nama_user" +
                    " from pengeluaran p" +
                    " left join acara a on a.id=p.acara_id" +
                    " join `user` usr on usr.id=p.user_id" +
                    " where p.active=1 and p.tgl_trx=?", date)
                    .map((rs, ctx) -> {
                        DtoOutputLaporanPemasukanPengeluaranHarian x = new DtoOutputLaporanPemasukanPengeluaranHarian();
                        x.nominalKeluar = rs.getInt("nominal");
                        x.keterangan = rs.getString("keterangan");
                        x.namaAcara = rs.getString("nama_acara");
                        x.namaUser = rs.getString("nama_user");
                        x.dibayarkanKepada = rs.getString("penerima");
                        return x;
                    })
                    .stream()
                    .forEach(result2::add);

            return result2;
        });

        return result;
    }
}
