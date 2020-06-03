package com.github.tivecs.tivecore.lab.utils;

import com.github.tivecs.tivecore.lab.utils.xseries.XBiome;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BiomeUtils {

    public final static List<XBiome> TAIGA = Collections.unmodifiableList(Arrays.asList(
            XBiome.TAIGA, XBiome.TAIGA_HILLS, XBiome.TAIGA_MOUNTAINS,
            XBiome.SNOWY_TAIGA, XBiome.SNOWY_TAIGA_HILLS, XBiome.SNOWY_TAIGA_MOUNTAINS,
            XBiome.GIANT_SPRUCE_TAIGA, XBiome.GIANT_TREE_TAIGA,
            XBiome.GIANT_SPRUCE_TAIGA_HILLS, XBiome.GIANT_TREE_TAIGA_HILLS
            )
    );

    public final static List<XBiome> OCEAN = Collections.unmodifiableList(Arrays.asList(
            XBiome.OCEAN, XBiome.COLD_OCEAN, XBiome.WARM_OCEAN, XBiome.LUKEWARM_OCEAN, XBiome.FROZEN_OCEAN
            )
    );

    public final static List<XBiome> DEEP_OCEAN = Collections.unmodifiableList(Arrays.asList(
            XBiome.DEEP_OCEAN, XBiome.DEEP_COLD_OCEAN, XBiome.DEEP_WARM_OCEAN, XBiome.DEEP_LUKEWARM_OCEAN, XBiome.DEEP_FROZEN_OCEAN
            )
    );

    public final static List<XBiome> DESERT = Collections.unmodifiableList(Arrays.asList(
            XBiome.DESERT, XBiome.DESERT_HILLS, XBiome.DESERT_LAKES
            )
    );

    public final static List<XBiome> FOREST = Collections.unmodifiableList(Arrays.asList(
            XBiome.FOREST, XBiome.BIRCH_FOREST, XBiome.DARK_FOREST, XBiome.FLOWER_FOREST,
            XBiome.TALL_BIRCH_FOREST, XBiome.BIRCH_FOREST_HILLS,
            XBiome.DARK_FOREST_HILLS
            )
    );

    public final static List<XBiome> SAVANNA = Collections.unmodifiableList(Arrays.asList(
            XBiome.SAVANNA, XBiome.SAVANNA_PLATEAU,
            XBiome.SHATTERED_SAVANNA, XBiome.SHATTERED_SAVANNA_PLATEAU
            )
    );

    public final static List<XBiome> EXTREME_HILLS = Collections.unmodifiableList(Arrays.asList(
            XBiome.MOUNTAINS, XBiome.MOUNTAIN_EDGE, XBiome.WOODED_MOUNTAINS,
            XBiome.GRAVELLY_MOUNTAINS, XBiome.MODIFIED_GRAVELLY_MOUNTAINS
            )
    );

    public final static List<XBiome> MESA = Collections.unmodifiableList(Arrays.asList(
            XBiome.BADLANDS, XBiome.BADLANDS_PLATEAU, XBiome.ERODED_BADLANDS,
            XBiome.MODIFIED_BADLANDS_PLATEAU, XBiome.MODIFIED_WOODED_BADLANDS_PLATEAU, XBiome.WOODED_BADLANDS_PLATEAU
            )
    );

    public final static List<XBiome> PLAINS = Collections.unmodifiableList(Arrays.asList(
            XBiome.PLAINS, XBiome.SUNFLOWER_PLAINS
            )
    );

    public final static List<XBiome> RIVER = Collections.unmodifiableList(Arrays.asList(
            XBiome.RIVER, XBiome.FROZEN_RIVER
            )
    );

    public final static List<XBiome> MUSHROOM_ISLAND = Collections.unmodifiableList(Arrays.asList(
            XBiome.MUSHROOM_FIELDS, XBiome.MUSHROOM_FIELD_SHORE
            )
    );

    public final static List<XBiome> JUNGLE = Collections.unmodifiableList(Arrays.asList(
            XBiome.JUNGLE, XBiome.JUNGLE_EDGE, XBiome.JUNGLE_HILLS,
            XBiome.MODIFIED_JUNGLE, XBiome.MODIFIED_JUNGLE_EDGE,
            XBiome.BAMBOO_JUNGLE, XBiome.BAMBOO_JUNGLE_HILLS
            )
    );

    //---------------------------------------------
    //            AREA BY DESCRIPTION
    //---------------------------------------------

    public final static List<XBiome> BAMBOO_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.BAMBOO_JUNGLE_HILLS, XBiome.BAMBOO_JUNGLE
            )
    );

    public final static List<XBiome> SNOWY_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.SNOWY_TAIGA, XBiome.SNOWY_TUNDRA, XBiome.SNOWY_BEACH, XBiome.SNOWY_MOUNTAINS,
            XBiome.SNOWY_TAIGA_MOUNTAINS, XBiome.SNOWY_TAIGA_HILLS,

            XBiome.ICE_SPIKES, XBiome.FROZEN_RIVER
            )
    );

    public final static List<XBiome> DRY_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.DESERT, XBiome.DESERT_LAKES, XBiome.DESERT_HILLS,

            XBiome.SAVANNA, XBiome.SAVANNA_PLATEAU,
            XBiome.SHATTERED_SAVANNA, XBiome.SHATTERED_SAVANNA_PLATEAU,

            XBiome.BADLANDS_PLATEAU, XBiome.BADLANDS_PLATEAU, XBiome.ERODED_BADLANDS, XBiome.WOODED_BADLANDS_PLATEAU,
            XBiome.MODIFIED_BADLANDS_PLATEAU, XBiome.MODIFIED_WOODED_BADLANDS_PLATEAU
            )
    );

    public final static List<XBiome> AQUA_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.OCEAN, XBiome.COLD_OCEAN, XBiome.WARM_OCEAN, XBiome.LUKEWARM_OCEAN, XBiome.FROZEN_OCEAN,
            XBiome.DEEP_OCEAN, XBiome.DEEP_COLD_OCEAN, XBiome.DEEP_WARM_OCEAN, XBiome.DEEP_LUKEWARM_OCEAN, XBiome.DEEP_FROZEN_OCEAN,

            XBiome.RIVER, XBiome.FROZEN_RIVER
            )
    );

    public final static List<XBiome> WARM_OCEAN_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.WARM_OCEAN, XBiome.DEEP_WARM_OCEAN
            )
    );

    public final static List<XBiome> LUKEWARM_OCEAN_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.LUKEWARM_OCEAN, XBiome.DEEP_LUKEWARM_OCEAN
            )
    );

    public final static List<XBiome> COLD_OCEAN_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.COLD_OCEAN, XBiome.DEEP_COLD_OCEAN
            )
    );

    public final static List<XBiome> FROZEN_OCEAN_AREA = Collections.unmodifiableList(Arrays.asList(
            XBiome.FROZEN_OCEAN, XBiome.DEEP_FROZEN_OCEAN
            )
    );

}
