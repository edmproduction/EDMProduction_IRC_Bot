package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.Scanner;

@Slf4j
public class ScaleCommand extends ListenerAdapter implements Command {
    private ArrayList<Scale> scales;

    private class Scale {
        String key;
        String scale;
        String[] notes;

        public Scale() {
            notes = new String[8];
        }
    }

    public ScaleCommand() {
        loadScales();
    }

    public void onMessage(MessageEvent event) {
        if(!ListenerUtils.isCommand(event.getMessage(), "scale")) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 3) {
            for(Scale scale : scales) {
                if(scale.key.equalsIgnoreCase(message[1]) && scale.scale.equalsIgnoreCase(message[2])) {

                    event.respond(scale.key + " " + scale.scale + ": " + StringUtils.join(scale.notes, ", "));
                    return;
                }
            }

            event.respond("I could not find the scale for " + message[1] + " " + message[2]);
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    private void loadScales() {
        Scanner file;

        scales = new ArrayList<Scale>();

        try {
            ClassLoader loader = getClass().getClassLoader();
            file = new Scanner(loader.getResourceAsStream("scales.txt"));
        }
        catch(Exception err) {
            System.out.println("asd");
            log.error("Could not load scales file.");
            return;
        }

        while(file.hasNextLine()) {
            Scale scale = new Scale();

            scale.key = file.next();
            scale.scale = file.next();

            for(int i = 0; i < 8; i++) {
                scale.notes[i] = file.next();
            }

            scales.add(scale);
        }
    }

    public String getHelp() {
        return "Shows the scale for the given <key> and <scale>. Example: @scale F Minor. Syntax: @scale <key> <scale>";
    }

    public boolean isOpOnlyCommand() {
        return false;
    }
}
