package xyz.crafttogether.chatbridge.irc;

import dev.polarian.ircj.objects.events.MessageEvent;

import java.util.HashMap;

/**
 * Handles the IRC commands
 */
public class CommandHandler {
    /**
     * Hashmap containing all the IRC commands
     */
    private static final HashMap<String, IrcCommand> commands = new HashMap<>();
    /**
     * The invalid command, command
     */
    private static IrcCommand invalidCommandHandler = null;

    /**
     * Parses the IRC command
     *
     * @param event The PrivMessageEvent object
     * @param prefix The prefix which invoked the method
     */
    public static void parseCommand(MessageEvent event, String prefix) {
        int separator = event.getMessage().indexOf(" ");
        IrcCommand command;
        if (separator == -1) {
            command = commands.getOrDefault(event.getMessage().substring(prefix.length()), invalidCommandHandler);
        } else {
            command = commands.getOrDefault(event.getMessage().substring(prefix.length(), separator), invalidCommandHandler);
        }
        if (command == null) {
            return;
        }
        command.invoke(event);
    }

    /**
     * Sets the invalid command handler
     *
     * @param commandHandler The command which handles invalid commands
     */
    public static void setInvalidCommandHandler(IrcCommand commandHandler) {
        invalidCommandHandler = commandHandler;
    }

    /**
     * Clears all commands from the hashmap, and nulls the invalid command handler
     */
    public static void clearCommands() {
        commands.clear();
        invalidCommandHandler = null;
    }

    /**
     * Add an IRC command
     *
     * @param commandName The name of the IRC command to invoke the command
     * @param command An instance of a class which implements the IrcCommand interface, which handles this command
     */
    public static void addCommand(String commandName, IrcCommand command) {
        commands.put(commandName, command);
    }
}
