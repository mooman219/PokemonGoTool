package com.gmail.mooman219.pokemongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Joseph Cumbo (mooman219)
 */
public class Authorization {

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
    public Authorization(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("expires_in") int expiresIn,
            @JsonProperty("id_token") String idToken,
            @JsonProperty("refresh_token") String refreshToken) {
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
     * Creates a new authorization from the given code.
     *
     * @param code the one time use code used to create an authorization.
     * @return an Authorization upon success, null if there was an issue with
     * the code.
     * @throws IOException
     */
    public static Authorization CreateAutorization(String code) throws IOException {
        byte[] payload = ("grant_type=authorization_code"
                + "&code=" + code
                + "&client_id=" + Main.CLIENT_ID
                + "&client_secret=" + Main.CLIENT_SECRET
                + "&redirect_uri=" + Main.URL_BASE + Main.DIR_AUTH).getBytes(StandardCharsets.UTF_8);

        URL myurl = new URL(Main.URL_GOOGLE_TOKEN);
        HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        con.setRequestProperty("Content-Length", payload.length + "");
        con.setDoOutput(true);
        con.setDoInput(true);

        try (DataOutputStream output = new DataOutputStream(con.getOutputStream());) {
            output.write(payload);
        }

        System.out.println(con.getResponseCode());
        System.out.println(con.getResponseMessage());
        if (con.getResponseCode() != 200) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(con.getInputStream(), Authorization.class);
    }
}
