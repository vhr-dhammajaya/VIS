package com.skopware.vdjvis.desktop;

import com.skopware.javautils.swing.BaseCrudFrame;
import com.skopware.javautils.swing.BasicCrudFrame;
import com.skopware.javautils.swing.MasterDetailFrame;
import com.skopware.javautils.swing.SwingHelper;
import com.skopware.vdjvis.api.Acara;
import com.skopware.vdjvis.api.Umat;
import com.skopware.vdjvis.api.User;

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
    private JMenuItem menuCatatDana;
    private JMenuItem menuCatatPengeluaran;
    private JMenu menuAbsensi;
    private JMenu menuLaporan;

    private JDesktopPane desktopPane;
    private BaseCrudFrame frameMasterAcara;
    private BaseCrudFrame frameMasterUmat;
    private BaseCrudFrame frameMasterUser;
    private BaseCrudFrame frameMasterLeluhur;
    private BaseCrudFrame frameSetLokasiFoto;
    private BaseCrudFrame frameSettingIuranSamanagara;

    public MainFrame() {
        super("VIS VDJ");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        menuMaster = new JMenu("Master");
        menuBar.add(menuMaster);

        menuMasterUmat = new JMenuItem("Umat");
        menuMasterUmat.addActionListener((event) -> {
            showWindow("frameMasterUmat", () -> new BasicCrudFrame<>("Master Umat", GridUmat.create()));
        });
        menuMasterAcara = new JMenuItem("Acara");
        menuMasterAcara.addActionListener((event) -> {
            showWindow("frameMasterAcara", () -> new BasicCrudFrame<>("Master Acara", GridAcara.create()));
        });
        menuMasterUser = new JMenuItem("User");
        menuMasterUser.addActionListener((event) -> {
            showWindow("frameMasterUser", () -> new BasicCrudFrame<>("Master User", GridUser.create()));
        });

        menuMaster.add(menuMasterUmat);
        menuMaster.add(menuMasterAcara);
        menuMaster.add(menuMasterUser);

        menuSamanagara = new JMenu("Samanagara");
        menuBar.add(menuSamanagara);
        menuPendaftaranSamanagara = new JMenuItem("Pendaftaran samanagara");
        menuPendaftaranSamanagara.addActionListener(event -> {
            showWindow("frameMasterLeluhur", () -> new MasterDetailFrame<>("Pendaftaran leluhur Samanagara", GridUmat.create(), GridLeluhur.create()));
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

        menuSamanagara.add(menuPendaftaranSamanagara);
//        menuSamanagara.add(menuDaftarLeluhur);
        menuSamanagara.add(menuLokasiFoto);
        menuSamanagara.add(menuSettingBiayaSamanagara);

        menuPendapatanPengeluaran = new JMenu("Pendapatan & Pengeluaran");
        menuPendaftaranDanaRutin = new JMenuItem("Pendaftaran dana sosial & tetap");
        menuCatatDana = new JMenuItem("Catat dana masuk");
        menuCatatPengeluaran = new JMenuItem("Catat pengeluaran");
        menuPendapatanPengeluaran.add(menuPendaftaranDanaRutin);
        menuPendapatanPengeluaran.add(menuCatatDana);
        menuPendapatanPengeluaran.add(menuCatatPengeluaran);
        menuBar.add(menuPendapatanPengeluaran);

        menuAbsensi = new JMenu("Absensi");
        menuBar.add(menuAbsensi);

        menuLaporan = new JMenu("Laporan");
        menuBar.add(menuLaporan);

        menuBar.add(Box.createHorizontalGlue());

        menuAccount = new JMenu("Akun");
        menuBar.add(menuAccount);
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

        menuAccount.add(menuGantiPassword);
        menuAccount.add(menuLogout);

        desktopPane = new JDesktopPane();
        setContentPane(desktopPane);
    }

    private <T extends BaseCrudFrame> void showWindow(String fieldName, Supplier<T> fnCreateWindow) {
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

            if (needToCreate) {
                windowObj.refreshData();
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
