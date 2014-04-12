package com.slize.edmpircbot.utils;

import com.github.jreddit.submissions.Submission;
import com.github.jreddit.submissions.Submissions;
import com.github.jreddit.submissions.Submissions.Page;
import com.github.jreddit.submissions.Submissions.Popularity;
import com.github.jreddit.user.User;
import com.github.jreddit.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Reddit {
    private User user;

    public Reddit() {

        Utils.setUserAgent("EDMPModBot-0.1");

        this.user = new User();
    }

    public Reddit(String username, String password) {
        Utils.setUserAgent("EDMPModBot-0.1");

        this.user = new User(username, password);
        try {
            this.user.connect();
        }
        catch(Exception err) {
            log.warn(err.getMessage(), err);
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
