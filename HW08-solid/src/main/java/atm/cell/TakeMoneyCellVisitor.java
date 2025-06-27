package atm.cell;

import atm.money.Banknote;

import java.util.ArrayList;
import java.util.List;

public class TakeMoneyCellVisitor implements CellVisitor<List<Banknote>> {
    private int amount;
    List<Banknote> banknotes = new ArrayList<>();

    public TakeMoneyCellVisitor(int amount) {
        this.amount = amount;
    }

    @Override
    public void visit(AtmCell cell) {
        if (cell.isEmpty() || amount == 0)
            return;
        while (amount >= cell.getRate().getValue() && !cell.isEmpty()) {
            Banknote banknote = cell.pop();
            banknotes.add(banknote);
            amount = amount - banknote.getRateValue();
        }
    }

    public List<Banknote> getResult() {
        return banknotes;
    }
}
