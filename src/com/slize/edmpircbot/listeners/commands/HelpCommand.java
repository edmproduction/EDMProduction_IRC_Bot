package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.Colors;

import java.util.ArrayList;

public class HelpCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) {
        if(!ListenerUtils.isCommand(event.getMessage(), "help")) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 1) {
            // Show all commands.
            ArrayList<String> commands = new ArrayList<String>();

            for(Listener listener : event.getBot().getConfiguration().getListenerManager().getListeners()) {
                if(listener instanceof Command) {
                    if(isAllowedToViewCommand((Command) listener, event.getChannel(), event.getUser())) {
                        commands.add(getCommandName((Command) listener));
                    }
                }
            }

            event.respond("Available commands are: " + StringUtils.join(commands, ", "));
        }
        else if(message.length == 2) {
            // Show help for a specific command.
            for(Listener listener : event.getBot().getConfiguration().getListenerManager().getListeners()) {
                if(listener instanceof Command) {
                    if(message[1].equalsIgnoreCase(getCommandName((Command) listener))) {
                        if(isAllowedToViewCommand((Command) listener, event.getChannel(), event.getUser())) {
                            event.respond(((Command) listener).getHelp());
                        }
                        else {
                            event.respond("You are not allowed to use that command.");
                        }

                        return;
                    }
                }
            }

            // If we get down here, then the command doesn't exist.
            event.respond("The command \"" + message[1] + "\" does not exist.");
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    private boolean isAllowedToViewCommand(Command command, Channel channel, User user) {
        if(ListenerUtils.isOp(channel, user)) {
            return true;
        }
        else if(command.isOpOnlyCommand()) {
            return false;
        }
        else {
            return true;
        }

    }

    private String getCommandName(Command command) {
        return StringUtils.removeEnd(command.getClass().getSimpleName(), "Command");
    }

    public String getHelp() {
        return "Shows available commands. Syntax. " + ListenerUtils.PREFIX + "help [command]";
    }

    public boolean isOpOnlyCommand() {
        return false;
    }
}
