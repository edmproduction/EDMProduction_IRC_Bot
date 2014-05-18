package com.slize.edmpircbot;

import com.slize.edmpircbot.listeners.*;
import com.slize.edmpircbot.listeners.commands.*;
import com.slize.edmpircbot.utils.Config;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import java.util.Arrays;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        Config config;

        // Handle command line arguments
        if(Arrays.asList(args).contains("-c")) {
            if(Arrays.asList(args).indexOf("-c") + 1 < args.length) {
                config = new Config(args[Arrays.asList(args).indexOf("-c") + 1]);
            }
            else {
                System.out.println("-c requires a file.");

                config = null;
                System.exit(1);
            }
        }
        else {
            config = new Config("./config.cfg");
        }

        String[] botCfg = config.loadBotSettings();

        Configuration configuration = new Configuration.Builder()
                .setServerHostname("irc.freenode.org")
                .setName(botCfg[0])
                .setRealName("Moderator bot for #edmproduction")
                .setLogin(botCfg[0])
                .setAutoNickChange(true)
                .setAutoReconnect(true)
                .setCapEnabled(true)

                // Other
                .addListener(new Connection(config))
                .addListener(new AntiSpam(config))
                .addListener(new PrintNewPosts(config))
                .addListener(new UrlTitlePoster())
                .addListener(new Response())
                .addListener(new RejoinChannel())

                // Commands
                .addListener(new HelpCommand())
                .addListener(new KickCommand())
                .addListener(new BanCommand())
                .addListener(new NoticeCommand())
                .addListener(new PrintCommand())
                .addListener(new SetCommand())
                .addListener(new SilentCommand(config))
                .addListener(new LogCommand())
                .addListener(new NoteCommand())
                .addListener(new FrequencyCommand())
                .addListener(new GetCommand())
                .addListener(new ScaleCommand())

                .buildConfiguration();
        PircBotX bot = new PircBotX(configuration);

        try {
            bot.startBot();
        }
        catch(Exception err) {
            err.printStackTrace();
        }
    }
}
