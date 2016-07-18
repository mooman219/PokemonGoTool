package com.gmail.mooman219.pokemongo;

import java.awt.Desktop;
import java.net.URI;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Main {

    public static void main(String[] args) {
        WebServer server = new WebServer(8891);
        server.start();
        openWebpage(server.authenticationHandler.address);
    }

    /**
     * Attempts to open a web page on the platform this is being run on. This
     * does nothing if it isn't supported on the current platform.
     *
     * @param url the url to open.
     */
    public static void openWebpage(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
