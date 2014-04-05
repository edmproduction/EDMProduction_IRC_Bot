package com.slize.edmpircbot;

import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Config {
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private Properties config;

    public Config(String fileName) throws Exception {
        try {
            FileHandler fh;
            fh = new FileHandler("config.log");
            LOGGER.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        }
        catch(Exception err) {
            err.printStackTrace();
        }

        config = new Properties();

        try {
            ClassLoader loader = getClass().getClassLoader();

            config.load(loader.getResourceAsStream(fileName));
        }
        catch(Exception err) {
            System.err.println("Critical Error: Could not load config file.");
            err.printStackTrace();
        }
    }

    public String[] loadBotSettings() {
        String[] bot = new String[3];

        bot[0] = config.getProperty("bot.nick");
        bot[1] = config.getProperty("bot.chan");
        bot[2] = config.getProperty("bot.subreddit");

        return bot;
    }

    public String[] loadNickServ() {
        String[] nickserv = new String[2];

        nickserv[0] = config.getProperty("nickserv.user");
        nickserv[1] = config.getProperty("nickserv.pass");

        return nickserv;
    }

    public String[] loadReddit() {
        String[] reddit = new String[2];

        reddit[0] = config.getProperty("reddit.user");
        reddit[1] = config.getProperty("reddit.pass");

        return reddit;
    }

    public String[] loadSpamSettings() {
        String[] spam = new String[5];

        spam[0] = config.getProperty("spam.time");
        spam[1] = config.getProperty("spam.lines");
        spam[2] = config.getProperty("spam.bantime.1");
        spam[3] = config.getProperty("spam.bantime.2");
        spam[4] = config.getProperty("spam.bantime.3");

        return spam;
    }
}
























