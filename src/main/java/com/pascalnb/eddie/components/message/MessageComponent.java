package com.pascalnb.eddie.components.message;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageComponent extends EddieComponent {

    private final Map<String, MessageCreateData> clipboard = new ConcurrentHashMap<>();


    public MessageComponent(ComponentConfig config) {
        super(config);

        register(
            new PasteCommand(this)
                .addPermissions(Permission.BAN_MEMBERS, Permission.MANAGE_SERVER),
            new CopyCommand(this)
                .addPermissions(Permission.BAN_MEMBERS, Permission.MANAGE_SERVER),
            new PasteContextCommand(this)
                .addPermissions(Permission.BAN_MEMBERS, Permission.MANAGE_SERVER)
        );
    }

    public Map<String, MessageCreateData> getClipboard() {
        return clipboard;
    }

}
