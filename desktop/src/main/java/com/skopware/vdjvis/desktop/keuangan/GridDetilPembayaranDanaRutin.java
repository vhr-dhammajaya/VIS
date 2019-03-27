package com.skopware.vdjvis.desktop.keuangan;

import java.util.Arrays;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.desktop.App;

public class GridDetilPembayaranDanaRutin {
    public static JDataGridOptions<DetilPembayaranDanaRutin> createBaseOptions() {
        JDataGridOptions<DetilPembayaranDanaRutin> o = new JDataGridOptions<>();

        o.columnConfigs = Arrays.asList(
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Jenis dana";
                    x.fieldName = "jenis";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Nama leluhur (ut Samanagara)";
                    x.fieldName = "leluhurSamanagara.nama";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Pembayaran untuk bulan";
                    x.fieldName = "untukBulan";
                }),
                ObjectHelper.apply(new BaseCrudTableModel.ColumnConfig(), x -> {
                    x.label = "Nominal";
                    x.fieldName = "nominal";
                })
        );

        o.enableAdd = o.enableEdit = o.enableDelete = o.enableFilter = false;
        o.recordType = DetilPembayaranDanaRutin.class;
        o.appConfig = App.config;
        o.shortControllerUrl = "/detil_pembayaran_dana_rutin";
        return o;
    }
}
