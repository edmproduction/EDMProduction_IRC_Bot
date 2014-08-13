package com.slize.edmpircbot.listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Joke class to check if lumadroid's octatrack has arived yet (It probably hasn't arived).
 */
public class Response extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        String[] message = event.getMessage().split(" ");

        if(message[0].equalsIgnoreCase("has") &&
                (message[1].equalsIgnoreCase("luma's") || message[1].equalsIgnoreCase("lumas") ||
                 message[1].equalsIgnoreCase("lumadroid's") || message[1].equalsIgnoreCase("lumadroids")) &&
                (message[2].equalsIgnoreCase("octatrack") || message[2].equalsIgnoreCase("octa")) &&
                (message[3].equalsIgnoreCase("arrived") || message[3].equalsIgnoreCase("arived"))) {
            System.out.println("debug");

            int rnd = (int) (Math.random() * 6);

            switch(rnd) {
                case 0:
                    event.respond("Probably not.");
                    break;
                case 1:
                    event.respond("I don't think so.");
                    break;
                case 2:
                    event.respond("Will lumadroid's octatrack ever arive? Pay atention to the next episode of \"has lumadroids's stuff arived yet?\"!");
                    break;
                case 3:
                    event.respond("Unlikely.");
                    break;
                case 4:
                    event.respond("Oh, you mean the thing I stole yesterday? Yeh, it has, but it's mine now >:D");
                    break;
                case 5:
                    event.respond("How can the Octatrack be real if our eyes aren't real?");
                    break;
            }
        }
    }
}
