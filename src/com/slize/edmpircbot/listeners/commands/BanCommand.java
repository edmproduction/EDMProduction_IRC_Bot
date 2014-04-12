package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *  Syntax: @ban \<channel\> \<nick\> [reason]
 */
@Slf4j
public class BanCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) {
        if(!ListenerUtils.isCommand(event.getMessage(), "ban") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length > 2) {
            // Get channel and user.
            Channel channel = ListenerUtils.getChannel(message[1], event.getBot().getUserChannelDao().getAllChannels());

            if(channel == null) {
                event.respond(Colors.RED + "Error: " + Colors.NORMAL +
                        "Channel " + message[1] + " could not be found.");
                return;
            }

            User user = ListenerUtils.getUser(message[2], channel.getUsers());

            if(user == null) {
                event.respond(message[2] + " not found in " + channel.getName());
                return;
            }

            // Ban user.
            if(message.length > 3) {
                String kickMessage = "";

                for(int i = 3; i < message.length; i++) {
                    kickMessage += message[i] + " ";
                }

                channel.send().ban(user.getHostmask());
                channel.send().kick(user, kickMessage.trim());
            }
            else if(message.length == 3) {
                channel.send().ban(user.getHostmask());
                channel.send().kick(user, "You have been banned from this channel.");
            }

            event.respond(user.getNick() + " banned from " + channel.getName());
            log.info(user.getNick() + " banned from " + channel.getName());
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }

    }

    public String getHelp() {
        return "Bans <nick> from <channel>. Syntax: " + ListenerUtils.PREFIX + "ban <channel> <nick> [reason]";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }
}
