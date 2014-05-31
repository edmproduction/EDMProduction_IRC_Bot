package com.slize.edmpircbot.listeners;

import com.slize.edmpircbot.utils.Config;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

/**
 *  This class handles connections.
 */
@Slf4j
public class Connection extends ListenerAdapter {
    private String nickServUsername;
    private String nickServPassword;
    private String channels;
    private String preferedNick;

    public Connection(Config config) {
        String[] nickServCfg = config.loadNickServ();
        String[] botCfg = config.loadBotSettings();

        nickServUsername = nickServCfg[0];
        nickServPassword = nickServCfg[1];
        channels = botCfg[1];
        preferedNick = botCfg[0];
    }

    public void onConnect(ConnectEvent event) {
        // Identify with NickServ
        event.getBot().sendIRC().message("NickServ", "IDENTIFY " + nickServUsername + " " + nickServPassword);

        try {
            Thread.sleep(5000); // Sleep for 5 seconds so we get some time for the identify to pass trough.
        }
        catch(InterruptedException err) {
            log.warn(err.getMessage(), err);
        }

        int tries = 0;

        while(!event.getBot().getNick().equals(preferedNick) && tries < 5) {
            log.info("Bot nick is not the same as the one in the config. Changing nick to " + preferedNick);

            event.getBot().sendIRC().message("NickServ", "GHOST " + preferedNick);

            try {
                Thread.sleep(5000); // Sleep for 5 seconds so we get some time for the ghost to pass trough.
            }
            catch(InterruptedException err) {
                log.warn(err.getMessage(), err);
            }

            event.getBot().sendIRC().changeNick(preferedNick);

            tries++;
        }



        // Join channels.
        event.getBot().sendIRC().joinChannel(channels);
    }
}
