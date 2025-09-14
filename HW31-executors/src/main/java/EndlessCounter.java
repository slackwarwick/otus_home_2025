import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class EndlessCounter {
    private static final Logger logger = LoggerFactory.getLogger(EndlessCounter.class);
    private String lastChanger = "PONG";

    private enum Dir {
        ASC(val -> val + 1),
        DESC(val -> val - 1);
        private final Function<Integer, Integer> nextValue;
        Dir(Function<Integer, Integer> nextValue) {
            this.nextValue = nextValue;
        }
        public int nextValue(int value) {
            return nextValue.apply(value);
        }
    };

    private synchronized void count(String changer) {
        Dir dir = Dir.ASC;
        int value = 0;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                // spurious wakeup https://en.wikipedia.org/wiki/Spurious_wakeup
                // поэтому не if
                while (lastChanger.equals(changer)) {
                    this.wait();
                }
                value = dir.nextValue(value);
                logger.info(String.valueOf(value));
                if (value == 1 && dir == Dir.DESC) {
                    dir = Dir.ASC;
                } else if (value == 10 && dir == Dir.ASC) {
                    dir = Dir.DESC;
                }
                lastChanger = changer;
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        EndlessCounter endlessCounter = new EndlessCounter();
        new Thread(() -> endlessCounter.count("ping")).start();
        new Thread(() -> endlessCounter.count("PONG")).start();
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
