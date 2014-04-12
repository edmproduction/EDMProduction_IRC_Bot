package com.slize.edmpircbot.utils;

import java.util.Properties;
public class Config {
    private Properties config;

    public Config(String fileName) throws Exception {

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
        String[] bot = new String[2];

        bot[0] = config.getProperty("bot.nick");
        bot[1] = config.getProperty("bot.chan");


        return bot;
    }

    public String[] loadNickServ() {
        String[] nickserv = new String[2];

        nickserv[0] = config.getProperty("nickserv.user");
        nickserv[1] = config.getProperty("nickserv.pass");

        return nickserv;
    }

    public String[] loadReddit() {
        String[] reddit = new String[3];

        reddit[0] = config.getProperty("reddit.user");
        reddit[1] = config.getProperty("reddit.pass");
        reddit[2] = config.getProperty("reddit.subreddit");

        return reddit;
    }

    public int[] loadSpamSettings() {
        int[] spam = new int[7];

        spam[0] = Integer.parseInt(config.getProperty("spam.time"));
        spam[1] = Integer.parseInt(config.getProperty("spam.lines"));
        spam[2] = Integer.parseInt(config.getProperty("spam.resettime"));
        spam[3] = Integer.parseInt(config.getProperty("spam.bantime.1"));
        spam[4] = Integer.parseInt(config.getProperty("spam.bantime.2"));
        spam[5] = Integer.parseInt(config.getProperty("spam.bantime.3"));
        spam[6] = Integer.parseInt(config.getProperty("spam.bantime.4", "-1"));

        return spam;
    }
}
























