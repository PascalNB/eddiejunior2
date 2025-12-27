package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;

public interface EddieComponentFactory<T extends EddieComponent> {

    T createComponent(Eddie eddie, GuildManager guildManager, ComponentDatabaseManager db);

}
