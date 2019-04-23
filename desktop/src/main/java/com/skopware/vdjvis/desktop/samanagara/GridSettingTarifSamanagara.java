package com.skopware.vdjvis.desktop.samanagara;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.SortConfig;
import com.skopware.vdjvis.api.entities.TarifSamanagara;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridSettingTarifSamanagara {
    public static JDataGrid<TarifSamanagara> create() {
        JDataGridOptions<TarifSamanagara> o = new JDataGridOptions<>();

        o.enableAdd = o.enableEdit = o.enableDelete = o.enableFilter = o.enablePaging = false;

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "startDate";
                    x.dbColumnName = "start_date";
                    x.label = "Berlaku mulai tgl";
                    x.sortable = false;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "endDate";
                    x.dbColumnName = "end_date";
                    x.label = "Berlaku s/d tgl";
                    x.sortable = false;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nominal";
                    x.label = "Nominal";
                    x.sortable = false;
                })
        );

        o.recordType = TarifSamanagara.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/tarif_samanagara";
        o.initialSortConfig.add(ObjectHelper.apply(new SortConfig(), x -> {
            x.field = "start_date";
            x.dir = SortConfig.SortDir.DESC;
        }));

        JButton btnUpdateNominal = new JButton("Update nominal");
        o.additionalToolbarButtons = Arrays.asList(btnUpdateNominal);

        JDataGrid<TarifSamanagara> grid = new JDataGrid<TarifSamanagara>(o);

        btnUpdateNominal.addActionListener(e -> {
            FormUpdateTarif form = new FormUpdateTarif(App.mainFrame, 10_000);
            form.jDataGrid = grid;
        });

        return grid;
    }

    private static class FormUpdateTarif extends JDialog {
        private JSpinner txtNominal;
        public JDataGrid<TarifSamanagara> jDataGrid;

        public FormUpdateTarif(Frame owner, int initialValue) {
            super(owner, "Update nominal iuran", false);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            txtNominal = new JSpinner(new SpinnerNumberModel(initialValue, 1000, Integer.MAX_VALUE, 1000));

            JPanel pnlMain = new JPanel(new FlowLayout());
            pnlMain.add(new JLabel("Nominal"));
            pnlMain.add(txtNominal);

            JButton btnOk = new JButton("Ok");
            JButton btnCancel = new JButton("Cancel");

            btnOk.addActionListener(e -> {
                SwingHelper.setEnabled(false, btnOk, btnCancel);

                int nominal = (int)txtNominal.getValue();

                try {
                    HttpHelper.makeHttpRequest(jDataGrid.controllerUrl, HttpPost::new, nominal, Boolean.class);

                    jDataGrid.refreshData();
                    this.dispose();
                }
                catch (Exception ex) {
                    SwingHelper.setEnabled(true, btnOk, btnCancel);
                    ex.printStackTrace();
                    SwingHelper.showErrorMessage(this, "Gagal mengupdate nominal iuran");
                }
            });
            btnCancel.addActionListener(e -> this.dispose());

            JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
            setContentPane(contentPane);

            setVisible(true);
            pack();
        }
    }
}
