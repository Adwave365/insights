package com.adwave.client.oauth;

import com.adwave.client.insights.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.misc.Regexp;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexboyce on 7/4/16.
 */
public class OAuth2 {

    final static String GRANT_TYPE_CODE = "authorization_code";
    final static String GRANT_TYPE_PASSWORD = "password";
    final static String GRANT_TYPE_REFRESH = "refresh_token";

    public static class Client {
        private String id;
        private String secret;
        private URL tokenUrl;
        private URL redirectUrl;
        private String token;
        private String refresh_token;
        private ZonedDateTime expires;

        public Client() {
            try {
                tokenUrl = new URL("https://insights.adwave365.com/token");
            } catch (MalformedURLException e) {
                System.err.println("The token URL is malformed");
            }
        }

        public String getId() {
            return id;
        }

        public Client setId(String id) {
            this.id = id;

            return this;
        }

        public String getSecret() {
            return secret;
        }

        public Client setSecret(String secret) {
            this.secret = secret;

            return this;
        }

        public URL getTokenUrl() {
            return tokenUrl;
        }

        public Client setTokenUrl(String tokenUrl) throws MalformedURLException {
            this.tokenUrl = new URL(tokenUrl);

            return this;
        }

        public Client setTokenUrl(URL tokenUrl) {
            this.tokenUrl = tokenUrl;

            return this;
        }

        public URL getRedirectUrl() {
            return redirectUrl;
        }

        public Client setRedirectUrl(URL redirectUrl) {
            this.redirectUrl = redirectUrl;

            return this;
        }

        public Client setRedirectUrl(String redirectUrl) throws MalformedURLException {
            this.redirectUrl = new URL(redirectUrl);

            return this;
        }

        public URL generateAuthorizeUrl(String username, String password) throws MalformedURLException {
            return new URL(String.format("%s?client_id=%s&client_secret=%s&grant_type=%s&response_type=token&username=%s&password=%s",
                    tokenUrl,
                    id,
                    secret,
                    GRANT_TYPE_PASSWORD,
                    username,
                    password
            ));
        }

        public OAuthTokenResponse authorize(String username, String password) throws IOException, OAuthException {
            OAuthTokenResponse response =  new OAuthTokenRequest(generateAuthorizeUrl(username, password)).send();

            token = response.getAccessToken();
            refresh_token = response.getRefreshToken();
            expires = ZonedDateTime.now().plusMinutes(response.getExpires());

            return response;
        }

        public OAuthTokenResponse authorize(String authCode) throws IOException, OAuthException {
            OAuthTokenResponse response = new OAuthTokenRequest(tokenUrl)
                    .setData(new OAuthPayload()
                            .setCode(authCode)
                            .setPublicId(id)
                            .setSecret(secret)
                            .setRedirectUri(redirectUrl.toString())
                    )
                    .send()
                    ;

            token = response.getAccessToken();
            refresh_token = response.getRefreshToken();
            expires = ZonedDateTime.now().plusMinutes(response.getExpires());

            return response;
        }

        public String getToken() {
            return token;
        }

        public ZonedDateTime getExpires() {
            return expires;
        }

        public String getRefreshToken() {
            return refresh_token;
        }

        public OAuthTokenResponse refreshToken(String refreshToken) throws IOException {
            OAuthTokenResponse response = new OAuthTokenRequest(tokenUrl)
                    .setData(new OAuthPayload()
                            .setRefreshToken(refreshToken)
                            .setPublicId(id)
                            .setSecret(secret)
                            .setRedirectUri(redirectUrl.toString())
                            .setGrantType(OAuth2.GRANT_TYPE_REFRESH)
                    )
                    .send()
                    ;

            token = response.getAccessToken();
            refresh_token = response.getRefreshToken();
            expires = ZonedDateTime.now().plusMinutes(response.getExpires());

            return response;
        }

        public List<?> send(List<? extends Entity> objects, URL edge, String method) throws IOException {
            ObjectMapper mapper = new ObjectMapper();

            if (ZonedDateTime.now().isAfter(expires)) {
                refreshToken(refresh_token);
            }

            HttpURLConnection connection = prepareConnection(edge, method);

            if (!method.equals("GET")) {
                OutputStream os = connection.getOutputStream();

                mapper.writeValue(os, objects);
                os.flush();
                os.close();
            }

            connection.connect();

            List<Integer> ids = new LinkedList<Integer>();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                if (connection.getHeaderFields().containsKey("Link")) {
                    Pattern rx = Pattern.compile("^<.*?(\\d+)(\\?_format=json|\\.json)?>$");
                    for(String link : connection.getHeaderFields().get("Link")) {
                        Matcher m = rx.matcher(link);
                        if (m.find()) {
                            ids.add(Integer.parseInt(m.group(1)));
                        }
                    }
                }
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                for (Entity object : objects) {
                    ids.add(object.getId());
                }
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return mapper.readValue(connection.getContent().toString(), objects.getClass());
            } else {
                throw new IOException();
            }

            return ids;
        }

        private HttpURLConnection prepareConnection(URL edge, String method) throws IOException {
            HttpURLConnection connection = edge.getProtocol().equals("https")
                    ? (HttpsURLConnection) edge.openConnection()
                    : (HttpURLConnection) edge.openConnection()
                    ;

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            return connection;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OAuthPayload {

        @JsonProperty("client_id")
        protected String publicId;
        @JsonProperty("client_secret")
        protected String secret;
        @JsonProperty("redirect_uri")
        protected String redirectUri;
        protected String code;
        protected String username;
        protected String password;
        @JsonProperty("refresh_token")
        protected String refreshToken;
        @JsonProperty("grant_type")
        protected String grantType = OAuth2.GRANT_TYPE_CODE;

        public OAuthPayload setPublicId(String id) {
            publicId = id;

            return this;
        }

        public String getPublicId() {
            return publicId;
        }

        public OAuthPayload setSecret(String s) {
            secret = s;

            return this;
        }

        public String getSecret() {
            return secret;
        }

        public OAuthPayload setRedirectUri(String uri) {
            redirectUri = uri;

            return this;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public OAuthPayload setCode(String c) {
            code = c;

            return this;
        }

        public String getCode() {
            return code;
        }

        public OAuthPayload setUsername(String username) {
            this.username = username;

            return this;
        }

        public String getUsername() { return username; }

        public OAuthPayload setPassword(String password) {
            this.password = password;

            return this;
        }

        public String getPassword() { return password; }

        public OAuthPayload setGrantType(String type) {
            grantType = type;

            return this;
        }

        public String getGrantType() { return grantType; }

        public OAuthPayload setRefreshToken(String token) {
            refreshToken = token;
            return this;
        }

        public String getRefreshToken() { return refreshToken; }
    }

    public static class OAuthTokenRequest {

        static final String METHOD_GET = "GET";
        static final String METHOD_POST = "POST";

        protected URL url;
        protected String method = METHOD_GET;
        protected String token;
        protected OAuthPayload data;

        public OAuthTokenRequest(String url) throws MalformedURLException {
            setUrl(url);
        }

        public OAuthTokenRequest(URL url) {
            setUrl(url);
        }

        public OAuthTokenRequest setUrl(String url) throws MalformedURLException {
            this.url = new URL(url);

            return this;
        }

        public OAuthTokenRequest setUrl(URL url) {
            this.url = url;

            return this;
        }

        public URL getUrl() {
            return this.url;
        }

        public OAuthTokenRequest setToken(String token) {
            this.token = token;

            return this;
        }

        public String getToken() { return token; }

        public OAuthTokenRequest setMethod(String method) {
            this.method = method;

            return this;
        }

        public String getMethod() { return method; }

        public OAuthTokenRequest setData(OAuthPayload data) {
            this.data = data;

            return this;
        }

        public OAuthPayload getData() { return data; }

        public OAuthTokenResponse send() throws IOException {
            HttpsURLConnection connection = (HttpsURLConnection) getUrl().openConnection();
            ObjectMapper mapper = new ObjectMapper();

            if (null != getToken()) {
                connection.setRequestProperty("Authorization", "Bearer " + getToken());
            }

            if (getMethod().equals(METHOD_POST)) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                mapper.writeValue(os, getData());
                os.flush();
                os.close();
            }

            connection.connect();

            InputStream is = connection.getInputStream();
            OAuthTokenResponse response = mapper.readValue(is, OAuthTokenResponse.class);

            is.close();

            return response;
        }
    }

    @JsonIgnoreProperties(value={"token_type", "scope"})
    public static class OAuthTokenResponse {
        @JsonProperty("access_token")
        protected String accessToken;

        @JsonProperty("refresh_token")
        protected String refreshToken;

        @JsonProperty("expires_in")
        protected long expires;

        public OAuthTokenResponse setAccessToken(String token) {
            accessToken = token;

            return this;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public OAuthTokenResponse setRefreshToken(String token) {
            refreshToken = token;

            return this;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public OAuthTokenResponse setExpires(int e) {
            expires = e;

            return this;
        }

        public long getExpires() {
            return expires;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OAuthErrorResponse {
        protected String error;

        @JsonProperty("error_description")
        protected String description;

        public OAuthErrorResponse setError(String error) {
            this.error = error;

            return this;
        }

        public String getError() { return error; }

        public OAuthErrorResponse setDescription(String description) {
            this.description = description;

            return this;
        }

        public String getDescription() { return description; }

        @Override
        public String toString() {
            return "Error " + error + ": " + description;
        }
    }
}
