package com.adwave365.oauth;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by alexboyce on 10/9/16.
 */
public class ClientTest {
    public OAuth2.Client client;
    private Properties properties = new Properties();

    @org.junit.Before
    public void setUp() throws Exception {
        InputStream is = new FileInputStream("src/resources/test.properties");
        properties.load(is);
        client = new OAuth2.Client()
            .setId(properties.getProperty("ADWAVE_TEST_CLIENT_ID"))
            .setSecret(properties.getProperty("ADWAVE_TEST_CLIENT_SECRET"))
        ;
    }

    @org.junit.Test
    public void testGetToken() throws Exception {
        OAuth2.OAuthTokenResponse response =
                client.authorize(properties.getProperty("ADWAVE_TEST_USER"), properties.getProperty("ADWAVE_TEST_PASSWORD"));

        assertNotNull(response.accessToken);
        assertNotNull(response.refreshToken);
        assertNotNull(response.expires);
    }
}