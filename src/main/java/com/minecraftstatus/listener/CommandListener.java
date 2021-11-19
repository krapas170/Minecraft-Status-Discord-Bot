package com.minecraftstatus.listener;

import com.minecraftstatus.Main;
import com.minecraftstatus.configs.DefaultConfig;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	public final static DefaultConfig CONFIG = new DefaultConfig("config.json");

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage().getContentDisplay();
		if(event.isFromType(ChannelType.TEXT)) {
			TextChannel channel = event.getTextChannel();
			//!tut arg0 arg1 arg2 ...
			if(message.startsWith(CONFIG.getString("prefix"))) {
				String[] args = message.substring(1).split(" ");	
				if(args.length > 0) {
					if(!Main.INSTANCE.getCmdMan().perform(args[0], event.getMember(), channel, event.getMessage())) {
						channel.sendMessage("`Unknown command`").queue();
					}
				}
			}
			if(channel.getIdLong() == 593768981135884309l) {
				//CounterGame.countUpdate(channel, event.getMessage());
			}
		}
	}
}
