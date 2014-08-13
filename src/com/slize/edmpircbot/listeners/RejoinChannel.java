package com.slize.edmpircbot.listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;

public class RejoinChannel extends ListenerAdapter {

    public void onKick(KickEvent event) {
        if(!event.getUser().getNick().equals(event.getBot().getNick())) {
            return;
        }

        event.getBot().sendIRC().joinChannel(event.getChannel().getName());
    }
}
