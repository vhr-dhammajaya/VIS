package com.skopware.vdjvis.desktop;

import com.skopware.javautils.swing.BaseCrudFrame;
import com.skopware.javautils.swing.BasicCrudFrame;
import com.skopware.javautils.swing.MasterDetailFrame;
import com.skopware.javautils.swing.SwingHelper;

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
    private JMenuItem menuMasterAcara;
    private JMenuItem menuMasterUser;
    private JMenu menuSamanagara;
    private JMenuItem menuPendaftaranSamanagara;
    private JMenuItem menuDaftarLeluhur;
    private JMenuItem menuLokasiFoto;
    private JMenuItem menuSettingBiayaSamanagara;
    private JMenu menuPendapatanPengeluaran;
    private JMenuItem menuPendaftaranDanaRutin;
    private JMenuItem menuCatatDanaLain;
    private JMenuItem menuCatatPengeluaran;
    private JMenu menuAbsensi;
    private JMenuItem menuAbsensiUmat;
    private JMenu menuLaporan;
    private JMenuItem menuLaporanDanaRutin;

    private JDesktopPane desktopPane;
    private JInternalFrame frameMasterAcara;
    private JInternalFrame frameMasterUmat;
    private JInternalFrame frameMasterUser;
    private JInternalFrame frameMasterLeluhur;
    private JInternalFrame frameSetLokasiFoto;
    private JInternalFrame frameSettingIuranSamanagara;
    private JInternalFrame framePendaftaranDanaRutin;
    private JInternalFrame framePembayaranDanaSosialTetap;
    private JInternalFrame framePendapatanNonRutin;
    private JInternalFrame framePengeluaran;
    private JInternalFrame frameAbsensiUmat;
    private JInternalFrame frameLaporanDanaRutin;

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

        menuMasterAcara = new JMenuItem("Acara");
        menuMasterAcara.addActionListener((event) -> {
            showWindow("frameMasterAcara", () -> new BasicCrudFrame<>("Master Acara", GridAcara.createDefault()));
        });

        menuMasterUser = new JMenuItem("User");
        menuMasterUser.addActionListener((event) -> {
            showWindow("frameMasterUser", () -> new BasicCrudFrame<>("Master User", GridUser.createDefault()));
        });
        //#endregion

        //#region menuSamanagara child
        menuPendaftaranSamanagara = new JMenuItem("Pendaftaran samanagara");
        menuPendaftaranSamanagara.addActionListener(event -> {
            showWindow("frameMasterLeluhur", () -> new MasterDetailFrame<>("Pendaftaran leluhur Samanagara", "Pilih umat", "Daftar leluhur untuk umat yg dipilih", GridUmat.createForGridSamanagara(), GridLeluhur.createDefault(), "umat_id"));
        });

        menuDaftarLeluhur = new JMenuItem("Daftar leluhur");

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
        menuPendaftaranDanaRutin = new JMenuItem("Dana sosial & tetap");
        menuPendaftaranDanaRutin.addActionListener(e -> {
            showWindow("framePendaftaranDanaRutin", () -> new MasterDetailFrame<>("Dana sosial / tetap", "Pilih umat", "Daftar dana sosial & tetap untuk umat yg dipilih", GridUmat.createNoAddEditDelete(), GridPendaftaranDanaRutin.createDefault(), "umat_id"));
        });

        menuCatatDanaLain = new JMenuItem("Catat dana masuk lainnya");
        menuCatatDanaLain.addActionListener(e -> {
            showWindow("framePendapatanNonRutin", () -> new BasicCrudFrame<>("Dana masuk / pendapatan", GridPendapatan.create()));
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
        //#endregion

        //#region menuLaporan child
        menuLaporanDanaRutin = new JMenuItem("Status iuran samanagara, dana sosial & tetap");
        menuLaporanDanaRutin.addActionListener(e -> {
            showWindow("frameLaporanDanaRutin", () -> new FrameLaporanDanaRutin());
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
        menuMaster.add(menuMasterAcara);
        menuMaster.add(menuMasterUser);

        menuSamanagara.add(menuPendaftaranSamanagara);
        menuSamanagara.add(menuLokasiFoto);
        menuSamanagara.add(menuSettingBiayaSamanagara);

        menuPendapatanPengeluaran.add(menuPendaftaranDanaRutin);
        menuPendapatanPengeluaran.add(menuCatatDanaLain);
        menuPendapatanPengeluaran.add(menuCatatPengeluaran);

        menuAbsensi.add(menuAbsensiUmat);

        menuLaporan.add(menuLaporanDanaRutin);

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
                            if (ex instanceof RuntimeException) {
                                throw (RuntimeException) ex;
                            }

                            throw new RuntimeException(ex);
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
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new RuntimeException(e);
        }
    }
}
