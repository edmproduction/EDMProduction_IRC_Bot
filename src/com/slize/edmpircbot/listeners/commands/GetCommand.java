package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.listeners.AntiSpam;
import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class GetCommand extends ListenerAdapter implements Command {

    @Override
    public void onMessage(MessageEvent event) {
        if(!ListenerUtils.isCommand(event.getMessage(), "get") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 2) {
            if(message[1].equalsIgnoreCase("spamLines")) {
                event.respond("spamLines: " + AntiSpam.getSpamLines());
            }
            else if(message[1].equalsIgnoreCase("spamTime")) {
                event.respond("spamTime: " + AntiSpam.getSpamTime());
            }
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax.");
        }
    }

    public boolean isOpOnlyCommand() {
        return true;
    }

    public String getHelp() {
        return "Returns <variable>. Syntax: " + ListenerUtils.PREFIX + "get <variable>";
    }
}
