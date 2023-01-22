package me.epic.betteritemconfig.exceptions;

public class PluginNotFoundException extends RuntimeException {

    public PluginNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
