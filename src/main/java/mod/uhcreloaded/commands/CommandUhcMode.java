package mod.uhcreloaded.commands;

import static mod.uhcreloaded.util.Misc.translate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.WorldInfo;

public class CommandUhcMode extends CommandBase {

	private static final String NAME = "uhcmode";
	private static final String USAGE = "/uhcmode <on/off>";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return USAGE;
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		int l = args.length;

		if (l == 1) {
			String mode = args[0];
			if (mode.equalsIgnoreCase("on")) {
				for (int a = 0; a < MinecraftServer.getServer().worldServers.length; a++) {
					WorldInfo info = MinecraftServer.getServer().worldServers[a].getWorldInfo();
					GameRules gamerules = MinecraftServer.getServer().worldServers[a].getGameRules();
					info.setHardcore(true);
					gamerules.setOrCreateGameRule("naturalRegeneration", "false");
				}
				sender.addChatMessage(new ChatComponentText(translate("commands.uhcreload.uhc.on")));
				notifyOperators(sender, this, "[UHCReload]UltraHardcore mode: ON.", new Object[] {});
			}

			if (mode.equalsIgnoreCase("off")) {
				for (int a = 0; a < MinecraftServer.getServer().worldServers.length; a++) {
					WorldInfo info = MinecraftServer.getServer().worldServers[a].getWorldInfo();
					GameRules gamerules = MinecraftServer.getServer().worldServers[a].getGameRules();
					info.setHardcore(false);
					gamerules.setOrCreateGameRule("naturalRegeneration", "true");
				}
				sender.addChatMessage(new ChatComponentText(translate("commands.uhcreload.uhc.off")));
				notifyOperators(sender, this, "[UHCReload]UltraHardcore mode: OFF.", new Object[] {});
			}
		}
		
		throw new UhcCommandException(translate("commands.uhcreload.error.args"));
	}

}
