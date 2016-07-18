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
     * Niantic client ID.
     */
    public static final String CLIENT_ID = "848232511240-73ri3t7plvk96pj4f85uj8otdat2alem.apps.googleusercontent.com";
    /**
     * Niantic client secret.
     */
    public static final String CLIENT_SECRET = "NCjF1TLi2CcY6t5mt0ZveuL7";
    /**
     * Google authentication URL for getting the code. The GET query parameters
     * are pre-populated as they're constant. The {@code redirect_uri} routes to
     * {@code DIR_AUTH_CODE}.
     */
    public static final String URL_GOOGLE_CODE = "https://accounts.google.com/o/oauth2/auth?"
            + "client_id=" + CLIENT_ID
            + "&redirect_uri=" + encode(URL_BASE + DIR_AUTH)
            + "&response_type=code"
            + "&scope=openid%20email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email";
    /**
     * Google authentication URL for getting the token.
     */
    public static final String URL_GOOGLE_TOKEN = "https://accounts.google.com/o/oauth2/token";

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
                 * valid or else Google will respond with a 400.
                 */
                res.putHeader("content-type", "text/plain");
                res.end("Post Received");
            } else if (req.params().contains("code")) {
                Authorization auth = Authorization.createAutorization(req.params().get("code"));
                res.putHeader("content-type", "text/plain");
                res.end("Token: " + auth == null ? "Failed" : auth.toString());
            } else {
                res.setStatusCode(302);
                res.putHeader("Location", URL_GOOGLE_CODE);
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
