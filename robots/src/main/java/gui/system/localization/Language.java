package gui.system.localization;

import lombok.Getter;

import java.util.*;

/**
 * Enum representing supported languages
 */
@Getter
public enum Language {
    ENGLISH("English", new Locale("en")),
    RUSSIAN("Русский", new Locale("ru"));

    private final String displayName;
    private final Locale locale;

    Language(String displayName, Locale locale) {
        this.displayName = displayName;
        this.locale = locale;
    }


}