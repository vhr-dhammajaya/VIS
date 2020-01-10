package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ExceptionHelper;
import com.skopware.javautils.swing.BaseCrudFrame;
import com.skopware.javautils.swing.BasicCrudFrame;
import com.skopware.javautils.swing.MasterDetailFrame;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.javautils.swing.grid.JDataGrid;
import com.skopware.vdjvis.api.entities.DetilPembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.PembayaranDanaRutin;
import com.skopware.vdjvis.api.entities.Pendapatan;
import com.skopware.vdjvis.api.entities.User;
import com.skopware.vdjvis.desktop.absensi.FrameAbsensiSiswa;
import com.skopware.vdjvis.desktop.absensi.FrameAbsensiUmat;
import com.skopware.vdjvis.desktop.keuangan.*;
import com.skopware.vdjvis.desktop.laporan.FrameLaporanAbsensiSiswa;
import com.skopware.vdjvis.desktop.laporan.FrameLaporanAbsensiUmat;
import com.skopware.vdjvis.desktop.laporan.FrameLaporanDanaRutin;
import com.skopware.vdjvis.desktop.laporan.FrameLaporanPemasukanPengeluaran;
import com.skopware.vdjvis.desktop.master.GridAcara;
import com.skopware.vdjvis.desktop.master.GridSiswa;
import com.skopware.vdjvis.desktop.master.GridUmat;
import com.skopware.vdjvis.desktop.master.GridUser;
import com.skopware.vdjvis.desktop.samanagara.FrameSetLokasiFoto;
import com.skopware.vdjvis.desktop.samanagara.GridLeluhur;
import com.skopware.vdjvis.desktop.samanagara.GridSettingTarifSamanagara;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public class MainFrame extends JFrame {
    private boolean loggedIn = false;

    private JMenuBar menuBar;

    private JMenu menuAccount;
    private JMenuItem menuGantiPassword;
    private JMenuItem menuLogout;

    private JMenu menuMaster;
    private JMenuItem menuMasterUmat;
    private JMenuItem menuMasterSiswa;
    private JMenuItem menuMasterAcara;
    private JMenuItem menuMasterUser;

    private JMenu menuSamanagara;
    private JMenuItem menuPendaftaranSamanagara;
    private JMenuItem menuLokasiFoto;
    private JMenuItem menuSettingBiayaSamanagara;

    private JMenu menuPendapatanPengeluaran;
    private JMenuItem menuPendaftaranDanaSosialTetap;
    private JMenuItem menuLihatHistoryPembayaranDanaRutin;
    private JMenuItem menuCatatDanaLain;
    private JMenuItem menuCatatPengeluaran;

    private JMenu menuAbsensi;
    private JMenuItem menuAbsensiUmat;
    private JMenuItem menuAbsensiSiswa;

    private JMenu menuLaporan;
    private JMenuItem menuLaporanDanaRutin;
    private JMenuItem menuLaporanPemasukanPengeluaran;
    private JMenuItem menuLaporanAbsensiUmat;
    private JMenuItem menuLaporanAbsensiSiswa;

    private JDesktopPane desktopPane;

    private JInternalFrame frameMasterAcara;
    private JInternalFrame frameMasterUmat;
    private JInternalFrame frameMasterSiswa;
    private JInternalFrame frameMasterUser;

    private JInternalFrame frameMasterLeluhur;
    private JInternalFrame frameSetLokasiFoto;
    private JInternalFrame frameSettingIuranSamanagara;

    private JInternalFrame framePendaftaranDanaSosialTetap;
    private JInternalFrame frameLihatHistoryPembayaranDanaRutin;
    private JInternalFrame framePendapatanNonRutin;
    private JInternalFrame framePengeluaran;

    private JInternalFrame frameAbsensiUmat;
    private JInternalFrame frameAbsensiSiswa;

    private JInternalFrame frameLaporanDanaRutin;
    private JInternalFrame frameLaporanPemasukanPengeluaran;
    private JInternalFrame frameLaporanAbsensiUmat;
    private JInternalFrame frameLaporanAbsensiSiswa;

    public MainFrame() {
        super("VIS VDJ");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();

        menuMaster = new JMenu("Master");
        menuSamanagara = new JMenu("Samanagara");
        menuPendapatanPengeluaran = new JMenu("Pendapatan & Pengeluaran");
        menuAbsensi = new JMenu("Absensi");
        menuLaporan = new JMenu("Laporan");
        menuAccount = new JMenu("Akun");

        //#region menuMaster child
        menuMasterUmat = new JMenuItem("Umat");
        menuMasterUmat.addActionListener((event) -> {
            showWindow("frameMasterUmat", () -> new BasicCrudFrame<>("Master Umat", GridUmat.createDefault()));
        });

        menuMasterSiswa = new JMenuItem("Siswa");
        menuMasterSiswa.addActionListener(e -> {
            showWindow("frameMasterSiswa", () -> new BasicCrudFrame<>("Master siswa", new JDataGrid<>(GridSiswa.createDefaultOptions())));
        });

        menuMasterAcara = new JMenuItem("Acara");
        menuMasterAcara.addActionListener((event) -> {
            showWindow("frameMasterAcara", () -> new BasicCrudFrame<>("Master Acara", GridAcara.createDefault()));
        });

        if (User.Type.PENGURUS.equals(App.currentUser.tipe)) {
            menuMasterUser = new JMenuItem("User");
            menuMasterUser.addActionListener((event) -> {
                showWindow("frameMasterUser", () -> new BasicCrudFrame<>("Master User", GridUser.createDefault()));
            });
        }
        //#endregion

        //#region menuSamanagara child
        menuPendaftaranSamanagara = new JMenuItem("Pendaftaran samanagara");
        menuPendaftaranSamanagara.addActionListener(event -> {
            showWindow("frameMasterLeluhur", () -> new MasterDetailFrame<>("Pendaftaran leluhur Samanagara", "Pilih umat", "Daftar leluhur untuk umat yg dipilih", GridUmat.createForGridSamanagara(), GridLeluhur.createDefault(), "umat_id"));
        });

        menuLokasiFoto = new JMenuItem("Lokasi foto");
        menuLokasiFoto.addActionListener(event -> {
            showWindow("frameSetLokasiFoto", () -> new FrameSetLokasiFoto());
        });

        menuSettingBiayaSamanagara = new JMenuItem("Setting biaya samanagara");
        menuSettingBiayaSamanagara.addActionListener(event -> {
            showWindow("frameSettingIuranSamanagara", () -> new BasicCrudFrame<>("Setting Iuran Samanagara", GridSettingTarifSamanagara.create()));
        });
        //#endregion

        //#region menuPendapatanPengeluaran child
        menuPendaftaranDanaSosialTetap = new JMenuItem("Dana sosial & tetap");
        menuPendaftaranDanaSosialTetap.addActionListener(e -> {
            showWindow("framePendaftaranDanaSosialTetap", () -> new MasterDetailFrame<>("Dana sosial / tetap", "Pilih umat", "Daftar dana sosial & tetap untuk umat yg dipilih", GridUmat.createNoAddEditDelete(), GridPendaftaranDanaSosialTetap.createDefault(), "umat_id"));
        });

        menuLihatHistoryPembayaranDanaRutin = new JMenuItem("Lihat history pembayaran samanagara, dana sosial & tetap");
        menuLihatHistoryPembayaranDanaRutin.addActionListener(e -> {
            showWindow("frameLihatHistoryPembayaranDanaRutin", () -> {
                JDataGrid<PembayaranDanaRutin> masterGrid = new JDataGrid<>(GridHistoryPembayaranDanaRutin.createForUser(App.currentUser));
                JDataGrid<DetilPembayaranDanaRutin> detailGrid = new JDataGrid<>(GridDetilPembayaranDanaRutin.createBaseOptions());
                return new MasterDetailFrame<>("History pembayaran iuran samanagara, dana sosial, dana tetap", "Pembayaran iuran samanagara, dana sosial & dana tetap", "Detil transaksi", masterGrid, detailGrid, "trx_id");
            });
        });

        menuCatatDanaLain = new JMenuItem("Catat dana masuk lainnya");
        menuCatatDanaLain.addActionListener(e -> {
            showWindow("framePendapatanNonRutin", () -> {
                JDataGrid<Pendapatan> grid = new JDataGrid<>(GridPendapatan.createForUser(App.currentUser));
                return new BasicCrudFrame<>("Dana masuk / pendapatan", grid);
            });
        });

        menuCatatPengeluaran = new JMenuItem("Catat pengeluaran");
        menuCatatPengeluaran.addActionListener(e -> {
            showWindow("framePengeluaran", () -> new BasicCrudFrame<>("Pengeluaran", GridPengeluaran.create()));
        });
        //#endregion

        //#region menuAbsensi child
        menuAbsensiUmat = new JMenuItem("Absensi umat");
        menuAbsensiUmat.addActionListener(e -> {
            showWindow("frameAbsensiUmat", () -> new FrameAbsensiUmat());
        });

        menuAbsensiSiswa = new JMenuItem("Absensi siswa");
        menuAbsensiSiswa.addActionListener(e -> {
            showWindow("frameAbsensiSiswa", () -> new FrameAbsensiSiswa());
        });
        //#endregion

        //#region menuLaporan child
        menuLaporanDanaRutin = new JMenuItem("Status iuran samanagara, dana sosial & tetap");
        menuLaporanDanaRutin.addActionListener(e -> {
            showWindow("frameLaporanDanaRutin", () -> new FrameLaporanDanaRutin());
        });

        menuLaporanPemasukanPengeluaran = new JMenuItem("Pemasukan & pengeluaran bulanan");
        menuLaporanPemasukanPengeluaran.addActionListener(e -> {
            showWindow("frameLaporanPemasukanPengeluaran", () -> new FrameLaporanPemasukanPengeluaran());
        });

        menuLaporanAbsensiUmat = new JMenuItem("Laporan absensi umat");
        menuLaporanAbsensiUmat.addActionListener(e -> {
            showWindow("frameLaporanAbsensiUmat", () -> new FrameLaporanAbsensiUmat());
        });

        menuLaporanAbsensiSiswa = new JMenuItem("Laporan absensi siswa");
        menuLaporanAbsensiSiswa.addActionListener(e -> {
            showWindow("frameLaporanAbsensiSiswa", () -> new FrameLaporanAbsensiSiswa());
        });
        //#endregion

        //#region menuAccount child
        menuGantiPassword = new JMenuItem("Ganti password");
        menuGantiPassword.addActionListener(event -> {
            FrameGantiPassword frame = new FrameGantiPassword();
            desktopPane.add(frame);
            frame.setVisible(true);
            frame.pack();
        });

        menuLogout = new JMenuItem("Logout");
        menuLogout.addActionListener(event -> {
            App.logout();
        });
        //#endregion

        menuMaster.add(menuMasterUmat);
        menuMaster.add(menuMasterSiswa);
        menuMaster.add(menuMasterAcara);

        if (User.Type.PENGURUS.equals(App.currentUser.tipe)) {
            menuMaster.add(menuMasterUser);
        }

        menuSamanagara.add(menuPendaftaranSamanagara);
        menuSamanagara.add(menuLokasiFoto);
        menuSamanagara.add(menuSettingBiayaSamanagara);

        menuPendapatanPengeluaran.add(menuPendaftaranDanaSosialTetap);
        menuPendapatanPengeluaran.add(menuLihatHistoryPembayaranDanaRutin);
        menuPendapatanPengeluaran.add(menuCatatDanaLain);
        menuPendapatanPengeluaran.add(menuCatatPengeluaran);

        menuAbsensi.add(menuAbsensiUmat);
        menuAbsensi.add(menuAbsensiSiswa);

        menuLaporan.add(menuLaporanDanaRutin);
        menuLaporan.add(menuLaporanPemasukanPengeluaran);
        menuLaporan.add(menuLaporanAbsensiUmat);
        menuLaporan.add(menuLaporanAbsensiSiswa);

        menuAccount.add(menuGantiPassword);
        menuAccount.add(menuLogout);

        menuBar.add(menuMaster);
        menuBar.add(menuSamanagara);
        menuBar.add(menuPendapatanPengeluaran);
        menuBar.add(menuAbsensi);
        menuBar.add(menuLaporan);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuAccount);

        setJMenuBar(menuBar);

        desktopPane = new JDesktopPane();
        setContentPane(desktopPane);
    }

    private <T extends JInternalFrame> void showWindow(String fieldName, Supplier<T> fnCreateWindow) {
        try {
            Field windowField = this.getClass().getDeclaredField(fieldName);
            windowField.setAccessible(true);
            T windowObj = (T) windowField.get(this);
            boolean needToCreate = windowObj == null;

            if (needToCreate) {
                windowObj = fnCreateWindow.get();
                windowField.set(this, windowObj);

                desktopPane.add(windowObj);

                windowObj.addInternalFrameListener(new InternalFrameAdapter() {
                    @Override
                    public void internalFrameClosed(InternalFrameEvent ev) {
                        try {
                            windowField.set(MainFrame.this, null);
                        }
                        catch (Exception ex) {
                            ExceptionHelper.rethrowSilently(ex);
                        }
                    }
                });
            }

            SwingHelper.moveToFrontAndMaximize(windowObj);

            if (needToCreate && windowObj instanceof BaseCrudFrame) {
                ((BaseCrudFrame) windowObj).refreshData();
            }
        }
        catch (Exception e) {
            ExceptionHelper.rethrowSilently(e);
        }
    }
}
