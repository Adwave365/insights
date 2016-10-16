package com.adwave365.insights;

import com.adwave365.oauth.OAuthException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by alexboyce on 8/20/16.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Account extends Entity {

    private String name;
    private ZonedDateTime created;
    final protected static String edgeBase = "accounts";

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

    protected URL getRestEdge() throws MalformedURLException {
        return getRestEdge(edgeBase);
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
     * @param created The created date and time as a String
     * @return Returns the Account object for chaining
     */
    @JsonSetter
    public Account setCreated(String created) {
        this.created = ZonedDateTime.parse(created, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));

        return this;
    }

    /**
     * @return The created date and time as a ZonedDateTime object
     */
    public ZonedDateTime getCreated() { return created; }

    /* CRUD Methods */

    public static Account create(InputStream stream) throws IOException {
        return create(stream, Account.class);
    }

    public static Account create(File file) throws IOException {
        return create(file, Account.class);
    }

    public static Account create(byte[] data) throws IOException {
        return create(data, Account.class);
    }

    public static Account create(String data) throws IOException {
        return create(data, Account.class);
    }

    public static Account create(BufferedReader data) throws IOException {
        return create(data, Account.class);
    }

    public static List<Account> get() throws IOException, URISyntaxException, OAuthException {
        Entity.Pager pager = new Entity.Pager();

        return Account.get(pager);
    }

    public static List<Account> get(Entity.Pager pager) throws IOException, URISyntaxException, OAuthException {
        URL base = getRestEdge(Account.edgeBase);
        URL edge = pager.appendURL(base);

        return get(new TypeReference<List<Account>>(){}, edge);
    }

    public static Account get(Integer id) throws IOException, OAuthException {
        return get(Account.class, getRestEdge(String.format("%s/%s", edgeBase, id)));
    }
}
