package com.gmail.mooman219.pokemongo;

import com.gmail.mooman219.pokemongo.net.AuthentcationHandler;
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
    private final int port;
    /**
     * The base address of the web server.
     */
    private final String address;

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

    /**
     * Gets the port the server is running on.
     *
     * @return the port the server is running on.
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the address the server is running on.
     *
     * @return the address the server is running on.
     */
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

        router.route(this.authenticationHandler.getRoute()).handler(this.authenticationHandler);

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
