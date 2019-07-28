package com.ruubel.massfollower.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfigParams {

    private String homeAccountName;
    private String homeAccountId;
    private String authorizationBearerToken;
    private String authToken;
    private String csrfToken;
    private List<String> accountsToFollow;

    private static final String TWITTER_BEARER_TOKEN = "TWITTER_BEARER_TOKEN";
    private static final String TWITTER_CSRF_TOKEN = "TWITTER_CSRF_TOKEN";
    private static final String TWITTER_AUTH_TOKEN = "TWITTER_AUTH_TOKEN";
    private static final String TWITTER_HOME_ACCOUNT_NAME = "TWITTER_HOME_ACCOUNT_NAME";
    private static final String TWITTER_HOME_ACCOUNT_ID = "TWITTER_HOME_ACCOUNT_ID";
    private static final String TWITTER_TARGET_ACCOUNTS = "TWITTER_TARGET_ACCOUNTS";

    public ConfigParams() throws Exception {
        authorizationBearerToken = Optional.ofNullable(System.getenv(TWITTER_BEARER_TOKEN)).orElseThrow(
                () -> new Exception(TWITTER_BEARER_TOKEN + " is not set in the environment"));
        csrfToken = Optional.ofNullable(System.getenv(TWITTER_CSRF_TOKEN)).orElseThrow(
                () -> new Exception(TWITTER_CSRF_TOKEN + " is not set in the environment"));
        authToken = Optional.ofNullable(System.getenv(TWITTER_AUTH_TOKEN)).orElseThrow(
                () -> new Exception(TWITTER_AUTH_TOKEN + " is not set in the environment"));
        homeAccountName = Optional.ofNullable(System.getenv(TWITTER_HOME_ACCOUNT_NAME)).orElseThrow(
                () -> new Exception(TWITTER_HOME_ACCOUNT_NAME + " is not set in the environment"));
        homeAccountId = Optional.ofNullable(System.getenv(TWITTER_HOME_ACCOUNT_ID)).orElseThrow(
                () -> new Exception(TWITTER_HOME_ACCOUNT_ID + " is not set in the environment"));

        String commaSeparatedAccounts = Optional.ofNullable(System.getenv(TWITTER_TARGET_ACCOUNTS)).orElseThrow(
                () -> new Exception(TWITTER_TARGET_ACCOUNTS + " is not set in the environment"));
        accountsToFollow = Arrays.asList(commaSeparatedAccounts.split("\\s*,\\s*"));

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

    public List<String> getAccountsToFollow() {
        return accountsToFollow;
    }
}
