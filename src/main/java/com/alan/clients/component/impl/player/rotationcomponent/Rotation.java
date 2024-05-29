package com.alan.clients.component.impl.player.rotationcomponent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Rotation {
    OFF("Off"),
    Rise("Rise"),
    Rebirth("Rebirth"),
    New("New");
    String name;

    @Override
    public String toString() {
        return name;
    }
}
