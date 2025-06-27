package atm.money;

public enum Rate {
    FIVE_HUNDRED(500),
    ONE_HUNDRED(100),
    FIFTY(50),
    ONE_THOUSAND(1000);

    private final int value;

    Rate(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
