package com.pascalnb.eddie.components.role;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.Permission;

public class RoleComponent extends EddieComponent {

    public static final String BUTTON_ID_PREFIX = "_r_";

    private final RoleMessageModal roleMessageModal;

    public RoleComponent(ComponentConfig config) {
        super(config);

        roleMessageModal = new RoleMessageModal(this);

        register(
            new RoleButtonListener(this),
            new EddieCommand<>(this, "manage-roles", "Manage roles", Permission.BAN_MEMBERS, Permission.MANAGE_ROLES)
                .addSubCommands(
                    new RoleMessageCommand(this)
                ),
            roleMessageModal
        );
    }

    public RoleMessageModal getRoleMessageModal() {
        return roleMessageModal;
    }

}
