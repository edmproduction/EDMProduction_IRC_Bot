package com.slize.edmpircbot;

import com.slize.edmpircbot.listeners.AntiSpam;
import com.slize.edmpircbot.listeners.Connection;
import com.slize.edmpircbot.listeners.PrintNewPosts;
import com.slize.edmpircbot.listeners.commands.*;
import com.slize.edmpircbot.utils.Config;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        Config config;

        // If the user does not supply a config, then give the them a warning and end the program.
        try {
            config = new Config(args[0]);
        }
        catch(ArrayIndexOutOfBoundsException err) {
            System.err.println("A config is needed.\n" + "java -jar <jarName> <config>");
            config = null;

            System.exit(1);
        }

        String[] botCfg = config.loadBotSettings();

        Configuration configuration = new Configuration.Builder()
                .setServerHostname("irc.freenode.org")
                .setName(botCfg[0])
                .setLogin(botCfg[0])
                .setAutoNickChange(true)
                .setAutoReconnect(true)
                .setCapEnabled(true)

                // Other
                .addListener(new Connection(config))
                .addListener(new AntiSpam(config))
                .addListener(new PrintNewPosts(config))
                // Commands
                .addListener(new HelpCommand())
                .addListener(new KickCommand())
                .addListener(new BanCommand())
                .addListener(new PrintCommand())
                .addListener(new SetCommand())
                .addListener(new SilentCommand(config))
                .addListener(new LogCommand())
                .addListener(new FrequencyCommand())
                .addListener(new NoteCommand())
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
