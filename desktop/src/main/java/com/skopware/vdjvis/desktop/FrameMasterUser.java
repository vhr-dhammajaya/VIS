package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudInternalFrame;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.vdjvis.api.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameMasterUser extends BaseCrudInternalFrame<User> {
    public static FrameMasterUser create() {
        List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
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

        return new FrameMasterUser(columnConfigs);
    }

    public FrameMasterUser(List<BaseCrudTableModel.ColumnConfig> columnConfigs) {
        super("Master User", columnConfigs, User.class, App.config);

        controllerUrl = App.config.url("/user");
    }

    @Override
    protected void showCreateForm() {
        new FormUser(App.mainFrame, "Buat User");
    }

    @Override
    protected void showEditForm(User record, int modelIdx) {
        new FormUser(App.mainFrame, "Edit User", record, modelIdx);
    }

    public class FormUser extends BaseCrudForm {
        private JTextField txtUsername;
        private JPasswordField txtPassword;
        private JTextField txtNama;
        private JComboBox<User.Type> cmbTipeUser;

        public FormUser(JFrame frame, String title) {
            super(frame, title);
        }

        public FormUser(JFrame frame, String title, User record, int modelIdx) {
            super(frame, title, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            txtUsername = new JTextField(45);
            txtNama = new JTextField(45);
            cmbTipeUser = new JComboBox<>(new User.Type[] {
                    User.Type.OPERATOR,
                    User.Type.PENGURUS,
                    User.Type.UMAT
            });

            String layoutSpec;

            if (isCreateNew) {
                txtPassword = new JPasswordField(45);

                layoutSpec = "right:pref, 4dlu, left:pref:grow," +
                        "4dlu," +
                        "right:pref, 4dlu, left:pref:grow," +
                        "4dlu," +
                        "right:pref, 4dlu, left:pref:grow," +
                        "4dlu," +
                        "right:pref, 4dlu, left:pref:grow";
            }
            else {
                layoutSpec = "right:pref, 4dlu, left:pref:grow," +
                        "4dlu," +
                        "right:pref, 4dlu, left:pref:grow," +
                        "4dlu," +
                        "right:pref, 4dlu, left:pref:grow";
            }

            FormLayout layout = new FormLayout(layoutSpec);
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            builder.append("Username", txtUsername);
            builder.nextLine();

            if (isCreateNew) {
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
