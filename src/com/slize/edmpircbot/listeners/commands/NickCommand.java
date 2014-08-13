package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class NickCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "nick") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 2) {
            event.getBot().sendIRC().changeNick(message[1]);
        }
        else {
            event.respond(Colors.RED + "Error:" + Colors.NORMAL + "Invalid syntax");
        }
    }

    public String getHelp() {
        return "Changes the bot's nick. Syntax: " + ListenerUtils.PREFIX + "nick <nick>";
    }

    @Override
    public boolean isOpOnlyCommand() {
        return true;
    }
}
