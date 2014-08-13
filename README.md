EDMProduction IRC Bot
=====================

This is a IRC bot used for the /r/edmproduction IRC channel.

## Usage

The program requires a config, formatted like this:

    bot.nick=<Nick>
    bot.chan=<IRC channels(s) (Separated by a comma and no spaces)>

    nickserv.user=<NickServ Username>
    nickserv.pass=<NickServ Password>

    reddit.user=<Reddit Username>
    reddit.pass=<Reddit Password>
    reddit.subreddit=<Subreddit(s) (Separated by a comma and no spaces)>

    spam.time=<Milliseconds>
    spam.lines=<Lines>
    spam.bantime.1=<Milliseconds>
    spam.bantime.2=<Milliseconds>
    spam.bantime.3=<Milliseconds>
    spam.bantime.4=<Milliseconds> (Defaults to -1 if nothing is supplied (permanent ban))

Place this config in the same folder as the compiled jar. If you call the config something else than `config.cfg`, then the program by typing `java -jar <jarname> -c <config>`



## Dependencies

* PircBotX
* jReddit
