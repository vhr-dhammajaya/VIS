package com.skopware.vdjvis.desktop.samanagara;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.db.BaseCrudDAO;
import com.skopware.javautils.db.DbHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.SortConfig;
import com.skopware.javautils.swing.grid.datasource.DropwizardDataSource;
import com.skopware.javautils.swing.grid.datasource.JdbiDataSource;
import com.skopware.vdjvis.api.entities.TarifSamanagara;
import com.skopware.vdjvis.desktop.App;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

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

        o.appConfig = App.config;
        o.dataSource = new JdbiDataSource<TarifSamanagara, BaseCrudDAO<TarifSamanagara>>(TarifSamanagara.class, App.jdbi, "hist_biaya_smngr", null) {
            @Override
            public PageData refreshData(GridConfig gridConfig) {
                return jdbi.withHandle(h -> {
                    PageData<TarifSamanagara> result = DbHelper.fetchPageData(h, "hist_biaya_smngr", gridConfig, TarifSamanagara.class, false);
                    return result;
                });
            }
        };

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
                    App.jdbi.useHandle(h -> {
                        // insert row baru dgn start_date = tgl hari ini & end_date = 31/12/9999
                        //  kecuali jika sudah ada row baru dgn start_date = hari ini, update row existing
                        // update end_date ut row berisi harga sebelumnya = tgl hari ini
                        LocalDate today = LocalDate.now();

                        Optional<String> existingUuid = h.select("select id from hist_biaya_smngr where start_date = ?", today)
                                .mapTo(String.class)
                                .findFirst();
                        if (existingUuid.isPresent()) {
                            h.createUpdate("update hist_biaya_smngr set nominal = ? where id = ?")
                                    .bind(0, nominal)
                                    .bind(1, existingUuid.get())
                                    .execute();
                        }
                        else {
                            String prevRecordId = h.createQuery("select id from hist_biaya_smngr order by start_date desc limit 1")
                                    .mapTo(String.class).findOnly();
                            h.createUpdate("update hist_biaya_smngr set end_date = ? where id = ?")
                                    .bind(0, today)
                                    .bind(1, prevRecordId)
                                    .execute();

                            TarifSamanagara r = new TarifSamanagara();
                            r.uuid = UUID.randomUUID().toString();
                            r.startDate = today;
                            r.endDate = LocalDate.of(3000, 12, 31);
                            r.nominal = nominal;

                            h.createUpdate("insert into hist_biaya_smngr(id, start_date, end_date, nominal) values(:uuid, :startDate, :endDate, :nominal)")
                                    .bindFields(r)
                                    .execute();
                        }
                    });

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
