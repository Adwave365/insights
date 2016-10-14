package com.adwave.client.insights;

import com.adwave.client.oauth.OAuth2;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alexboyce on 10/9/16.
 */
public class AccountTest {

    private OAuth2.OAuthTokenResponse response;

    public void getToken() throws Exception {
        response =
                Account.client.authorize(System.getenv("ADWAVE_TEST_USER"), System.getenv("ADWAVE_TEST_PASSWORD"));
    }

    @Before
    public void setUp() throws Exception {
        if (null == Account.client) {
            Account.client = new OAuth2.Client()
                    .setId(System.getenv("ADWAVE_TEST_CLIENT_ID"))
                    .setSecret(System.getenv("ADWAVE_TEST_CLIENT_SECRET"))
            ;
            getToken();
        }
    }

    @Test
    public void testGet() throws Exception {
        List<Account> accounts = Account.get();

        assertFalse(accounts.isEmpty());
    }

    @Test
    public void testGetById() throws Exception {
        Account account = Account.get(1);

        assertEquals(account.getId(), 1);
        assertNotNull(account.getName());
        assertNotNull(account.getCreated());
    }

    @Test
    public void testPersist() throws Exception {
        Account account = new Account();

        account.setName("Testing Account");

        boolean saved = account.save();

        assertTrue(saved);
        assertNotNull(account.getId());

        boolean deleted = account.delete();

        assertTrue(deleted);
        assertNull(account.getId());
    }
}