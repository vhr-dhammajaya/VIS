package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.*;
import com.skopware.javautils.swing.BaseCrudForm;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.Acara;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

public class GridAcara {
    public static JDataGrid<Acara> createDefault() {
        JDataGridOptions<Acara> o = createDefaultOptions();

        return new JDataGrid<>(o);
    }

    public static JDataGrid<Acara> createNoAddEditDelete() {
        JDataGridOptions<Acara> o = createDefaultOptions();
        o.enableAdd = o.enableEdit = o.enableDelete = false;
        return new JDataGrid<>(o);
    }

    public static JDataGridOptions<Acara> createDefaultOptions() {
        JDataGridOptions<Acara> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("nama");
                    x.label = ("Nama acara");
                })
        );

        o.recordType = Acara.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/acara";

        o.fnShowCreateForm = () -> new FormAcara(App.mainFrame);
        o.fnShowEditForm = (record, modelIdx) -> new FormAcara(App.mainFrame, record, modelIdx);
        return o;
    }

    public static class FormAcara extends BaseCrudForm<Acara> {
        private JTextField txtNama;

        public FormAcara(Frame owner) {
            super(owner, "Buat acara", Acara.class);
        }

        public FormAcara(Frame owner, Acara record, int modelIdx) {
            super(owner, "Edit acara", Acara.class, record, modelIdx);
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
