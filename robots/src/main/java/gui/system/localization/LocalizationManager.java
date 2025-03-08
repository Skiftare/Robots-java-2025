package gui.system.localization;


import log.Logger;
import lombok.Getter;

import java.util.*;

/**
 * Singleton manager for localization
 */
public class LocalizationManager {
    private static final String BUNDLE_NAME = "messages";
    private static LocalizationManager instance;

    @Getter
    private Language currentLanguage = Language.RUSSIAN;
    private ResourceBundle bundle;
    private final List<LocaleChangeListener> listeners = new ArrayList<>();

    private LocalizationManager() {
        loadBundle();
    }

    public static synchronized LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }

    private void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, currentLanguage.getLocale());
        } catch (MissingResourceException e) {
            Logger.error("Failed to load resource bundle: " + e.getMessage());
            bundle = new ListResourceBundle() {
                @Override
                protected Object[][] getContents() {
                    return new Object[0][];
                }
            };
        }
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            Logger.error("Missing translation key: " + key);
            return "!" + key + "!";
        }
    }

    public void setLanguage(Language language) {
        if (language != currentLanguage) {
            currentLanguage = language;
            loadBundle();
            notifyListeners();
        }
    }

    public void addListener(LocaleChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (LocaleChangeListener listener : listeners) {
            listener.localeChanged();
        }
    }
}