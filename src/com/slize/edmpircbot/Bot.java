package com.slize.edmpircbot;

import com.github.jreddit.submissions.Submission;
import org.jibble.pircbot.*;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.logging.*;

public class Bot extends PircBot {
    private final static Logger LOGGER = Logger.getLogger(Bot.class.getName());

    private String host;
    private Config config;

    private Reddit reddit;
    private ArrayList<Submission[]> lastSubmissions = new ArrayList<Submission[]>();
    private ArrayList<Boolean> silentMode = new ArrayList<Boolean>();

    private ArrayList<SpamUser> spamUsers = new ArrayList<SpamUser>();
    private int spamTime;
    private int spamLines;

    public Bot(String nick, String channel, String subreddit, Config config, String nickServUsername, String nickServPassword,
               String redditUsername, String redditPassword) throws Exception {
        try {
            FileHandler fh;
            fh = new FileHandler("bot.log");
            LOGGER.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        }
        catch(Exception err) {
            err.printStackTrace();
        }

        this.setVerbose(true);

        this.config = config;
        this.host = "irc.freenode.net";

        this.setName(nick);
        this.connect(this.host);
        this.sendMessage("NickServ", "IDENTIFY " + nickServUsername + " " + nickServPassword);
        Thread.sleep(10000); // Sleep for 10 seconds so we get some kickTime for the identify to pass trough.
        this.joinChannel(channel);

        this.reddit = new Reddit(redditUsername, redditPassword);

        String[] subreddits = subreddit.split(",");
        for(String ignored : subreddits) {
            silentMode.add(false);
        }

        this.spamTime = Integer.parseInt(config.loadSpamSettings()[0]);
        this.spamLines = Integer.parseInt(config.loadSpamSettings()[1]);
    }

    protected void onMessage(String channel, String sender, String login, String hostname, String message)  {
        String[] messageSplit = message.split(" ");
        User user = null;


        // Get user, so we can check later if the user is a op.
        for(User tmpUser : getUsers(channel)) {
            if(tmpUser.equals(sender)) {
                user = tmpUser;
                break;
            }
        }

        // Check if the user is spamming.
        if(!user.isOp()) {
            boolean foundUser = false;

            if(spamUsers.size() == 0) {
                spamUsers.add(new SpamUser(sender, hostname, channel));
            }

            for(int i = 0; i < spamUsers.size() - 1; i++) {
                if(spamUsers.get(i).nick.equals(sender) && spamUsers.get(i).channel.equals(channel)) {
                    spamUsers.get(i).lines++;

                    LOGGER.finest("Found user");

                    System.err.println("DEBUG: times kicked = " + spamUsers.get(i).timesKicked);

                    if((int)System.currentTimeMillis() - spamUsers.get(i).kickTime <= spamTime &&
                            spamUsers.get(i).lines >= spamLines) {
                        if(spamUsers.get(i).timesKicked >= 3) {
                            ban(channel, "*!*@" + spamUsers.get(i).hostname);

                            switch (spamUsers.get(i).timesBanned) {
                                case 0: spamUsers.get(i).banTime = Integer.parseInt(config.loadSpamSettings()[2]);
                                        break;
                                case 1: spamUsers.get(i).banTime = Integer.parseInt(config.loadSpamSettings()[3]);
                                        break;
                                case 2: spamUsers.get(i).banTime = Integer.parseInt(config.loadSpamSettings()[4]);
                                        break;
                                case 3: spamUsers.get(i).banTime = -1; // Permanent ban.
                                        break;
                            }

                            spamUsers.get(i).timesBanned++;
                            spamUsers.get(i).timesKicked = 0;
                            spamUsers.get(i).isBanned = true;

                            kick(channel, sender, "You have been banned for " + (spamUsers.get(i).banTime / 1000 / 60) +
                                                  " minutes for flooding the channel.");

                            LOGGER.info("Banning " + sender + " for " + spamUsers.get(i).banTime +
                                        " milliseconds for spamming the channel.");

                            return;
                        }

                        kick(channel, sender, "Please don't flood the channel.");
                        LOGGER.info("Kicking " + sender + " for spamming the channel.");

                        spamUsers.get(i).timesKicked++;
                        spamUsers.get(i).kickTime = (int) System.currentTimeMillis();
                        spamUsers.get(i).lines = 0;

                        return;
                    }
                    else if((int)System.currentTimeMillis() - spamUsers.get(i).kickTime >= spamTime) {
                        LOGGER.finest("Replacing time and lines.");
                        spamUsers.get(i).kickTime = (int) System.currentTimeMillis();
                        spamUsers.get(i).lines = 0;
                    }

                    foundUser = true;
                }
            }

            if(!foundUser) {
                LOGGER.finest("Creating new user");
                spamUsers.add(new SpamUser(sender, hostname, channel));
            }
        }

        try {
            // @help [command]
            if(messageSplit[0].equalsIgnoreCase("@help")) {
                try {
                    if(messageSplit[1].equalsIgnoreCase("print") && user.isOp()) {
                        sendMessage(channel, Colors.BOLD + "@print [subreddit]; " + Colors.NORMAL +
                                             "If nothing is printed, it means that there are no new submissions.");
                    }
                    else if(messageSplit[1].equalsIgnoreCase("silent") && user.isOp()) {
                        sendMessage(channel, Colors.BOLD + "@silent [subreddit] <on/off>; " + Colors.NORMAL +
                                             "Turns automatic checks for new submissions on [subreddit] off.");
                    }
                    else if(messageSplit[1].equalsIgnoreCase("log") && user.isOp()) {
                        sendMessage(channel, Colors.BOLD + "@log <info/fine/finer/finest>; " + Colors.NORMAL +
                                "Changes log mode.");
                    }
                    else if(messageSplit[1].equalsIgnoreCase("kick") && user.isOp()) {
                        sendMessage(channel, Colors.BOLD + "@kick <nick> <channel> [reason]; " + Colors.NORMAL +
                                "Kicks <nick> from [channel]. If [channel] is not present, then it will kick from #edmproduction.");
                    }
                    else if(messageSplit[1].equalsIgnoreCase("ban") && user.isOp()) {
                        sendMessage(channel, Colors.BOLD + "@ban <nick> <channel> [reason]; " + Colors.NORMAL +
                                "Bans <nick> from [channel]. If [channel] is not present, then it will ban from #edmproduction.");
                    }
                    else if(messageSplit[1].equalsIgnoreCase("set") && user.isOp()) {
                        sendMessage(channel, Colors.BOLD + "@set <variable> <value>; " + Colors.NORMAL +
                                "Sets the value of <variable>. Available variables are: spamTime, spamLines");
                    }
                    else if(messageSplit[1].equalsIgnoreCase("frequency")) {
                        sendMessage(channel, Colors.BOLD + "@frequency <frequency(Hz)>; " + Colors.NORMAL +
                                Colors.RED + "Not implemented." + Colors.NORMAL + "Returns the frequency's corresponding note.");
                    }
                    else {
                        sendMessage(channel, "Command not found.");
                    }
                }
                catch(ArrayIndexOutOfBoundsException err) {
                    String commands = "";

                    // Operator commands
                    if(user.isOp()) {
                        commands = "print, silent, log, kick, :";
                    }

                    commands += "frequency (To be implemented)"; // User commands

                    sendMessage(channel, "Available commands are: " + commands + ". Do @help <command> for more help.");
                }
            }
            // @kick <nick> <channel> [reason]
            else if(messageSplit[0].equalsIgnoreCase("@kick") && user.isOp()) {
                try {
                    String kickMessage = "";

                    for(int i = 3; i < messageSplit.length; i++) {
                        kickMessage += messageSplit[i] + " ";
                    }

                    kick(messageSplit[2], messageSplit[1], kickMessage);
                }
                catch(ArrayIndexOutOfBoundsException err1) {
                    try {
                        kick(messageSplit[2], messageSplit[1]);
                    }
                    catch(ArrayIndexOutOfBoundsException err2) {
                        sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL +
                                "Invalid Syntax. @kick <nick> <channel> [reason]");
                    }

                }

            }
            // @ban <nick> <channel> [reason]
            else if(messageSplit[0].equalsIgnoreCase("@ban") && user.isOp()) {
                try {
                    String kickMessage = "";

                    for(int i = 3; i < messageSplit.length; i++) {
                        kickMessage += messageSplit[i] + " ";
                    }

                    ban(messageSplit[2], messageSplit[1] + "!*@*");
                    kick(messageSplit[2], messageSplit[1], kickMessage);
                }
                catch(ArrayIndexOutOfBoundsException err1) {
                    try {
                        ban(messageSplit[2], messageSplit[1]);
                        kick(messageSplit[2], messageSplit[1], "You have been banned from this channel.");
                    }
                    catch(ArrayIndexOutOfBoundsException err2) {
                        sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL +
                                    "Invalid Syntax. @ban <nick> <channel> [reason]");
                    }
                }
            }
            // @print [subreddit]
            else if(messageSplit[0].equalsIgnoreCase("@print") && user.isOp()) {
                try {
                    printNewSubmissions(channel, messageSplit[1]);
                }
                catch(ArrayIndexOutOfBoundsException err) {
                    // Print new submissions from main subreddit.
                    printNewSubmissions(channel, config.loadBotSettings()[2].split(",")[0]);
                }
            }
            // @silent [subreddit] <on/off>
            else if(messageSplit[0].equalsIgnoreCase("@silent") && user.isOp()) {
                try {
                    String[] subreddits = config.loadBotSettings()[2].split(",");

                    for(int i = 0; i < subreddits.length; i++) {
                        if(messageSplit[2].equals("on") && !silentMode.get(i)) {
                            silentMode.set(i, true);

                            LOGGER.finer("Silent Mode: On");
                            sendMessage(channel, "Silent mode is now on for " + messageSplit[1] + ".");
                        }
                        else if(messageSplit[2].equals("off") && silentMode.get(i)) {
                            silentMode.set(i, false);

                            LOGGER.finer("Silent Mode: Off");
                            sendMessage(channel, "Silent mode is now off for " + messageSplit[1] + ".");
                        }
                        else {
                            sendMessage(channel, "Silent mode is already turned " + messageSplit[2] + " for this subreddit.");
                        }
                    }
                }
                catch(ArrayIndexOutOfBoundsException err) {
                    try {
                        if(messageSplit[1].equals("on")) {
                            for(int i = 0; i < silentMode.size() - 1; i++) {
                                silentMode.set(i, true);
                            }

                            LOGGER.fine("Silent Mode: On");
                            sendMessage(channel, "Silent mode is now on for all subreddits.");
                        }
                        else if(messageSplit[1].equals("off")) {
                            for(int i = 0; i < silentMode.size() - 1; i++) {
                                silentMode.set(i, false);
                            }

                            LOGGER.fine("Silent Mode: Off");
                            sendMessage(channel, "Silent mode is now off for all subreddits.");
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException err2) {
                        sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax. @silent [subreddit] <on/off>");
                    }
                }
            }
            // @log <mode>
            else if(messageSplit[0].equalsIgnoreCase("@log") && user.isOp()) {
                try {
                    if(messageSplit[1].equalsIgnoreCase("info")) {
                        setLoggerLevel(Level.INFO);
                        sendMessage(channel, "Logging level set to " + LOGGER.getLevel());
                    }
                    else if(messageSplit[1].equalsIgnoreCase("fine")) {
                        setLoggerLevel(Level.FINE);
                        sendMessage(channel, "Logging level set to " + LOGGER.getLevel());
                    }
                    else if(messageSplit[1].equalsIgnoreCase("finer")) {
                        setLoggerLevel(Level.FINER);
                        sendMessage(channel, "Logging level set to " + LOGGER.getLevel());
                    }
                    else if(messageSplit[1].equalsIgnoreCase("finest")) {
                        setLoggerLevel(Level.FINEST);
                        sendMessage(channel, "Logging level set to " + LOGGER.getLevel());
                    }
                    else if(messageSplit[1].equalsIgnoreCase("all")) {
                        setLoggerLevel(Level.ALL);
                        sendMessage(channel, "Logging level set to " + LOGGER.getLevel());
                    }
                    else {
                        sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid mode.");
                    }
                }
                catch(ArrayIndexOutOfBoundsException err) {
                    sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax. @log <mode>");
                }
            }
            else if(messageSplit[0].equalsIgnoreCase("@set") && user.isOp()) {
                try {
                    if(messageSplit[1].equalsIgnoreCase("spamTime")) {
                        spamTime = Integer.parseInt(messageSplit[2]);
                    }
                    else if(messageSplit[1].equalsIgnoreCase("spamLines")) {
                        spamLines = Integer.parseInt(messageSplit[2]);
                    }
                }
                catch(ArrayIndexOutOfBoundsException err) {
                    sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax. @set <variable> <value>");
                }
                catch(NumberFormatException err) {
                    sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "You have to input a number");
                }
            }
        }
        catch(NullPointerException err) {
            LOGGER.warning("Could find sender in channel users.");
        }
    }

    protected void onServerPing(String response) {
        super.onServerPing(response);

        String[] subreddits = config.loadBotSettings()[2].split(",");

        // Print new submissions for all subreddits in the config to the main channel
        // if silentMode is not on for that subreddit.
        for(int i = 0; i < subreddits.length; i++) {
            if(!silentMode.get(i)) {
                if(subreddits[i].equals("edmproduction")) {
                    printNewSubmissions(config.loadBotSettings()[1].split(",")[1], subreddits[i]);
                }
                else {
                    printNewSubmissions(config.loadBotSettings()[1].split(",")[0], subreddits[i]);
                }
            }
        }

        // Check if users ban time is over.
        for(int i = 0; i < spamUsers.size(); i++) {
            if (spamUsers.get(i).isBanned) {
                if ((int) System.currentTimeMillis() - spamUsers.get(i).banTime > spamUsers.get(i).banTime && spamUsers.get(i).banTime != -1) {
                    unBan(spamUsers.get(i).channel, "*!*@" + spamUsers.get(i).hostname);
                    spamUsers.get(i).isBanned = false;
                    LOGGER.info("Unbanning " + spamUsers.get(i).nick);
                }
            }
        }
    }

    protected void onDisconnect() {
        LOGGER.info("Disconnected from server, trying to reconnect.");

        while(!isConnected()) {
            try {
                this.connect(host);
            }
            catch(ConnectException err) {
                LOGGER.warning("Connection timed out when trying to connect.");
            }
            catch(Exception err) {
                LOGGER.log(Level.WARNING, "Error when trying to connect to server.", err);
            }

            // Sleep for 5 seconds so we don't spam the connection.
            try {
                Thread.sleep(5000);
            }
            catch(InterruptedException err) {
                LOGGER.log(Level.WARNING, err.getMessage(), err);
            }
        }

        if(this.isConnected()) {
            this.sendMessage("NickServ", "IDENTIFY " + config.loadNickServ()[0] + " " + config.loadNickServ()[1]);
            try {
                Thread.sleep(10000); // Sleep for 10 seconds so we get some kickTime for the identify to pass trough.
            }
            catch(InterruptedException err) {
                LOGGER.log(Level.WARNING, err.getMessage(), err);
            }
            this.joinChannel(config.loadBotSettings()[1]);

            LOGGER.info("Connected");
        }
        else {
            LOGGER.info("Could not reconnect, exiting.");
            System.exit(0);
        }
    }

    private void printNewSubmissions(String channel, String subreddit) {
        Submission[] submissions;
        Submission[] lastSubmissions = null;
        boolean submissionExists;
        int lastSubmissionIndex = 0;

        LOGGER.finest("Printing new submissions.");

        // Get new submissions from "subreddit".
        try {
            submissions = reddit.getNewPosts(subreddit);
        }
        catch(Exception err) {
            LOGGER.log(Level.SEVERE, "Could not get new posts.", err);
            sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Could not get new posts.");

            return;
        }

        // Find correct lastSubmissions list, if it does note exist then create a new list.
        try {
            for(Submission[] s : this.lastSubmissions) {
                if(s[0].getSubreddit().equals(submissions[0].getSubreddit())) {
                    lastSubmissions = s;
                    break;
                }

                lastSubmissionIndex++;
            }

            if(lastSubmissions == null) {
                LOGGER.finest("Creating new lastSubmission entry for " + submissions[0].getSubreddit());
                this.lastSubmissions.add(submissions);
                return;
            }
        }
        catch(Exception err) {
            LOGGER.log(Level.SEVERE, "Could not figure out what lastSubmission array to use when fetching new submissions.", err);

            return;
        }

        // Check if there are any new submissions, and if there are, print them.
        for(Submission submission : submissions) {
            submissionExists = false;

            for(int i = lastSubmissions.length - 1; i >= 0; i--) {
                try {
                    if (submission.getURL().equals(lastSubmissions[i].getURL())) {
                        submissionExists = true;
                    }
                }
                catch(NullPointerException err) {
                    // Ignore posts that return null. The loop will encounter a null if the subreddit has
                    // less than 25 posts.
                }
            }

            // If the submission does not exist, then print it to "channel".
            if(!submissionExists) {
                try {
                    sendMessage(channel, Colors.PURPLE + "New Submission: " + Colors.NORMAL +
                            submission.getTitle() + " (" + "http://reddit.com" +
                            submission.getURL() + ")");
                } catch (Exception err) {
                    LOGGER.log(Level.WARNING, "Could not get post title and/or author.", err);
                    sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Could not get post title and/or author.");
                }
            }
            else {
                // When there are no more new submissions, we replace the old submission array with the current one.
                this.lastSubmissions.set(lastSubmissionIndex, submissions);
                LOGGER.finest("Replacing submissions.");

                return;
            }

        }
    }

    private void setLoggerLevel(Level level) {
        LOGGER.setLevel(level);
        Logger log = LogManager.getLogManager().getLogger("");

        for (Handler h : log.getHandlers()) {
            h.setLevel(level);
        }
    }
}
