package com.github.tivecs.tivecore.lab.utils;

import java.util.Random;

public class RollUtils {

    private final static Random random = new Random();

    public static boolean chance(float value){
        if (value > 1 && value < 0){
            throw new IllegalArgumentException("Chance value must between 0 and 1");
        }

        float f = random.nextFloat();

        return value >= f || value == 1;
    }

}
