package com.skopware.vdjvis.desktop.master;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudForm;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.User;
import com.skopware.vdjvis.desktop.App;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridUser {
    public static JDataGrid<User> createDefault() {
        JDataGridOptions<User> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "username";
                    x.label = "Username";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nama";
                    x.label = "Nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "tipe";
                    x.label = "Tipe";
                })
        );

        o.recordType = User.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/user";

        o.fnShowCreateForm = () -> new FormUser(App.mainFrame);
        o.fnShowEditForm = (record, modelIdx) -> new FormUser(App.mainFrame, record, modelIdx);

        return new JDataGrid<>(o);
    }

    public static class FormUser extends BaseCrudForm<User> {
        private JTextField txtUsername;
        private JPasswordField txtPassword;
        private JTextField txtNama;
        private JComboBox<User.Type> cmbTipeUser;

        public FormUser(JFrame frame) {
            super(frame, "Buat user", User.class);
        }

        public FormUser(JFrame frame, User record, int modelIdx) {
            super(frame, "Edit user", User.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            txtUsername = new JTextField(45);
            txtNama = new JTextField(45);
            cmbTipeUser = new JComboBox<>(new User.Type[] {
                    User.Type.OPERATOR,
                    User.Type.PENGURUS
            });

            String layoutSpec = "right:pref, 4dlu, left:pref:grow";

            FormLayout layout = new FormLayout(layoutSpec);
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            builder.append("Username", txtUsername);
            builder.nextLine();

            if (isCreateNew) {
                txtPassword = new JPasswordField(45);
                builder.append("Password", txtPassword);
                builder.nextLine();
            }

            builder.append("Nama", txtNama);
            builder.nextLine();
            builder.append("Jenis", cmbTipeUser);
//            builder.nextLine();

            pnlFormFields = builder.build();
        }

        @Override
        protected void syncModelToGui() {
            User r = this.editedRecord;
            txtUsername.setText(r.username);
            txtNama.setText(r.nama);
            cmbTipeUser.setSelectedItem(r.tipe);
        }

        @Override
        protected boolean validateFormFields() {
            List<String> errors = new ArrayList<>();

            if (txtUsername.getText().isEmpty()) {
                errors.add("Username tidak boleh kosong");
            }

            if (isCreateNew && txtPassword.getPassword().length == 0) {
                errors.add("Password tidak boleh kosong");
            }

            if (txtNama.getText().isEmpty()) {
                errors.add("Nama tidak boleh kosong");
            }

            if (cmbTipeUser.getSelectedIndex() == -1) {
                errors.add("Jenis user harus di-set");
            }

            String errMsg = String.join("\n", errors);

            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(getOwner(), errMsg, "Error", JOptionPane.ERROR_MESSAGE);
            }

            return errors.isEmpty();
        }

        @Override
        protected void syncGuiToModel() {
            User r = this.editedRecord;
            r.username = txtUsername.getText();
            r.nama = txtNama.getText();
            r.tipe = (User.Type) cmbTipeUser.getSelectedItem();

            if (isCreateNew) {
                r.password = new String(txtPassword.getPassword());
            }
        }
    }
}
