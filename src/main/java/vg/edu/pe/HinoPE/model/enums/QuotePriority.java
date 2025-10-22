package vg.edu.pe.HinoPE.model.enums;

public enum QuotePriority {
    ALTA("alta"),
    MEDIA("media"),
    BAJA("baja");

    private final String value;

    QuotePriority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QuotePriority fromValue(String value) {
        for (QuotePriority priority : QuotePriority.values()) {
            if (priority.value.equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid QuotePriority: " + value);
    }
}
