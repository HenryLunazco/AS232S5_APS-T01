package vg.edu.pe.HinoPE.model.enums;

public enum VehicleStatus {
    DISPONIBLE("disponible"),
    RESERVADO("reservado"),
    VENDIDO("vendido");

    private final String value;

    VehicleStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VehicleStatus fromValue(String value) {
        for (VehicleStatus status : VehicleStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid VehicleStatus: " + value);
    }
}
