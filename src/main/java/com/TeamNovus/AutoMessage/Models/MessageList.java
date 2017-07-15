package com.TeamNovus.AutoMessage.Models;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.TeamNovus.AutoMessage.AutoMessage;

import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;

public class MessageList {
	private boolean enabled = true;
	private int interval = 45;
	private long expiry = -1L;
	private boolean random = false;
	private List<Message> messages = new LinkedList<Message>();

	private transient int currentIndex = 0;
	
	public MessageList() {
		messages.add(new Message("First message in the list!"));
		messages.add(new Message("&aSecond message in the list with formatters!"));
		messages.add(new Message("&bThird message in the list with formatters and a \nnew line!"));
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getExpiry() {
		return expiry;
	}

	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() >= expiry && expiry != -1;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public void addMessage(Message message) {
		this.messages.add(message);
	}

	public Message getMessage(Integer index) {
		try {
			return this.messages.get(index.intValue());
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void addMessage(Integer index, Message message) {
		try {
			this.messages.add(index.intValue(), message);
		} catch (IndexOutOfBoundsException e) {
			this.messages.add(message);
		}
	}

	public boolean editMessage(Integer index, Message message) {
		try {
			return this.messages.set(index.intValue(), message) != null;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public boolean removeMessage(Integer index) {
		try {
			return this.messages.remove(index.intValue()) != null;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public boolean hasMessages() {
		return messages.size() > 0;
	}

	public void setCurrentIndex(int index) {
		this.currentIndex = index;

		if (currentIndex >= messages.size() || currentIndex < 0) {
			this.currentIndex = 0;
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void broadcast(int index) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			broadcastTo(index, player);
		}

		broadcastTo(index, Bukkit.getConsoleSender());
	}

	public void broadcastTo(int index, CommandSender to) {
		Message message = getMessage(index);

		if (message != null) {
			List<String> messages = message.getMessages();
			List<String> commands = message.getCommands();

			for (int i = 0; i < messages.size(); i++) {
				String m = messages.get(i);

				if (message.isJsonMessage(i) && to instanceof Player) {

					try {
						String msge = "[{\"text\":\"\"}," + colourCodeToJson(m + ",{\"text\":\" \",\"color\":\"gold\"}]");
						AutoMessage.plugin.getLogger().info(msge);

						PacketPlayOutChat msg = new PacketPlayOutChat(ChatSerializer.a(msge));
						AutoMessage.plugin.getLogger().info(msg.toString());
						((CraftPlayer) ((Player) to)).getHandle().playerConnection.sendPacket(msg);
					} catch (Exception ignore) {
						ignore.printStackTrace();
					}
				}
			}

			for (String command : commands) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceFirst("/", ""));
			}
		}
	}
	
	public static String colourCodeToJson(String msg) {
		return msg
				.replace("&0", "\"},{\"color\":\"black\",\"text\":\"")
				.replace("&1", "\"},{\"color\":\"dark_blue\",\"text\":\"")
				.replace("&2", "\"},{\"color\":\"dark_green\",\"text\":\"")
				.replace("&3", "\"},{\"color\":\"dark_aqua\",\"text\":\"")
				.replace("&4", "\"},{\"color\":\"dark_red\",\"text\":\"")
				.replace("&5", "\"},{\"color\":\"dark_purple\",\"text\":\"")
				.replace("&6", "\"},{\"color\":\"gold\",\"text\":\"")
				.replace("&7", "\"},{\"color\":\"gray\",\"text\":\"")
				.replace("&8", "\"},{\"color\":\"dark_gray\",\"text\":\"")
				.replace("&9", "\"},{\"color\":\"blue\",\"text\":\"")
				.replace("&a", "\"},{\"color\":\"green\",\"text\":\"")
				.replace("&b", "\"},{\"color\":\"aqua\",\"text\":\"")
				.replace("&c", "\"},{\"color\":\"red\",\"text\":\"")
				.replace("&d", "\"},{\"color\":\"light_purple\",\"text\":\"")
				.replace("&e", "\"},{\"color\":\"yellow\",\"text\":\"")
				.replace("&f", "\"},{\"color\":\"white\",\"text\":\"")
				.replace("&k", "\"},{\"obfuscated\":true,\"text\":\"")
				.replace("&l", "\"},{\"bold\":true,\"text\":\"")
				.replace("&m", "\"},{\"strikethrough\":true,\"text\":\"")
				.replace("&n", "\"},{\"underlined\":true,\"text\":\"")
				.replace("&o", "\"},{\"italic\":true,\"text\":\"")
				.replace("\"text\":\"\"},{", "");
	}
}
