package com.skopware.vdjvis.desktop.laporan;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.poi.excel.ExcelHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JForeignKeyPicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.dto.laporan.DtoOutputLaporanStatusDanaRutin;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.master.GridUmat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdbi.v3.core.statement.Query;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        btnRefresh.addActionListener(e -> {
            List<DtoOutputLaporanStatusDanaRutin> data = fetchReportData();
            tableModel.setData(data);
        });

        JButton btnExcel = new JButton("Download laporan (Excel)");
        btnExcel.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                List<DtoOutputLaporanStatusDanaRutin> data = fetchReportData();

                ExcelHelper.saveListToXlsx(data, jfc.getSelectedFile(),
                        (data2, workbook) -> {
                            Sheet sheet1 = workbook.createSheet();
                            Row headerRow = sheet1.createRow(0);
                            CellStyle headerStyle = workbook.createCellStyle();
                            XSSFFont headerFont = workbook.createFont();
                            headerFont.setBold(true);
                            headerStyle.setFont(headerFont);

                            Cell headerCell = headerRow.createCell(8);
                            headerCell.setCellValue("Pembayaran terakhir pada");
                            headerCell.setCellStyle(headerStyle);

                            headerRow = sheet1.createRow(1);
                            headerCell = headerRow.createCell(0);
                            headerCell.setCellValue("Nama umat");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(1);
                            headerCell.setCellValue("No. telpon");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(2);
                            headerCell.setCellValue("Alamat");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(3);
                            headerCell.setCellValue("Jenis dana");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(4);
                            headerCell.setCellValue("Nama leluhur (ut iuran samanagara)");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(5);
                            headerCell.setCellValue("Status bayar");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(6);
                            headerCell.setCellValue("Berapa bulan");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(7);
                            headerCell.setCellValue("Kurang bayar (Rp)");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(8);
                            headerCell.setCellValue("Tahun");
                            headerCell.setCellStyle(headerStyle);

                            headerCell = headerRow.createCell(9);
                            headerCell.setCellValue("Bulan");
                            headerCell.setCellStyle(headerStyle);

                            for (int i = 1; i <= data2.size(); i++) {
                                DtoOutputLaporanStatusDanaRutin record = data2.get(i - 1);
                                Row row = sheet1.createRow(i+1);
                                Cell cell = row.createCell(0);
                                cell.setCellValue(record.namaUmat);

                                cell = row.createCell(1);
                                cell.setCellValue(record.noTelpon);

                                cell = row.createCell(2);
                                cell.setCellValue(record.alamat);

                                cell = row.createCell(3);
                                cell.setCellValue(record.jenisDana.name());

                                cell = row.createCell(4);
                                cell.setCellValue(record.namaLeluhur);

                                cell = row.createCell(5);
                                cell.setCellValue(record.statusBayar.strStatus);

                                cell = row.createCell(6);
                                cell.setCellValue(record.statusBayar.countBulan);

                                cell = row.createCell(7);
                                cell.setCellValue(record.statusBayar.nominal);

                                cell = row.createCell(8);
                                cell.setCellValue(record.statusBayar.lastPaidMonth.getYear());
                                cell = row.createCell(9);
                                cell.setCellValue(record.statusBayar.lastPaidMonth.getMonthValue());
                            }
                        },
                        (ex) -> {
                            SwingHelper.showErrorMessage(this, "Error: gagal menyimpan laporan");
                        });
            }
        });

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEADING));
        pnlFilter.add(new JLabel("Pilih umat"));
        pnlFilter.add(edUmat);
        pnlFilter.add(btnRefresh);
        pnlFilter.add(btnExcel);

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

    private List<DtoOutputLaporanStatusDanaRutin> fetchReportData() {
        Umat record = edUmat.getRecord();

        String idUmat = record == null ? null : record.uuid;

        List<DtoOutputLaporanStatusDanaRutin> result = App.jdbi.withHandle(handle -> {
            List<DtoOutputLaporanStatusDanaRutin> result2 = new ArrayList<>();

            YearMonth todayMonth = YearMonth.now();

            //#region hitung status dana sosial & tetap
            Query selPendaftaranDanaRutin;
            if (idUmat != null) {
                selPendaftaranDanaRutin = handle.select("select p.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from pendaftaran_dana_rutin p" +
                        " join umat u on u.uuid = p.umat_id" +
                        " where p.active=1 and p.umat_id=?", idUmat);
            } else {
                selPendaftaranDanaRutin = handle.select("select p.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from pendaftaran_dana_rutin p" +
                        " join umat u on u.uuid = p.umat_id" +
                        " where p.active=1");
            }

            List<PendaftaranDanaRutin> listDanaRutin = selPendaftaranDanaRutin
                    .map((rs, ctx) -> {
                        PendaftaranDanaRutin x = new PendaftaranDanaRutin();

                        x.uuid = rs.getString("id");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
                        x.nominal = rs.getInt("nominal");
                        x.tipe = DetilPembayaranDanaRutin.Type.valueOf(rs.getString("tipe"));

                        x.umat = new Umat();
                        x.umat.uuid = rs.getString("umat_id");
                        x.umat.nama = rs.getString("nama_umat");
                        x.umat.noTelpon = rs.getString("no_telpon");
                        x.umat.alamat = rs.getString("alamat");

                        return x;
                    })
                    .list();
            List<StatusBayar> listStatusBayarDanaRutin = PendaftaranDanaRutin.computeStatusBayar(handle, listDanaRutin, todayMonth);

            for (int i = 0; i < listDanaRutin.size(); i++) {
                PendaftaranDanaRutin danaRutin = listDanaRutin.get(i);
                StatusBayar statusBayar = listStatusBayarDanaRutin.get(i);

                result2.add(ObjectHelper.apply(new DtoOutputLaporanStatusDanaRutin(), x -> {
                    x.namaUmat = danaRutin.umat.nama;
                    x.noTelpon = danaRutin.umat.noTelpon;
                    x.alamat = danaRutin.umat.alamat;

                    x.jenisDana = danaRutin.tipe;

                    x.statusBayar = statusBayar;
                }));
            }
            //#endregion

            //#region hitung status iuran samanagara
            Query qSelLeluhur;

            if (idUmat != null) {
                qSelLeluhur = handle.select("select l.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from leluhur_smngr l" +
                        " join umat u on u.uuid=l.umat_id" +
                        " where l.active=1 and l.umat_id=?", idUmat);
            } else {
                qSelLeluhur = handle.select("select l.*, u.nama as nama_umat, u.no_telpon, u.alamat" +
                        " from leluhur_smngr l" +
                        " join umat u on u.uuid=l.umat_id" +
                        " where l.active=1");
            }

            List<Leluhur> listLeluhur = qSelLeluhur
                    .map((rs, ctx) -> {
                        Leluhur x = new Leluhur();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));

                        x.penanggungJawab = new Umat();
                        x.penanggungJawab.uuid = rs.getString("umat_id");
                        x.penanggungJawab.nama = rs.getString("nama_umat");
                        x.penanggungJawab.alamat = rs.getString("alamat");
                        x.penanggungJawab.noTelpon = rs.getString("no_telpon");
                        return x;
                    })
                    .list();

            List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara = Leluhur.fetchListTarifSamanagara(handle);
            List<StatusBayar> listStatusBayarSamanagara = Leluhur.computeStatusBayar(handle, listLeluhur, todayMonth, listTarifSamanagara);

            for (int i = 0; i < listLeluhur.size(); i++) {
                Leluhur leluhur = listLeluhur.get(i);
                StatusBayar statusBayar = listStatusBayarSamanagara.get(i);

                result2.add(ObjectHelper.apply(new DtoOutputLaporanStatusDanaRutin(), x -> {
                    x.namaUmat = leluhur.penanggungJawab.nama;
                    x.noTelpon = leluhur.penanggungJawab.noTelpon;
                    x.alamat = leluhur.penanggungJawab.alamat;
                    x.namaLeluhur = leluhur.nama;

                    x.jenisDana = DetilPembayaranDanaRutin.Type.samanagara;

                    x.statusBayar = statusBayar;
                }));
            }
            //#endregion

            Collections.sort(result2, (a, b) -> {
                // statusBayar can be < -1 or > 1. Need to normalize so that all "Kurang bayar", "Lebih bayar" is -1, 1
                int statusBayarANormalized = a.statusBayar.status < 0 ? -1 : a.statusBayar.status == 0 ? 0 : 1;
                int statusBayarBNormalized = b.statusBayar.status < 0 ? -1 : b.statusBayar.status == 0 ? 0 : 1;

                int cmpStatusBayar = Integer.compare(statusBayarANormalized, statusBayarBNormalized);
                if (cmpStatusBayar == 0) {
                    // compare nominal
                    int cmpNominalDesc = Long.compare(a.statusBayar.nominal, b.statusBayar.nominal);
                    return -cmpNominalDesc;
                } else {
                    return cmpStatusBayar;
                }
            });

            return result2;
        });
        return result;
    }
}
