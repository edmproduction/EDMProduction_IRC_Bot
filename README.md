EDMProduction IRC Bot
=====================

This is a IRC bot used for the /r/edmproduction IRC channel.

## Usage

The program requires a config, formatted like this:

    bot.nick=<Nick>
    bot.chan=<IRC channels(s) (Sepperated by a comma and no spaces)>
    bot.subreddit=<Subreddit(s) (Sepperated by a comma and no spaces)>

    nickserv.user=<NickServ Username>
    nickserv.pass=<NickServ Password>

    reddit.user=<Reddit Username>
    reddit.pass=<Reddit Password>

    spam.time=<Milliseconds>
    spam.lines=<Lines>
    spam.bantime.1=<Milliseconds>
    spam.bantime.2=<Milliseconds>
    spam.bantime.3=<Milliseconds>

Place this config in, for example, /res/, then run by typing `java -jar <jarname> <config>`



## Dependencies

* PircBot
* jReddit
