package atm;

import atm.cell.*;
import atm.exception.AtmException;
import atm.money.Banknote;
import atm.money.Rate;

import java.util.*;

public class RockSolidAtm {
    private final Map<Rate, AtmCell> cells = new TreeMap<>(
            Comparator.comparing(Rate::getValue,
                Comparator.reverseOrder()));

    public RockSolidAtm() {
        for (Rate rate : Rate.values()) {
            cells.put(rate, new AtmCell(rate));
        }
    }

    public int getBalance() {
        return visit(cells.values(), new BalanceCellVisitor());
    }

    public void offer(List<Banknote> banknotes) {
        for (Banknote banknote : banknotes) {
            AtmCell cell = cells.get(banknote.getRate());
            if (cell == null) {
                throw new AtmException("No cell found");
            }
            cell.offer(banknote);
        }
    }

    public List<Banknote> takeMoney(int value) {
        if (!visit(cells.values(), new CheckMoneyVisitor(value))) {
            throw new AtmException("No such amount");
        }
        return visit(cells.values(), new TakeMoneyCellVisitor(value));
    }

    private <T> T visit(Collection<AtmCell> cells, CellVisitor<T> visitor) {
        for (AtmCell cell : cells) {
            cell.visit(visitor);
        }
        return visitor.getResult();
    }
}
