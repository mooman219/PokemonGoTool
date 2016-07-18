package com.gmail.mooman219.pokemongo;

import com.gmail.mooman219.pokemongo.bus.EventBus;
import com.gmail.mooman219.pokemongo.util.WebUtil;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class PokemonGoTool {

    private static PokemonGoTool Instance;

    private final WebServer webServer;

    private final EventBus eventBus;

    private PokemonGoTool() {
        this.webServer = new WebServer(8891);
        this.eventBus = new EventBus();
    }

    private void start() {
        webServer.start();
        WebUtil.openWebpage(webServer.getAuthenticationHandler().getAddress());
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public static void main(String[] args) {
        PokemonGoTool.Instance = new PokemonGoTool();
        PokemonGoTool.Instance.start();
    }

    public static PokemonGoTool getInstance() {
        return Instance;
    }
}
