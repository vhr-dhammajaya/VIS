package com.skopware.vdjvis.desktop.samanagara;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudFrame;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.datasource.DropwizardDataSource;
import com.skopware.vdjvis.api.entities.CellFoto;
import com.skopware.vdjvis.api.entities.Leluhur;
import com.skopware.vdjvis.api.entities.PapanFoto;
import com.skopware.vdjvis.api.dto.DtoPlacePhoto;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class FrameSetLokasiFoto extends BaseCrudFrame {
    private JDataGrid<Leluhur> gridMendiang;
//    private List<Leluhur> listLeluhur;
//    private Map<String, Leluhur> leluhurById;

    private List<PapanFoto> papanFotoList;
    private Map<String, PapanFoto> papanById;

    private Map<String, CellFoto> cellFotoById;

    private PanelSemuaPapanFoto panelSemuaPapanFoto;
    private JPopupMenu popup;
    private JMenuItem mnuCut;
    private JMenuItem mnuPaste;
    private CellFoto cellForCutAndPaste;
    private Leluhur leluhurClipboard;

    public FrameSetLokasiFoto() {
        super("Set lokasi foto");

        JDataGridOptions<Leluhur> options = new JDataGridOptions<>();
        options.enableAdd = options.enableEdit = options.enableDelete = false;

        options.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "penanggungJawab.nama";
                    x.dbColumnName = "umat_nama";
                    x.label = "Nama Penanggung Jawab";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nama";
                    x.label = "Nama Mendiang";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatLahir";
                    x.dbColumnName = "tempat_lahir";
                    x.label = "Tempat Lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglLahir";
                    x.dbColumnName = "tgl_lahir";
                    x.label = "Tgl Lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatMati";
                    x.dbColumnName = "tempat_mati";
                    x.label = "Meninggal di";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglMati";
                    x.dbColumnName = "tgl_mati";
                    x.label = "Tgl Meninggal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "hubunganDgnUmat";
                    x.dbColumnName = "hubungan_dgn_umat";
                    x.label = "Hubungan dgn penanggung jawab";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglDaftar";
                    x.dbColumnName = "tgl_daftar";
                    x.label = "Tgl daftar";
                })
        );

        options.appConfig = App.config;
        options.dataSource = new DropwizardDataSource<>(App.config.url("/leluhur"), Leluhur.class);

        gridMendiang = new JDataGrid<>(options);
        gridMendiang.glassPane = progressGlassPane;

        panelSemuaPapanFoto = new PanelSemuaPapanFoto();

        mnuCut = new JMenuItem("Cut");
        mnuCut.addActionListener(e -> {
            cutLeluhur();
        });

        mnuPaste = new JMenuItem("Paste");
        mnuPaste.setEnabled(false);
        mnuPaste.addActionListener(e -> {
            pasteLeluhur();
        });

        popup = new JPopupMenu();
        popup.add(mnuCut);
        popup.add(mnuPaste);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));
        pnlMain.add(gridMendiang);
        pnlMain.add(panelSemuaPapanFoto);

        JScrollPane scrollPane = new JScrollPane(pnlMain);
        setContentPane(scrollPane);
    }

    @Override
    public void refreshData() {
        progressGlassPane.setVisible(true);

        gridMendiang.setFilterAndSort();

        try {
            PageData<Leluhur> leluhurList = HttpHelper.makeHttpRequest(App.config.url("/leluhur"), HttpGetWithBody::new, gridMendiang.gridConfig, PageData.class, Leluhur.class);
            List<PapanFoto> papanFotoList = HttpHelper.makeHttpRequest(App.config.url("/lokasi_foto/list_papan"), HttpGetWithBody::new, null, List.class, PapanFoto.class);

            gridMendiang.setTableRows(leluhurList);
//            listLeluhur = result.val1.rows;
//            leluhurById = ObjectHelper.groupListById(listLeluhur, leluhur -> leluhur.uuid);

            this.papanFotoList = papanFotoList;
            papanById = ObjectHelper.groupListById(papanFotoList, papanFoto -> papanFoto.uuid);

            cellFotoById = new HashMap<>();

            for (PapanFoto papan : papanFotoList) {
                CellFoto[][] cells = papan.arrCellFoto;

                for (int i = 0; i < cells.length; i++) {
                    for (int j = 0; j < cells[i].length; j++) {
                        cells[i][j].papan = papanById.get(cells[i][j].papan.uuid);

//                        Leluhur leluhur = leluhurById.get(cells[i][j].leluhur.uuid);
//                        if (leluhur != null) {
//                            cells[i][j].leluhur = leluhur;
//                            leluhur.cellFoto = cells[i][j];
//                        }

                        cellFotoById.put(cells[i][j].uuid, cells[i][j]);
                    }
                }
            }

            panelSemuaPapanFoto.refresh();
        }
        catch (Exception ex) {
            gridMendiang.gridConfig.pagingConfig.revertPageNum();

            ex.printStackTrace();
            SwingHelper.showErrorMessage(this, "Error saat membaca database");
        }
    }

    private void placePhoto(Leluhur leluhur, CellFoto destCell, Runnable onSuccess) {
        DtoPlacePhoto requestParam = new DtoPlacePhoto();

        // if previously already placed somewhere else, need to repaint/clear that table too
        if (leluhur.cellFoto != null) {
            requestParam.mendiangOriginCellId = leluhur.cellFoto.uuid;
        }

        // if destination cell is already occupied by another photo
        if (destCell.leluhur != null) {
            requestParam.existingIdMendiangInDestCell = destCell.leluhur.uuid;
        }

        requestParam.idMendiang = leluhur.uuid;
        requestParam.destCellId = destCell.uuid;

        try {
            HttpHelper.makeHttpRequest(App.config.url("/lokasi_foto"), HttpPost::new, requestParam, boolean.class);

            CellFoto cellAsal = null;
            boolean kosongkanCellAsal = leluhur.cellFoto != null;

            if (kosongkanCellAsal) {
                cellAsal = cellFotoById.get(leluhur.cellFoto.uuid);
                cellAsal.leluhur = null;
            }

            if (destCell.leluhur != null) {
                destCell.leluhur.cellFoto = null;
            }

            destCell.leluhur = leluhur;
            leluhur.cellFoto = destCell;

            if (cellAsal != null) {
                cellAsal.papan.jTable.repaint();
            }
            destCell.papan.jTable.repaint();

            if (onSuccess != null) {
                onSuccess.run();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            SwingHelper.showErrorMessage(this, "Gagal memindahkan foto");
        }
    }

    public void cutLeluhur() {
        leluhurClipboard = cellForCutAndPaste.leluhur;
        mnuPaste.setEnabled(true);
    }

    public void pasteLeluhur() {
        placePhoto(leluhurClipboard, cellForCutAndPaste, () -> mnuPaste.setEnabled(false));
    }

    public class PanelSemuaPapanFoto extends JPanel {
        private JPanel panel;

        public PanelSemuaPapanFoto() {
            panel = this;
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        }

        public void refresh() {
            panel.removeAll();

            for (PapanFoto papan : papanFotoList) {
                panel.add(new PanelSatuPapanFoto(papan));
            }

            panel.revalidate();
            panel.repaint();
        }
    }

    public class PanelSatuPapanFoto extends JPanel {
        private PapanFoto papan;
        private CellFoto[][] cells;
        private PapanFotoTableModel tableModel;
        private JTable table;

        public PanelSatuPapanFoto(PapanFoto papan) {
            this.papan = papan;
            this.cells = papan.arrCellFoto;
            setLayout(new BorderLayout());

            tableModel = new PapanFotoTableModel(papan);
            table = new JTable(tableModel);
            papan.jTable = table;
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setCellSelectionEnabled(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Leluhur leluhur = gridMendiang.getSelectedRecord();
                    if (leluhur == null) {
                        return;
                    }

                    // letakkan mendiang yg terpilih ke cell yg di-klik
                    CellFoto cell = getClickedCell(e.getPoint());

                    placePhoto(leluhur, cell, null);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    maybeShowPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup(e);
                }

                private void maybeShowPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        CellFoto cell = getClickedCell(e.getPoint());
                        cellForCutAndPaste = cell;
                        mnuCut.setEnabled(cell.leluhur != null);

                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });

            JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
            top.add(new JLabel("Papan " + papan.nama));

            add(top, BorderLayout.NORTH);
            add(table, BorderLayout.CENTER);
        }

        public CellFoto getClickedCell(Point pt) {
            int row = table.rowAtPoint(pt);
            int col = table.columnAtPoint(pt);
            CellFoto cell = cells[row][col];
            return cell;
        }
    }

    public static class PapanFotoTableModel extends AbstractTableModel {
        private PapanFoto papan;

        public PapanFotoTableModel(PapanFoto papan) {
            this.papan = papan;
        }

        @Override
        public int getRowCount() {
            return papan.height;
        }

        @Override
        public int getColumnCount() {
            return papan.width;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CellFoto c = papan.arrCellFoto[rowIndex][columnIndex];
            String value = String.format("%s %02d", papan.nama, papan.width*rowIndex + columnIndex + 1);

            if (c.leluhur != null) {
                value += " - " + c.leluhur.nama;
            }

            return value;
        }
    }
}
