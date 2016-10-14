package com.adwave.client.oauth;

import static org.junit.Assert.*;

/**
 * Created by alexboyce on 10/9/16.
 */
public class ClientTest {
    public OAuth2.Client client;

    @org.junit.Before
    public void setUp() throws Exception {
        client = new OAuth2.Client()
            .setId(System.getenv("ADWAVE_TEST_CLIENT_ID"))
            .setSecret(System.getenv("ADWAVE_TEST_CLIENT_SECRET"))
        ;
    }

    @org.junit.Test
    public void testGetToken() throws Exception {
        OAuth2.OAuthTokenResponse response =
                client.authorize(System.getenv("ADWAVE_TEST_USER"), System.getenv("ADWAVE_TEST_PASSWORD"));

        assertNotNull(response.accessToken);
        assertNotNull(response.refreshToken);
        assertNotNull(response.expires);
    }
}