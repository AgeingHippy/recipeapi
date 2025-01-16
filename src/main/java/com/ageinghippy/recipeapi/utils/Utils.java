package com.ageinghippy.recipeapi.utils;

public class Utils {

    /**
     * If @preferred is not null, return preferred. Else return alternative
     *
     * @param preferred
     * @param alternative
     * @return {@code T} preferred not null ? preferred : alternative
     */
    public static <T> T nvl(T preferred, T alternative) {
        return preferred != null ? preferred : alternative;
    }
}
