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
=======
threadbot
=========

Threadbot for reddit, currently tailored towards /r/edmproduction with a goal of making it generic.

In its current state, it posts a thread depending on what day it is (I've hardcoded in Monday-Thursday even though I really shouldn't have, and I need to include the other days of the week. This will change!). The title and text of the post are stored in the config file. If the bot has mod privileges it'll try and distinguish the post. 

The script requires [Requests](http://docs.python-requests.org/en/latest/#), and has been tested with Python 2.6 and 2.7. The config file must be defined by -c:

	$ python threadbot.py -c threadbot.cfg

This is what a config file should look like for /r/myWonderfulTerrificSubreddit


	[threadbot]
	subreddit = myWonderfulTerrificSubreddit
	username = mySuperCoolUsername
	password = mySuperSecurePassword
	debug_day = 0 # if this parameter is there, it overrides the weekday (0=Monday, 1=Tuesday, ...)

	[monday]

	title = "monday's thread!" 
	# the script will append the date to the title - "monday's thread! (March 14)"

	text = "this is a thread and it will soon be distinguished"

I plan to add something to the config parsing that treats an option as a list and then uses that list to iterate through config files, so you can have as many threads as you want and on any day.
