package unboxthecat.meowoflegends.helper;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class Geometric {
    public interface BinaryBlockPredicate {
        boolean isGood(Block block);
    }

    public static ArrayList<Block> getBlockInCylinder(Location location, int radius, int depth, int height) {
        return getBlockInCylinder(location, radius, depth, height, (Block block) -> true);
    }

    public static ArrayList<Block> getBlockInCylinder(Location location, int radius, int depth, int height, BinaryBlockPredicate predicate) {
        ArrayList<Block> blocks = new ArrayList<>();

        World world = location.getWorld();
        if (world != null) {
            int radiusSquare = radius * radius;
            for (int x = -radius; x <= radius; ++x) {
                int xSquare = x * x;

                for (int z = -radius; z <= radius; ++z) {
                    int zSquare = z * z;

                    if (xSquare + zSquare <= radiusSquare) {
                        for (int y = -depth; y <= height; ++y) {
                            Block block = world.getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
                            if (predicate.isGood(block)) {
                                blocks.add(block);
                            }
                        }
                    }
                }
            }
        }

        return blocks;
    }
}
