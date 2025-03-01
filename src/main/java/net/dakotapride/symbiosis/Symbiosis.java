package net.dakotapride.symbiosis;

import net.dakotapride.symbiosis.registrar.BlockRegistrar;
import net.dakotapride.symbiosis.registrar.CreativeModeTabRegistrar;
import net.dakotapride.symbiosis.registrar.ItemRegistrar;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Symbiosis.MOD_ID)
public class Symbiosis {
    public static final String MOD_ID = "symbiosis";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Symbiosis(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BlockRegistrar.registrar(modEventBus);
        ItemRegistrar.registrar(modEventBus);
        CreativeModeTabRegistrar.registrar(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabRegistrar.SYMBIOSIS.getKey())
//            event.accept(BlockRegistrar.BULB_ITEM);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        //LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //LOGGER.info("HELLO FROM CLIENT SETUP");
            //LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void registerColourHandlers(RegisterColorHandlersEvent.Item event) {
            BlockColors colors = event.getBlockColors();
            event.register(
                    (p_92687_, p_92688_) -> {
                        BlockState blockstate = ((BlockItem)p_92687_.getItem()).getBlock().defaultBlockState();
                        return colors.getColor(blockstate, null, null, p_92688_);
                    },
                    BlockRegistrar.BULB.get()
            );
        }

        @SubscribeEvent
        public static void registerColourHandlers(RegisterColorHandlersEvent.Block event) {
//            event.register(
//                    (blockState, tintGetter, blockPos, i) -> tintGetter != null && blockPos != null
//                            ? BiomeColors.getAverageGrassColor(tintGetter, blockPos)
//                            : GrassColor.getDefaultColor(),
//                    BlockRegistrar.BULB.get()
//            );
            event.register((state, world, pos, tintIndex) -> world != null && pos != null
                            ? BiomeColors.getAverageGrassColor(world, pos)
                            : GrassColor.get(0.5D, 1.0D),
                    BlockRegistrar.BULB.get());
        }
    }
}
