package com.adwave.oauth;

import com.adwave.insights.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

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
        private LocalDateTime expires;

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

        public URL authorize(String username, String password) throws MalformedURLException {
            return new URL(String.format("%s?client_id=%s&client_secret=%s&grant_type=%s&response_type=token&username=%s&password=%s",
                    tokenUrl,
                    id,
                    secret,
                    GRANT_TYPE_PASSWORD,
                    username,
                    password
            ));
        }

        public OAuthTokenResponse getToken(String username, String password) throws IOException, OAuthException {
            OAuthTokenResponse response =  new OAuthTokenRequest(authorize(username, password)).send();

            token = response.getAccessToken();
            refresh_token = response.getRefreshToken();
            expires = LocalDateTime.now().plusMinutes(response.getExpires());

            return response;
        }

        public OAuthTokenResponse getToken(String authCode) throws IOException, OAuthException {
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
            expires = LocalDateTime.now().plusMinutes(response.getExpires());

            return response;
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
            expires = LocalDateTime.now().plusMinutes(response.getExpires());

            return response;
        }

        public List<Integer> send(List<? extends Entity> objects, URL edge, String method) throws IOException {
            ObjectMapper mapper = new ObjectMapper();

            if (LocalDateTime.now().isAfter(expires)) {
                refreshToken(refresh_token);
            }

            HttpURLConnection connection = (HttpURLConnection) edge.openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoInput(true);

            if (method.equals("POST") || method.equals("PUT")) {
                connection.setDoOutput(true);
            }

            OutputStream os = connection.getOutputStream();

            mapper.writeValue(os, objects);
            os.flush();
            os.close();

            connection.connect();

            List<Integer> ids = new LinkedList<Integer>();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {

                if (connection.getHeaderFields().containsKey("Link")) {
                    List<String> links = connection.getHeaderFields().get("Link");

                    for (String link : links) {
                        String[] parts = link.replace(">", "").split("/");
                        String filename = parts[parts.length - 1].replace(".json", "");
                        ids.add(Integer.parseInt(filename));
                    }
                }
            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                for (Entity object : objects) {
                    ids.add(object.getId());
                }
            } else {
                System.err.println(connection.getContent());
                throw new IOException();
            }

            return ids;
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
            HttpURLConnection connection = (HttpURLConnection) getUrl().openConnection();
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
