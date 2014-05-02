package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.ServerPingEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Syntax: @ban \<channel\> \<nick\> [reason]
 */
@Slf4j
public class BanCommand extends ListenerAdapter implements Command {
    private ArrayList<BannedUser> bannedUsers = new ArrayList<BannedUser>();

    private class BannedUser {
        public User user;
        public Channel channel;
        public long banTime;
        public long timeBanned;

        public BannedUser(Channel channel, User user, long banTime, long timeBanned) {
            this.user = user;
            this.channel = channel;
            this.banTime = banTime;
            this.timeBanned = timeBanned;
        }
    }

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
            String kickMessage = "You have been banned from this channel.";
            String timeMessage = "";

            // Check for reason. -r "Reason"
            if(Arrays.asList(message).contains("-r")) {
                if(Arrays.asList(message).indexOf("-r") + 1 < message.length &&
                        !message[Arrays.asList(message).indexOf("-r") + 1].equalsIgnoreCase("-t")) {
                    int indexOfReasonStart = event.getMessage().indexOf("\"", event.getMessage().indexOf("-r"));
                    int indexOfLastQuotationMark = event.getMessage().indexOf("\"", indexOfReasonStart + 1);

                    log.debug("start: " + indexOfReasonStart + ", end: " + indexOfLastQuotationMark);

                    if(indexOfReasonStart != -1 && indexOfLastQuotationMark != -1) {
                        kickMessage += " " + event.getMessage().substring(indexOfReasonStart + 1, indexOfLastQuotationMark);
                    }
                    else {
                        event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Missing quotation mark(s).");
                        return;
                    }
                }
                else {
                    event.respond(Colors.RED + "Error: " + Colors.NORMAL + "-r requires text in quotation marks after it.");
                    return;
                }
            }

            // Check for time. -t HH:mm:ss
            if(Arrays.asList(message).contains("-t")) {
                if(Arrays.asList(message).indexOf("-t") + 1 < message.length &&
                        !message[Arrays.asList(message).indexOf("-t") + 1].equalsIgnoreCase("-r")) {
                    long time;

                    try {
                        time = parseInterval(message[Arrays.asList(message).indexOf("-t") + 1]);
                    }
                    catch(IllegalArgumentException err) {
                        event.respond(Colors.RED + "Error: " + Colors.NORMAL + "-t requires the format HH:mm:ss");
                        return;
                    }

                    bannedUsers.add(new BannedUser(channel, user, time, System.currentTimeMillis()));
                    timeMessage = " (For " + time / 1000 / 60 + " minutes)";
                    kickMessage += timeMessage;
                }
                else {
                    event.respond(Colors.RED + "Error: " + Colors.NORMAL + "-t requires the amount of time after it.");
                    return;
                }
            }

            channel.send().ban(user.getHostmask());
            channel.send().kick(user, kickMessage);

            event.respond(user.getNick() + " banned from " + channel.getName() + timeMessage);
            log.info(user.getNick() + " banned from " + channel.getName() + timeMessage);
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }

    }

    public void onRemoveChannelBan(RemoveChannelBanEvent event) {
        // If the user gets unbanned manually, then remove his/her banned status.
        for (BannedUser bannedUser : bannedUsers) {
            if (bannedUser.user.getNick().equals(event.getUser().getNick())) {
                bannedUsers.remove(bannedUser);
                log.debug("Removed " + bannedUser.user.getNick() + " from bannedUsers.");
            }
        }
    }

    public void onServerPing(ServerPingEvent event) {
        // Check if users ban time is over.
        for (BannedUser bannedUser : bannedUsers) {
            if(bannedUser.banTime < System.currentTimeMillis() - bannedUser.timeBanned) {
                // Unban user.
                event.getBot().getUserChannelDao().getChannel(bannedUser.channel.getName()).send()
                        .unBan(bannedUser.user.getHostmask());
                bannedUsers.remove(bannedUser);

                log.info("Unbanning " + bannedUser.user.getNick());
            }
        }
    }

    private static long parseInterval(String time) {
        final Pattern p = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})$");
        final Matcher m = p.matcher(time);

        if(m.matches()) {
            final long hr = Long.parseLong(m.group(1)) * TimeUnit.HOURS.toMillis(1);
            final long min = Long.parseLong(m.group(2)) * TimeUnit.MINUTES.toMillis(1);
            final long sec = Long.parseLong(m.group(3)) * TimeUnit.SECONDS.toMillis(1);
            return hr + min + sec;
        }
        else {
            throw new IllegalArgumentException(time + " is not a supported interval format.");
        }
    }

    public String getHelp() {
        return "Bans <nick> from <channel>. " +
               "Example: " + ListenerUtils.PREFIX + "ban #edmproduction lumadroid -t 00:15:00 -r \"Your octatrack is here.\". " +
               "Syntax: " + ListenerUtils.PREFIX + "ban <channel> <nick> [-t HH:mm:ss] [-r \"reason\"]";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }
}
