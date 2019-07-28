package com.ruubel.massfollower.config;

import java.util.Optional;

public class ConfigParams {

    private String homeAccountName;
    private String homeAccountId;
    private String authorizationBearerToken;
    private String authToken;
    private String csrfToken;

    public ConfigParams() throws Exception {
        authorizationBearerToken = Optional.ofNullable(System.getenv("TWITTER_BEARER_TOKEN")).orElseThrow(
                () -> new Exception("TWITTER_BEARER_TOKEN is not set in the environment"));
        csrfToken = Optional.ofNullable(System.getenv("TWITTER_CSRF_TOKEN")).orElseThrow(
                () -> new Exception("TWITTER_CSRF_TOKEN is not set in the environment"));
        authToken = Optional.ofNullable(System.getenv("TWITTER_AUTH_TOKEN")).orElseThrow(
                () -> new Exception("TWITTER_AUTH_TOKEN is not set in the environment"));
        homeAccountName = Optional.ofNullable(System.getenv("TWITTER_HOME_ACCOUNT_NAME")).orElseThrow(
                () -> new Exception("TWITTER_HOME_ACCOUNT_NAME is not set in the environment"));
        homeAccountId = Optional.ofNullable(System.getenv("TWITTER_HOME_ACCOUNT_ID")).orElseThrow(
                () -> new Exception("TWITTER_HOME_ACCOUNT_ID is not set in the environment"));
    }

    public String getHomeAccountName() {
        return homeAccountName;
    }

    public String getAuthorizationBearerToken() {
        return authorizationBearerToken;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public String getHomeAccountId() {
        return homeAccountId;
    }

    public String getAuthToken() {
        return authToken;
    }
}
