package com.adwave.insights;

import com.adwave.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by alexboyce on 8/20/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Account extends Entity {

    private String name;
    private ZonedDateTime created;
    final private static String edgeBase = "accounts";

    public Account() {
        super();
    }

    /**
     * @param name The Account's name
     */
    public Account(String name) {
        super();
        this.name = name;
    }

    /**
     *
     * @param id The Id of the Account
     */
    public Account(Integer id) {
        super();
        this.id = id;
    }

    /**
     *
     * @param id The Id of the Account
     * @param name The name of the account
     */
    public Account(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /* Setters and Getters */

    /**
     * @param id The Account ID
     * @return Returns the Account object for chaining
     */
    @Override public Account setId(Integer id) {
        super.setId(id);

        return this;
    }

    /**
     * @param name The Account name
     * @return Returns the Account object ofr chaining
     */
    public Account setName(String name) {
        this.name = name;

        return this;
    }

    /**
     * @return The name of the Account
     */
    public String getName() { return name; }

    /**
     * @param created The created date and time as a ZonedDateTime object
     * @return Returns the Account object for chaining
     */
    public Account setCreated(ZonedDateTime created) {
        this.created = created;

        return this;
    }

    /**
     * @param created The created date and time as a CharSequence
     * @return Returns the Account object for chaining
     */
    public Account setCreated(CharSequence created) {
        this.created = ZonedDateTime.parse(created);

        return this;
    }

    /**
     * @return The created date and time as a ZonedDateTime object
     */
    public ZonedDateTime getCreated() { return created; }

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
