package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.listeners.PrintNewPosts;
import com.slize.edmpircbot.utils.Config;
import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;

public class SilentCommand extends ListenerAdapter implements Command {
    Config config;
    String[] subreddits;

    public SilentCommand(Config config) {
        this.config = config;
        this.subreddits = config.loadBotSettings()[1].split(",");
    }

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "silent") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");
        ArrayList<Boolean> silentMode = PrintNewPosts.getSilentMode();

        if(message.length == 3) {
            for(int i = 0; i < subreddits.length; i++) {
                if(message[2].equals("on") && !silentMode.get(i)) {
                    silentMode.set(i, true);

                    event.respond("Silent mode is now on for " + message[1] + ".");
                    break;
                }
                else if(message[2].equals("off") && silentMode.get(i)) {
                    silentMode.set(i, false);

                    event.respond("Silent mode is now off for " + message[1] + ".");
                    break;
                }
                else {
                    event.respond("Silent mode is already " + message[2] + " for this subreddit.");
                }
            }
        }
        else if(message.length == 2) {
            if(message[1].equals("on")) {
                for(int i = 0; i < silentMode.size() - 1; i++) {
                    silentMode.set(i, true);
                }

                event.respond("Silent mode is now on for all subreddits.");
            }
            else if(message[1].equals("off")) {
                for(int i = 0; i < silentMode.size() - 1; i++) {
                    silentMode.set(i, false);
                }

                event.respond("Silent mode is now off for all subreddits.");
            }
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
            return;
        }

        PrintNewPosts.setSilentMode(silentMode);
    }

    public String getHelp() {
        return "Turns silent mode on or off for a subreddit. Syntax: " + ListenerUtils.PREFIX + "silent <subreddit> <on/off>";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }
}
