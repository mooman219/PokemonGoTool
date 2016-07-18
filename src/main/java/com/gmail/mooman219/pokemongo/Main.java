package com.gmail.mooman219.pokemongo;

import java.awt.Desktop;
import java.net.URI;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Main {


    public static void main(String[] args) {
        new WebServer().start();
        openWebpage(WebServer.URL_BASE + WebServer.DIR_AUTH);
    }

    public static void openWebpage(String uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(uri));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
