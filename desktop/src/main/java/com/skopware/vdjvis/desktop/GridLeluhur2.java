package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.Leluhur;

import java.util.Arrays;

public class GridLeluhur2 extends JDataGrid<Leluhur> {
    public static GridLeluhur2 create() {
        JDataGridOptions<Leluhur> options = new JDataGridOptions<>();
        options.enableAdd = options.enableEdit = options.enableDelete = false;

        options.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "penanggungJawab.nama";
                    x.dbColumnName = "umat_nama";
                    x.label = "Nama Penanggung Jawab";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nama";
                    x.label = "Nama Mendiang";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatLahir";
                    x.dbColumnName = "tempat_lahir";
                    x.label = "Tempat Lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglLahir";
                    x.dbColumnName = "tgl_lahir";
                    x.label = "Tgl Lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatMati";
                    x.dbColumnName = "tempat_mati";
                    x.label = "Meninggal di";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglMati";
                    x.dbColumnName = "tgl_mati";
                    x.label = "Tgl Meninggal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "hubunganDgnUmat";
                    x.dbColumnName = "hubungan_dgn_umat";
                    x.label = "Hubungan dgn penanggung jawab";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglDaftar";
                    x.dbColumnName = "tgl_daftar";
                    x.label = "Tgl daftar";
                })
        );

        options.recordType = Leluhur.class;
        options.appConfig = App.config;
        options.shortControllerUrl = "/leluhur2";

        return new GridLeluhur2(options);
    }

    public GridLeluhur2(JDataGridOptions<Leluhur> options) {
        super(options);
    }

    @Override
    protected void showCreateForm() {
    }

    @Override
    protected void showEditForm(Leluhur record, int modelIdx) {

    }
}
