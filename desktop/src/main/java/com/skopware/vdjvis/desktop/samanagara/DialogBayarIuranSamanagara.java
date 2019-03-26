package com.skopware.vdjvis.desktop.samanagara;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.javautils.swing.jtable.celleditor.JSpinnerCellEditor;
import com.skopware.vdjvis.api.dto.DtoPembayaranSamanagara;
import com.skopware.vdjvis.api.dto.DtoStatusBayarLeluhur;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.TarifSamanagara;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        listStatusLeluhur = HttpHelper.makeHttpRequest(App.config.url("/leluhur/status_bayar"), HttpGetWithBody::new, umat, List.class, DtoStatusBayarLeluhur.class);

        PageData<TarifSamanagara> tarifSamanagaraPageData = HttpHelper.makeHttpRequest(App.config.url("/tarif_samanagara"), HttpGetWithBody::new, ObjectHelper.apply(new GridConfig(), x -> {
            x.enableFilter = false;
            x.enablePaging = false;
        }), PageData.class, TarifSamanagara.class);
        listTarifSamanagara = tarifSamanagaraPageData.rows.stream()
                .map(tarifSamanagara -> new Tuple3<>(tarifSamanagara.startDate, tarifSamanagara.endDate, tarifSamanagara.nominal))
                .collect(Collectors.toList());

        leluhurTableModel = new BaseCrudTableModel<>();
        List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "leluhurNama";
                    x.label = "Nama leluhur";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "strStatusBayar";
                    x.label = "Status bayar";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "countBulan";
                    x.label = "Berapa bulan";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nominal";
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
                        YearMonth currYm = leluhur.lastPaymentMonth.plusMonths(cntBulan);
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

            DtoPembayaranSamanagara dto = new DtoPembayaranSamanagara();
            dto.umatId = umat.uuid;
            dto.listLeluhur = listStatusLeluhur;
            dto.tglTrans = edTglTrans.getDate();
            dto.channel = (String) edChannel.getSelectedItem();
            dto.keterangan = edKeterangan.getText();

            HttpHelper.makeHttpRequest(App.config.url("/leluhur/bayar_iuran_samanagara"), HttpPost::new, dto, boolean.class);
            this.dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> this.dispose());

        JPanel contentPane = SwingHelper.createOkCancelPanel(scrollPane, btnOk, btnCancel);
        setContentPane(contentPane);
    }
}
