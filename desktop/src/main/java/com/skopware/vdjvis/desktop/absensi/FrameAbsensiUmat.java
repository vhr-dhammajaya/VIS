package com.skopware.vdjvis.desktop.absensi;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.jtable.cellrenderer.JButtonCellRenderer;
import com.skopware.javautils.swing.jtable.cellrenderer.LocalDateCellRenderer;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.stream.Collectors;

public class FrameAbsensiUmat extends JInternalFrame {
    public static final String VISITOR_COUNT_FORMAT = "Daftar umat yg hadir (%d umat)";
    private JTextField txtInputNama;

    private List<BaseCrudTableModel.ColumnConfig> tblSearchResultColumnConfigs;
    private SearchResultTableModel tblSearchResultModel;
    private JTable tblSearchResult;

    private JLabel lblVisitorCount;
    private List<BaseCrudTableModel.ColumnConfig> tblUmatHadirColumnConfigs;
    private UmatHadirTableModel tblUmatHadirModel;
    private JTable tblUmatHadir;

    private List<Umat> listUmat;
    private List<Umat> listSearchResult;
    private List<Umat> listUmatHadir;

    public FrameAbsensiUmat() {
        super("Absensi", true, true, true, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        listUmat = HttpHelper.makeHttpRequest(App.config.url("/absensi/list_umat"), HttpGetWithBody::new, null, List.class, Umat.class);
        listUmatHadir = HttpHelper.makeHttpRequest(App.config.url("/absensi/daftar_umat_hadir"), HttpGetWithBody::new, null, List.class, Umat.class);
        listSearchResult = new ArrayList<>();

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

        lblVisitorCount = new JLabel(String.format(VISITOR_COUNT_FORMAT, listUmatHadir.size()));

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

        tblSearchResultModel = new SearchResultTableModel();

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

            Umat sel = listSearchResult.get(selRowIdx);
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

        tblUmatHadirModel = new UmatHadirTableModel();

        tblUmatHadir = new JTable(tblUmatHadirModel);
        tblUmatHadir.setFocusable(false);
        tblUmatHadir.setFillsViewportHeight(true);

        tblUmatHadir.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUmatHadir.setRowSelectionAllowed(true);
        tblUmatHadir.setCellSelectionEnabled(false);
        tblUmatHadir.setColumnSelectionAllowed(false);

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
        umatHadirPanel.add(new JScrollPane(tblUmatHadir), BorderLayout.CENTER);

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

        List<Umat> searchResult = listUmat.stream()
                .filter(umat -> umat.nama.toLowerCase().contains(input.toLowerCase()))
                .collect(Collectors.toList());
        setSearchResult(searchResult);
    }

    private void setSearchResult(List<Umat> result) {
        listSearchResult = result;
        tblSearchResultModel.fireTableDataChanged();
    }

    private void absenBarcode(String input) {
        Optional<Umat> match = listUmat.stream()
                .filter(umat -> input.equals(umat.idBarcode))
                .findFirst();
        if (match.isPresent()) {
            catatKehadiran(match.get());
        }
    }

    private void catatKehadiran(Umat u) {
        if (!listUmatHadir.contains(u)) {
            HttpHelper.makeHttpRequest(App.config.url("/absensi/absen_umat"), HttpPost::new, u, boolean.class);

            int oldSize = listUmatHadir.size();
            listUmatHadir.add(u);
            tblUmatHadirModel.fireTableRowsInserted(oldSize, oldSize);

            int newSize = listUmatHadir.size();
            lblVisitorCount.setText(String.format(VISITOR_COUNT_FORMAT, newSize));
        }
    }

    public class SearchResultTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return listSearchResult.size();
        }

        @Override
        public int getColumnCount() {
            return tblSearchResultColumnConfigs.size();
        }

        @Override
        public String getColumnName(int column) {
            return tblSearchResultColumnConfigs.get(column).label;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return tblSearchResultColumnConfigs.get(columnIndex).dataType;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Umat row = listSearchResult.get(rowIndex);
            String fieldName = tblSearchResultColumnConfigs.get(columnIndex).fieldName;
            return ObjectHelper.getFieldValue(row, fieldName);
        }
    }

    public class UmatHadirTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return listUmatHadir.size();
        }

        @Override
        public int getColumnCount() {
            return tblUmatHadirColumnConfigs.size();
        }

        @Override
        public String getColumnName(int column) {
            return tblUmatHadirColumnConfigs.get(column).label;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return tblUmatHadirColumnConfigs.get(columnIndex).dataType;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Umat row = listUmatHadir.get(rowIndex);
            String fieldName = tblUmatHadirColumnConfigs.get(columnIndex).fieldName;
            return ObjectHelper.getFieldValue(row, fieldName);
        }
    }
}
