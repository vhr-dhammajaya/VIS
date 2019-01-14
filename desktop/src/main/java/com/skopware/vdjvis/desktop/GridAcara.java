package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.*;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.Acara;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

public class GridAcara extends JDataGrid<Acara> {
    public static GridAcara create() {
        List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("nama");
                    x.label = ("Nama acara");
                })
        );
        return new GridAcara(columnConfigs);
    }

    public GridAcara(List<BaseCrudTableModel.ColumnConfig> columnConfigs) {
        super(columnConfigs, Acara.class, App.config);

        controllerUrl = App.config.url("/acara");
    }

    protected void showCreateForm() {
        new FormAcara(App.mainFrame, "Buat acara");
        // lines below executed after dialog closed
    }

    protected void showEditForm(Acara record, int modelIdx) {
        new FormAcara(App.mainFrame, "Edit acara", record, modelIdx);
    }

    public class FormAcara extends BaseCrudForm {
        private JTextField txtNama;

        public FormAcara(Frame owner, String title) {
            super(owner, title);
        }

        public FormAcara(Frame owner, String title, Acara editedRecord, int modelIdx) {
            super(owner, title, editedRecord, modelIdx);
        }

        @Override
        protected void initFormFields() {
            txtNama = new JTextField(45);

            FormLayout formLayout = new FormLayout("right:pref, 4dlu, left:pref:grow");
            DefaultFormBuilder builder = new DefaultFormBuilder(formLayout);
            builder.border(Borders.DIALOG);

            builder.append("Nama", txtNama);

            pnlFormFields = builder.build();

        }

        @Override
        protected void syncModelToGui() {
            Acara r = (Acara) this.editedRecord;
            txtNama.setText(r.nama);
        }

        @Override
        protected boolean validateFormFields() {
            List<String> errors = new ArrayList<>();

            if (txtNama.getText().isEmpty()) {
                errors.add("Nama tidak boleh kosong");
            }

            String errMsg = String.join("\n", errors);

            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(getOwner(), errMsg, "Error", JOptionPane.ERROR_MESSAGE);
            }

            return errors.isEmpty();
        }

        @Override
        protected void syncGuiToModel() {
            Acara r = (Acara) this.editedRecord;
            r.nama = txtNama.getText();
        }
    }
}
