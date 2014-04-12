package com.slize.edmpircbot.listeners;

import org.pircbotx.Channel;
import org.pircbotx.User;

class SpamUser {
    public User user;
    public Channel channel;
    public int lines;
    public int kickTime;
    public int banTime;
    public int lastKickTime;
    public int timesBanned;
    public int timesKicked;
    public boolean isBanned;


    public SpamUser(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
        this.lines = 1;
        this.kickTime = (int) System.currentTimeMillis();
        this.banTime = (int) System.currentTimeMillis();
        this.lastKickTime = (int) System.currentTimeMillis();
        this.timesKicked = 0;
        this.timesBanned = 0;
        this.isBanned = false;
    }
}
