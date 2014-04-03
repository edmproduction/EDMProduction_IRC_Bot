package com.slize.edmpircbot;

import com.github.jreddit.submissions.Submission;
import com.github.jreddit.submissions.Submissions.Popularity;
import com.github.jreddit.submissions.Submissions.Page;
import com.github.jreddit.submissions.Submissions;
import com.github.jreddit.user.User;
import com.github.jreddit.utils.Utils;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Reddit {
    private static final Logger LOGGER = Logger.getLogger(Reddit.class.getName());

    private User user;

    public Reddit() {
        try {
            FileHandler fh;
            fh = new FileHandler("reddit.log");
            LOGGER.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        }
        catch(Exception err) {
            err.printStackTrace();
        }

        Utils.setUserAgent("EDMPModBot-0.1");

        this.user = new User();
    }

    public Reddit(String username, String password) {
        try {
            FileHandler fh;
            fh = new FileHandler("reddit.log");
            LOGGER.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
        }
        catch(Exception err) {
            err.printStackTrace();
        }

        Utils.setUserAgent("EDMPModBot-0.1");

        this.user = new User(username, password);
        try {
            this.user.connect();
        }
        catch(Exception err) {
            LOGGER.log(Level.WARNING, err.getMessage(), err);
        }
    }

    public Submission[] getNewPosts(String subreddit) throws Exception {
        Submission[] submissions = new Submission[25];
        int i = 0;

        for(Submission submission : Submissions.getSubmissions(subreddit, Popularity.NEW, Page.FRONTPAGE, user)) {
            submissions[i] = submission;
            i++;
        }

        return submissions;
    }

}
