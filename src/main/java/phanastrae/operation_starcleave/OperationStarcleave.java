package phanastrae.operation_starcleave;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationStarcleave implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("operation_starcleave");

    public static Identifier id(String path) {
    	return new Identifier("operation_starcleave", path);
	}

	@Override
	public void onInitialize() {

	}
}