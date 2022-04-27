package com.jerryio.nrpt;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoResourcePackThxMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("nrpt");

	@Override
	public void onInitialize() {
		LOGGER.info("No resource pack! Thank you.");
	}
}
