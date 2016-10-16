package com.adwave365.insights;

import com.adwave365.oauth.OAuth2;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by alexboyce on 10/9/16.
 */
public class AccountTest {

    private OAuth2.OAuthTokenResponse response;
    private Properties properties = new Properties();

    public void getToken() throws Exception {
        response =
                Account.client.authorize(properties.getProperty("ADWAVE_TEST_USER"), properties.getProperty("ADWAVE_TEST_PASSWORD"));
    }

    @Before
    public void setUp() throws Exception {
        if (null == Account.client) {
            InputStream is = new FileInputStream("src/resources/test.properties");
            properties.load(is);
            Account.client = new OAuth2.Client()
                    .setId(properties.getProperty("ADWAVE_TEST_CLIENT_ID"))
                    .setSecret(properties.getProperty("ADWAVE_TEST_CLIENT_SECRET"))
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