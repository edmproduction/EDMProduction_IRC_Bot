package com.slize.edmpircbot.listeners.commands;

interface Command {
    public String getHelp();
    public boolean isOpOnlyCommand();
}
