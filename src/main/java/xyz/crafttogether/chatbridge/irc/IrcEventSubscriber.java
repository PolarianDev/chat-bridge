package xyz.crafttogether.chatbridge.irc;

import dev.polarian.ircj.DisconnectReason;
import dev.polarian.ircj.EventAdapter;
import dev.polarian.ircj.objects.events.MessageEvent;
import dev.polarian.ircj.objects.events.WelcomeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.crafttogether.chatbridge.ChatBridge;
import xyz.crafttogether.chatbridge.MessageSource;
import xyz.crafttogether.chatbridge.discord.DiscordMessageSender;
import xyz.crafttogether.chatbridge.minecraft.listeners.MinecraftMessageSender;
import xyz.crafttogether.craftcore.configuration.ConfigHandler;

/**
 * Event listener which listens for IRC events provided by the IRCJ library
 */
public class IrcEventSubscriber extends EventAdapter {
    /**
     * SLF4J Logger object
     */
    private static final Logger logger = LoggerFactory.getLogger(IrcEventSubscriber.class);

    /**
     * Invoked when the IRC client disconnects from the IRC server
     *
     * @param reason The reason for the disconnect
     * @param e The exception thrown by the IRC client
     */
    @Override
    public void onDisconnectEvent(DisconnectReason reason, Exception e) {
        logger.warn("Disconnected from IRC server for reason: " + reason.toString().toLowerCase());
        switch (reason) {
            case TIMEOUT:
                if (ChatBridge.getRemainingAttempts() > 0) ChatBridge.connectIrc();
                ChatBridge.decrementRemainingAttempts();
                break;

            case FORCE_DISCONNECTED:
                return;

            case ERROR:
                logger.error(e.getMessage());
        }
    }

    /**
     * Invoked when a message is received by the IRC client
     *
     * @param event The PrivMessageEvent object
     */
    @Override
    public void onMessageEvent(MessageEvent event) {
        String prefix = ConfigHandler.getConfig().getIrcCommandPrefix();
        if (event.getMessage().startsWith(prefix)) {
            CommandHandler.parseCommand(event, prefix);
        }
        MinecraftMessageSender.send(event.getUser().getNick(), event.getMessage(), MessageSource.IRC);
        DiscordMessageSender.send(event.getUser().getNick(), event.getMessage(), null, MessageSource.IRC);
    }

    /**
     * Invoked when the IRC client successfully connects to the IRC server
     *
     * @param event The WelcomeEvent object
     */
    @Override
    public void onWelcomeEvent(WelcomeEvent event) {
        ChatBridge.resetAttempts();
    }
}
