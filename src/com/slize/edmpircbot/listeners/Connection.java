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

    public Connection(Config config) {
        String[] nickServCfg = config.loadNickServ();
        String[] botCfg = config.loadBotSettings();

        nickServUsername = nickServCfg[0];
        nickServPassword = nickServCfg[1];
        channels = botCfg[1];
    }

    public void onConnect(ConnectEvent event) throws Exception {
        // Identify with NickServ
        event.getBot().sendIRC().message("NickServ", "IDENTIFY " + nickServUsername + " " + nickServPassword);

        try {
            Thread.sleep(10000); // Sleep for 10 seconds so we get some time for the identify to pass trough.
        }
        catch(InterruptedException err) {
            log.warn(err.getMessage(), err);
        }

        // Join channels.
        event.getBot().sendIRC().joinChannel(channels);
    }
}
