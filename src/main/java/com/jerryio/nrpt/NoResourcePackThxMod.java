package com.jerryio.nrpt;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class NoResourcePackThxMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("nrpt");

	@Override
	public void onInitialize() {
		LOGGER.info("No resource pack! Thank you.");
	}
}
