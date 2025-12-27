package com.pascalnb.eddie.components.ping;

import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponent;

public class PingComponent extends EddieComponent {

    public PingComponent(ComponentConfig config) {
        super(config);

        addCommand(
            new PingCommand(this)
        );
    }

}
