package com.skopware.vdjvis.desktop.keuangan;

import java.util.Arrays;

import com.skopware.javautils.ObjectHelper;
import com.skopware.javautils.db.PageData;
import com.skopware.javautils.swing.BaseCrudTableModel;
import com.skopware.javautils.swing.grid.GridConfig;
import com.skopware.javautils.swing.grid.JDataGridHelper;
import com.skopware.javautils.swing.grid.JDataGridOptions;
import com.skopware.javautils.swing.grid.datasource.JdbiDataSource;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.desktop.App;
import com.skopware.vdjvis.jdbi.dao.DetilPembayaranDanaRutinDAO;

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
        o.appConfig = App.config;
        o.dataSource = new JdbiDataSource<DetilPembayaranDanaRutin, DetilPembayaranDanaRutinDAO>(DetilPembayaranDanaRutin.class, App.jdbi, "v_detil_pembayaran_dana_rutin", DetilPembayaranDanaRutinDAO.class) {

			@Override
			public PageData<DetilPembayaranDanaRutin> refreshData(GridConfig gridConfig) {
				return jdbi.withHandle(h -> JDataGridHelper.fetchPageData(h, tableNameForSelect, gridConfig, recordType, false));
			}
        	
        };
        return o;
    }
}
