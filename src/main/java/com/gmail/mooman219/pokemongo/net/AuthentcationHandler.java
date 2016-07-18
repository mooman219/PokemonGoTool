package com.gmail.mooman219.pokemongo.net;

import com.gmail.mooman219.pokemongo.UserToken;
import com.gmail.mooman219.pokemongo.WebServer;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class AuthentcationHandler implements Handler<RoutingContext> {

    /**
     * The fully qualified address with the route included.
     */
    private final String address;

    /**
     * The route this handler lives.
     */
    private final String route;

    /**
     * The last successful authorization made, null if one hasn't been made yet.
     */
    private UserToken lastToken = null;

    /**
     * Creates a new AuthentcationHandler.
     *
     * @param address the fully qualified address with the route included.
     * @param route the route this handler lives.
     */
    public AuthentcationHandler(String address, String route) {
        this.address = address + route;
        this.route = route;
    }

    /**
     * Gets the fully qualified address with the route included.
     *
     * @return the fully qualified address with the route included.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the route this handler lives.
     *
     * @return the route this handler lives.
     */
    public String getRoute() {
        return route;
    }

    /**
     * Gets the last successful authorization made on the web server. This may
     * be null if a successful authorization hasn't been made yet.
     *
     * @return the last successful authorization made, null if one hasn't been
     * made yet.
     */
    public UserToken getLastToken() {
        return lastToken;
    }

    /**
     * Handles authentication for the server.
     *
     * @param context the routing context.
     */
    @Override
    public void handle(RoutingContext context) {
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
            /**
             * Try to parse the one time use code to create the UserToken.
             */
            UserToken auth = UserToken.createUserToken(req.params().get("code"), this.address);
            res.putHeader("content-type", "text/html");
            if (auth != null) {
                this.lastToken = auth;
                res.end("<b>Success</b>, authenticated with the server.\nYou may close this window now.");
            } else {
                res.end("<b>Error</b>, unable to authenticate with the server. <a href=" + this.address + ">Click here to try again.</a>");
            }
        } else {
            /**
             * Redirect to the google auth service to generate the one time use
             * code needed for the UserToken.
             */
            res.setStatusCode(302);
            res.putHeader("Location", UserToken.URL_GOOGLE_AUTH
                    + "client_id=" + UserToken.CLIENT_ID
                    + "&redirect_uri=" + WebServer.encodeUrl(this.address) // We need to encode because this is a get parameter.
                    + "&response_type=code"
                    + "&scope=openid%20email%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
            res.end();
        }
    }

}
