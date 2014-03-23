package com.slize.edmpircbot;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] botCfg;
        String[] nickservCfg;
        String[] redditCfg;
        Bot bot;

        Config config = new Config("config.xml");

        botCfg = config.loadBotSettings();
        nickservCfg = config.loadNickServ();
        redditCfg = config.loadReddit();

        if(nickservCfg == null && redditCfg == null) {
            bot = new Bot(botCfg[0], botCfg[1], botCfg[2]);
        }
        else if(redditCfg == null) {
            bot = new Bot(botCfg[0], botCfg[1], botCfg[2], nickservCfg[0], nickservCfg[1]);
        }
        else {
            bot = new Bot(botCfg[0], botCfg[1], botCfg[2], nickservCfg[0], nickservCfg[1], redditCfg[0], redditCfg[1]);
        }

        bot.setVerbose(true);
    }
}