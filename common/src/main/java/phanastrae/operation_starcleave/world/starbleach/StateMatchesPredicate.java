package phanastrae.operation_starcleave.world.starbleach;


import com.google.common.collect.ImmutableList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class StateMatchesPredicate implements Predicate<BlockState> {
    private final List<Block> blocks;
    private final List<TagKey<Block>> blockTags;

    public StateMatchesPredicate(Builder builder) {
        this.blocks = ImmutableList.copyOf(builder.blocks);
        this.blockTags = ImmutableList.copyOf(builder.blockTags);
    }

    public static StateMatchesPredicate fromBlock(Block block) {
        return new Builder(block).build();
    }

    public static StateMatchesPredicate fromBlockTag(TagKey<Block> blockTag) {
        return new Builder(blockTag).build();
    }

    public static StateMatchesPredicate fromBlocks(Block... blocks) {
        return new Builder(blocks).build();
    }

    @Override
    public boolean test(BlockState state) {
        for (Block block : blocks) {
            if (state.is(block)) {
                return true;
            }
        }

        for (TagKey<Block> blockTag : blockTags) {
            if (state.is(blockTag)) {
                return true;
            }
        }

        return false;
    }

    public static class Builder {
        private final List<Block> blocks;
        private final List<TagKey<Block>> blockTags;

        public Builder() {
            this.blocks = new ArrayList<>();
            this.blockTags = new ArrayList<>();
        }

        public Builder(Block block) {
            this();
            this.addBlock(block);
        }

        public Builder(TagKey<Block> blockTag) {
            this();
            this.addBlockTag(blockTag);
        }

        public Builder(Block... blocks) {
            this();
            this.addBlocks(blocks);
        }

        public Builder addBlock(Block block) {
            this.blocks.add(block);
            return this;
        }

        public Builder addBlockTag(TagKey<Block> blockTag) {
            this.blockTags.add(blockTag);
            return this;
        }

        public Builder addBlocks(Block... blocks) {
            this.blocks.addAll(Arrays.asList(blocks));
            return this;
        }

        public StateMatchesPredicate build() {
            return new StateMatchesPredicate(this);
        }
    }
}
