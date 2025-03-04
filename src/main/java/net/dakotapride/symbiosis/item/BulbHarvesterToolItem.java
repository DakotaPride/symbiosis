package net.dakotapride.symbiosis.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Tool;

import java.util.List;

public class BulbHarvesterToolItem extends Item {
    public BulbHarvesterToolItem(Properties properties) {
        super(properties);
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(Tool.Rule.overrideSpeed(BlockTags.LEAVES, 2.0F)), 1.0F, 1);
    }
}
