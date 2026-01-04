package xyz.crafttogether.chatbridge.minecraft.listeners;

import dev.polarian.ircj.utils.Formatting;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.crafttogether.chatbridge.ChatBridge;
import xyz.crafttogether.chatbridge.irc.IrcMessageSender;
import xyz.crafttogether.craftcore.CraftCore;
import xyz.crafttogether.craftcore.configuration.ConfigHandler;
import xyz.crafttogether.weg.EventListener;
import xyz.crafttogether.weg.Weg;

import java.awt.*;
import java.io.IOException;

/**
 * Event listener which listens for events provided by the Weg API --> the craft together AFK plugin
 */
public class WegListener extends EventListener {
    /**
     * A SLF4J Logger object
     */
    private static final Logger logger = LoggerFactory.getLogger(WegListener.class);

    /**
     * Event invoked when a player on the server goes AFK (depends on how Weg classifies when someone goes afk,
     * it is configurable within the Weg configuration file).
     *
     * @param player The player which went AFK
     */
    @Override
    public void onAfkEvent(Player player) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(player.getName() + " has went AFK")
                .setColor(Color.GRAY)
                .build();
        long channelId = ConfigHandler.getConfig().getDiscordChannelId();
        TextChannel channel = CraftCore.getJda().getTextChannelById(channelId);
        if (channel == null) {
            logger.error("Failed to get discord channel");
        }
        channel.sendMessageEmbeds(embed).queue();
        try {
            IrcMessageSender.sendMinecraftAfkMessage(player.getName());
            ChatBridge.updateIrcChannelStatistics(Bukkit.getOnlinePlayers().size(), Weg.getAfkPlayers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Event invoked when an AFK player returns
     *
     * @param player The player which is no longer AFK
     */
    @Override
    public void onReturnEvent(Player player) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(player.getName() + " is no longer AFK")
                .setColor(Color.GRAY)
                .build();
        long channelId = ConfigHandler.getConfig().getDiscordChannelId();
        TextChannel channel = CraftCore.getJda().getTextChannelById(channelId);
        if (channel == null) {
            logger.error("Failed to get discord channel");
        }
        channel.sendMessageEmbeds(embed).queue();
        try {
            IrcMessageSender.sendMinecraftReturnMessage(player.getName());
            ChatBridge.updateIrcChannelStatistics(Bukkit.getOnlinePlayers().size(), Weg.getAfkPlayers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
