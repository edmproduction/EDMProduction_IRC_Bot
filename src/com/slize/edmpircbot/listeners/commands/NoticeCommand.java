package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class NoticeCommand extends ListenerAdapter implements Command {


    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "notice") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length >= 3) {
            Channel channel = null;
            User user = null;

            if(message[1].startsWith("#")) {
                channel = ListenerUtils.getChannel(message[1], event.getBot().getUserChannelDao().getAllChannels());
            }
            else {
                user = event.getBot().getUserChannelDao().getUser(message[1]);
            }

            // Get notice message
            String noticeMessage = "";

            for(int i = 2; i < message.length; i++) {
                noticeMessage += message[i] + " ";
            }

            noticeMessage = noticeMessage.trim();

            // Send notice.
            if(channel != null) {
                channel.send().notice(noticeMessage);
            }
            else if(user != null) {
                user.send().notice(noticeMessage);
            }
            else {
                event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Could not find user/channel.");
            }
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    public boolean isOpOnlyCommand() {
        return true;
    }

    public String getHelp() {
        return "Sends a notice to <channel/nick>. Syntax: " + ListenerUtils.PREFIX + "notice <channel/nick> <message>";
    }
}
