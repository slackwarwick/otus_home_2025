package atm.cell;

public class BalanceCellVisitor implements CellVisitor<Integer> {
    private int value = 0;

    @Override
    public void visit(AtmCell cell) {
        value = value + cell.getTotalValue();
    }

    public Integer getResult() {
        return value;
    }
}
