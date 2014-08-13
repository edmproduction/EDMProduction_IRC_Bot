package com.slize.edmpircbot.listeners;

import com.github.jreddit.entity.Submission;
import com.slize.edmpircbot.utils.Config;
import com.slize.edmpircbot.utils.Reddit;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerPingEvent;

import java.util.ArrayList;

@Slf4j
public class PrintNewPosts extends ListenerAdapter {
    private String[] subreddits;
    private String[] channels;
    private static ArrayList<Boolean> silentMode = new ArrayList<Boolean>();
    private ArrayList<Submission[]> lastSubmissions = new ArrayList<Submission[]>();

    private Reddit reddit;

    public PrintNewPosts(Config config) {
        String[] redditCfg = config.loadReddit();

        this.subreddits = config.loadReddit()[2].split(",");
        this.channels = config.loadBotSettings()[1].split(",");

        for(String ignored : redditCfg[2].split(",")) {
            silentMode.add(false);
        }

        this.reddit = new Reddit(redditCfg[0], redditCfg[1]);
    }

    public void onServerPing(ServerPingEvent event) throws Exception {
        super.onServerPing(event);

        for(int i = 0; i < subreddits.length; i++) {
            if(!silentMode.get(i)) {
                if(subreddits[i].equals("edmproduction")) {
                    printNewSubmissions(event.getBot().getUserChannelDao().getChannel(channels[1]), subreddits[i]);
                }
                else {
                    printNewSubmissions(event.getBot().getUserChannelDao().getChannel(channels[0]), subreddits[i]);
                }
            }
        }
    }

    public void printNewSubmissions(Channel channel, String subreddit) {
        Submission[] submissions;
        Submission[] lastSubmissions = null;
        boolean submissionExists;
        int lastSubmissionIndex = 0;

        log.debug("Printing new submissions for " + subreddit + ".");

        // Get new submissions from "subreddit".
        try {
            submissions = reddit.getNewPosts(subreddit);
        }
        catch(Exception err) {
            log.error("Could not get new posts.", err);

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
                log.debug("Creating new lastSubmission entry for " + submissions[0].getSubreddit());
                this.lastSubmissions.add(submissions);

                return;
            }
        }
        catch(Exception err) {
            log.warn("Could not figure out what lastSubmission array to use when fetching new submissions.", err);

            return;
        }

        for(Submission s : submissions) {
            log.debug(s.getTitle());
        }

        log.debug("");
        log.debug("");
        log.debug("");

        for(Submission s : lastSubmissions) {
            log.debug(s.getTitle());
        }

        log.debug("");
        log.debug("");
        log.debug("");

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
                    channel.send().message(Colors.PURPLE + "New Submission: " + Colors.NORMAL + submission.getTitle() +
                            " (" + "http://redd.it/" + submission.getFullName().substring(3) + ")");
                } catch (Exception err) {
                    log.error("Could not get post title and/or url.", err);
                }
            }
            else {
                // When there are no more new submissions, we replace the old submission array with the current one.
                this.lastSubmissions.set(lastSubmissionIndex, submissions);
                log.debug("Replacing submissions.");

                return;
            }

        }
    }

    public static void setSilentMode(ArrayList<Boolean> newArrayList) {
        silentMode = newArrayList;
    }

    public static ArrayList<Boolean> getSilentMode() {
        return silentMode;
    }

    public String[] getSubreddits() {
        return subreddits;
    }
}
