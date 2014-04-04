package com.slize.edmpircbot;

import com.github.jreddit.submissions.Submission;
import org.jibble.pircbot.*;

import java.util.ArrayList;
import java.util.logging.*;

public class Bot extends PircBot {
    private final static Logger LOGGER = Logger.getLogger(Bot.class.getName());

    private String host;
    private Config config;

    private Reddit reddit;
    private ArrayList<Submission[]> lastSubmissions = new ArrayList<Submission[]>();
    private ArrayList<Boolean> silentMode = new ArrayList<Boolean>();

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
        Thread.sleep(10000); // Sleep for 10 seconds so we get some time for the identify to pass trough.
        this.joinChannel(channel);

        this.reddit = new Reddit(redditUsername, redditPassword);

        String[] subreddits = subreddit.split(",");
        for(String ignored : subreddits) {
            silentMode.add(false);
        }
    }

    protected void onMessage(String channel, String sender, String login, String hostname, String message)  {
        String[] messageSplit = message.split(" ");

        if(messageSplit[0].equalsIgnoreCase("@print")) {
            try {
                printNewSubmissions(channel, messageSplit[1]);
            }
            catch(ArrayIndexOutOfBoundsException err) {
                // Print new submissions from main subreddit.
                printNewSubmissions(channel, config.loadBotSettings()[2].split(",")[0]);
            }
        }
        else if(messageSplit[0].equals("@silent")) {
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
                            silentMode.set(i, false);
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
                    sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax. @silent [channel] <on/off>");
                }
            }
        }
        else if(messageSplit[0].equals("@log")) {
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
    }

    protected void onDisconnect() {
        int attempts = 0;

        LOGGER.info("Disconnected from server, trying to reconnect.");

        while(!isConnected() && attempts < 20) {
            try {
                this.connect(host);
            }
            catch(Exception err) {
                LOGGER.log(Level.WARNING, err.getMessage(), err);
            }

            // Sleep for 5 seconds so we don't use all the attempts in one go.
            try {
                Thread.sleep(5000);
            }
            catch(InterruptedException err) {
                LOGGER.log(Level.WARNING, err.getMessage(), err);
            }

            attempts++;
        }

        if(this.isConnected()) {
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

        LOGGER.info("Printing new submissions.");

        try {
            submissions = reddit.getNewPosts(subreddit);
        }
        catch(NullPointerException err) {
            LOGGER.log(Level.SEVERE, err.getMessage(), err);

            return;
        }
        catch(Exception err) {
            LOGGER.log(Level.SEVERE, err.getMessage(), err);
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
            LOGGER.log(Level.SEVERE, err.getMessage(), err);
            sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL +
                        "Could not figure out what lastSubmission array to use when fetching new submissions.");

            return;
        }

        // Check if there are any new submissions, and if there are, print them.
        for(Submission submission : submissions) {
            try {
                submissionExists = false;


                for(int i = lastSubmissions.length - 1; i >= 0; i--) {
                    try {
                        if (submission.getURL().equals(lastSubmissions[i].getURL())) {
                            LOGGER.finest("Submission exists in lastSubmissions.");
                            submissionExists = true;
                        }
                    }
                    catch(NullPointerException err) {
                        // Ignore posts that return null. loop will encounter a null if the subreddit has
                        // less than 25 posts.
                    }
                }

                if (!submissionExists) {
                    try {
                        sendMessage(channel, Colors.PURPLE + "New Submission: " + Colors.NORMAL +
                                submission.getTitle() + " (" + "http://reddit.com" +
                                submission.getURL() + ")");
                    } catch (Exception err) {
                        LOGGER.log(Level.WARNING, err.getMessage(), err);
                        sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Could not get post title and/or author.");
                    }
                }
                else {
                    // When there are no more new submissions, we replace the old submission array with the current one.
                    this.lastSubmissions.set(lastSubmissionIndex, submissions);
                    LOGGER.finer("Replacing submissions.");

                    return;
                }
            }
            // The program will always get a NullPointerException when first booting up.
            // We fix this by catching the exception and putting the last submission to i + 1.
            catch (Exception err) {
                LOGGER.log(Level.WARNING, err.getMessage(), err);
                sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Could not get new posts.");
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
