package com.pascalnb.eddie.database;

import com.pascalnb.dbwrapper.action.Promise;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.List;

public class ComponentDatabaseManager {

    private final DatabaseManager databaseManager;
    private final String guildId;
    private final String componentId;

    public ComponentDatabaseManager(DatabaseManager databaseManager, String guildId, String componentId) {
        this.databaseManager = databaseManager;
        this.guildId = guildId;
        this.componentId = componentId;
    }

    public List<String> getSettings(String setting) {
        return databaseManager.getSettings(guildId, componentId, setting);
    }

    public String getSetting(String setting) {
        return databaseManager.getSetting(guildId, componentId, setting);
    }

    @CheckReturnValue
    public Promise<Void> setSetting(String setting, Object value) {
        return databaseManager.setSetting(guildId, componentId, setting, value);
    }

    @CheckReturnValue
    public Promise<Void> removeSetting(String setting) {
        return databaseManager.removeSetting(guildId, componentId, setting);
    }

    @CheckReturnValue
    public Promise<Void> removeSettingValue(String setting, String value) {
        return databaseManager.removeSettingValue(guildId, componentId, setting, value);
    }

    @CheckReturnValue
    public Promise<Void> addSettings(String setting, Collection<String> values) {
        return databaseManager.addSettings(guildId, componentId, setting, values);
    }

    @CheckReturnValue
    public Promise<Void> addSetting(String setting, String value) {
        return databaseManager.addSetting(guildId, componentId, setting, value);
    }

}
