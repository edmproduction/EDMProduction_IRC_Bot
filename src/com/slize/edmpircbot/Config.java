package com.slize.edmpircbot;

import java.io.IOException;
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
        String[] bot = new String[3];

        bot[0] = config.getProperty("bot.nick");
        bot[1] = config.getProperty("bot.chan");
        bot[2] = config.getProperty("bot.subreddit");

        return bot;
    }

    public String[] loadNickServ() throws IOException {
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
}
























