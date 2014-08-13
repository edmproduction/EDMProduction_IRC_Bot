package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class SayCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "say") || !ListenerUtils.isOp(event.getChannel(), event.getUser()))  {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length >= 3) {
            Channel channel = ListenerUtils.getChannel(message[1], event.getBot().getUserChannelDao().getAllChannels());

            if(channel == null) {
                event.respond(Colors.RED + "Error: " + Colors.NORMAL +
                        "Channel, " + message[1] + ", could not be found.");
                return;
            }

            // Get message
            String userMessage = "";

            for(int i = 2; i < message.length; i++) {
                userMessage += message[i] + " ";
            }

            userMessage = userMessage.trim();

            channel.send().message(userMessage);
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax.");
        }
    }

    public String getHelp() {
        return "Makes the bot say something in <channel>. Syntax: " + ListenerUtils.PREFIX + "say <channel> <message>";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }
}
