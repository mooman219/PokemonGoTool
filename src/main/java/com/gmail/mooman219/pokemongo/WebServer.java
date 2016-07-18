package com.gmail.mooman219.pokemongo;

import com.gmail.mooman219.pokemongo.handler.AuthentcationHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class WebServer {

    /**
     * The port the web server will run on.
     */
    private final int port;
    /**
     * The base address of the web server.
     */
    private final String address;
    /**
     * The authentication handler for the server.
     */
    private final AuthentcationHandler authenticationHandler;

    /**
     * Creates a new WebServer.
     *
     * @param port the port the web server will run on.
     */
    public WebServer(int port) {
        this.port = port;
        this.address = "http://localhost:" + port;
        this.authenticationHandler = new AuthentcationHandler(this.address, "/auth");
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public AuthentcationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    /**
     * Starts the WebServer.
     */
    public void start() {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        this.authenticationHandler.register(router);

        server.requestHandler(router::accept).listen(this.port);
    }
}
