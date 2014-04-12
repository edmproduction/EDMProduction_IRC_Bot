package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class NoteCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "note")) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 2) {
            // Easter Egg
            if(message[1].equalsIgnoreCase("brown")) {
                event.respond("If I were to share the frequency of the brown note with you, then you would probably use it for evil purposes.");

                return;
            }

            try {
                for(FrequencyCommand.NotesAndFrequencies notesAndFrequencies : FrequencyCommand.getNotesAndFrequenciesList()) {
                    if(notesAndFrequencies.frequency < 50 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 1.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 100 && notesAndFrequencies.frequency > 50 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 4.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 200 && notesAndFrequencies.frequency > 100 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 7.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 300 && notesAndFrequencies.frequency > 200 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 12.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 1000 && notesAndFrequencies.frequency > 300 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 17.5f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 2000 && notesAndFrequencies.frequency > 1000 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 50.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 3000 && notesAndFrequencies.frequency > 2000 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 100.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 4000 && notesAndFrequencies.frequency > 3000 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 170.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency < 5000 && notesAndFrequencies.frequency > 4000 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 245.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                    else if(notesAndFrequencies.frequency > 5000 &&
                            isCloseTo(notesAndFrequencies.frequency, Float.parseFloat(message[1]), 310.0f)) {
                        event.respond("The closest note to " + message[1] + " Hz is " +
                                notesAndFrequencies.note + " (" + notesAndFrequencies.frequency + " Hz)");

                        return;
                    }
                }
            }
            catch(NumberFormatException err) {
                System.out.println("NumberFormatException");
                // This catch is ignored.
            }

            // If the note is not found (or he/she inputs something that is not a number), tell the user.
            event.respond("I could not find that frequency.");
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    private boolean isCloseTo(float frequency, float message, float difference) {
        return frequency <= message + difference && frequency >= message - difference;
    }

    public String getHelp() {
        return "Shows the corresponding note for <frequency>. Syntax: " + ListenerUtils.PREFIX + "note <frequency>";
    }

    public boolean isOpOnlyCommand() {
        return false;
    }
}
