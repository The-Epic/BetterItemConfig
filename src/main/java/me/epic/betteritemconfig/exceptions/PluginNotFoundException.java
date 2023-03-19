package me.epic.betteritemconfig.exceptions;

/*
Thrown if a plugin is not found when adding Persistent Data back to an item.

 */
public class PluginNotFoundException extends RuntimeException {

    public PluginNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
