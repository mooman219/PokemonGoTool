package com.gmail.mooman219.pokemongo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.On;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Main {

    public static final int PORT = 8888;
    /**
     * The base URL for the web server.
     */
    public static final String URL_BASE = "http://localhost:" + PORT;
    /**
     * The internal route that handles authentication.
     */
    public static final String DIR_AUTH = "/Auth";
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

    public static void main(String[] args) {
        // Configure the port.
        On.port(PORT);
        
        // Handle the authentication.
        On.get(DIR_AUTH).plain(new ReqRespHandler() {
            @Override
            public Object execute(Req req, Resp resp) throws Exception {
                if (req.data().containsKey("code")) {
                    String code = (String) req.data().get("code");
                    return "Result: " + Authorization.CreateAutorization(code).toString();
                } else {
                    resp.redirect(URL_GOOGLE_CODE);
                    return "";
                }
            }
        });

        /**
         * When the token request is made, the redirect-uri needs to be valid or
         * else Google will respond with a 400.
         */
        On.post(DIR_AUTH).plain("Post Received");
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
