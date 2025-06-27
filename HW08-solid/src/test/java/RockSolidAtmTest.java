import atm.RockSolidAtm;
import atm.exception.AtmException;
import atm.money.Banknote;
import atm.money.Rate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RockSolidAtmTest {
    @Test
    public void testAtm() {
        RockSolidAtm atm = new RockSolidAtm();
        assertEquals(0, atm.getBalance(), "Empty balance");

        atm.offer(Banknote.pack(Rate.ONE_THOUSAND, 110));
        atm.offer(Banknote.pack(Rate.FIVE_HUNDRED, 1));
        atm.offer(Banknote.pack(Rate.ONE_HUNDRED, 2));
        atm.offer(Banknote.pack(Rate.FIFTY, 3));
        assertEquals(110850, atm.getBalance(), "Balance before take");

        List<Banknote> banknotes = atm.takeMoney(1850);
        assertFalse(banknotes.isEmpty());
        assertEquals(1850, Banknote.totalValue(banknotes), "Supported value");
        assertEquals(109000, atm.getBalance(), "Balance after take");

        assertThrows(AtmException.class, () -> atm.takeMoney(12), "Unsupported value");
    }
}