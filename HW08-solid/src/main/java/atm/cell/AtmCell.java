package atm.cell;

import atm.money.Banknote;
import atm.money.Rate;

import java.util.ArrayDeque;
import java.util.Deque;

public class AtmCell {
    private final Rate rate;
    Deque<Banknote> banknotes = new ArrayDeque<>();

    public AtmCell(Rate rate) {
        this.rate = rate;
    }

    public int getTotalValue() {
        return Banknote.totalValue(banknotes);
    }

    public void visit(CellVisitor<?> visitor) {
        visitor.visit(this);
    }

    public void offer(Banknote banknote) {
        banknotes.offer(banknote);
    }

    public Banknote pop() {
        return banknotes.pop();
    }

    public Rate getRate() {
        return rate;
    }

    public boolean isEmpty() {
        return banknotes.isEmpty();
    }
}
