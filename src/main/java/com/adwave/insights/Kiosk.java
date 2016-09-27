package com.adwave.insights;


import com.adwave.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    public Kiosk setAccount(Integer account_id) {
        this.account = new Account();

        return this;
    }

    public Account getAccount() { return account; }

    public Kiosk setTimezone(String timezone) {
        this.timezone = timezone;

        return this;
    }

    public String getTimezone() { return timezone; }


    /* CRUD Operations */

    public static Kiosk create(InputStream stream) throws IOException {
        return Entity.create(stream, Kiosk.class);
    }

    public static Kiosk create(File file) throws IOException {
        return Entity.create(file, Kiosk.class);
    }

    public static Kiosk create(byte[] data) throws IOException {
        return Entity.create(data, Kiosk.class);
    }

    public static Kiosk create(String data) throws IOException {
        return Entity.create(data, Kiosk.class);
    }

    public static Kiosk create(BufferedReader data) throws IOException {
        return Entity.create(data, Kiosk.class);
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

        return Entity.get(new TypeReference<List<Kiosk>>(){}, new URL(Entity.getRestEdge(edgeBase), params));
    }

    public static Kiosk get(Integer id) throws IOException, OAuthException {
        return Entity.get(Kiosk.class, Entity.getRestEdge(edgeBase + "/" + id));
    }

}
