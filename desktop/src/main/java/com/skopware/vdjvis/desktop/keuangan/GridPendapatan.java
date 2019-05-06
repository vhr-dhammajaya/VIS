package com.skopware.vdjvis.desktop.keuangan;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.httpclient.HttpHelper;
import com.skopware.javautils.swing.*;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.Acara;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.api.entities.Umat;
import com.skopware.vdjvis.api.entities.User;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.desktop.DialogInputAlasanMintaPembetulan;
import com.skopware.vdjvis.desktop.master.GridAcara;
import com.skopware.vdjvis.desktop.master.GridUmat;
import org.apache.http.client.methods.HttpPost;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class GridPendapatan {
    public static JDataGridOptions<Pendapatan> createForUser(User user) {
        switch (user.tipe) {
            case PENGURUS:
                return createBaseOptions();
            case OPERATOR:
                return createOptionsForOperator();
            default:
                throw new IllegalStateException();
        }
    }

    public static JDataGridOptions<Pendapatan> createBaseOptions() {
        JDataGridOptions<Pendapatan> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Umat yg berdana";
                    x.fieldName = "umat.nama";
                    x.dbColumnName = "umat_nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Tgl dana";
                    x.fieldName = "tglTransaksi";
                    x.dbColumnName = "tgl_trx";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Nominal";
                    x.fieldName = x.dbColumnName = "nominal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Cara dana";
                    x.fieldName = x.dbColumnName = "channel";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Jenis dana";
                    x.fieldName = "jenisDana";
                    x.dbColumnName = "jenis_dana";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Untuk acara";
                    x.fieldName = "acara.nama";
                    x.dbColumnName = "acara_nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "correctionStatus";
                    x.dbColumnName = "correction_status";
                    x.label = "Butuh pembetulan?";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "correctionRequestReason";
                    x.dbColumnName = "corr_req_reason";
                    x.label = "Alasan";
                })
        );
        o.recordType = Pendapatan.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/pendapatan";

        o.fnShowCreateForm = () -> new FormPendapatan(App.mainFrame);
        o.fnShowEditForm = (rec, idx) -> new FormPendapatan(App.mainFrame, rec, idx);

        JButton btnCetakKuitansi = new JButton("Cetak tanda terima");
        btnCetakKuitansi.addActionListener(e -> {
            Pendapatan sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }

            DialogPrepareTandaTerima.Input jasperParams = new DialogPrepareTandaTerima.Input();
            jasperParams.set(sel);

            DialogPrepareTandaTerima dialog = new DialogPrepareTandaTerima(App.mainFrame, jasperParams);
            dialog.setVisible(true);
            dialog.pack();
        });

        o.additionalToolbarButtons.add(btnCetakKuitansi);

        return o;
    }

    public static JDataGridOptions<Pendapatan> createOptionsForOperator() {
        JDataGridOptions<Pendapatan> o = createBaseOptions();
        o.enableEdit = o.enableDelete = false;

        JButton btnMintaPembetulan = new JButton("Minta pembetulan");
        btnMintaPembetulan.addActionListener(e -> {
            Pendapatan sel = o.grid.getSelectedRecord();
            if (sel == null) {
                return;
            }
            int selIdx = o.grid.getSelectedRecordIdx();

            DialogInputAlasanMintaPembetulan dialogInput = new DialogInputAlasanMintaPembetulan(reason -> {
                sel.correctionStatus = true;
                sel.correctionRequestReason = reason;
                HttpHelper.makeHttpRequest(App.config.url("/pendapatan/request_koreksi"), HttpPost::new, sel, boolean.class);
                o.grid.tableModel.fireTableRowsUpdated(selIdx, selIdx);
            });
            dialogInput.setVisible(true);
            dialogInput.pack();
        });

        o.additionalToolbarButtons.add(btnMintaPembetulan);

        return o;
    }

    public static class FormPendapatan extends BaseCrudForm<Pendapatan> {

        private JForeignKeyPicker<Umat> edUmat;
        private JDatePicker edTglTrans;
        private JSpinner edNominal;
        private JComboBox<String> edChannel;
        private JComboBox<Pendapatan.JenisDana> edJenisDana;
        private JTextArea edKeterangan;
        private JForeignKeyPicker<Acara> edAcara;

        private JCheckBox edButuhPembetulan;
        private JTextArea edAlasanButuhPembetulan;

        public FormPendapatan(Frame owner) {
            super(owner, "Input pendapatan", Pendapatan.class);

            onBackendSuccess = (createdRecord) -> {
                this.dispose();

                DialogPrepareTandaTerima.Input jasperParams = new DialogPrepareTandaTerima.Input();
                jasperParams.set(createdRecord);

                DialogPrepareTandaTerima dialog = new DialogPrepareTandaTerima(App.mainFrame, jasperParams);
                dialog.setVisible(true);
                dialog.pack();
            };
        }

        public FormPendapatan(Frame owner, Pendapatan record, int modelIdx) {
            super(owner, "Edit pendapatan", Pendapatan.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            edUmat = new JForeignKeyPicker<>(App.mainFrame, GridUmat.createNoAddEditDelete());

            edTglTrans = new JDatePicker();
            edTglTrans.setDate(LocalDate.now());

            edNominal = new JSpinner(new SpinnerNumberModel(1000, 1000, Integer.MAX_VALUE, 1000));
            edChannel = new JComboBox<>(new String[]{"Tunai", "EDC", "Transfer ke rek. BCA"});
            edJenisDana = new JComboBox<>(new Pendapatan.JenisDana[] {
//                Pendapatan.JenisDana.IURAN_SAMANAGARA,
//                Pendapatan.JenisDana.DANA_SOSIAL,
//                Pendapatan.JenisDana.DANA_TETAP,
                Pendapatan.JenisDana.DANA_SUKARELA,
                Pendapatan.JenisDana.DANA_PARAMITTA,
                Pendapatan.JenisDana.HAPPY_SUNDAY,
                Pendapatan.JenisDana.KOTAK_DANA,
                Pendapatan.JenisDana.LAIN_2
            });
            edKeterangan = new JTextArea(10, 20);
            edAcara = new JForeignKeyPicker<>(App.mainFrame, GridAcara.createNoAddEditDelete());

            List<List<Tuple2<String, Component>>> formFields = new ArrayList<>();
            formFields.add(Arrays.asList(new Tuple2<>("Umat yg berdana", edUmat)));
            formFields.add(Arrays.asList(new Tuple2<>("Tgl dana", edTglTrans), new Tuple2<>("Nominal", edNominal)));
            formFields.add(Arrays.asList(new Tuple2<>("Cara dana", edChannel), new Tuple2<>("Jenis dana", edJenisDana)));
            formFields.add(Arrays.asList(new Tuple2<>("Keterangan", edKeterangan)));
            formFields.add(Arrays.asList(new Tuple2<>("Untuk acara", edAcara)));

            if (!isCreateNew) {
                edButuhPembetulan = new JCheckBox();

                edAlasanButuhPembetulan = new JTextArea(10, 20);
                edAlasanButuhPembetulan.setEditable(false);

                formFields.add(Arrays.asList(new Tuple2<>("Butuh pembetulan?", edButuhPembetulan)));
                formFields.add(Arrays.asList(new Tuple2<>("Alasan butuh pembetulan", edAlasanButuhPembetulan)));
            }

            pnlFormFields = SwingHelper.buildForm2(formFields);
        }

        @Override
        protected void syncModelToGui() {
            Pendapatan r = this.editedRecord;

            edUmat.setRecord(r.umat);
            edTglTrans.setDate(r.tglTransaksi);
            edNominal.setValue(r.nominal);
            edChannel.setSelectedItem(r.channel);
            edJenisDana.setSelectedItem(r.jenisDana);
            edKeterangan.setText(r.keterangan);
            edAcara.setRecord(r.acara);

            if (!isCreateNew) {
                edButuhPembetulan.setSelected(r.correctionStatus);
                edAlasanButuhPembetulan.setText(r.correctionRequestReason);
            }
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(this,
                    new Tuple2<>(edTglTrans.getDate() == null, "Tanggal tidak boleh kosong"),
                    new Tuple2<>(edNominal.getValue() == null, "Nominal tidak boleh kosong")
            );
        }

        @Override
        protected void syncGuiToModel() {
            Pendapatan r = this.editedRecord;

            Umat umat = edUmat.getRecord();
            r.umat = new Umat(); // reset uuid to null
            if (umat != null) {
                r.umat = umat;
            }

            r.tglTransaksi = edTglTrans.getDate();
            r.nominal = (int) edNominal.getValue();
            r.channel = (String) edChannel.getSelectedItem();
            r.jenisDana = (Pendapatan.JenisDana) edJenisDana.getSelectedItem();
            r.keterangan = edKeterangan.getText();

            Acara acara = edAcara.getRecord();
            r.acara = new Acara(); // reset uuid to null
            if (acara != null) {
                r.acara = acara;
            }

            if (!isCreateNew) {
                r.correctionStatus = edButuhPembetulan.isSelected();
                if (!r.correctionStatus) {
                    r.correctionRequestReason = "";
                }
            }
        }
    }
}
