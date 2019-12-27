package com.skopware.vdjvis.desktop.absensi;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.jtable.cellrenderer.LocalDateCellRenderer;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.jdbi.v3.core.Handle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.stream.Collectors;

public class FrameAbsensiUmat extends JInternalFrame {
    public static final String VISITOR_COUNT_FORMAT = "Daftar umat yg hadir (%d umat)";
    private JTextField txtInputNama;

    private List<BaseCrudTableModel.ColumnConfig> tblSearchResultColumnConfigs;
    private BaseCrudTableModel<Umat> tblSearchResultModel;
    private JTable tblSearchResult;

    private JLabel lblVisitorCount;
    private List<BaseCrudTableModel.ColumnConfig> tblOrangHadirColumnConfigs;
    private BaseCrudTableModel<Umat> tblOrangHadirModel;
    private JTable tblOrangHadir;

    private List<Umat> listOrang;

    public FrameAbsensiUmat() {
        super("Absensi umat", true, true, true, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        List<Umat> prevOrangHadirList;
        try (Handle h = App.jdbi.open()) {
            listOrang = h.select("select uuid, nama, alamat, tgl_lahir, id_barcode from umat where active=1")
                    .map((rs, ctx) -> {
                        Umat x = new Umat();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        x.alamat = rs.getString("alamat");
                        x.tglLahir = DateTimeHelper.toLocalDate(rs.getDate("tgl_lahir"));
                        x.idBarcode = rs.getString("id_barcode");
                        return x;
                    })
                    .list();

            prevOrangHadirList = h.select("select u.uuid, u.nama from kehadiran k" +
                    " join umat u on u.uuid=k.umat_id" +
                    " where k.tgl=?", LocalDate.now())
                    .map((rs, ctx) -> {
                        Umat x = new Umat();
                        x.uuid = rs.getString("uuid");
                        x.nama = rs.getString("nama");
                        return x;
                    })
                    .list();
        }

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
        tblSearchResultModel.setRecordType(Umat.class);
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

            Umat sel = tblSearchResultModel.get(selRowIdx);
            txtInputNama.setText("");
            catatKehadiran(sel);
        });

        tblOrangHadirColumnConfigs = Arrays.asList(
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
        tblOrangHadirModel.setColumnConfigs(tblOrangHadirColumnConfigs);
        tblOrangHadirModel.setRecordType(Umat.class);
        tblOrangHadirModel.setData(prevOrangHadirList);

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

        List<Umat> searchResult = listOrang.stream()
                .filter(umat -> umat.nama.toLowerCase().contains(input.toLowerCase()))
                .collect(Collectors.toList());
        setSearchResult(searchResult);
    }

    private void setSearchResult(List<Umat> result) {
        tblSearchResultModel.setData(result);
    }

    private void absenBarcode(String input) {
        Optional<Umat> match = listOrang.stream()
                .filter(umat -> input.equals(umat.idBarcode))
                .findFirst();
        if (match.isPresent()) {
            catatKehadiran(match.get());
        }
    }

    private void catatKehadiran(Umat u) {
        if (!tblOrangHadirModel.getData().contains(u)) {
            App.jdbi.useHandle(handle -> {
                LocalDate today = LocalDate.now();

                Optional<String> id = handle.select("select umat_id from kehadiran where umat_id=? and tgl=?", u.uuid, today)
                        .mapTo(String.class)
                        .findFirst();

                if (!id.isPresent()) {
                    handle.createUpdate("insert into kehadiran(umat_id, tgl) values(:umat_id, :tgl)")
                            .bind("umat_id", u.uuid)
                            .bind("tgl", today)
                            .execute();
                }
            });

            tblOrangHadirModel.add(u);

            int newSize = tblOrangHadirModel.getRowCount();
            lblVisitorCount.setText(String.format(VISITOR_COUNT_FORMAT, newSize));
        }
    }
}
