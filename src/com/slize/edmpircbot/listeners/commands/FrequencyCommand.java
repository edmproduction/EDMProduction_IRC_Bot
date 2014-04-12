package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import lombok.extern.slf4j.Slf4j;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

@Slf4j
public class FrequencyCommand extends ListenerAdapter implements Command {
    private static ArrayList<NotesAndFrequencies> notesAndFrequenciesList = new ArrayList<NotesAndFrequencies>();

    protected class NotesAndFrequencies {
        public String note;
        public float frequency;
        public float wavelength;
    }

    public FrequencyCommand() {
        loadNotesAndFrequencies();
    }

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "frequency")) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 2) {
            for(NotesAndFrequencies notesAndFrequencies : notesAndFrequenciesList) {
                if(notesAndFrequencies.note.equalsIgnoreCase(message[1])) {
                    event.respond("The frequency for " + notesAndFrequencies.note + " is " + notesAndFrequencies.frequency + " Hz");

                    return;
                }
            }

            // If the note is not found, tell the user.
            event.respond("I could not find that note.");
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    private void loadNotesAndFrequencies() {
        Scanner file;
        int i = 0;

        try {
            file = new Scanner(new File("res/note_and_frequencies.txt"));
        }
        catch(Exception err) {
            log.error("Could not load note and frequency file.", err);
            return;
        }

        while(file.hasNextLine()) {
            // The file is structured with notes first, then frequency, then wavelength.
            notesAndFrequenciesList.add(new NotesAndFrequencies());
            notesAndFrequenciesList.get(i).note = file.next();
            notesAndFrequenciesList.get(i).frequency = Float.parseFloat(file.next());
            notesAndFrequenciesList.get(i).wavelength = Float.parseFloat(file.next());

            i++;
        }
    }

    public static ArrayList<NotesAndFrequencies> getNotesAndFrequenciesList() {
        return notesAndFrequenciesList;
    }

    public String getHelp() {
        return "Shows the corresponding frequency for <note>. Notes should be supplied with numbers (for example, C5 or F#5). Syntax: " + ListenerUtils.PREFIX + "frequency <note>";
    }

    public boolean isOpOnlyCommand() {
        return false;
    }
}
