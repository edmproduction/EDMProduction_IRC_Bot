package com.slize.edmpircbot;

import com.github.jreddit.submissions.Submission;
import com.github.jreddit.submissions.Submissions.Popularity;
import com.github.jreddit.submissions.Submissions.Page;
import com.github.jreddit.submissions.Submissions;
import com.github.jreddit.user.User;
import com.github.jreddit.utils.Utils;

public class Reddit {
    private String subreddit;
    private User user;

    public Reddit(String subreddit) {
        Utils.setUserAgent("EDMPModBot-0.1");

        this.subreddit = subreddit;
        this.user = new User();
    }

    public Reddit(String subreddit, String username, String password) {
        Utils.setUserAgent("EDMPModBot-0.1");

        this.subreddit = subreddit;
        this.user = new User(username, password);
        try {
            this.user.connect();
        }
        catch(Exception err) {
            err.printStackTrace();
        }
    }

    public Submission[] getNewPosts() throws Exception {
        Submission[] submissions = new Submission[25];
        int i = 0;

        for(Submission submission : Submissions.getSubmissions(subreddit, Popularity.NEW, Page.FRONTPAGE, user)) {
            submissions[i] = submission;
            i++;
        }

        return submissions;
    }

}
