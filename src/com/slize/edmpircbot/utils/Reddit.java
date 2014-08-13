package com.slize.edmpircbot.utils;

import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Reddit {
    private User user;
    private RestClient restClient = new HttpRestClient();

    public Reddit() {
    }

    public Reddit(String username, String password) {
        restClient.setUserAgent("EDMPModBot-0.1");

        this.user = new User(restClient, username, password);
        try {
            this.user.connect();
        }
        catch(Exception err) {
            log.warn(err.getMessage(), err);
        }
    }

    public Submission[] getNewPosts(String subreddit) throws Exception {
        Submission[] submissions = new Submission[25];
        Submissions tempSubms = new Submissions(restClient, user);
        int i = 0;

        for(Submission submission : tempSubms.ofSubreddit(subreddit, SubmissionSort.NEW, 0, 24, null, null, true)) {
            submissions[i] = submission;
            i++;
        }

        return submissions;
    }

}
