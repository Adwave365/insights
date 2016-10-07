package com.adwave.insights;

import com.adwave.oauth.OAuth2;
import com.adwave.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexboyce on 8/20/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Entity {

    @Nullable
    protected Integer id;
    protected static ObjectMapper mapper = new ObjectMapper();
    protected static String protocol = "http";
    protected static String host = "localhost";
    protected static int port = 8000;
    protected static String baseUrl = "/api/";
    protected static String edgeBase;

    @JsonIgnore
    public static OAuth2.Client client;

    protected Entity() {}

    public Integer getId() {
        return id;
    }

    public <T> T setId(Integer id) {
        this.id = id;

        return (T) this;
    }

    protected URL getRestEdge() throws MalformedURLException {
        String edge = edgeBase;

        if (null != this.id) {
            edge = edgeBase.concat("/" + this.id);
        }

        return Entity.getRestEdge(edge);
    }

    protected static URL getRestEdge(String path) throws MalformedURLException {
        return new URL(protocol, host, port, baseUrl.concat(path));
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
        HttpURLConnection connection = (HttpURLConnection) edge.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        InputStream is = connection.getInputStream();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new OAuthException(mapper.readValue(is, OAuth2.OAuthErrorResponse.class));
        } else if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
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

    static List<Integer> save(List<? extends Entity> objects, URL edge, String method) throws IOException {
        List<Integer> ids = new LinkedList<Integer>();
        int blocks = objects.size() / 50;
        int start;

        for (int i = 0; i < blocks; i++) {
            start = i * 50;
            List<? extends Entity> subset = objects.subList(start, start + 50);
            ids.addAll(client.send(subset, edge, method));
        }

        if (objects.size() > blocks) {
            start = blocks * 50;
            List<? extends Entity> subset = objects.subList(start, objects.size());
            ids.addAll(client.send(subset, edge, method));
        }

        return ids;
    }

    static void save(Entity object) throws IOException {
        List<Entity> objects = new LinkedList<Entity>();
        objects.add(object);

        List<Integer> ids = Entity.save(objects, object.getRestEdge(), null != object.id ? "PUT" : "POST");
        object.id = ids.get(0);
    }

    static void delete(Entity object) throws IOException {
        URL edge = object.getRestEdge();
        HttpURLConnection connection = (HttpURLConnection) edge.openConnection();

        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
    }

    public void save() throws IOException {
        Entity.save(this);
    }

    public void delete() throws IOException {
        Entity.delete(this);
    }
}
