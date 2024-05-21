package com.alan.clients.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SilentRender {
    OFF("Off"),
    Rebirth("Rebirth"),
    Rebirth2("Rebirth2"),
    New("New");
    String name;

    @Override
    public String toString() {
        return name;
    }
}
