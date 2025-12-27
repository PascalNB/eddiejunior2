package com.pascalnb.eddie.components.ping;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.database.ComponentDatabaseManager;

public class PingComponent extends EddieComponent {

    public PingComponent(Eddie eddie, GuildManager gm, ComponentDatabaseManager db) {
        super(eddie, gm, db);

        addCommand(
            new PingCommand(this)
        );
    }

}
