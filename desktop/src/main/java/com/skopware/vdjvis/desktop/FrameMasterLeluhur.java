package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudInternalFrame;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.vdjvis.api.Leluhur;

import java.util.Arrays;
import java.util.List;

public class FrameMasterLeluhur extends BaseCrudInternalFrame<Leluhur> {
    public FrameMasterLeluhur(List<BaseCrudTableModel.ColumnConfig> columnConfigs) {
        super("Master Leluhur", columnConfigs, Leluhur.class, App.config);

        controllerUrl = App.config.url("/leluhur");
    }

    public static FrameMasterLeluhur create() {
        List<BaseCrudTableModel.ColumnConfig> columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = x.dbColumnName = "nama";
                    x.label = "Nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatLahir";
                    x.dbColumnName = "tempat_lahir";
                    x.label = "Tempat lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglLahir";
                    x.dbColumnName = "tgl_lahir";
                    x.label = "Tgl lahir";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tempatMati";
                    x.dbColumnName = "tempat_mati";
                    x.label = "Meninggal di";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglMati";
                    x.dbColumnName = "tgl_mati";
                    x.label = "Tgl meninggal";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "hubunganDgnUmat";
                    x.dbColumnName = "hubungan_dgn_umat";
                    x.label = "Hubungan dgn umat";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.fieldName = "tglDaftar";
                    x.dbColumnName = "tgl_daftar";
                    x.label = "Tgl daftar";
                })
        );

        return new FrameMasterLeluhur(columnConfigs);
    }

    @Override
    protected void showCreateForm() {

    }

    @Override
    protected void showEditForm(Leluhur record, int modelIdx) {

    }
}
