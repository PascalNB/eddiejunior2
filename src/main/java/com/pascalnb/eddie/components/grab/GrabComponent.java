package com.pascalnb.eddie.components.grab;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.Permission;

public class GrabComponent extends EddieComponent {

    public GrabComponent(ComponentConfig config) {
        super(config);

        register(
            new EddieCommand<>(this, "grab", "grab", Permission.BAN_MEMBERS)
                .addSubCommands(
                    new GrabBannerCommand(this),
                    new GrabEmojiCommand(this),
                    new GrabMemberIconCommand(this),
                    new GrabUserIconCommand(this),
                    new GrabSplashCommand(this)
                ),
            new GrabStickerCommand(this)
                .addPermissions(Permission.BAN_MEMBERS)
        );
    }

}
