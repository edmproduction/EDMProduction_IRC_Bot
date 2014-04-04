EDMProduction IRC Bot
=====================

This is a IRC bot used for the /r/edmproduction IRC channel.

## Usage

The program requires a config, formatted like this:

  bot.nick=<NICK>
  bot.chan=<IRC_CHANNEL(S)>
  bot.subreddit=<SUBREDDIT(S) (Sepperated by a comma)>
  nickserv.user=<NICKSERV USERNAME>
  nickserv.pass=<NICKSERV PASSWORD>
  reddit.user=<REDDIT USERNAME>
  reddit.pass=<REDDIT PASSWORD>

Place this config in, for example, /res/, then run by typing `java -jar <JARNAME> <CONFIG>`



## Dependencies

* PircBot
* jReddit
