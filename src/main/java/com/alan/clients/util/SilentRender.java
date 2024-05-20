package com.alan.clients.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SilentRender {
    OFF("Off"),
    Lavender("Lavender"),
    Lavender2("Lavender2"),
    New("New");
    String name;

    @Override
    public String toString() {
        return name;
    }
}
