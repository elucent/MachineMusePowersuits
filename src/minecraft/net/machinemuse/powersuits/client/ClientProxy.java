package net.machinemuse.powersuits.client;

import net.machinemuse.powersuits.block.TileEntityTinkerTable;
import net.machinemuse.powersuits.block.TinkerTableRenderer;
import net.machinemuse.powersuits.common.CommonProxy;
import net.machinemuse.powersuits.common.Config;
import net.machinemuse.powersuits.common.PowersuitsMod;
import net.machinemuse.powersuits.network.MusePacketHandler;
import net.machinemuse.powersuits.tick.PlayerTickHandlerClient;
import net.machinemuse.powersuits.tick.RenderTickHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * The Client Proxy does all the things that should only be done client-side,
 * like registering client-side handlers and renderers.
 * 
 * @author MachineMuse
 * 
 */
public class ClientProxy extends CommonProxy {
	private static EquipmentRenderer equipmentRenderer = new EquipmentRenderer();
	private static RenderTickHandler renderTickHandler;
	private static PlayerTickHandlerClient playerTickHandler;

	/**
	 * Register all the custom renderers for this mod.
	 */
	@Override
	public void registerRenderers() {
		// MinecraftForgeClient.registerItemRenderer(PowersuitsMod.powerTool.itemID,
		// equipmentRenderer);
		int tinkTableRenderID = RenderingRegistry.getNextAvailableRenderId();
		TinkerTableRenderer tinkTableRenderer = new TinkerTableRenderer(
				tinkTableRenderID);
		PowersuitsMod.tinkerTable.setRenderType(tinkTableRenderID);
		ClientRegistry.bindTileEntitySpecialRenderer(
				TileEntityTinkerTable.class, tinkTableRenderer);
		RenderingRegistry.registerBlockHandler(tinkTableRenderer);
		MinecraftForgeClient.preloadTexture(Config.SEBK_ICON_PATH);
		MinecraftForgeClient.preloadTexture(Config.WC_ICON_PATH);
		MinecraftForgeClient.preloadTexture(Config.TINKERTABLE_TEXTURE_PATH);
		MinecraftForgeClient.preloadTexture(Config.BLANK_ARMOR_MODEL_PATH);
	}

	/**
	 * Register the tick handler (for on-tick behaviour) and packet handler (for
	 * network synchronization and permission stuff).
	 */
	@Override
	public void registerHandlers() {
		playerTickHandler = new PlayerTickHandlerClient();
		TickRegistry.registerTickHandler(playerTickHandler, Side.CLIENT);

		renderTickHandler = new RenderTickHandler();
		TickRegistry.registerTickHandler(renderTickHandler, Side.CLIENT);

		packetHandler = new MusePacketHandler().register();
	}

	@Override
	public void postInit() {

	}
}