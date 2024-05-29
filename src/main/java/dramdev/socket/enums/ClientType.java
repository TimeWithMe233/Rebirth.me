package dramdev.socket.enums;



/**
 * @author DiaoLing
 * @since 4/5/2024
 */
public enum ClientType {
    EMPTY("Empty"),
    rebirth("rebirth"),
    Kura("Kura"),
    Rebirth("Rebirth"),
    ArtDay("ArtDay"),
    FoxSense("FoxSense"),
    Dominic("Dominic"),
    Artist("Artist"),
    EDFDP("Edfdp"),
    Salt("Salt"),
    Forever("Forever"),
    NEVER("Never");
    private final String name;

    ClientType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
