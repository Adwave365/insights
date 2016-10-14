package com.adwave.client.insights;


import com.adwave.client.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexboyce on 8/20/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Kiosk extends Entity {
    private String name;
    private Account account;
    private String timezone;
    private ZonedDateTime updated;
    private ZonedDateTime created;
    final private static String edgeBase = "kiosks";

    public Kiosk() { }

    public Kiosk(Integer id) {
        this.id = id;
    }

    public Kiosk(String name) {
        this.name = name;
    }

    public Kiosk(String name, Account account) {
        this.name = name;
        this.account = account;
    }

    public Kiosk(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Kiosk(Integer id, String name, Account account) {
        this.id = id;
        this.name = name;
        this.account = account;
    }

    protected URL getRestEdge() throws MalformedURLException {
        return Entity.getRestEdge(edgeBase);
    }

    /* Setters ang Getters */

    public Kiosk setId(Integer id) {
        super.setId(id);

        return this;
    }

    public Kiosk setName(String name) {
        this.name = name;

        return this;
    }

    public String getName() { return name; }

    public Kiosk setAccount(Account account) {
        this.account = account;

        return this;
    }

    public Account getAccount() { return account; }

    public Kiosk setTimezone(String timezone) {
        this.timezone = timezone;

        return this;
    }

    public String getTimezone() { return timezone; }

    /**
     * @param updated The updated date and time as a ZonedDateTime object
     * @return Returns the Kiosk object for chaining
     */
    public Kiosk setUpdated(ZonedDateTime updated) {
        this.updated = updated;

        return this;
    }

    /**
     * @param updated The created date and time as a String
     * @return Returns the Kiosk object for chaining
     */
    @JsonSetter
    public Kiosk setUpdated(String updated) {
        this.updated = ZonedDateTime.parse(updated, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

        return this;
    }

    /**
     * @return The updated date and time as a ZonedDateTime object
     */
    public ZonedDateTime getUpdated() { return updated; }

    /**
     * @param created The created date and time as a ZonedDateTime object
     * @return Returns the Kiosk object for chaining
     */
    public Kiosk setCreated(ZonedDateTime created) {
        this.created = created;

        return this;
    }

    /**
     * @param created The created date and time as a String
     * @return Returns the Kiosk object for chaining
     */
    @JsonSetter
    public Kiosk setCreated(String created) {
        this.created = ZonedDateTime.parse(created, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

        return this;
    }

    /**
     * @return The created date and time as a ZonedDateTime object
     */
    public ZonedDateTime getCreated() { return created; }


    /* CRUD Operations */

    public static Kiosk create(InputStream stream) throws IOException {
        return create(stream, Kiosk.class);
    }

    public static Kiosk create(File file) throws IOException {
        return create(file, Kiosk.class);
    }

    public static Kiosk create(byte[] data) throws IOException {
        return create(data, Kiosk.class);
    }

    public static Kiosk create(String data) throws IOException {
        return create(data, Kiosk.class);
    }

    public static Kiosk create(BufferedReader data) throws IOException {
        return create(data, Kiosk.class);
    }

    public static List<Kiosk> get() throws IOException, OAuthException {
        return Kiosk.get(new LinkedList<Account>());
    }

    public static List<Kiosk> get(int page) throws IOException, OAuthException {
        return Kiosk.get(new LinkedList<Account>(), page);
    }

    public static List<Kiosk> get(int page, int limit) throws IOException, OAuthException {
        return Kiosk.get(new LinkedList<Account>(), page, limit);
    }

    public static List<Kiosk> get(List<Account> accounts) throws IOException, OAuthException {
        return Kiosk.get(accounts, 0, 20);
    }

    public static List<Kiosk> get(List<Account> accounts, int page) throws IOException, OAuthException {
        return Kiosk.get(accounts, page, 20);
    }

    public static List<Kiosk> get(List<Account> accounts, int page, int limit) throws IOException, OAuthException {
        String params = String.format("?page=%d&limit=%d", page, limit);

        for(Account account : accounts) {
            params = params.concat(String.format("&account[]=%d", account.getId()));
        }

        return get(new TypeReference<List<Kiosk>>(){}, new URL(getRestEdge(edgeBase), params));
    }

    public static Kiosk get(Integer id) throws IOException, OAuthException {
        return get(Kiosk.class, getRestEdge(edgeBase + "/" + id));
    }

}
