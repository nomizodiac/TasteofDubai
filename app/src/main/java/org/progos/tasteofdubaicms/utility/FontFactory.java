package org.progos.tasteofdubaicms.utility;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by NomBhatti on 12/2/2015.
 */
public class FontFactory {

    private static FontFactory instance;
    private HashMap<String, Typeface> fontMap = new HashMap<>();

    private FontFactory() {
    }

    public static FontFactory getInstance() {
        if (instance == null) {
            instance = new FontFactory();
        }
        return instance;
    }

    public Typeface getFont(Context context, String font) {
        Typeface typeface = fontMap.get(font);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/" + font);
            fontMap.put(font, typeface);
        }
        return typeface;
    }
}
