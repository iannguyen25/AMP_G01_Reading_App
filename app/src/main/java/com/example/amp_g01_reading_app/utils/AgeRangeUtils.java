package com.example.amp_g01_reading_app.utils;

public class AgeRangeUtils {
    public static int getMinAge(String ageRange) {
        return Integer.parseInt(ageRange.split(" - ")[0]);
    }

    public static int getMaxAge(String ageRange) {
        return Integer.parseInt(ageRange.split(" - ")[1].replace(" tuổi", ""));
    }
}