package com.gameserverlive.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import com.gameserverlive.Main;
import com.gameserverlive.commands.types.EmbedMessage;
import com.gameserverlive.commands.types.ServerCommand;
import com.gameserverlive.managers.LiteSQL;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;

public class GameServerStatus implements ServerCommand {

    @Override
	public void performCommand(String[] args, Member m, TextChannel channel, Message message) {
		
		if(m.hasPermission(Permission.ADMINISTRATOR)) {
		
            Guild guild = channel.getGuild();
			ResultSet set = LiteSQL.onQuery("SELECT * FROM gameserver WHERE guildid = " + guild.getIdLong());
			ResultSet game = LiteSQL.onQuery("SELECT game FROM gameserver WHERE game = " + guild.getIdLong());

			try {
				if(!set.next()) {
                    if(game = mc) {
                    }
                    else if(game = csgo) {
                    }
                    EmbedBuilder builder = new EmbedBuilder();
                    String title = "Under Construction";
                    String description = "This command will be create in the future. Please try again at another time!";
                    EmbedMessage.run(title, description, channel);
                    channel.sendMessageEmbeds(builder.build()).queue();
                }
            }
        }
    }

    private void queueShutdown(TextChannel channel) {
        running = true;
        sendShutdownMessage(channel);
        Console.info("Shutdown Message send!");
        //startCancelListener(message);
        new Timer(timeMillis, e -> performShutdown()).start();
    }





						Category category = guild.createCategory("Statistiken").complete();
						category.getManager().setPosition(-1).queue();
						
						PermissionOverride override = new PermissionOverrideActionImpl(category.getJDA(), category, category.getGuild().getPublicRole()).complete();

						
						category.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
						
						
						LiteSQL.onUpdate("INSERT INTO statchannels(guildid, categoryid) VALUES(" + guild.getIdLong() + ", " + category.getIdLong() + ")");
						
						fillCategory(category);
					} else {
						long categoryid = set.getLong("categoryid");
						channel.sendMessage("Kategorie erstellt!").queue();
						Category cat = guild.getCategoryById(categoryid);
						
						
						if(args.length == 2) {
							if(args[1].equalsIgnoreCase("delete")) {
								LiteSQL.onUpdate("DELETE FROM statchannels WHERE guildid = " + guild.getIdLong());
								
								cat.getChannels().forEach(chan -> {
									chan.delete().complete();
								});
								cat.delete().queue();
								
								
								return;
							}
						}
						
						
						
						
						
						cat.getChannels().forEach(chan -> {
							chan.delete().complete();
						});
						
						fillCategory(cat);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

	}
	
	public static void fillCategory(Category cat) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy");
		cat.createVoiceChannel("🕗 Uhrzeit: " + df.format(Calendar.getInstance().getTime()) + "Uhr").queue();
		cat.createVoiceChannel("📅 Datum: " + df2.format(Calendar.getInstance().getTime())).queue();
		
		List<Member> members = cat.getGuild().getMembers();
		cat.createVoiceChannel("📈 Server Mitglieder: " + members.size()).queue();
		int online = 0;
		
		for(Member memb : members) {
			if(memb.getOnlineStatus() != OnlineStatus.OFFLINE) {
				if(!memb.getUser().isBot()) {
					online++;
				}
			}
		}
		cat.createVoiceChannel("🔘 Online User: " + online).queue();
		cat.createVoiceChannel("✅ BOT ONLINE").queue();
		
		PermissionOverride override = new PermissionOverrideActionImpl(cat.getJDA(), cat, cat.getGuild().getPublicRole()).complete();
		
		
		//System.out.println("OVerride: " + (override == null ? "NULL" : override.toString()));
		
		cat.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
	}

	public static void sync(Category cat) {
		cat.getChannels().forEach(chan -> {
			chan.getManager().sync().queue();
		});
	}
	
	public static void updateCategory(Category cat) {
		if(cat.getChannels().size() == 5) {
			sync(cat);
			List<GuildChannel> channels = cat.getChannels();
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy");
			
			channels.get(0).getManager().setName("🕗 Uhrzeit: " + df.format(Calendar.getInstance().getTime()) + "Uhr").queue();
			channels.get(1).getManager().setName("📅 Datum: " + df2.format(Calendar.getInstance().getTime())).queue();
			List<Member> members = cat.getGuild().getMembers();
			int online = 0;
			
			for(Member memb : members) {
				if(memb.getOnlineStatus() != OnlineStatus.OFFLINE) {
					if(!memb.getUser().isBot()) {
						online++;
					}
				}
			}
			channels.get(2).getManager().setName("📈 Server Mitglieder: " + members.size()).queue();
			channels.get(3).getManager().setName("🔘 Online User: " + online).queue();
		}
	}
	
	public static void checkStats() {
		((JDA) Main.bot).getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryid FROM statchannels WHERE guildid = " + guild.getIdLong());
			
			try {
				if(set.next()) {
					long catid = set.getLong("categoryid");
					StatschannelCommand.updateCategory(guild.getCategoryById(catid));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}
	
	public static void onStartUP() {
		((JDA) Main.bot).getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryid FROM statchannels WHERE guildid = " + guild.getIdLong());
			
			try {
				if(set.next()) {
					long catid = set.getLong("categoryid");
					Category cat = guild.getCategoryById(catid);
					
					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});
					
					fillCategory(cat);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}
	
	public static void onShutdown() {
		((JDA) Main.bot).getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryid FROM statchannels WHERE guildid = " + guild.getIdLong());
			
			try {
				if(set.next()) {
					long catid = set.getLong("categoryid");
					Category cat = guild.getCategoryById(catid);
					
					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});
					
					VoiceChannel offline = cat.createVoiceChannel("🔴 BOT OFFLINE").complete();
					PermissionOverride override = new PermissionOverrideActionImpl(cat.getJDA(), offline, cat.getGuild().getPublicRole()).complete();
					
					offline.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}
}