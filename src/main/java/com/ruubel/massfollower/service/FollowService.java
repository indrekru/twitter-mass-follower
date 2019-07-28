package com.ruubel.massfollower.service;

import com.ruubel.massfollower.config.ConfigParams;
import com.ruubel.massfollower.dao.FollowedRepository;
import com.ruubel.massfollower.model.Followed;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class FollowService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private double waitBetweenFollowsSeconds = 1;
    private double waitBetweenUnfollowsSeconds = 0.1;

    private int followerCount = 200;

    private FollowedRepository followedRepository;
    private FollowingAmountService followingAmountService;
    private MailingService mailingService;
    private ConfigParams configParams;

    @Autowired
    public FollowService(FollowedRepository followedRepository, FollowingAmountService followingAmountService, MailingService mailingService, ConfigParams configParams) {
        this.followedRepository = followedRepository;
        this.followingAmountService = followingAmountService;
        this.mailingService = mailingService;
        this.configParams = configParams;
    }

    public int add(int a, int b) {
        return a + b;
    }

    public void execute() throws Exception {

        long[] followers = getImFollowingAndMyFollowers();
        long imFollowing = followers[0];
        long myFollowers = followers[1];

        if (followers[0] >= 3500) {
            // Do unfollows
            // Do follows
            log.info("Do unfollows first");
            doUnfollows();
            doFollows();
        } else {
            // Do follows
            log.info("Do follows first");
            doFollows();
        }

        // Update followers
        followers = getImFollowingAndMyFollowers();
        imFollowing = followers[0];
        myFollowers = followers[1];

        if (imFollowing == 0) {
            log.info("Account blocked with recaptcha, notify");
            mailingService.notifyRecaptchaBlock();
        }
        followingAmountService.saveFollowingAmounts(imFollowing, myFollowers);
        log.info("Finished, done for today");
    }

    private void doUnfollows() throws Exception {

        HttpResponse response = Request.Get(
                String.format("https://api.twitter.com/1.1/friends/list.json?user_id=%s&count=%s", configParams.getHomeAccountId(), followerCount))
                .addHeader(HttpHeaders.AUTHORIZATION, configParams.getAuthorizationBearerToken())
                .execute()
                .returnResponse();

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("Failed fetching home followers: '%s'", configParams.getHomeAccountId());
            return;
        }
        HttpEntity entity = response.getEntity();
        JSONObject json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));

        JSONArray users = json.getJSONArray("users");

        for (Object it : users) {
            JSONObject user = (JSONObject) it;

            long id = user.getLong("id");
            String name = user.getString("name");
            String screenName = user.getString("screen_name");

            Followed followed = followedRepository.findByExternalId(String.valueOf(id));
            boolean shouldUnfollow = shouldUnfollow(followed);
            if (!shouldUnfollow) {
                log.info(String.format("Shouldn't unfollow '%s' yet", screenName));
                continue;
            }

            String cookie = String.format("auth_token=%s; ct0=%s;", configParams.getAuthToken(), configParams.getCsrfToken());

            response = Request.Post("https://api.twitter.com/1.1/friendships/destroy.json")
                .addHeader(HttpHeaders.AUTHORIZATION, configParams.getAuthorizationBearerToken())
                .addHeader("x-csrf-token", configParams.getCsrfToken())
                .addHeader("cookie", cookie)
                .bodyForm(
                    Form.form()
                    .add("id", String.valueOf(id))
                    .build())
                .execute()
                .returnResponse();

            statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
            if (statusCode != 200) {
                log.error(String.format("Couldn't execute: %s, got statusCode: %s, response: %s", name, statusCode, json));
                break;
            }

            log.info(String.format("Unfollowed '%s' successfully", name));

            Thread.sleep((long)(waitBetweenUnfollowsSeconds * 1000.0));
        }

    }

    private void doFollows() throws Exception {

        List<String> accounts = new ArrayList<String>(){{
            add("airbnb");
            add("santanderuk");
            add("HSBC");
            add("AskLloydsBank");
            add("WesternUnion");
            add("BarackObama");
            add("TheEllenShow");
            add("realDonaldTrump");
            add("BillGates");
            add("elonmusk");
            add("nytimes");
            add("NASA");
            add("ReformedBroker");
            add("TheStalwart");
            add("ritholtz");
            add("StockCats");
            add("awealthofcs");
            add("John_Hempton");
            add("BarbarianCap");
            add("muddywatersre");
            add("Carl_C_Icahn");
            add("herbgreenberg");
            add("zerohedge");
            add("pmarca");
            add("WarrenBuffett");
            add("LendingClub");
            add("Zopa");
            add("RobinhoodApp");
            add("RevolutApp");
            add("monzo");
            add("imaginecurve");
            add("starlingbank");
            add("freetrade");
            add("IBKR");
            add("degiroeu");
            add("GrantCardone");
            add("theRealKiyosaki");
            add("Citibank");
            add("jpmorgan");
        }};

        Random random = new Random();
        String account = accounts.get(random.nextInt(accounts.size()));
        log.info(String.format("Start following '%s'", account));

        HttpResponse response = Request.Get(
                String.format("https://api.twitter.com/1.1/users/show.json?screen_name=%s", account))
                .addHeader(HttpHeaders.AUTHORIZATION, configParams.getAuthorizationBearerToken())
                .execute()
                .returnResponse();
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("Failed fetching account: '%s'", account);
            return;
        }
        HttpEntity entity = response.getEntity();
        JSONObject json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));

        long userId = json.getLong("id");

        response = Request.Get(
                String.format("https://api.twitter.com/1.1/followers/list.json?cursor=-1&user_id=%s&count=%s", userId, followerCount))
                .addHeader(HttpHeaders.AUTHORIZATION, configParams.getAuthorizationBearerToken())
                .execute()
                .returnResponse();

        statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("Failed fetching followers: '%s'", account);
            return;
        }

        entity = response.getEntity();
        json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));

        JSONArray users = json.getJSONArray("users");

        String cookie = String.format("auth_token=%s; ct0=%s;", configParams.getAuthToken(), configParams.getCsrfToken());

        for (Object it : users) {
            JSONObject user = (JSONObject) it;

            long id = user.getLong("id");
            String name = user.getString("name");
            String screenName = user.getString("screen_name");
            Boolean following = user.getBoolean("following");

            // Check if already following
            if (following != null && following) {
                log.info(String.format("Already followed '%s', skip", screenName));
                continue;
            }

            // Validate if real user
            String description = user.getString("description");
            if (description == null || description.trim().isEmpty()) {
                log.info(String.format("Skip '%s', probably a bot", screenName));
                continue;
            }

            Followed followed = followedRepository.findByExternalId(String.valueOf(id));
            if (followed != null) {
                log.info("Have already followed " + followed.getExternalName() + ", skip");
                continue;
            }

            response = Request.Post("https://api.twitter.com/1.1/friendships/create.json")
                .addHeader(HttpHeaders.AUTHORIZATION, configParams.getAuthorizationBearerToken())
                .addHeader("x-csrf-token", configParams.getCsrfToken())
                .addHeader("cookie", cookie)
                .bodyForm(
                    Form.form()
                    .add("id", String.valueOf(id))
                    .build())
                .execute()
                .returnResponse();

            statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            json = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
            if (statusCode != 200) {
                log.error(String.format("Couldn't execute: %s, got statusCode: %s, response: %s", name, statusCode, json));
                break;
            }

            followed = new Followed(screenName, String.valueOf(userId));
            followedRepository.save(followed);

            log.info(String.format("Followed '%s' successfully", name));

            Thread.sleep((long)(waitBetweenFollowsSeconds * 1000.0));
        }

    }

    private long[] getImFollowingAndMyFollowers() throws Exception {
        long[] followers = new long[2];

        String homeUrl = String.format("https://twitter.com/%s", configParams.getHomeAccountName());

        HttpResponse response = Request.Get(homeUrl)
                .execute()
                .returnResponse();

        String html = EntityUtils.toString(response.getEntity(), "UTF-8");

        Document parsed = Jsoup.parse(html);

        Elements imFollowingElements = parsed.select("a.ProfileNav-stat[data-nav=\"following\"]");
        Elements myFollowersElements = parsed.select("a.ProfileNav-stat[data-nav=\"followers\"]");

        long imFollowing = 0;
        long myFollowers = 0;

        if (imFollowingElements.size() > 0) {
            Element valueElement = imFollowingElements.get(0).select("span.ProfileNav-value").get(0);
            String imFollowingStr = valueElement.attr("data-count");
            imFollowing = Long.parseLong(imFollowingStr);
        }

        if (myFollowersElements.size() > 0) {
            Element valueElement = myFollowersElements.get(0).select("span.ProfileNav-value").get(0);
            String myFollowersStr = valueElement.attr("data-count");
            myFollowers = Long.parseLong(myFollowersStr);
        }

        followers[0] = imFollowing;
        followers[1] = myFollowers;

        return followers;
    }

    private boolean shouldUnfollow(Followed followed) {
        if (followed != null) {
            Instant followedDate = followed.getFollowed();
            Instant now = Instant.now();
            Duration duration = Duration.between(followedDate, now);
            if (Math.abs(duration.toHours()) >= 48) {
                log.info("Have followed at least 2 days, remove");
                return true;
            } else {
                return false;
            }
        } else {
            // Nothing in DB, so remove it
            return true;
        }
    }

}
