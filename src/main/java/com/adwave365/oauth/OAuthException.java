package com.adwave365.oauth;

/**
 * Created by alexboyce on 8/18/16.
 */
public class OAuthException extends Exception {
    OAuth2.OAuthErrorResponse errorResponse;

    public OAuthException(OAuth2.OAuthErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public void setErrorResponse(OAuth2.OAuthErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    @Override
    public String getMessage() {
        return errorResponse.toString();
    }
}
