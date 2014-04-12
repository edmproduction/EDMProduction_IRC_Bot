package com.slize.edmpircbot.listeners.commands;

import com.slize.edmpircbot.utils.ListenerUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class LogCommand extends ListenerAdapter implements Command {

    public void onMessage(MessageEvent event) throws Exception {
        if(!ListenerUtils.isCommand(event.getMessage(), "log") || !ListenerUtils.isOp(event.getChannel(), event.getUser())) {
            return;
        }

        String[] message = event.getMessage().split(" ");

        if(message.length == 2) {
            event.respond("To be implemented.");
        }
        else {
            event.respond(Colors.RED + "Error: " + Colors.NORMAL + "Invalid Syntax.");
        }
    }

    /*
    private void setLoggerLevel(Level level) {
        Main.LOGGER.setLevel(level);
        Logger log = LogManager.getLogManager().getLogger(Main.class.getSimpleName());

        for (Handler h : log.getHandlers()) {
            h.setLevel(level);
        }
    } */

    public String getHelp() {
        return "Sets logger level. Syntax: " + ListenerUtils.PREFIX + "log <level>";
    }

    public boolean isOpOnlyCommand() {
        return true;
    }
}
