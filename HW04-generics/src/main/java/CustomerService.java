import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {
    private final TreeMap<Customer, String> map = new TreeMap<>(Comparator.comparing(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> first = map.firstEntry();
        if (first != null)
            return Map.entry(first.getKey().clone(), first.getValue());
        return null;
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> next = map.higherEntry(customer);
        if (next != null)
            return Map.entry(next.getKey().clone(), next.getValue());
        return null;
    }

    public void add(Customer customer, String data) {
        map.put(customer, data);
    }
}
