package com.slize.edmpircbot.listeners;

import com.slize.edmpircbot.utils.Config;
import com.slize.edmpircbot.utils.ListenerUtils;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.ServerPingEvent;

import java.util.ArrayList;

@Slf4j
public class AntiSpam extends ListenerAdapter {
    private ArrayList<SpamUser> spamUsers;

    private static int spamTime;
    private static int spamLines;
    private int[] banTime;
    private static int resetTime;

    public AntiSpam(Config config) {
        this.banTime = new int[4];

        this.spamUsers = new ArrayList<SpamUser>();
        spamTime = config.loadSpamSettings()[0];
        spamLines = config.loadSpamSettings()[1];
        resetTime = config.loadSpamSettings()[2];

        for(int i = 0; i < banTime.length; i++) {
            this.banTime[i] = config.loadSpamSettings()[i + 3];
        }
    }

    public void onMessage(MessageEvent event) {
        if(ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        boolean foundUser = false;

        for(SpamUser spamUser : spamUsers) {
            if(spamUser.user.getNick().equals(event.getUser().getNick()) && spamUser.channel.equals(event.getChannel())) {
                foundUser = true;
                spamUser.lines++;

                if((int)System.currentTimeMillis() - spamUser.kickTime <= spamTime && spamUser.lines >= spamLines &&
                        !spamUser.isBanned) {
                    // If the user has more lines then "spamLines" and it's inside spamTime, then kick the user.
                    if(spamUser.timesKicked >= 3) {
                        // If the user has already been kicked 3 times, then ban him/her for x minutes.

                        spamUser.isBanned = true;

                        switch (spamUser.timesBanned) {
                            // If the user has been
                            case 0: spamUser.banTime = banTime[0];
                                break;
                            case 1: spamUser.banTime = banTime[1];
                                break;
                            case 2: spamUser.banTime = banTime[2];
                                break;
                            case 3: spamUser.banTime = banTime[3]; // Permanent ban.
                                break;
                        }

                        spamUser.timesBanned++;
                        spamUser.timesKicked = 0;
                        spamUser.lastKickTime = (int) System.currentTimeMillis();

                        event.getChannel().send().ban(spamUser.user.getHostmask());
                        event.getChannel().send().kick(event.getUser(),
                                "You have been banned for " + (spamUser.banTime / 1000 / 60) +
                                " minutes for flooding the channel.");

                        log.info("Banned " + spamUser.user.getNick() + " from " + event.getChannel().getName() +
                                "for " + (spamUser.banTime / 1000 / 60) + "minutes for flooding the channel.");

                        return;
                    }

                    event.getChannel().send().kick(event.getUser(), "Please don't flood the channel.");

                    spamUser.timesKicked++;
                    spamUser.lines = 0;
                    spamUser.kickTime = (int) System.currentTimeMillis();
                    spamUser.lastKickTime = (int) System.currentTimeMillis();

                    log.info("Kicked " + spamUser.user.getNick() + " from " + event.getChannel().getName() +
                            "for flooding the channel.");

                    return;
                }
                else if((int)System.currentTimeMillis() - spamUser.kickTime >= spamTime) {
                    // If the time since the last "kickTime" is more then spamTime then reset lines and kickTime.
                    spamUser.kickTime = (int) System.currentTimeMillis();
                    spamUser.lines = 0;
                }
            }
        }

        if(!foundUser) {
            // If the user was not found, then create a new user.
            spamUsers.add(new SpamUser(event.getUser(), event.getChannel()));
            log.debug("Creating new spamUser for " + event.getUser().getNick());
        }
    }

    public void onRemoveChannelBan(RemoveChannelBanEvent event) {
        // If the user gets unbanned manually, then remove his/her banned status.
        for (SpamUser spamUser : spamUsers) {
            if (spamUser.isBanned && spamUser.user.getNick().equals(event.getUser().getNick())) {
                    spamUser.isBanned = false;
            }
        }
    }

    public void onServerPing(ServerPingEvent event) {
        // Check if users ban time is over.
        for (SpamUser spamUser : spamUsers) {
            if (spamUser.isBanned) {
                if(spamUser.banTime < (int) System.currentTimeMillis() - spamUser.lastKickTime && spamUser.banTime != -1) {
                    // Unban user.
                    event.getBot().getUserChannelDao().getChannel(spamUser.channel.getName()).send()
                            .unBan(spamUser.user.getHostmask());
                    spamUser.isBanned = false;

                    log.info("Unbanning " + spamUser.user.getNick());
                }
                else if(spamUser.lastKickTime > (int) System.currentTimeMillis() + resetTime &&
                        (spamUser.banTime != -1 || spamUser.channel.getNormalUsers().contains(spamUser.user))) {
                    // Reset user timers if "resetTime" milliseconds have passed.
                    log.info("Resetting times kicked and banned for " + spamUser.user.getNick());

                    spamUser.timesBanned = 0;
                    spamUser.timesKicked = 0;
                }
            }
        }
    }

    public static void setSpamTime(int newValue) {
        spamTime = newValue;
    }

    public static void setSpamLines(int newValue) {
        spamLines = newValue;
    }
}
