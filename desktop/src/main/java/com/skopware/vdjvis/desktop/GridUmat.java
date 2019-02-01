package com.skopware.vdjvis.desktop;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.*;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.Umat;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GridUmat {
    public static JDataGrid<Umat> createDefault() {
        return new JDataGrid<>(createDefaultOptions());
    }

    public static JDataGrid<Umat> createNoAddEditDelete() {
        JDataGridOptions<Umat> o = createDefaultOptions();
        o.enableAdd = o.enableEdit = o.enableDelete = false;
        return new JDataGrid<>(o);
    }

    public static JDataGridOptions<Umat> createDefaultOptions() {
        JDataGridOptions<Umat> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
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

        o.recordType = Umat.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/umat";

        o.fnShowCreateForm = () -> new FormUmat(App.mainFrame);
        o.fnShowEditForm = (record, modelIdx) -> new FormUmat(App.mainFrame, record, modelIdx);

        return o;
    }

    public static class FormUmat extends BaseCrudForm<Umat> {
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
        private EnumFormFieldMapper<String> mapperGolDarah;

        private ButtonGroup grpJenisKelamin;
        private JRadioButton btnJenisKelaminPria;
        private JRadioButton btnJenisKelaminWanita;
        private EnumFormFieldMapper<String> mapperJenisKelamin;

        private JComboBox<String> cmbStatusNikah;

        private JComboBox<String> txtPendidikanTerakhir;
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

        public FormUmat(Frame owner) {
            super(owner, "Buat umat", Umat.class);
        }

        public FormUmat(Frame owner, Umat record, int modelIdx) {
            super(owner, "Edit umat", Umat.class, record, modelIdx);
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
            mapperGolDarah = new EnumFormFieldMapper<>(
                    new Tuple2<>("O", btnGolDarahO),
                    new Tuple2<>("A", btnGolDarahA),
                    new Tuple2<>("B", btnGolDarahB),
                    new Tuple2<>("AB", btnGolDarahAB));

            btnJenisKelaminPria = new JRadioButton("Laki-2");
            btnJenisKelaminWanita = new JRadioButton("Perempuan");
            grpJenisKelamin = new ButtonGroup();
            grpJenisKelamin.add(btnJenisKelaminPria);
            grpJenisKelamin.add(btnJenisKelaminWanita);
            mapperJenisKelamin = new EnumFormFieldMapper<>(new Tuple2<>("L", btnJenisKelaminPria), new Tuple2<>("P", btnJenisKelaminWanita));

            cmbStatusNikah = new JComboBox<>(new String[]{"Single", "Menikah"});
            cmbStatusNikah.setEditable(true);

            txtPendidikanTerakhir = new JComboBox<>(new String[]{"S1", "S2", "S3", "D3", "SMA", "Lainnya (ketik)"});
            txtPendidikanTerakhir.setEditable(true);

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

            mapperGolDarah.modelToGui(r.golDarah);
            mapperJenisKelamin.modelToGui(r.jenisKelamin);
            cmbStatusNikah.setSelectedItem(r.statusNikah);

            txtPendidikanTerakhir.setSelectedItem(r.pendidikanTerakhir);
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
            r.golDarah = mapperGolDarah.guiToModel();
            r.jenisKelamin = mapperJenisKelamin.guiToModel();
            r.statusNikah = (String) cmbStatusNikah.getSelectedItem();
            r.pendidikanTerakhir = (String) txtPendidikanTerakhir.getSelectedItem();
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
