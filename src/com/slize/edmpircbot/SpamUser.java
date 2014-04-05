package com.slize.edmpircbot;

class SpamUser {
    protected String nick;
    protected String hostname;
    protected String channel;
    protected int lines;
    protected int kickTime;
    protected int timesKicked;
    protected int timesBanned;
    protected int banTime;
    protected boolean isBanned;


    public SpamUser(String nick, String hostname, String channel) {
        this.nick = nick;
        this.hostname = hostname;
        this.channel = channel;
        this.lines = 1;
        this.kickTime = (int) System.currentTimeMillis();
        this.banTime = (int) System.currentTimeMillis();
        this.timesKicked = 0;
        this.timesBanned = 0;
        this.isBanned = false;
    }
}
