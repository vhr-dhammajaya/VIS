package com.skopware.vdjvis.desktop.absensi;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.jtable.cellrenderer.LocalDateCellRenderer;
import com.skopware.vdjvis.api.entities.Siswa;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FrameAbsensiSiswa extends JInternalFrame {
    public static final String VISITOR_COUNT_FORMAT = "Daftar siswa yg hadir (%d siswa)";
    private JTextField txtInputNama;

    private List<BaseCrudTableModel.ColumnConfig> tblSearchResultColumnConfigs;
    private BaseCrudTableModel<Siswa> tblSearchResultModel;
    private JTable tblSearchResult;

    private JLabel lblVisitorCount;
    private List<BaseCrudTableModel.ColumnConfig> tblUmatHadirColumnConfigs;
    private BaseCrudTableModel<Siswa> tblOrangHadirModel;
    private JTable tblOrangHadir;

    private List<Siswa> listOrang;

    public FrameAbsensiSiswa() {
        super("Absensi siswa", true, true, true, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        listOrang = HttpHelper.makeHttpRequest(App.config.url("/absensi_siswa/list"), HttpGetWithBody::new, null, List.class, Siswa.class);

        //#region init controls
        JLabel lblInputNama = new JLabel("Nama / ID Umat");
        txtInputNama = new JTextField(30);
        txtInputNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String previousText = txtInputNama.getText();
                String currentText;

                if (Character.isLetter(e.getKeyChar())) {
                    currentText = previousText + e.getKeyChar();
                } else {
                    currentText = previousText;
                }

                // begin search & display suggestion
                boolean isBarcode = checkIsBarcode(currentText);
                if (!isBarcode) {
                    searchByName(currentText);
                }
            }
        });

        txtInputNama.addActionListener(e -> {
            String input = txtInputNama.getText();
            boolean isBarcode = checkIsBarcode(input);

            if (isBarcode) {
                txtInputNama.setText("");
                absenBarcode(input);
            }
        });

        tblSearchResultColumnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nama";
                    x.label = "Nama";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "alamat";
                    x.label = "Alamat";
                    x.dataType = String.class;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglLahir";
                    x.label = "Tgl lahir";
                    x.dataType = LocalDate.class;
                })
        );

        tblSearchResultModel = new BaseCrudTableModel<>();
        tblSearchResultModel.setColumnConfigs(tblSearchResultColumnConfigs);
        tblSearchResultModel.setRecordType(Siswa.class);
        tblSearchResultModel.setData(new ArrayList<>());

        tblSearchResult = new JTable(tblSearchResultModel);
        tblSearchResult.setFocusable(false);
        tblSearchResult.setFillsViewportHeight(true);
        tblSearchResult.setDefaultRenderer(LocalDate.class, new LocalDateCellRenderer());

        tblSearchResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSearchResult.setRowSelectionAllowed(true);
        tblSearchResult.setCellSelectionEnabled(false);
        tblSearchResult.setColumnSelectionAllowed(false);

        tblSearchResult.getSelectionModel().addListSelectionListener(e -> {
            int selRowIdx = tblSearchResult.getSelectedRow();
            if (selRowIdx == -1) {
                return;
            }

            Siswa sel = tblSearchResultModel.get(selRowIdx);
            txtInputNama.setText("");
            catatKehadiran(sel);
        });

        tblUmatHadirColumnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nama";
                    x.label = "Nama";
                    x.dataType = String.class;
                })/*,
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nama";
                    x.label = "Hapus";
                    x.dataType = String.class;
                })*/
        );

        tblOrangHadirModel = new BaseCrudTableModel<>();
        tblOrangHadirModel.setColumnConfigs(tblUmatHadirColumnConfigs);
        tblOrangHadirModel.setRecordType(Siswa.class);

        List<Siswa> prevUmatHadirList = HttpHelper.makeHttpRequest(App.config.url("/absensi_siswa/daftar_hadir"), HttpGetWithBody::new, null, List.class, Siswa.class);
        tblOrangHadirModel.setData(prevUmatHadirList);

        lblVisitorCount = new JLabel(String.format(VISITOR_COUNT_FORMAT, tblOrangHadirModel.getRowCount()));

        tblOrangHadir = new JTable(tblOrangHadirModel);
        tblOrangHadir.setFocusable(false);
        tblOrangHadir.setFillsViewportHeight(true);

        tblOrangHadir.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOrangHadir.setRowSelectionAllowed(true);
        tblOrangHadir.setCellSelectionEnabled(false);
        tblOrangHadir.setColumnSelectionAllowed(false);

//        TableColumn btnDeleteColumn = tblUmatHadir.getColumnModel().getColumn(1);
//        JButtonCellRenderer btnDeleteCellRenderer = new JButtonCellRenderer("Hapus");
//        btnDeleteCellRenderer.addActionListener(e -> System.out.println("btnDelete clicked")); // doesn't get called. Must add cell editor
//        btnDeleteColumn.setCellRenderer(btnDeleteCellRenderer);
        //#endregion

        //#region init layout
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        topPanel.add(lblInputNama);
        topPanel.add(txtInputNama);

        JPanel searchResultsPanel = new JPanel(new BorderLayout());
        searchResultsPanel.add(new JLabel("Hasil pencarian"), BorderLayout.NORTH);
        searchResultsPanel.add(new JScrollPane(tblSearchResult), BorderLayout.CENTER);

        JPanel umatHadirPanel = new JPanel(new BorderLayout());
        umatHadirPanel.add(lblVisitorCount, BorderLayout.NORTH);
        umatHadirPanel.add(new JScrollPane(tblOrangHadir), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 5));
        centerPanel.add(searchResultsPanel);
        centerPanel.add(umatHadirPanel);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        setContentPane(contentPane);
        //#endregion

        txtInputNama.requestFocusInWindow();
    }

    private boolean checkIsBarcode(String input) {
        if (input.matches("\\d{13}")) {
            return true;
        }
        return false;
    }

    private void searchByName(String input) {
        if (input.length() < 2) {
            setSearchResult(new ArrayList<>());
            return;
        }

        List<Siswa> searchResult = listOrang.stream()
                .filter(umat -> umat.nama.toLowerCase().contains(input.toLowerCase()))
                .collect(Collectors.toList());
        setSearchResult(searchResult);
    }

    private void setSearchResult(List<Siswa> result) {
        tblSearchResultModel.setData(result);
    }

    private void absenBarcode(String input) {
        Optional<Siswa> match = listOrang.stream()
                .filter(umat -> input.equals(umat.idBarcode))
                .findFirst();
        if (match.isPresent()) {
            catatKehadiran(match.get());
        }
    }

    private void catatKehadiran(Siswa u) {
        if (!tblOrangHadirModel.getData().contains(u)) {
            HttpHelper.makeHttpRequest(App.config.url("/absensi_siswa/absen"), HttpPost::new, u, boolean.class);

            tblOrangHadirModel.add(u);

            int newSize = tblOrangHadirModel.getRowCount();
            lblVisitorCount.setText(String.format(VISITOR_COUNT_FORMAT, newSize));
        }
    }
}
