package com.gmail.mooman219.pokemongo;

import com.gmail.mooman219.pokemongo.handler.AuthentcationHandler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class WebServer {

    /**
     * The port the web server will run on.
     */
    public final int port;
    /**
     * The base address of the web server.
     */
    public final String address;
    /**
     * The authentication handler for the server.
     */
    public final AuthentcationHandler authenticationHandler;

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

    /**
     * Encodes the given value to be used in as a query parameter.
     *
     * @param value the value to encode.
     * @return the encoded value, empty string if UTF-8 isn't support on the
     * platform.
     */
    public static String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error: UTF-8 encoding is not supported on this platform.");
            return "";
        }
    }

}
