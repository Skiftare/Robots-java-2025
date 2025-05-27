package game.mods;

/**
 * Base interface for all mods
 */
public interface IMod {
    String getName();

    String getVersion();

    String getAuthor();

    String getDescription();

    void initialize(ModRegistry registry);

    void shutdown();
}