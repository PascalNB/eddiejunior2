package com.pascalnb.eddie.database;

import com.pascalnb.dbwrapper.Mapper;
import com.pascalnb.dbwrapper.Query;
import com.pascalnb.dbwrapper.StringMapper;
import com.pascalnb.dbwrapper.action.DatabaseAction;
import com.pascalnb.dbwrapper.action.Promise;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static final DatabaseManager INSTANCE = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    private static final Query GET_SETTINGS = new Query(
        "SELECT value FROM setting WHERE guild_id=? AND component_id=? AND name=?;");

    private static final Query ADD_SETTING = new Query(
        "INSERT INTO setting (guild_id,component_id,name,value) VALUES(?,?,?,?);");

    private static final Query REMOVE_SETTING = new Query(
        "DELETE FROM setting WHERE guild_id=? AND component_id=? AND name=?;");

    private static final Query REMOVE_SETTING_VALUE = new Query(
        "DELETE FROM setting WHERE guild_id=? AND component_id=? AND name=? AND value=?;");

    public static boolean createDatabase() throws IOException {
        File dbFile = new File("database.sqlite3");
        if (!dbFile.exists()) {
            return dbFile.createNewFile();
        }
        return true;
    }

    @CheckReturnValue
    public Promise<Void> initialize() {
        return DatabaseAction.of("""
                create table if not exists setting (
                guild_id integer not null,
                component_id text not null,
                name text not null,
                value text default null,
                constraint setting_pk primary key (guild_id, component_id, name, value)
                );
                """)
            .execute();
    }

    public ComponentDatabaseManager forComponent(String guildId, String componentId) {
        return new ComponentDatabaseManager(this, guildId, componentId);
    }

    public List<String> getSettings(String guildId, String componentId, String setting) {
        return DatabaseAction.of(
                GET_SETTINGS.withArgs(guildId, componentId, setting),
                Mapper.stringList()
            )
            .query()
            .await();
    }

    public String getSetting(String guildId, String componentId, String setting) {
        return DatabaseAction.of(GET_SETTINGS.withArgs(guildId, componentId, setting))
            .query(Mapper.stringValue())
            .await();
    }

    @CheckReturnValue
    public Promise<Void> addSetting(String guildId, String componentId, String setting, String value) {
        return DatabaseAction.of(ADD_SETTING.withArgs(guildId, componentId, setting, value)).execute();
    }

    @CheckReturnValue
    public Promise<Void> setSetting(String guildId, String componentId, String setting, @NotNull Object value) {
        return DatabaseAction.allOf(
            DatabaseAction.of(REMOVE_SETTING.withArgs(guildId, componentId, setting)),
            DatabaseAction.of(ADD_SETTING.withArgs(guildId, componentId, setting, value))
        ).execute();
    }

    @CheckReturnValue
    public Promise<Void> removeSetting(String guildId, String componentId, String setting) {
        return DatabaseAction.of(REMOVE_SETTING.withArgs(guildId, componentId, setting)).execute();
    }

    @CheckReturnValue
    public Promise<Void> removeSettingValue(String guildId, String componentId, String setting, String value) {
        return DatabaseAction.of(REMOVE_SETTING_VALUE.withArgs(guildId, componentId, setting, value)).execute();
    }

}