package atm.money;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Banknote {
    private final Rate rate;

    public Banknote(Rate rate) {
        this.rate = rate;
    }

    public static List<Banknote> pack(Rate rate, int value) {
        ArrayList<Banknote> result = new ArrayList<>();
        for (int i = 0; i < value; ++i) {
            result.add(new Banknote(rate));
        }
        return result;
    }

    public static int totalValue(Collection<Banknote> banknotes) {
        return banknotes.stream().mapToInt(Banknote::getRateValue).sum();
    }

    public Rate getRate() {
        return rate;
    }

    public int getRateValue() {
        return rate.getValue();
    }
}
