package com.skopware.vdjvis.desktop.master;

import com.skopware.javautils.DateTimeHelper;
import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.Tuple2;
import com.skopware.javautils.swing.BaseCrudForm;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.datasource.JdbiDataSource;
import com.skopware.vdjvis.api.entities.Siswa;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.jdbi.dao.SiswaDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;

public class GridSiswa {
    public static JDataGridOptions<Siswa> createDefaultOptions() {
        JDataGridOptions<Siswa> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "nama";
                    x.dbColumnName = "nama";
                    x.label = "Nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "namaAyah";
                    x.dbColumnName = "nama_ayah";
                    x.label = "Nama Ayah";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "namaIbu";
                    x.dbColumnName = "nama_ibu";
                    x.label = "Nama Ibu";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "alamat";
                    x.dbColumnName = "alamat";
                    x.label = "Alamat";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "kota";
                    x.dbColumnName = "kota";
                    x.label = "Kota";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "noTelpon";
                    x.dbColumnName = "no_telpon";
                    x.label = "No. telpon";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglLahir";
                    x.dbColumnName = "tgl_lahir";
                    x.label = "Tgl lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "sekolah";
                    x.dbColumnName = "sekolah";
                    x.label = "Sekolah di";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "alamatSekolah";
                    x.dbColumnName = "alamat_sekolah";
                    x.label = "Alamat sekolah";
                })
        );

        o.appConfig = App.config;
        o.dataSource = new JdbiDataSource<Siswa, SiswaDAO>(Siswa.class, App.jdbi, "siswa", SiswaDAO.class) {
            @Override
            public Siswa createRecord(Siswa x) {
                return jdbi.withHandle(handle -> {
                    int ymTglDaftar = DateTimeHelper.computeMySQLYearMonth(x.tglDaftar);
                    int seqNum = handle.select("select count(*) from siswa where extract(year_month from tgl_daftar) = ?", ymTglDaftar)
                            .mapTo(int.class)
                            .findOnly();
                    seqNum++;

                    x.idBarcode = String.format("%s%02d%02d%02d%05d",
                            String.valueOf(x.tglDaftar.getYear()).substring(2), x.tglDaftar.getMonthValue(),
                            x.tglLahir.getDayOfMonth(), x.tglLahir.getMonthValue(),
                            seqNum);

                    SiswaDAO dao = handle.attach(daoClass);
                    dao.create(x);
                    Siswa result = dao.get(x.getUuid());
                    return result;
                });
            }
        };

        o.fnShowCreateForm = () -> new FormSiswa(App.mainFrame);
        o.fnShowEditForm = (record, modelIdx) -> new FormSiswa(App.mainFrame, record, modelIdx);

        return o;
    }

    public static class FormSiswa extends BaseCrudForm<Siswa> {
        private JTextField edNama;

        private JTextField edNamaAyah;
        private JTextField edNamaIbu;

        private JTextArea edAlamat;
        private JTextField edKota;

        private JTextField edNoTelpon;

        private JTextField edTempatLahir;
        private JDatePicker edTglLahir;

        private JTextField edSekolah;
        private JTextArea edAlamatSekolah;

        public FormSiswa(Frame owner) {
            super(owner, "Buat siswa", Siswa.class);
        }

        public FormSiswa(Frame owner, Siswa record, int modelIdx) {
            super(owner, "Edit siswa", Siswa.class, record, modelIdx);
        }

        @Override
        protected void initFormFields() {
            edNama = new JTextField(20);

            edNamaAyah = new JTextField(20);
            edNamaIbu = new JTextField(20);

            edAlamat = new JTextArea(5, 20);
            edKota = new JTextField(20);

            edNoTelpon = new JTextField(25);

            edTempatLahir = new JTextField(20);
            edTglLahir = new JDatePicker();

            edSekolah = new JTextField(20);
            edAlamatSekolah = new JTextArea(5, 20);

            pnlFormFields = SwingHelper.buildForm2(
                    Arrays.asList(
                            Arrays.asList(new Tuple2<>("Nama", edNama)),
                            Arrays.asList(new Tuple2<>("Nama ayah", edNamaAyah), new Tuple2<>("Nama ibu", edNamaIbu)),
                            Arrays.asList(new Tuple2<>("Alamat", edAlamat)),
                            Arrays.asList(new Tuple2<>("Kota", edKota)),
                            Arrays.asList(new Tuple2<>("No. telpon", edNoTelpon)),
                            Arrays.asList(new Tuple2<>("Tempat lahir", edTempatLahir), new Tuple2<>("Tgl lahir", edTglLahir)),
                            Arrays.asList(new Tuple2<>("Sekolah", edSekolah)),
                            Arrays.asList(new Tuple2<>("Alamat sekolah", edAlamatSekolah))
                    )
            );
        }

        @Override
        protected void syncModelToGui() {
            Siswa r = this.editedRecord;

            edNama.setText(r.nama);

            edNamaAyah.setText(r.namaAyah);
            edNamaIbu.setText(r.namaIbu);

            edAlamat.setText(r.alamat);
            edKota.setText(r.kota);

            edNoTelpon.setText(r.noTelpon);

            edTempatLahir.setText(r.tempatLahir);
            edTglLahir.setDate(r.tglLahir);

            edSekolah.setText(r.sekolah);
            edAlamatSekolah.setText(r.alamatSekolah);
        }

        @Override
        protected boolean validateFormFields() {
            return SwingHelper.validateFormFields(this,
                    new Tuple2<>(edNama.getText().isEmpty(), "Nama tidak boleh kosong"),
                    new Tuple2<>(edNamaAyah.getText().isEmpty(), "Nama ayah tidak boleh kosong"),
                    new Tuple2<>(edNamaIbu.getText().isEmpty(), "Nama ibu tidak boleh kosong"),
                    new Tuple2<>(edAlamat.getText().isEmpty() || edKota.getText().isEmpty(), "Alamat & kota tidak boleh kosong"),
                    new Tuple2<>(edNoTelpon.getText().isEmpty(), "No telpon tidak boleh kosong"),
                    new Tuple2<>(edTglLahir.getDate() == null, "Tgl lahir tidak boleh kosong")
            );
        }

        @Override
        protected void syncGuiToModel() {
            Siswa r = this.editedRecord;

            r.nama = edNama.getText();

            r.namaAyah = edNamaAyah.getText();
            r.namaIbu = edNamaIbu.getText();

            r.alamat = edAlamat.getText();
            r.kota = edKota.getText();

            r.noTelpon = edNoTelpon.getText();

            r.tempatLahir = edTempatLahir.getText();
            r.tglLahir = edTglLahir.getDate();

            r.sekolah = edSekolah.getText();
            r.alamatSekolah = edAlamatSekolah.getText();

            if (isCreateNew) {
                r.tglDaftar = LocalDate.now();
            }
        }
    }
}
