package com.adwave.client.insights;

import com.adwave.client.json.ZonedDateTimeSerializer;
import com.adwave.client.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by alexboyce on 8/21/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entry extends Entity {
    private Kiosk kiosk;
    private String uuid;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime connected;
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime disconnected;
    private String model;
    private String manufacturer;
    private ZonedDateTime timestamp;

    final private static String edgeBase = "entries";

    public Entry() { }

    public Entry(Integer id) {
        this.id = id;
    }

    public Entry(Integer id, Kiosk kiosk, ZonedDateTime connected) {
        this.id = id;
        this.kiosk = kiosk;
        this.connected = connected;
    }

    public Entry(Integer id, Kiosk kiosk, ZonedDateTime connected, ZonedDateTime disconnected) {
        this.id = id;
        this.kiosk = kiosk;
        this.connected = connected;
        this.disconnected = disconnected;
    }

    public Entry(Integer id, Kiosk kiosk, String model, String manufacturer) {
        this.id = id;
        this.kiosk = kiosk;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public Entry(Integer id, Kiosk kiosk, String model, String manufacturer, ZonedDateTime connected, ZonedDateTime disconnected) {
        this.id = id;
        this.kiosk = kiosk;
        this.model = model;
        this.manufacturer = manufacturer;
        this.connected = connected;
        this.disconnected = disconnected;
    }

    public Entry(Integer id, String uuid, Kiosk kiosk, String model, String manufacturer, ZonedDateTime connected, ZonedDateTime disconnected) {
        this.id = id;
        this.uuid = uuid;
        this.kiosk = kiosk;
        this.model = model;
        this.manufacturer = manufacturer;
        this.connected = connected;
        this.disconnected = disconnected;
    }

    public Entry(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    public Entry(Kiosk kiosk, ZonedDateTime connected) {
        this.kiosk = kiosk;
        this.connected = connected;
    }

    public Entry(Kiosk kiosk, ZonedDateTime connected, ZonedDateTime disconnected) {
        this.kiosk = kiosk;
        this.connected = connected;
        this.disconnected = disconnected;
    }

    public Entry(Kiosk kiosk, String model, String manufacturer) {
        this.kiosk = kiosk;
        this.model = model;
        this.manufacturer = manufacturer;
    }

    public Entry(Kiosk kiosk, String model, String manufacturer, ZonedDateTime connected, ZonedDateTime disconnected) {
        this.kiosk = kiosk;
        this.model = model;
        this.manufacturer = manufacturer;
        this.connected = connected;
        this.disconnected = disconnected;
    }

    public Entry(String uuid, Kiosk kiosk, String model, String manufacturer, ZonedDateTime connected, ZonedDateTime disconnected) {
        this.uuid = uuid;
        this.kiosk = kiosk;
        this.model = model;
        this.manufacturer = manufacturer;
        this.connected = connected;
        this.disconnected = disconnected;
    }

    protected URL getRestEdge() throws MalformedURLException {
        return Entity.getRestEdge(edgeBase);
    }

    /* Setters and Getters */

    public Kiosk getKiosk() {
        return kiosk;
    }

    public Entry setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;

        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public Entry setUuid(String uuid) {
        this.uuid = uuid;

        return this;
    }

    public ZonedDateTime getConnected() {
        return connected;
    }

    public Entry setConnected(ZonedDateTime connected) {
        this.connected = connected;

        return this;
    }

    public ZonedDateTime getDisconnected() {
        return disconnected;
    }

    public Entry setDisconnected(ZonedDateTime disconnected) {
        this.disconnected = disconnected;

        return this;
    }

    public String getModel() {
        return model;
    }

    public Entry setModel(String model) {
        this.model = model;

        return this;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Entry setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;

        return this;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public Entry setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;

        return this;
    }

    /* CRUD Methods */

    public static Entry create(InputStream stream) throws IOException {
        return create(stream, Entry.class);
    }

    public static Entry create(File file) throws IOException {
        return create(file, Entry.class);
    }

    public static Entry create(byte[] data) throws IOException {
        return create(data, Entry.class);
    }

    public static Entry create(String data) throws IOException {
        return create(data, Entry.class);
    }

    public static Entry create(BufferedReader data) throws IOException {
        return create(data, Entry.class);
    }

    public static List<Entry> get(List<Kiosk> kiosks, ZonedDateTime end) throws IOException, OAuthException {
        return Entry.get(kiosks, end.minusMonths(1), end, 0, 500);
    }

    public static List<Entry> get(List<Kiosk> kiosks, ZonedDateTime start, ZonedDateTime end) throws IOException, OAuthException {
        return Entry.get(kiosks, start, end, 0, 500);
    }

    public static List<Entry> get(List<Kiosk> kiosks, ZonedDateTime start, ZonedDateTime end, int page) throws IOException, OAuthException {
        return Entry.get(kiosks, start, end, page, 500);
    }

    public static List<Entry> get(List<Kiosk> kiosks, ZonedDateTime start, ZonedDateTime end, int page, int limit) throws IOException, OAuthException {
        String params = String.format("?start=%s&end=%s&page=%d&limit=%d",
                start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                page,
                limit
        );

        for(Kiosk kiosk : kiosks) {
            params = params.concat(String.format("&kiosks[]=%d", kiosk.getId()));
        }

        URL edge = new URL(getRestEdge(edgeBase), params);

        return get(new TypeReference<List<Entry>>(){}, edge);
    }

    public static Entry get(Integer id) throws IOException, OAuthException {
        return get(Entry.class, getRestEdge(edgeBase + "/" + id));
    }
}
