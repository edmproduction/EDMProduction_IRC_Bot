package com.slize.edmpircbot;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] botCfg;
        String[] nickservCfg;
        String[] redditCfg;
        Bot bot;

        Config config = new Config(args[0]);

        botCfg = config.loadBotSettings();
        nickservCfg = config.loadNickServ();
        redditCfg = config.loadReddit();

        bot = new Bot(botCfg[0], botCfg[1], botCfg[2], config, nickservCfg[0], nickservCfg[1], redditCfg[0], redditCfg[1]);
    }
}
