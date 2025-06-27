package atm.cell;

import java.util.Collection;

public interface CellVisitor<T> {
    void visit(AtmCell cell);
    T getResult();
}
