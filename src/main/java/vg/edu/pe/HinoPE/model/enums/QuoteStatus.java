package vg.edu.pe.HinoPE.model.enums;

public enum QuoteStatus {
    PENDIENTE("pendiente"),
    EN_PROCESO("en-proceso"),
    ENVIADA("enviada"),
    CERRADA("cerrada");

    private final String value;

    QuoteStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QuoteStatus fromValue(String value) {
        for (QuoteStatus status : QuoteStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid QuoteStatus: " + value);
    }
}
