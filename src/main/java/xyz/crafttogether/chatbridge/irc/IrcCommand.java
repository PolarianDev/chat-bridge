package xyz.crafttogether.chatbridge.irc;

import dev.polarian.ircj.objects.events.MessageEvent;

/**
 * Interface used by IrcCommands which can be invoked when the command is received
 */
public interface IrcCommand {
    void invoke(MessageEvent event);
}
