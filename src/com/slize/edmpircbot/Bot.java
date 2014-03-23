package com.slize.edmpircbot;

import com.github.jreddit.submissions.Submission;
import org.jibble.pircbot.*;

public class Bot extends PircBot {
    private String channel;

    private Reddit reddit;
    private Submission lastSubmission;
    private boolean silentMode = false;

    public Bot(String nick, String channel, String subreddit) throws Exception {
        this.channel = channel;

        this.setName(nick);
        this.connect("irc.freenode.net");
        this.joinChannel(this.channel);

        this.reddit = new Reddit(subreddit);
    }

    public Bot(String nick, String channel, String subreddit, String NickServUsername, String NickServPassword) throws Exception {
        this.channel = channel;

        this.setName(nick);
        this.connect("irc.freenode.net");
        this.sendMessage("NickServ", "IDENTIFY " + NickServUsername + " " + NickServPassword);
        this.joinChannel(this.channel);

        this.reddit = new Reddit(subreddit);
    }

    public Bot(String nick, String channel, String subreddit, String nickServUsername, String nickServPassword,
               String redditUsername, String redditPassword) throws Exception {
        this.channel = channel;

        this.setName(nick);
        this.connect("irc.freenode.net");
        this.sendMessage("NickServ", "IDENTIFY " + nickServUsername + " " + nickServPassword);
        this.joinChannel(this.channel);

        this.reddit = new Reddit(subreddit, redditUsername, redditPassword);
    }

    protected void onMessage(String channel, String sender, String login, String hostname, String message)  {
        String[] messageSplit = message.split(" ");

        if(message.equalsIgnoreCase("@print")) {
            printNewSubmissions();
        }

        if(messageSplit[0].equals("@silent")) {
            try {
                if(messageSplit[1].equals("on") && !silentMode) {
                    silentMode = true;

                    System.out.println("Silent Mode: On");
                    sendMessage(channel, "Silent mode is now on.");
                }
                else if(messageSplit[1].equals("off") && silentMode) {
                    silentMode = false;

                    System.out.println("Silent Mode: Off");
                    sendMessage(channel, "Silent mode is now off.");
                }
                else {
                    sendMessage(channel, "Silent mode is already turned " + messageSplit[1]);
                }
            }
            catch(ArrayIndexOutOfBoundsException err) {
                sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax. @silent <on/off>");
            }
        }
        else if(messageSplit[0].equals("@var")) {
            try {
                if(messageSplit[1].equalsIgnoreCase("silent") || messageSplit[1].equalsIgnoreCase("silentmode")) {
                    sendMessage(channel, String.valueOf(silentMode));
                }
                else if(messageSplit[1].equalsIgnoreCase("lastsubmission")) {
                    sendMessage(channel, lastSubmission.toString() + " (" + lastSubmission.getTitle() + ")");
                }
            }
            catch(ArrayIndexOutOfBoundsException err) {
                sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Invalid syntax. @var <variable>");
            }
            catch(NullPointerException err) {
                sendMessage(channel, "Variable is NULL.");
            }
            catch(Exception err) {
                System.err.println(err);
                sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Check console for log.");
            }
        }
    }

    protected void onServerPing(String response) {
        super.onServerPing(response);

        if(silentMode) {
            return;
        }

        printNewSubmissions();
    }

    private void printNewSubmissions() {
        Submission[] submissions;

        try {
            submissions = reddit.getNewPosts();
        }
        catch(Exception err) {
            err.printStackTrace();
            sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Could not get new posts.");

            return;
        }

        for(int i = 0; i < submissions.length; i++) {
            try {
                if(!submissions[i].getURL().equals(lastSubmission.getURL())) {

                    try {
                        sendMessage(channel, submissions[i].getTitle() + " (" + "http://reddit.com" + submissions[i].getURL() + ")");
                    }
                    catch(Exception err) {
                        System.err.println(err);
                        sendMessage(channel, Colors.RED + "Error: " + Colors.NORMAL + "Could not get post title and/or author.");
                    }
                }
                else {
                    // If the last submission is still the last submission on the subreddit,
                    // then the program will end up in the catch statement.
                    try {
                        lastSubmission = submissions[i - 1];
                    }
                    catch(ArrayIndexOutOfBoundsException err) {
                        lastSubmission = submissions[i];
                    }

                    return;
                }
            }
            // The program will always get a NullPointerException when first booting up.
            // We fix this by catching the exception and putting the last submission to i + 1.
            catch(NullPointerException err) {
                lastSubmission = submissions[i + 1];
            }
            catch(Exception err) {
                System.err.println(err);
            }
        }
    }

}























