package com.adwave365.insights;

import com.adwave365.oauth.OAuth2;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by alexboyce on 10/13/16.
 */
public class KioskTest {
    private OAuth2.OAuthTokenResponse response;
    private Properties properties = new Properties();

    public void getToken() throws Exception {
        response =
                Kiosk.client.authorize(properties.getProperty("ADWAVE_TEST_USER"), properties.getProperty("ADWAVE_TEST_PASSWORD"));
    }

    @Before
    public void setUp() throws Exception {
        if (null == Kiosk.client) {
            InputStream is = new FileInputStream("src/resources/test.properties");
            properties.load(is);
            Kiosk.client = new OAuth2.Client()
                    .setId(properties.getProperty("ADWAVE_TEST_CLIENT_ID"))
                    .setSecret(properties.getProperty("ADWAVE_TEST_CLIENT_SECRET"))
            ;
            getToken();
        }
    }

    @Test
    public void testSend() throws Exception {
        Account account = new Account();

        account.setName("Testing Account");

        boolean accountSaved = account.save();

        assertTrue(accountSaved);

        Kiosk kiosk = new Kiosk();
        kiosk.setName("Testing Kiosk")
                .setAccount(account)
                .setTimezone("America/New_York");

        assertTrue(kiosk.save());
        assertNotNull(kiosk.getId());

        Kiosk other = Kiosk.get(kiosk.getId());
        assertEquals(kiosk.getId(), other.getId());
        assertTrue(kiosk.getName().equals(other.getName()));

        assertTrue(kiosk.delete());
        account.delete();
    }
}