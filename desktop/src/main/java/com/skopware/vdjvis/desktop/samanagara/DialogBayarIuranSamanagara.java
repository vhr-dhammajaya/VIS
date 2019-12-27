package com.skopware.vdjvis.desktop.samanagara;

import com.skopware.javautils.*;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.jtable.celleditor.JSpinnerCellEditor;
import com.skopware.vdjvis.api.dto.DtoStatusBayarLeluhur;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.keuangan.DialogPrepareTandaTerima;
import org.jdbi.v3.core.Handle;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class DialogBayarIuranSamanagara extends JDialog {
    private static final int IDX_COL_MAU_BAYAR_BRP_BULAN = 4;
    private static final int IDX_COL_NOMINAL_YG_MAU_DIBAYARKAN = 5;
    private Umat umat;
    private List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara;
    private List<DtoStatusBayarLeluhur> listStatusLeluhur;
    private JTable tblLeluhur;
    //    private LeluhurTableModel leluhurTableModel;
    private BaseCrudTableModel<DtoStatusBayarLeluhur> leluhurTableModel;
    private JFormattedTextField edTotalBayarRp;
    private JDatePicker edTglTrans;
    private JComboBox<String> edChannel;
    private JTextArea edKeterangan;

    public DialogBayarIuranSamanagara(Umat umat) {
        super(App.mainFrame, "Bayar iuran samanagara", false);
        this.umat = umat;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        edTglTrans = new JDatePicker();
        edTglTrans.setDate(LocalDate.now());

        edChannel = new JComboBox<>(new String[]{"Tunai", "EDC", "Transfer ke rek. BCA"});
        edKeterangan = new JTextArea(10, 20);

        try (Handle handle = App.jdbi.open()) {
            listTarifSamanagara = handle.select("select start_date, end_date, nominal from hist_biaya_smngr")
                    .map((rs, ctx) -> {
                        Tuple3<LocalDate, LocalDate, Integer> x = new Tuple3<>();
                        x.val1 = DateTimeHelper.toLocalDate(rs.getDate("start_date"));
                        x.val2 = DateTimeHelper.toLocalDate(rs.getDate("end_date"));
                        x.val3 = rs.getInt("nominal");
                        return x;
                    })
                    .list();

            YearMonth todayMonth = YearMonth.now();

            listStatusLeluhur = handle.select("select uuid, nama, tgl_daftar from leluhur_smngr where active=1 and umat_id=?", umat.uuid)
                    .map((rs, ctx) -> {
                        Leluhur x = new Leluhur();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        x.tglDaftar = DateTimeHelper.toLocalDate(rs.getDate("tgl_daftar"));
                        return x;
                    })
                    .stream()
                    .map(leluhur -> {
                        StatusBayar statusBayar = Leluhur.computeStatusBayar(handle, umat.uuid, leluhur.uuid, leluhur.tglDaftar, todayMonth, listTarifSamanagara);

                        DtoStatusBayarLeluhur dto = new DtoStatusBayarLeluhur();
                        dto.leluhurId = leluhur.uuid;
                        dto.leluhurNama = leluhur.nama;
                        dto.leluhurTglDaftar = leluhur.tglDaftar;
                        dto.statusBayar = statusBayar;
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        leluhurTableModel = new BaseCrudTableModel<>();
        List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "leluhurNama";
                    x.label = "Nama leluhur";
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
                    x.label = "Rp";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "mauBayarBrpBulan";
                    x.label = "Mau bayar brp bulan?";
                    x.editable = true;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nominalYgMauDibayarkan";
                    x.label = "Nominal yg mau dibayarkan";
                })
        );
        leluhurTableModel.setColumnConfigs(columnConfigs);
        leluhurTableModel.setRecordType(DtoStatusBayarLeluhur.class);
        leluhurTableModel.setData(listStatusLeluhur);

        leluhurTableModel.addTableModelListener(e -> {
            /*
            this if is neccessary
            if not, will stack overflow.
            Because fireTableCellUpdated(..., IDX_COL_NOMINAL_YG_MAU_DIBAYARKAN); (called below) will trigger this lambda (recursive)
             */
            if (e.getColumn() == IDX_COL_MAU_BAYAR_BRP_BULAN) {
                // recompute edTotalBayarRp
                int totalBayarRp = 0;

                for (int cntLeluhur = 0; cntLeluhur < listStatusLeluhur.size(); cntLeluhur++) {
                    DtoStatusBayarLeluhur leluhur = listStatusLeluhur.get(cntLeluhur);
                    int totalBayarUtLeluhurX = 0;

                    for (int cntBulan = 1; cntBulan <= leluhur.mauBayarBrpBulan; cntBulan++) {
                        YearMonth currYm = leluhur.statusBayar.lastPaidMonth.plusMonths(cntBulan);
                        LocalDate currDate = leluhur.leluhurTglDaftar.withYear(currYm.getYear()).withMonth(currYm.getMonthValue());

                        int nominalBulanIni = DateTimeHelper.findValueInDateRange(currDate, listTarifSamanagara).get();
                        totalBayarUtLeluhurX += nominalBulanIni;
                        totalBayarRp += nominalBulanIni;
                    }

                    leluhur.nominalYgMauDibayarkan = totalBayarUtLeluhurX;
                    leluhurTableModel.fireTableCellUpdated(cntLeluhur, IDX_COL_NOMINAL_YG_MAU_DIBAYARKAN);
                }

                edTotalBayarRp.setValue(totalBayarRp);
            }
        });
        tblLeluhur = new JTable(leluhurTableModel);
        TableColumn colMauBayarBrpBulan = tblLeluhur.getColumnModel().getColumn(IDX_COL_MAU_BAYAR_BRP_BULAN);
        colMauBayarBrpBulan.setCellEditor(new JSpinnerCellEditor(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)));

        edTotalBayarRp = new JFormattedTextField(new DecimalFormat("###,###,##0"));
        edTotalBayarRp.setColumns(9);
        edTotalBayarRp.setEditable(false);

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.PAGE_AXIS));
        pnlContent.add(new JLabel("Daftar leluhur untuk umat ini & status bayarnya"));

//        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlContent.add(tblLeluhur.getTableHeader());
        pnlContent.add(tblLeluhur);

        JPanel pnlFormFields = SwingHelper.buildForm1(Arrays.asList(
                new Tuple2<>("Total Bayar (Rp)", edTotalBayarRp),
                new Tuple2<>("Tgl transaksi", edTglTrans),
                new Tuple2<>("Cara bayar", edChannel),
                new Tuple2<>("Keterangan", edKeterangan)
        ));
        pnlContent.add(pnlFormFields);

        JScrollPane scrollPane = new JScrollPane(pnlContent);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            if (!SwingHelper.validateFormFields(App.mainFrame,
                    new Tuple2<>(edTglTrans.getDate() == null, "Tgl transaksi harus diisi"))) {
                return;
            }

            PembayaranDanaRutin pembayaran = new PembayaranDanaRutin();
            Holder<String> keperluanDana = new Holder<>();

            try (Handle handle = App.jdbi.open()) {
                handle.useTransaction(handle1 -> {
                    pembayaran.uuid = UUID.randomUUID().toString();

                    pembayaran.umat = umat;

                    pembayaran.tipe = PembayaranDanaRutin.Type.samanagara;
                    pembayaran.tgl = edTglTrans.getDate();
                    pembayaran.totalNominal = listStatusLeluhur.stream()
                            .mapToInt(x -> x.nominalYgMauDibayarkan)
                            .reduce(0, (left, right) -> left + right);
                    pembayaran.channel = (String) edChannel.getSelectedItem();
                    pembayaran.keterangan = edKeterangan.getText();

                    handle1.createUpdate("insert into pembayaran_samanagara_sosial_tetap(uuid, umat_id, tipe, tgl, total_nominal, channel, keterangan)" +
                            " values(:uuid, :umatId, :tipe, :tgl, :totalNominal, :channel, :keterangan)")
                            .bind("uuid", pembayaran.uuid)
                            .bind("umatId", pembayaran.umat.uuid)
                            .bind("tipe", pembayaran.tipe.name())
                            .bind("tgl", pembayaran.tgl)
                            .bind("totalNominal", pembayaran.totalNominal)
                            .bind("channel", pembayaran.channel)
                            .bind("keterangan", pembayaran.keterangan)
                            .execute();
                    pembayaran.noSeq = handle1.select("select no_seq from pembayaran_samanagara_sosial_tetap where uuid=?", pembayaran.uuid)
                            .mapTo(int.class)
                            .findOnly();

                    for (DtoStatusBayarLeluhur leluhur : listStatusLeluhur) {
                        DetilPembayaranDanaRutin templateDetil = new DetilPembayaranDanaRutin();

                        templateDetil.parentTrx = pembayaran;
                        templateDetil.jenis = DetilPembayaranDanaRutin.Type.samanagara;
                        templateDetil.leluhurSamanagara = new Leluhur();
                        templateDetil.leluhurSamanagara.uuid = leluhur.leluhurId;

                        YearMonth lastPaidMonth = Leluhur.fetchLastPaidMonth(handle1, pembayaran.umat.uuid, templateDetil.leluhurSamanagara.uuid, leluhur.leluhurTglDaftar);
                        YearMonth currentMonth = lastPaidMonth.plusMonths(1);

                        for (int i = 0; i < leluhur.mauBayarBrpBulan; i++) {
                            LocalDate currDate = leluhur.leluhurTglDaftar.withYear(currentMonth.getYear()).withMonth(currentMonth.getMonthValue());
                            int nominal = DateTimeHelper.findValueInDateRange(currDate, listTarifSamanagara).get();

                            DetilPembayaranDanaRutin detil = templateDetil.clone();
                            detil.uuid = UUID.randomUUID().toString();
                            detil.untukBulan = currentMonth;
                            detil.nominal = nominal;

                            handle1.createUpdate("insert into detil_pembayaran_dana_rutin(uuid, trx_id, jenis, leluhur_id, ut_thn_bln, nominal)" +
                                    " values(:uuid, :trx_id, :jenis, :leluhur_id, :ut_thn_bln, :nominal)")
                                    .bind("uuid", detil.uuid)
                                    .bind("trx_id", detil.parentTrx.uuid)
                                    .bind("jenis", detil.jenis.name())
                                    .bind("leluhur_id", detil.leluhurSamanagara.uuid)
                                    .bind("ut_thn_bln", LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), 1))
                                    .bind("nominal", detil.nominal)
                                    .execute();

                            pembayaran.mapDetilPembayaran.put(detil.uuid, detil);
                            currentMonth = currentMonth.plusMonths(1);
                        }
                    }

                    keperluanDana.value = pembayaran.computeKeperluanDana(handle1);
                });
            }

            DialogPrepareTandaTerima.Input jasperParams = new DialogPrepareTandaTerima.Input();
            jasperParams.set(pembayaran, keperluanDana.value);

            DialogPrepareTandaTerima dialog = new DialogPrepareTandaTerima(App.mainFrame, jasperParams);
            dialog.setVisible(true);
            dialog.pack();

            this.dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(scrollPane, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
