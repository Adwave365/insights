package com.adwave.insights;

import com.adwave.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by alexboyce on 8/20/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Account extends Entity {

    private String name;
    private LocalDateTime created;
    final private static String edgeBase = "accounts";

    public Account() {
        super();
    }

    public Account(String name) {
        super();
        this.name = name;
    }

    public Account(Integer id) {
        super();
        this.id = id;
    }

    public Account(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /* Setters and Getters */

    @Override
    public Account setId(Integer id) {
        super.setId(id);

        return this;
    }

    public Account setName(String name) {
        this.name = name;

        return this;
    }

    public String getName() { return name; }

    public Account setCreated(LocalDateTime created) {
        this.created = created;

        return this;
    }

    public Account setCreated(CharSequence created) {
        this.created = LocalDateTime.parse(created);

        return this;
    }

    public LocalDateTime getCreated() { return created; }

    /* CRUD Methods */

    public static Account create(InputStream stream) throws IOException {
        return Entity.create(stream, Account.class);
    }

    public static Account create(File file) throws IOException {
        return Entity.create(file, Account.class);
    }

    public static Account create(byte[] data) throws IOException {
        return Entity.create(data, Account.class);
    }

    public static Account create(String data) throws IOException {
        return Entity.create(data, Account.class);
    }

    public static Account create(BufferedReader data) throws IOException {
        return Entity.create(data, Account.class);
    }

    public static List<Account> get() throws IOException, OAuthException {
        return Account.get(0, 20);
    }

    public static List<Account> get(int page) throws IOException, OAuthException {
        return Account.get(page, 20);
    }

    public static List<Account> get(int page, int limit) throws IOException, OAuthException {
        return Entity.get(new TypeReference<List<Account>>(){},
                new URL(Entity.getRestEdge(edgeBase),
                String.format("?page=%d&limit=%d", page, limit)));
    }

    public static Account get(Integer id) throws IOException, OAuthException {
        return Entity.get(Account.class, Entity.getRestEdge(edgeBase + "/" + id));
    }
}
