package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.listeners.AntiSpam;
import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class SetCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "set") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 3) {
            if(message[1].equalsIgnoreCase("spamLines")) {
                AntiSpam.setSpamLines(Integer.parseInt(message[2]));
                event.respond("spamLines set to " + message[2]);
            }
            else if(message[2].equalsIgnoreCase("spamTime")) {
                AntiSpam.setSpamTime(Integer.parseInt(message[2]));
                event.respond("spamTime set to " + message[2]);
            }
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    public String getHelp() {
        return "Sets a variable to a value. Syntax: " + ListenerUtils.PREFIX + "set <variable> <value>";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }

}
