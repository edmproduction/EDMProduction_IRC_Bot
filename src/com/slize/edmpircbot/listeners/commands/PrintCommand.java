package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.listeners.PrintNewPosts;
import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class PrintCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) {
        if(!ListenerUtils.isCommand(event.getMessage(), "print") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");
        PrintNewPosts printNewPosts = (PrintNewPosts) ListenerUtils.getListener(event.getBot().getConfiguration().getListenerManager().getListeners(), "PrintNewPosts");

        if(message.length == 1) {
            printNewPosts.printNewSubmissions(event.getChannel(), printNewPosts.getSubreddits()[0]);
        }
        else if(message.length == 2) {
            printNewPosts.printNewSubmissions(event.getChannel(), message[1]);
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    public String getHelp() {
        return "Forces printing of new submissions. Syntax: " + ListenerUtils.PREFIX + "print [subreddit]";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }
}
