threadbot
=========

Threadbot for reddit, currently tailored towards /r/edmproduction with a goal of making it generic.

This is what a config file should look like for /r/myWonderfulTerrificSubreddit


	[threadbot]
	subreddit = myWonderfulTerrificSubreddit
	username = mySuperCoolUsername
	password = mySuperSecurePassword
	debug_day = 0 # if this parameter is there, it overrides the weekday (0=Monday, 1=Tuesday, ...)

	[monday]
	title = "monday's thread!" # the script will append the date - "monday's thread! (March 14)"
	text = "this is a thread and it will soon be distinguished"

I plan to add something to the config parsing that treats an option as a list and then uses that list to iterate through config files, so you can have as many threads as you want and on any day.