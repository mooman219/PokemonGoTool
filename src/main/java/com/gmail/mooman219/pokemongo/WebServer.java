package com.gmail.mooman219.pokemongo;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class WebServer {

    public static final int PORT = 8888;
    /**
     * The base URL for the web server.
     */
    public static final String URL_BASE = "http://localhost:" + PORT;
    /**
     * The internal route that handles authentication.
     */
    public static final String DIR_AUTH = "/auth";
    /**
     * The last successful authorization made, null if one hasn't been made yet.
     */
    private Authorization lastAuth = null;

    /**
     * Gets the last successful authorization made on the web server. This may
     * be null if a successful authorization hasn't been made yet.
     *
     * @return the last successful authorization made, null if one hasn't been
     * made yet.
     */
    public Authorization getAuthorization() {
        return lastAuth;
    }

    /**
     * Starts the WebServer, listening on the default port.
     */
    public void start() {
        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/auth").handler(context -> {
            HttpServerResponse res = context.response();
            HttpServerRequest req = context.request();
            if (req.method() == HttpMethod.POST) {
                /**
                 * When the token request is made, the redirect-uri needs to be
                 * valid or else Google will respond with a 400. Therefore, we
                 * handle the post case differently.
                 */
                res.putHeader("content-type", "text/plain");
                res.end("Post Received");
            } else if (req.params().contains("code")) {
                Authorization auth = Authorization.createAutorization(req.params().get("code"));
                res.putHeader("content-type", "text/html");
                if (auth != null) {
                    this.lastAuth = auth;
                    res.end("<b>Success</b>, authenticated with the server.\nYou may close this window now.");
                } else {
                    res.end("<b>Error</b>, unable to authenticate with the server. <a href=" + URL_BASE + DIR_AUTH + ">Click here to try again.</a>");
                }
            } else {
                res.setStatusCode(302);
                res.putHeader("Location", Authorization.URL_GOOGLE_CODE);
                res.end();
            }
        });

        server.requestHandler(router::accept).listen(PORT);
    }

    /**
     * Encodes the given value to be used in as a query parameter.
     *
     * @param value the value to encode.
     * @return the encoded value, empty string if UTF-8 isn't support on the
     * platform.
     */
    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Error: UTF-8 encoding is not supported on this platform.");
            return "";
        }
    }

}
