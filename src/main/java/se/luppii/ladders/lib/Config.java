package se.luppii.ladders.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class Config {
	
	// General
	public static Property checkForUpdates;
	public static Property sturdyLadderLeftClick;
	
	// Updater
	public static Property ropeLadderLeftClick;
	
	public static void loadConfig(FMLPreInitializationEvent e) {
		
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		
		try {
			config.load();
			
			// Block config.
			sturdyLadderLeftClick = config.get(Configuration.CATEGORY_GENERAL, "Sturdy Ladder extend on left click", true);
			ropeLadderLeftClick = config.get(Configuration.CATEGORY_GENERAL, "Rope Ladder extend on left click", true);
			
			// Update Checker
			checkForUpdates = config.get("updater", "Check for updates", true);
			
			config.save();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
}
