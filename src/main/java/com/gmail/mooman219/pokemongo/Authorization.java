package com.gmail.mooman219.pokemongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.gmail.mooman219.pokemongo.WebServer.DIR_AUTH;
import static com.gmail.mooman219.pokemongo.WebServer.URL_BASE;
import static com.gmail.mooman219.pokemongo.WebServer.encode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Authorization {

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

    /**
     * The token that can be sent to a Google API.
     */
    public final String accessToken;
    /**
     * Identifies the type of token returned. Currently, this field always has
     * the value Bearer.
     */
    public final String tokenType;
    /**
     * The remaining lifetime of the access token.
     */
    public final int expiresIn;
    /**
     * A JWT that contains identity information about the user that is digitally
     * signed by Google. If your request included an identity scope such as
     * openid, profile, or email.
     */
    public final String idToken;
    /**
     * A token that may be used to obtain a new access token, included by
     * default for installed applications. Refresh tokens are valid until the
     * user revokes access.
     */
    public final String refreshToken;

    /**
     * Represents an authorization response from Google.
     *
     * @param accessToken
     * @param tokenType
     * @param expiresIn
     * @param idToken
     * @param refreshToken
     */
    public Authorization(String accessToken, String tokenType, int expiresIn, String idToken, String refreshToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "Authorization{" + "accessToken=" + accessToken + ", tokenType=" + tokenType + ", expiresIn=" + expiresIn + ", idToken=" + idToken + ", refreshToken=" + refreshToken + '}';
    }

    /**
     * Creates a new Authorization from the refresh token of the current
     * Authorization. This performs a http request to the authentication
     * endpoint.
     *
     * @return an Authorization upon success, null if there was an issue with
     * the code.
     * @throws IOException
     */
    public Authorization refresh() {
        byte[] payload = ("grant_type=authorization_code"
                + "&refresh_token=" + this.refreshToken
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET).getBytes(StandardCharsets.UTF_8);

        Map<String, Object> res = queryAuthenticationApi(payload);
        if (res == null) {
            return null;
        }

        return new Authorization(
                (String) res.get("access_token"),
                (String) res.get("token_type"),
                (int) res.get("expires_in"),
                (String) res.get("id_token"),
                this.refreshToken);
    }

    /**
     * Creates a new authorization from the given code.
     *
     * @param code the one time use code used to create an authorization.
     * @return an Authorization upon success, null if there was an issue with
     * the code.
     * @throws IOException
     */
    public static Authorization createAutorization(String code) {
        byte[] payload = ("grant_type=authorization_code"
                + "&code=" + code
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET
                + "&redirect_uri=" + WebServer.URL_BASE + WebServer.DIR_AUTH).getBytes(StandardCharsets.UTF_8);

        Map<String, Object> res = queryAuthenticationApi(payload);
        if (res == null) {
            return null;
        }

        return new Authorization(
                (String) res.get("access_token"),
                (String) res.get("token_type"),
                (int) res.get("expires_in"),
                (String) res.get("id_token"),
                (String) res.get("refresh_token"));
    }

    /**
     * Queries the authentication endpoint with the payload.
     *
     * @param payload the post data to send to the authentication endpoint.
     * @return the mapped json response.
     * @throws IOException
     */
    private static Map<String, Object> queryAuthenticationApi(byte[] payload) {
        try {
            URL url = new URL(URL_GOOGLE_TOKEN);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            con.setRequestProperty("Content-Length", payload.length + "");
            con.setDoOutput(true);
            con.setDoInput(true);

            try (DataOutputStream output = new DataOutputStream(con.getOutputStream());) {
                output.write(payload);
            }

            if (con.getResponseCode() != 200) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(con.getInputStream(), Map.class);
        } catch (IOException ex) {
            System.out.println("Error, unable to create authorization: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}
