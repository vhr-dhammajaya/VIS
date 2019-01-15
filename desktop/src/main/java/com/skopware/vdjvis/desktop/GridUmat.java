package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.Umat;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridUmat extends JDataGrid<Umat> {
    public static GridUmat create() {
        JDataGridOptions<Umat> options = new JDataGridOptions<>();

        options.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("nama");
                    x.label = ("Nama");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("alamat");
                    x.label = ("Alamat");
                    x.fitContentWidth = false;
//                    x.width = 10;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("kota");
                    x.label = ("Kota");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("kodePos");
                    x.dbColumnName = "kode_pos";
                    x.label = ("Kode Pos");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("noTelpon");
                    x.dbColumnName = "no_telpon";
                    x.label = ("No. Telpon");
                    x.fitContentWidth = false;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("email");
                    x.label = ("Email");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("tempatLahir");
                    x.dbColumnName = "tempat_lahir";
                    x.label = ("Tempat Lahir");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("tglLahir");
                    x.dbColumnName = "tgl_lahir";
                    x.label = ("Tgl Lahir");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("golDarah");
                    x.dbColumnName = "gol_darah";
                    x.label = ("Gol Darah");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("jenisKelamin");
                    x.dbColumnName = "jenis_kelamin";
                    x.label = ("Jenis Kelamin");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("statusNikah");
                    x.dbColumnName = "status_nikah";
                    x.label = ("Status Nikah");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("pendidikanTerakhir");
                    x.dbColumnName = "pendidikan_terakhir";
                    x.label = ("Pendidikan");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("jurusan");
                    x.label = ("Jurusan");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("pekerjaan");
                    x.label = ("Pekerjaan");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("bidangUsaha");
                    x.dbColumnName = "bidang_usaha";
                    x.label = ("Bidang Usaha");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("namaKerabat");
                    x.dbColumnName = "nama_kerabat";
                    x.label = ("Nama Kerabat");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("alamatKerabat");
                    x.dbColumnName = "alamat_kerabat";
                    x.label = ("Alamat Kerabat");
                    x.fitContentWidth = false;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("kotaKerabat");
                    x.dbColumnName = "kota_kerabat";
                    x.label = ("Kota Kerabat");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("kodePosKerabat");
                    x.dbColumnName = "kode_pos_kerabat";
                    x.label = ("Kode Pos Kerabat");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("noTelpKerabat");
                    x.dbColumnName = "no_telp_kerabat";
                    x.label = ("No Telpon Kerabat");
                    x.fitContentWidth = false;
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("namaUpasaka");
                    x.dbColumnName = "nama_upasaka";
                    x.label = ("Nama Upasaka");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = ("penahbis");
                    x.label = ("Penahbis");
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = ("tglPenahbisan");
                    x.dbColumnName = "tgl_penahbisan";
                    x.label = ("Tgl Penahbisan");
                })
        );

        options.recordType = Umat.class;
        options.appConfig = App.config;
        options.shortControllerUrl = "/umat";

        return new GridUmat(options);
    }

    public GridUmat(JDataGridOptions<Umat> options) {
        super(options);
    }

    @Override
    protected void showCreateForm() {
        new FormUmat(App.mainFrame, "Buat Umat");
    }

    @Override
    protected void showEditForm(Umat record, int modelIdx) {
        new FormUmat(App.mainFrame, "Edit Umat", record, modelIdx);
    }

    public class FormUmat extends BaseCrudForm {
        private JTextField txtNama;
        private JTextArea txtAlamat;
        private JTextField txtKota;
        private JTextField txtKodePos;
        private JTextField txtNoTelpon;
        private JTextField txtEmail;
        private JTextField txtTempatLahir;
        private JDatePicker txtTglLahir;

        private ButtonGroup grpGolDarah;
        private JRadioButton btnGolDarahO;
        private JRadioButton btnGolDarahA;
        private JRadioButton btnGolDarahB;
        private JRadioButton btnGolDarahAB;

        private ButtonGroup grpJenisKelamin;
        private JRadioButton btnJenisKelaminPria;
        private JRadioButton btnJenisKelaminWanita;

        private JComboBox<String> cmbStatusNikah;

        private JTextField txtPendidikanTerakhir;
        private JTextField txtJurusan;
        private JTextField txtPekerjaan;
        private JTextField txtBidangUsaha;

        private JTextField txtNamaKerabat;
        private JTextArea txtAlamatKerabat;
        private JTextField txtKotaKerabat;
        private JTextField txtKodePosKerabat;
        private JTextField txtNoTelponKerabat;

        private JTextField txtNamaUpasaka;
        private JTextField txtNamaPenahbis;
        private JDatePicker txtTglPenahbisan;

        public FormUmat(Frame owner, String title) {
            super(owner, title);
        }

        public FormUmat(Frame owner, String title, Umat record, int modelIdx) {
            super(owner, title, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            txtNama = new JTextField(45);
            txtAlamat = new JTextArea(5, 40);
            txtKota = new JTextField(45);
            txtKodePos = new JTextField(45);
            txtNoTelpon = new JTextField(50);
            txtEmail = new JTextField(45);
            txtTempatLahir = new JTextField(45);
            txtTglLahir = new JDatePicker();

            btnGolDarahO = new JRadioButton("O");
            btnGolDarahA = new JRadioButton("A");
            btnGolDarahB = new JRadioButton("B");
            btnGolDarahAB = new JRadioButton("AB");
            grpGolDarah = new ButtonGroup();
            grpGolDarah.add(btnGolDarahO);
            grpGolDarah.add(btnGolDarahA);
            grpGolDarah.add(btnGolDarahB);
            grpGolDarah.add(btnGolDarahAB);

            btnJenisKelaminPria = new JRadioButton("Laki-2");
            btnJenisKelaminWanita = new JRadioButton("Perempuan");
            grpJenisKelamin = new ButtonGroup();
            grpJenisKelamin.add(btnJenisKelaminPria);
            grpJenisKelamin.add(btnJenisKelaminWanita);

            cmbStatusNikah = new JComboBox<>(new String[]{"Single", "Menikah", "Lainnya"});

            txtPendidikanTerakhir = new JTextField(45);
            txtJurusan = new JTextField(45);
            txtPekerjaan = new JTextField(45);
            txtBidangUsaha = new JTextField(45);

            txtNamaKerabat = new JTextField(45);
            txtAlamatKerabat = new JTextArea(5, 40);
            txtNoTelponKerabat = new JTextField(50);
            txtKotaKerabat = new JTextField(45);
            txtKodePosKerabat = new JTextField(45);
            txtNoTelpon = new JTextField(50);

            txtNamaUpasaka = new JTextField(45);
            txtNamaPenahbis = new JTextField(45);
            txtTglPenahbisan = new JDatePicker();

            FormLayout layout = new FormLayout("right:pref, 4dlu, left:pref:grow," +
                    "4dlu," +
                    "right:pref, 4dlu, left:pref:grow," +
                    "4dlu," +
                    "right:pref, 4dlu, left:pref:grow");
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            builder.border(Borders.DIALOG);

            builder.appendSeparator("Data pribadi");
            builder.append("Nama", txtNama);
            builder.nextLine();

            builder.append("Alamat", txtAlamat);
            builder.nextLine();

            builder.append("Kota", txtKota);
            builder.append("Kode Pos", txtKodePos);
            builder.nextLine();

            builder.append("No. Telpon", txtNoTelpon);
            builder.append("Email", txtEmail);
            builder.nextLine();

            builder.append("Tempat Lahir", txtTempatLahir);
            builder.append("Tanggal Lahir", txtTglLahir);
            builder.nextLine();

            builder.append("Nama Upasaka", txtNamaUpasaka);
            builder.append("Penahbis", txtNamaPenahbis);
            builder.append("Tgl Penahbisan", txtTglPenahbisan);
            builder.nextLine();

            JPanel pnlGolDarah = new JPanel(new FlowLayout());
            pnlGolDarah.add(btnGolDarahO);
            pnlGolDarah.add(btnGolDarahA);
            pnlGolDarah.add(btnGolDarahB);
            pnlGolDarah.add(btnGolDarahAB);

            JPanel pnlJenisKelamin = new JPanel(new FlowLayout());
            pnlJenisKelamin.add(btnJenisKelaminPria);
            pnlJenisKelamin.add(btnJenisKelaminWanita);

            builder.append("Gol. Darah", pnlGolDarah);
            builder.append("Sex", pnlJenisKelamin);
            builder.append("Status Nikah", cmbStatusNikah);
            builder.nextLine();

            builder.append("Pendidikan terakhir", txtPendidikanTerakhir);
            builder.append("Jurusan", txtJurusan);
            builder.nextLine();

            builder.append("Pekerjaan", txtPekerjaan);
            builder.append("Bidang Usaha", txtBidangUsaha);
            builder.nextLine();

            builder.appendSeparator("Data kerabat yg bisa dihubungi");
            builder.append("Nama", txtNamaKerabat);
            builder.nextLine();

            builder.append("Alamat", txtAlamatKerabat);
            builder.nextLine();

            builder.append("Kota", txtKotaKerabat);
            builder.append("Kode Pos", txtKodePosKerabat);
            builder.nextLine();

            builder.append("No. Telpon", txtNoTelponKerabat);

            pnlFormFields = builder.build();
        }

        @Override
        protected void syncModelToGui() {
            Umat r = this.editedRecord;
            txtNama.setText(r.nama);
            txtAlamat.setText(r.alamat);
            txtKota.setText(r.kota);
            txtKodePos.setText(r.kodePos);
            txtNoTelpon.setText(r.noTelpon);
            txtEmail.setText(r.email);
            txtTempatLahir.setText(r.tempatLahir);
            txtTglLahir.setDate(r.tglLahir);
            txtNamaUpasaka.setText(r.namaUpasaka);
            txtNamaPenahbis.setText(r.penahbis);
            txtTglPenahbisan.setDate(r.tglPenahbisan);

            if (r.golDarah != null) {
                switch (r.golDarah) {
                    case "O":
                        btnGolDarahO.setSelected(true);
                        break;
                    case "A":
                        btnGolDarahA.setSelected(true);
                        break;
                    case "B":
                        btnGolDarahB.setSelected(true);
                        break;
                    case "AB":
                        btnGolDarahAB.setSelected(true);
                        break;
                }
            }

            if (r.jenisKelamin != null) {
                switch (r.jenisKelamin) {
                    case "L":
                        btnJenisKelaminPria.setSelected(true);
                        break;
                    case "P":
                        btnJenisKelaminWanita.setSelected(true);
                        break;
                }
            }

            cmbStatusNikah.setSelectedItem(r.statusNikah);

            txtPendidikanTerakhir.setText(r.pendidikanTerakhir);
            txtJurusan.setText(r.jurusan);
            txtPekerjaan.setText(r.pekerjaan);
            txtBidangUsaha.setText(r.bidangUsaha);

            txtNamaKerabat.setText(r.namaKerabat);
            txtAlamatKerabat.setText(r.alamatKerabat);
            txtNoTelponKerabat.setText(r.noTelpKerabat);
            txtKotaKerabat.setText(r.kotaKerabat);
            txtKodePosKerabat.setText(r.kodePosKerabat);
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(
                    this,
                    new Tuple2<>(txtNama.getText().isEmpty(), "Nama tidak boleh kosong"),
                    new Tuple2<>(txtAlamat.getText().isEmpty() || txtKota.getText().isEmpty(), "Alamat & kota tidak boleh kosong"),
                    new Tuple2<>(txtNoTelpon.getText().isEmpty(), "No. telpon tidak boleh kosong"));
        }

        @Override
        protected void syncGuiToModel() {
            Umat r = this.editedRecord;
            r.nama = txtNama.getText();
            r.alamat = txtAlamat.getText();
            r.kota = txtKota.getText();
            r.kodePos = txtKodePos.getText();
            r.noTelpon = txtNoTelpon.getText();
            r.email = txtEmail.getText();
            r.tempatLahir = txtTempatLahir.getText();
            r.tglLahir = txtTglLahir.getDate();
            r.namaUpasaka = txtNamaUpasaka.getText();
            r.penahbis = txtNamaPenahbis.getText();
            r.tglPenahbisan = txtTglPenahbisan.getDate();
            r.golDarah = btnGolDarahO.isSelected() ? "O" : btnGolDarahA.isSelected() ? "A" : btnGolDarahB.isSelected() ? "B" : btnGolDarahAB.isSelected() ? "AB" : null;
            r.jenisKelamin = btnJenisKelaminPria.isSelected() ? "L" : btnJenisKelaminWanita.isSelected() ? "P" : null;
            r.statusNikah = (String) cmbStatusNikah.getSelectedItem();
            r.pendidikanTerakhir = txtPendidikanTerakhir.getText();
            r.jurusan = txtJurusan.getText();
            r.pekerjaan = txtPekerjaan.getText();
            r.bidangUsaha = txtBidangUsaha.getText();
            r.namaKerabat = txtNamaKerabat.getText();
            r.alamatKerabat = txtAlamatKerabat.getText();
            r.noTelpKerabat = txtNoTelponKerabat.getText();
            r.kotaKerabat = txtKotaKerabat.getText();
            r.kodePosKerabat = txtKodePosKerabat.getText();
        }
    }
}
