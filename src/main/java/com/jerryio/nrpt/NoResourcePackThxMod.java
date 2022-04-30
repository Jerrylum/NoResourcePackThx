package com.jerryio.nrpt;

import net.fabricmc.api.ModInitializer;
//#if MC_VERSION >= 1.17.0
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//#else
/// import org.apache.logging.log4j.Logger;
/// import org.apache.logging.log4j.LogManager;
//#endif

public class NoResourcePackThxMod implements ModInitializer {
	//#if MC_VERSION >= 1.17.0
    public static final Logger LOGGER = LoggerFactory.getLogger("nrpt");
	//#else
    /// public static final Logger LOGGER = LogManager.getLogger("nrpt");
	//#endif

	@Override
	public void onInitialize() {
		LOGGER.info("No resource pack! Thank you. - @MC_VERSION@");
	}
}
