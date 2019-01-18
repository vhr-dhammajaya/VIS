package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple3;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.httpclient.HttpGetWithBody;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudFrame;
import com.skopware.javautils.swing.BaseCrudSwingWorker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.CellFoto;
import com.skopware.vdjvis.api.Leluhur;
import com.skopware.vdjvis.api.PapanFoto;
import com.skopware.vdjvis.api.PlacePhotoRequestParam;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class FrameSetLokasiFoto extends BaseCrudFrame {
    private JDataGrid<Leluhur> gridMendiang; // fixme change to grid leluhur
    private List<PapanFoto> papanFotoList;
    private Map<String, CellFoto> cellFotoById;
    private PanelSemuaPapanFoto panelSemuaPapanFoto;
    private JPopupMenu popup;
    private JMenuItem mnuCut;
    private JMenuItem mnuPaste;
    private CellFoto cellForCutAndPaste;
    private Leluhur leluhurClipboard;

    public FrameSetLokasiFoto() {
        super("Set lokasi foto");

        gridMendiang = GridLeluhur2.create(); // fixme add popup menu to place umat to board cell
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

        // refresh daftar papan
        BaseCrudSwingWorker<Tuple3<
                PageData<Leluhur>,
                List<PapanFoto>,
                Map<String, CellFoto>>> worker = new BaseCrudSwingWorker<>(progressGlassPane);

        worker.onDoInBackground = () -> {
            PageData<Leluhur> leluhurList = HttpHelper.makeHttpRequest(App.config.url("/leluhur2"), HttpGetWithBody::new, gridMendiang.gridConfig, PageData.class, Leluhur.class);

            List<PapanFoto> papanFotoList = HttpHelper.makeHttpRequest(App.config.url("/lokasi_foto/list_papan"), HttpGetWithBody::new, null, List.class, PapanFoto.class);
            List<CellFoto> cellFotoList = HttpHelper.makeHttpRequest(App.config.url("/lokasi_foto/list_cell"), HttpGetWithBody::new, null, List.class, CellFoto.class);

            Map<String, List<CellFoto>> cellFotoByPapanId = ObjectHelper.groupList(cellFotoList, e -> e.papanId);

            for (PapanFoto papan : papanFotoList) {
                papan.initCells();

                List<CellFoto> cells = cellFotoByPapanId.get(papan.getUuid());

                for (CellFoto c : cells) {
                    c.papan = papan;
                    papan.arrCellFoto[c.row][c.col] = c;
                }
            }

            Map<String, CellFoto> cellFotoById = ObjectHelper.groupListById(cellFotoList, e -> e.uuid);

            return new Tuple3<>(leluhurList, papanFotoList, cellFotoById);
        };

        worker.onSuccess = result -> {
            gridMendiang.setTableRows(result.val1);

            this.papanFotoList = result.val2;
            this.cellFotoById = result.val3;
            panelSemuaPapanFoto.refresh();
        };

        worker.onError = ex -> {
            gridMendiang.gridConfig.pagingConfig.revertPageNum();

            ex.printStackTrace();
            SwingHelper.showErrorMessage(this, "Error saat membaca database");
        };

        worker.execute();
    }

    private void placePhoto(Leluhur x, CellFoto dest, Runnable onSuccess) {
        PlacePhotoRequestParam requestParam = new PlacePhotoRequestParam();

        if (x.cellFotoId != null) { // previously already placed somewhere else, need to repaint/clear that table too
            requestParam.originCellId = x.cellFotoId;
        }

        if (dest.leluhur != null) { // destination cell is already occupied by another photo
            requestParam.destCellExistingIdMendiang = dest.leluhur.uuid;
        }

        requestParam.idMendiang = x.uuid;
        requestParam.destCellId = dest.uuid;

        BaseCrudSwingWorker<Boolean> worker = new BaseCrudSwingWorker<>(progressGlassPane);
        worker.onDoInBackground = () -> HttpHelper.makeHttpRequest(App.config.url("/lokasi_foto"), HttpPost::new, requestParam, boolean.class);

        worker.onSuccess = dummy -> {
            CellFoto xOriginCell = null;
            if (x.cellFotoId != null) {
                xOriginCell = cellFotoById.get(x.cellFotoId);
                xOriginCell.leluhur = null;
            }

            if (dest.leluhur != null) {
                dest.leluhur.cellFotoId = null;
                dest.leluhur.cellFoto = null;
            }

            dest.leluhur = x;

            x.cellFotoId = dest.uuid;
            x.cellFoto = dest;

            if (xOriginCell != null) {
                xOriginCell.papan.jTable.repaint();
            }
            dest.papan.jTable.repaint();

            if (onSuccess != null) {
                onSuccess.run();
            }
        };

        worker.onError = ex -> {
            ex.printStackTrace();
            SwingHelper.showErrorMessage(this, "Gagal memindahkan foto");
        };

        worker.execute();
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
