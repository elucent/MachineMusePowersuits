package net.machinemuse.powersuits.powermodule.armor;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.ModuleManager;
import net.machinemuse.api.moduletrigger.IPlayerTickModule;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseHeatUtils;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/**
 * Created by User: Andrew2448
 * 4:35 PM 6/21/13
 */
public class WaterTankModule extends PowerModuleBase implements IPlayerTickModule {
    public static final String MODULE_WATER_TANK = "Water Tank";
    public static final String WATER_TANK_SIZE = "Tank Size";
    public static final String ACTIVATION_PERCENT = "Heat Activation Percent";
    final ItemStack bucketWater = new ItemStack(Items.WATER_BUCKET);

    public WaterTankModule(List<IModularItem> validItems) {
        super(validItems);
        addBaseProperty(WATER_TANK_SIZE, 200);
        addBaseProperty(MuseCommonStrings.WEIGHT, 1000);
        addBaseProperty(ACTIVATION_PERCENT, 0.5);
        addTradeoffProperty("Activation Percent", ACTIVATION_PERCENT, 0.5, "%");
        addTradeoffProperty("Tank Size", WATER_TANK_SIZE, 800, " buckets");
        addTradeoffProperty("Tank Size", MuseCommonStrings.WEIGHT, 4000, "g");
        addInstallCost(bucketWater);
        addInstallCost(new ItemStack(Blocks.GLASS, 8));
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.controlCircuit, 2));
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(bucketWater).getParticleTexture();
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_ENVIRONMENTAL;
    }

    @Override
    public String getDataName() {
        return MODULE_WATER_TANK;
    }

    @Override
    public String getUnlocalizedName() {
        return "waterTank";
    }

    @Override
    public String getDescription() {
        return "Store water which can later be used to cool yourself in emergency situations.";
    }
    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (MuseItemUtils.getWaterLevel(item) > ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
            MuseItemUtils.setWaterLevel(item, ModuleManager.computeModularProperty(item, WATER_TANK_SIZE));
        }

        // Fill tank if player is in water
        Block block = player.worldObj.getBlockState(player.getPosition()).getBlock();
        if (((block == Blocks.WATER) || block == Blocks.FLOWING_WATER) && MuseItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
            MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) + 1);
        }

        // Fill tank if raining
        int xCoord = MathHelper.floor_double(player.posX);
        int zCoord = MathHelper.floor_double(player.posZ);
        boolean isRaining = (player.worldObj.getBiomeForCoordsBody(player.getPosition()).getRainfall() > 0) && (player.worldObj.isRaining() || player.worldObj.isThundering());
        if (isRaining && player.worldObj.canBlockSeeSky(player.getPosition().add(0,1,0))
                && (player.worldObj.getTotalWorldTime() % 5) == 0 && MuseItemUtils.getWaterLevel(item) < ModuleManager.computeModularProperty(item, WATER_TANK_SIZE)) {
            MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) + 1);
        }

        // Apply cooling
        double currentHeat = MuseHeatUtils.getPlayerHeat(player);
        double maxHeat = MuseHeatUtils.getMaxHeat(player);
        if ((currentHeat / maxHeat) >= ModuleManager.computeModularProperty(item, ACTIVATION_PERCENT) && MuseItemUtils.getWaterLevel(item) > 0) {
            MuseHeatUtils.coolPlayer(player, 1);
            MuseItemUtils.setWaterLevel(item, MuseItemUtils.getWaterLevel(item) - 1);
            for (int i = 0; i < 4; i++) {
                player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }
}