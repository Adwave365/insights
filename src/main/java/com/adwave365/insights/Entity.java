package com.adwave365.insights;

import com.adwave365.oauth.OAuth2;
import com.adwave365.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexboyce on 8/20/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
abstract public class Entity {

    @Nullable
    protected Integer id;
    protected static ObjectMapper mapper = new ObjectMapper();
    protected static String protocol = "https";
    protected static String host = "insights.adwave365.com";
    protected static int port = 443;
    protected static String baseUrl = "/api/";
    protected static String edgeBase;

    @JsonIgnore
    @Nullable
    public static OAuth2.Client client;

    protected Entity() {}

    @Nullable
    public Integer getId() {
        return id;
    }

    public <T extends Entity> T setId(Integer id) {
        this.id = id;

        return (T) this;
    }

    abstract protected URL getRestEdge() throws MalformedURLException;

    protected static URL getRestEdge(String path) throws MalformedURLException {
        URL base = new URL(protocol, host, port, baseUrl);

        return new URL(base, path);
    }

    public static <T> T create(Class<T> valueType) throws InstantiationException, IllegalAccessException {
        return valueType.newInstance();
    }

    public static <T> T create(InputStream stream, Class<T> valueType) throws IOException {
        return mapper.readValue(stream, valueType);
    }

    public static <T> T create(File file, Class<T> valueType) throws IOException {
        return mapper.readValue(file, valueType);
    }

    public static <T> T create(byte[] data, Class<T> valueType) throws IOException {
        return mapper.readValue(data, valueType);
    }

    public static <T> T create(String data, Class<T> valueType) throws IOException {
        return mapper.readValue(data, valueType);
    }

    public static <T> T create(BufferedReader data, Class<T> valueType) throws IOException {
        return mapper.readValue(data, valueType);
    }

    static InputStream get(URL edge) throws IOException, OAuthException {
        HttpsURLConnection connection = (HttpsURLConnection) edge.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", String.format("Bearer %s", client.getToken()));

        InputStream is = connection.getInputStream();

        if (connection.getResponseCode() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
            throw new OAuthException(mapper.readValue(is, OAuth2.OAuthErrorResponse.class));
        } else if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new IOException();
        }

        return is;

    }

    static <T> T get(Class<T> valueType, URL edge) throws IOException, OAuthException {
        return mapper.readValue(get(edge), valueType);
    }

    static <T> T get(TypeReference<T> valueType, URL edge) throws IOException, OAuthException {
        return mapper.readValue(get(edge), valueType);
    }

    static List<Integer> send(List<? extends Entity> objects, URL edge, String method) throws IOException {
        List<Integer> ids = new LinkedList<>();
        int blocks = objects.size() / 50;
        int start;

        for (int i = 0; i < blocks; i++) {
            start = i * 50;
            List<? extends Entity> subset = objects.subList(start, start + 50);
            ids.addAll((List<Integer>) client.send(subset, edge, method));
        }

        if (objects.size() > blocks) {
            start = blocks * 50;
            List<? extends Entity> subset = objects.subList(start, objects.size());
            ids.addAll((List<Integer>) client.send(subset, edge, method));
        }

        return ids;
    }

    public static List<Integer> insert(List<? extends Entity> objects) throws IOException {
        if (objects.isEmpty()) {
            return new LinkedList<>();
        }

        return Entity.send(objects, objects.get(0).getRestEdge(), "POST");
    }

    public static List<Integer> update(List<? extends Entity> objects) throws IOException {
        if (objects.isEmpty()) {
            return new LinkedList<>();
        }

        return Entity.send(objects, objects.get(0).getRestEdge(), "PUT");
    }

    public static List<Integer> delete(List<? extends Entity> objects) throws IOException {
        if (objects.isEmpty()) {
            return new LinkedList<>();
        }

        List<Integer> ids = Entity.send(objects, objects.get(0).getRestEdge(), "DELETE");

        for (Entity o : objects) {
            o.setId(null);
        }

        return ids;
    }

    public boolean save() throws IOException {
        List<Entity> l = new LinkedList<>();
        List<Integer> ids;

        l.add(this);

        if (null == this.id) {
            ids = Entity.insert(l);
            if (!ids.isEmpty()) {
                this.id = ids.get(0);

                return true;
            }

            return false;
        }

        return !Entity.update(l).isEmpty();
    }

    public boolean delete() throws IOException {
        List<Entity> l = new LinkedList<>();
        l.add(this);

        boolean success = !Entity.delete(l).isEmpty();

        if (success) {
            this.id = null;
        }

        return success;
    }

    public static class Pager {
        private int page = 0;
        private int limit = 20;

        public Pager() {

        }

        public Pager(int page) {
            setPage(page);
        }

        public Pager(int page, int limit) {
            setPage(page);
            setLimit(limit);
        }

        public Pager setPage(int page) throws RuntimeException {
            if (page < 0) {
                throw new RuntimeException("Page cannot be below 0");
            }

            this.page = page;

            return this;
        }

        public int getPage() {
            return page;
        }

        public Pager setLimit(int limit) throws RuntimeException {
            if (limit < 1) {
                throw new RuntimeException("Limit cannot be below 1");
            }

            this.limit = limit;

            return this;
        }

        public int getLimit() {
            return limit;
        }

        public URL appendURL(URL url) throws URISyntaxException, MalformedURLException {
            String path =
                    String.format("%s%spage=%s&limit=%s", url.toString(),
                            url.toString().contains("?") ? "&" : "?", page, limit);

            return new URL(path);
        }
    }
}
