package com.slize.edmpircbot.listeners;

import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

@Slf4j
public class UrlTitlePoster extends ListenerAdapter {

    public void onMessage(MessageEvent event) {
        String[] message = event.getMessage().split(" ");

        for(String messagePart : message) {
            if(messagePart.contains("http://") || messagePart.contains("https://")) {
                String documentContent = "";
                String title;

                try {
                    documentContent = readUrlContent(messagePart);
                }
                catch(MalformedURLException err) {
                    log.debug("Malformed URL.", err);
                }
                catch(IOException err) {
                    log.warn("Could not make a new scanner.", err);
                }

                title = getTitle(documentContent);

                log.debug(title);

                if(title != null) {
                    event.getChannel().send().message(Colors.BOLD + title);
                }

                break;
            }
        }
    }

    public static String getTitle(String content) {
        String startTag = "<title";
        String endTag = "</title>";

        int titleStart = content.indexOf(startTag);
        int titleStartEnd = content.indexOf(">", titleStart);
        int titleEnd = content.indexOf(endTag);

        log.debug((titleStart + titleStartEnd - titleStart) + " " + titleEnd);

        if(titleStart != -1 && titleEnd != -1) {
            return content.substring(titleStart + (titleStartEnd - titleStart) + 1, titleEnd).trim();
        }
        else {
            return null;
        }
    }

    public static String readUrlContent(String urlLocation) throws IOException {
        URL url = new URL(urlLocation);
        Scanner scanner = new Scanner(url.openStream());
        String content = "";

        while(scanner.hasNextLine()) {
            content += scanner.nextLine();
        }

        scanner.close();

        return content;
    }
}
