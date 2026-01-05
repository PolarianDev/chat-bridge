package xyz.crafttogether.chatbridge.irc.commands;

import dev.polarian.ircj.objects.events.MessageEvent;
import xyz.crafttogether.chatbridge.irc.IrcCommand;

import java.io.IOException;

/**
 * Command which is executed when the slash command is invalid
 */
public class InvalidCommand implements IrcCommand {
    @Override
    public void invoke(MessageEvent event) {
        try {
            event.sendMessage(event.getChannel(), "Invalid command");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
