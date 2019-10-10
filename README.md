# Twitter mass follower (NEW TWITTER)
[![CircleCI](https://circleci.com/gh/indrekru/twitter-mass-follower.svg?style=svg)](https://circleci.com/gh/indrekru/twitter-mass-follower)

<img src="https://raw.githubusercontent.com/indrekru/twitter-mass-follower/master/img.png" width="200px">

This is a server-side twitter mass-follower + unfollower. It uses a scheduler and runs every 10 minutes, it runs as long as Twitter restrictions (rate limiting) kick in, so it'll wait and do it again.
It will decide either to follow or unfollow a bunch of people, depending how many you are currently following. It unfollows people that have been followed for at least 2 days. It always needs to keep your current following number under 5000, otherwise - "Special" twitter restrictions kick in.
If this thing runs on a scheduler every day, your follower amount will grow (Check the graph at https://mass-follower.netlify.com/).

It keeps track of followed/unfollowed users in Postgres or HSQLDB local file storage database (depends what profile you run it with, default is postgres). In case of HSQLDB, run it with `-Dspring.profiles.active=hsql` and make sure the app can create a directory named `db` in the root directory and create/modify files inside it.
Also runs a cleanup job every day to remove older entries to keep followed table's DB row count below 8500, because heroku has 10000 DB row limit on free plan.
Also, twitter triggers a picture recaptcha from time to time (every ~2 days), when that is detected, it will send an email to notify the owner to go and manually solve the recaptcha, and everything continues.

Interface to show progress: https://mass-follower.netlify.com/

I wrote a blog post about it: https://fintechify.wordpress.com/2019/03/06/twitter-mass-follower/

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See running for notes on how to run the project on a system.

### Prerequisites

1. Clone the project to your local environment:
    ```
    git clone https://github.com/indrekru/twitter-mass-follower.git
    ```

2. You need to define environment variables:
   ```
   TWITTER_HOME_ACCOUNT_NAME
   TWITTER_HOME_ACCOUNT_ID
   TWITTER_BEARER_TOKEN
   TWITTER_CSRF_TOKEN
   TWITTER_AUTH_TOKEN
   TWITTER_TARGET_ACCOUNTS
   ```
   
   #### Where to find these values
   
   Log in to your twitter account and open up developer tools.
   
   * `TWITTER_HOME_ACCOUNT_NAME` - your own account name in twitter
   
   * `TWITTER_HOME_ACCOUNT_ID` - your own account id in twitter (long)
   
   * `TWITTER_BEARER_TOKEN` - Passed in requests as header called:
       ```
       authorization
       ```
        Looks like:
       ```
       'Bearer whateverRandomNumbersLetters...'
       ```
   * `TWITTER_CSRF_TOKEN` - passed as header named:
       ```
       x-csrf-token
       ```
   * `TWITTER_AUTH_TOKEN` - See your twitter cookies and extract value from:
       ```
       auth_token=whatever;
       ```
   * `TWITTER_TARGET_ACCOUNTS` - Comma separated twitter account names, which to choose from randomly on every iteration
   
    #### If you run it with Postgres, you'll also need these environment variables:

    ```
    POSTGRES_URL
    POSTGRES_USER
    POSTGRES_PASSWORD
    ```

3. You need maven installed on your environment:

    #### Mac (homebrew):
    
    ```
    brew install maven
    ```
    #### Ubuntu:
    ```
    sudo apt-get install maven
    ```

### Installing

Once you have maven installed on your environment, install the project dependencies via:

```
mvn install
```

## Testing

Run all tests:
```
mvn test
```

## Running

Once you have installed dependencies, this can be run from the `Application.java` main method directly,
or from a command line:
```
mvn spring-boot:run
```

And now if all went well, watch the terminal spit out logs as it's doing its magic.

## In production

This is project is currently in production.

### Health

https://mass-follower1.herokuapp.com/api/v1/health

### All followed records:

https://mass-follower1.herokuapp.com/api/v1/followed

### Follower stats:

https://mass-follower1.herokuapp.com/api/v1/follow-stats

### Commands

Deploy new version:
```
git push heroku master
```

Check logs:
```
heroku logs -tail -a mass-follower1
```

Restart:
```
heroku restart -a mass-follower1
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot 2
* [Spock](http://spockframework.org/) - Spock testing framework
* [Maven](https://maven.apache.org/) - Dependency Management
* [HSQLDB](http://hsqldb.org/) - Local file storage database

## Contributing

If you have any improvement suggestions please create a pull request and I'll review it.


## Authors

* **Indrek Ruubel** - *Initial work* - [Github](https://github.com/indrekru)

See also the list of [contributors](https://github.com/indrekru/design-patterns-spring-boot/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License

## Acknowledgments

* Big thanks to Pivotal for Spring Boot framework, love it!
