package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudSwingWorker;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.SortConfig;
import com.skopware.vdjvis.api.TarifSamanagara;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridSettingTarifSamanagara extends JDataGrid<TarifSamanagara> {
    public static GridSettingTarifSamanagara create() {
        JDataGridOptions<TarifSamanagara> options = new JDataGridOptions<>();

        options.enableAdd = options.enableEdit = options.enableDelete = options.enableFilter = options.enablePaging = false;

        options.columnConfigs = Arrays.asList(
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

        options.recordType = TarifSamanagara.class;
        options.appConfig = App.config;
        options.shortControllerUrl = "/tarif_samanagara";
        options.initialSortConfig = ObjectHelper.apply(new ArrayList<>(), list -> {
            list.add(ObjectHelper.apply(new SortConfig(), x -> {
                x.field = "start_date";
                x.dir = SortConfig.SortDir.DESC;
            }));
        });

        return new GridSettingTarifSamanagara(options);
    }

    public GridSettingTarifSamanagara(JDataGridOptions<TarifSamanagara> options) {
        super(options);
    }

    @Override
    protected void addAdditionalButtons() {
        JButton btnUpdateHarga = new JButton("Update nominal");
        btnUpdateHarga.addActionListener(e -> {
            new FormUpdateTarif(App.mainFrame, 10_000);
        });

        pnlButton.add(btnUpdateHarga);
    }

    @Override
    protected void showCreateForm() {
    }

    @Override
    protected void showEditForm(TarifSamanagara record, int modelIdx) {
    }

    private class FormUpdateTarif extends JDialog {
        private JSpinner txtNominal;

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

                glassPane.setVisible(true);
                BaseCrudSwingWorker<Boolean> worker = new BaseCrudSwingWorker<>(glassPane);

                worker.onDoInBackground = () -> {
                    return HttpHelper.makeHttpRequest(GridSettingTarifSamanagara.this.controllerUrl, HttpPost::new, nominal, Boolean.class);
                };

                worker.onSuccess = dummy -> {
                    // refresh grid
                    GridSettingTarifSamanagara.this.refreshData();
                    this.dispose();
                };

                worker.onError = ex -> {
                    SwingHelper.setEnabled(true, btnOk, btnCancel);
                    ex.printStackTrace();
                    SwingHelper.showErrorMessage(this, "Gagal mengupdate nominal iuran");
                };

                worker.execute();
            });
            btnCancel.addActionListener(e -> this.dispose());

            JPanel contentPane = SwingHelper.createOkCancelPanel(pnlMain, btnOk, btnCancel);
            setContentPane(contentPane);

            setVisible(true);
            pack();
        }
    }
}
