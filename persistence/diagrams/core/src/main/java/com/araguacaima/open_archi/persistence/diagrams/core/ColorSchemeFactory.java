package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.open_archi.persistence.commons.Utils;

import java.util.HashSet;
import java.util.Set;

public class ColorSchemeFactory {
    public static final ColorScheme PRIMARY_DEFAULT = new ColorScheme(ColorSchemeOption.PRIMARY, Utils.randomHexColor(), "#333333", "white");
    public static final ColorScheme SECONDARY_DEFAULT = new ColorScheme(ColorSchemeOption.SECONDARY, "white", "#333333", "black");
    public static final Set<ColorScheme> DEFAULT = new HashSet<ColorScheme>() {{
        add(PRIMARY_DEFAULT);
        add(SECONDARY_DEFAULT);
    }};
}
