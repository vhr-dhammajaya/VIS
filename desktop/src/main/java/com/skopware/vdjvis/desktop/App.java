package com.skopware.vdjvis.desktop;

import com.skopware.javautils.ExceptionHelper;
import com.skopware.javautils.swing.BaseCrudAppConfig;
import com.skopware.javautils.swing.JDatePicker;
import com.skopware.vdjvis.api.entities.*;
import com.skopware.vdjvis.jdbi.rowmappers.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.swing.*;
import java.awt.*;

public class App {
    public static boolean loggedIn = false;
    public static User currentUser;
    
    public static BaseCrudAppConfig config;
    public static MainFrame mainFrame;
    public static DialogLogin formLogin;
    public static Jdbi jdbi;

    public static void main(String[] args) {
        config = new BaseCrudAppConfig();

        jdbi = Jdbi.create(config.serverUrl, "root", "T3kn0s!123"); // todo read from properties
        jdbi.installPlugin(new SqlObjectPlugin());

        jdbi.registerRowMapper(Acara.class, new AcaraRowMapper());
        jdbi.registerRowMapper(Umat.class, new UmatRowMapper());
        jdbi.registerRowMapper(Siswa.class, new SiswaRowMapper());
        jdbi.registerRowMapper(User.class, new UserRowMapper());

        jdbi.registerRowMapper(Leluhur.class, new LeluhurRowMapper());
        jdbi.registerRowMapper(TarifSamanagara.class, new TarifSamanagaraRowMapper());

        jdbi.registerRowMapper(PendaftaranDanaRutin.class, new PendaftaranDanaRutinRowMapper());
        jdbi.registerRowMapper(PembayaranDanaRutin.class, new PembayaranDanaRutinRowMapper());
        jdbi.registerRowMapper(DetilPembayaranDanaRutin.class, new DetilPembayaranDanaRutinRowMapper());
        jdbi.registerRowMapper(Pendapatan.class, new PendapatanRowMapper());
        jdbi.registerRowMapper(Pengeluaran.class, new PengeluaranRowMapper());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ExceptionHelper.rethrowSilently(ex);
        }

        SwingUtilities.invokeLater(() -> {
            formLogin = new DialogLogin();
            formLogin.setVisible(true);
            formLogin.pack();
        });
    }
    
    public static void login(User u) {
        currentUser = u;

        formLogin.setVisible(false);

        mainFrame = new MainFrame();
        JDatePicker.popupOwner = mainFrame;
        mainFrame.setVisible(true);
        mainFrame.pack();
        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        
        loggedIn = true;
    }

    public static void logout() {
        formLogin.setVisible(true);

        mainFrame.dispose();

        loggedIn = false;
        currentUser = null;
    }
}
