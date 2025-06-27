package atm.cell;

public class CheckMoneyVisitor implements CellVisitor<Boolean> {
    private int amount;

    public CheckMoneyVisitor(int amount) {
        this.amount = amount;
    }

    @Override
    public void visit(AtmCell cell) {
        if (cell.isEmpty() || amount <= 0)
            return;
        int rateValue = cell.getRate().getValue();
        int totalValue = cell.getTotalValue();
        while (amount >= rateValue && totalValue > 0) {
            totalValue -= rateValue;
            amount -= rateValue;
        }
    }

    @Override
    public Boolean getResult() {
        return amount == 0;
    }
}
