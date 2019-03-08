package com.skopware.vdjvis.desktop.samanagara;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.vdjvis.api.dto.DtoPembayaranSamanagara;
import com.skopware.vdjvis.api.dto.DtoStatusBayarLeluhur;
import com.skopware.vdjvis.api.entities.TarifSamanagara;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DialogBayarIuranSamanagara extends JDialog {
    private Umat umat;
    private List<Tuple3<LocalDate, LocalDate, Integer>> listTarifSamanagara;
    private List<DtoStatusBayarLeluhur> listStatusLeluhur;
    private JTable tblLeluhur;
    private LeluhurTableModel leluhurTableModel;
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

        leluhurTableModel = new LeluhurTableModel();
        leluhurTableModel.addTableModelListener(e -> {
            // recompute edTotalBayarRp
            int totalBayarRp = 0;

            for (DtoStatusBayarLeluhur leluhur : listStatusLeluhur) {
                for (int i = 1; i <= leluhur.mauBayarBrpBulan; i++) {
                    YearMonth currYm = leluhur.lastPaymentMonth.plusMonths(i);
                    LocalDate currDate = leluhur.leluhurTglDaftar.withYear(currYm.getYear()).withMonth(currYm.getMonthValue());

                    int nominalBulanIni = DateTimeHelper.findValueInDateRange(currDate, listTarifSamanagara).get();
                    totalBayarRp += nominalBulanIni;
                }
            }

            edTotalBayarRp.setValue(totalBayarRp);
        });
        tblLeluhur = new JTable(leluhurTableModel);

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

    private static String[] columnLabels = {"Nama leluhur", "Status bayar", "Berapa bulan", "Rp", "Mau bayar brp bulan?"};
    private static Class[] columnClasses = {String.class, String.class, Integer.class, Integer.class, Integer.class}; // can't use int.class if want to be editable
    private static boolean[] columnEditableFlags = {false, false, false, false, true};
    private static Map<Integer, String> columnKeyByIndex = ObjectHelper.apply(new HashMap<>(), x -> {
        x.put(0, "leluhur_nama");
        x.put(1, "strStatusBayar");
        x.put(2, "diffInMonths");
        x.put(3, "totalRp");
        x.put(4, "mauBayarBrpBulan?");
    });

    private class LeluhurTableModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            return listStatusLeluhur.size();
        }

        @Override
        public int getColumnCount() {
            // nama leluhur, strStatusBayar, berapa bulan, rp, mau bayar brp bulan
            return columnLabels.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnLabels[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClasses[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnEditableFlags[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DtoStatusBayarLeluhur row = listStatusLeluhur.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return row.leluhurNama;
                case 1:
                    return row.strStatusBayar;
                case 2:
                    return row.diffInMonths;
                case 3:
                    return row.nominal;
                case 4:
                    return row.mauBayarBrpBulan;
                default:
                    throw new IllegalArgumentException("columnIndex: " + columnIndex);
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            DtoStatusBayarLeluhur row = listStatusLeluhur.get(rowIndex);

            if (columnIndex == 4) {
                row.mauBayarBrpBulan = (int) aValue;
            }

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
