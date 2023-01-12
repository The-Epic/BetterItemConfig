package me.epic.betteritemconfig;

public class PluginNotFoundException extends RuntimeException {

    public PluginNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
