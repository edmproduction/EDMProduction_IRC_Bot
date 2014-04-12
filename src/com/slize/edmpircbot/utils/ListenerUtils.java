package com.slize.edmpircbot.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;

public final class ListenerUtils {
    public static char PREFIX = '@';

    private ListenerUtils() {
        // We don't want anyone to make a instance of this class.
    }

    public static boolean isCommand(String message, String command) {
        return message.split(" ")[0].equalsIgnoreCase(PREFIX + command);
    }

    public static boolean isOp(Channel channel, User user) {
        ImmutableSortedSet<User> ops = channel.getOps();

        return ops.contains(user);
    }

    public static Channel getChannel(String channelName, ImmutableSortedSet<Channel> channels) {
        for(Channel channel : channels) {
            if(channel.getName().equalsIgnoreCase(channelName)) {
                return channel;
            }
        }
        // If channel is not found, then return null
        return null;
    }

    public static User getUser(String nick, ImmutableSortedSet<User> users) {
        for(User user : users) {
            if(user.getNick().equalsIgnoreCase(nick)) {
                return user;
            }
        }
        // If the user is not found, then return null.
        return null;
    }

    public static Listener getListener(ImmutableSet<Listener<PircBotX>> listeners, String wantedListener) {
        for(Listener listener : listeners) {
            if(listener.getClass().getSimpleName().equalsIgnoreCase(wantedListener)) {
                return listener;
            }
        }
        // If the listener is not found, return null.
        return null;
    }


}
