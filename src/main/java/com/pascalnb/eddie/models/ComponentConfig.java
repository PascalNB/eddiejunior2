package com.pascalnb.eddie.models;

import com.pascalnb.eddie.ComponentLogger;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;

public record ComponentConfig(GuildManager guildManager,
                              ComponentDatabaseManager componentDatabaseManager,
                              ComponentLogger componentLogger) {

}
