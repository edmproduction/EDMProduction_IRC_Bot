package com.slize.edmpircbot;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] botCfg;
        String[] nickservCfg;
        String[] redditCfg;
        Config config;
        Bot bot;

        // If the user does not supply a config, then give the them a warning and end the program.
        try {
            config = new Config(args[0]);
        }
        catch(ArrayIndexOutOfBoundsException err) {
            System.err.println("A config is needed.\n" + "java -jar <JarName> <Config>");
            config = null;

            System.exit(0);
        }

        botCfg = config.loadBotSettings();
        nickservCfg = config.loadNickServ();
        redditCfg = config.loadReddit();

        bot = new Bot(botCfg[0], botCfg[1], botCfg[2], config, nickservCfg[0], nickservCfg[1], redditCfg[0], redditCfg[1]);
    }
}
