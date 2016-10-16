package com.adwave.client.insights;

import com.adwave.client.oauth.OAuth2;
import org.joda.time.tz.DateTimeZoneBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

/**
 * Created by alexboyce on 10/16/16.
 */
public class EntryTest {
    private OAuth2.OAuthTokenResponse response;

    public void getToken() throws Exception {
        response =
                Kiosk.client.authorize(System.getenv("ADWAVE_TEST_USER"), System.getenv("ADWAVE_TEST_PASSWORD"));
    }

    @Before
    public void setUp() throws Exception {
        if (null == Kiosk.client) {
            Kiosk.client = new OAuth2.Client()
                    .setId(System.getenv("ADWAVE_TEST_CLIENT_ID"))
                    .setSecret(System.getenv("ADWAVE_TEST_CLIENT_SECRET"))
            ;
            getToken();
        }
    }

    @Test
    public void testSave() throws Exception {
        Account account = new Account();

        account.setName("Testing Account");

        assertTrue(account.save());

        ZoneId tz = ZoneId.of("America/New_York");

        Kiosk kiosk = new Kiosk();
        kiosk.setName("Testing Kiosk")
                .setAccount(account)
                .setTimezone(tz.toString());

        assertTrue(kiosk.save());

        Entry entry = new Entry();
        entry.setUuid("ABC123");
        entry.setKiosk(kiosk);
        entry.setManufacturer("Apple");
        entry.setModel("iPhone 5/5s/5c/6");
        entry.setConnected(ZonedDateTime.of(2016, 10, 16, 11, 41, 0, 0, tz));
        entry.setDisconnected(ZonedDateTime.of(2016, 10, 16, 12, 0, 0, 0, tz));

        assertTrue(entry.save());
        assertNotNull(entry.getId());

        assertTrue(entry.delete());
        assertNull(entry.getId());

        kiosk.delete();
        account.delete();
    }
}