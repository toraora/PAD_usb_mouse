package padsolver;

import java.util.*;

enum Orb {
    RED("R", 1),
    BLUE("B", 1),
    GREEN("G", 1),
    LIGHT("L", 1),
    DARK("D", 1),
    HEAL("H", 1),
    BLOCK("*", 1),
    NONE("-", 0);

    private static final List<Orb> VALUES =
        Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size() - 2;
    private static final Random RANDOM = new Random();

    private String disp;
    double weight;
    
    Orb(String s, double w) {
        disp = s;
        weight = w;
    }

    static Orb randomOrb() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    public String toString() {
        return disp;
    }

    boolean equal(Orb o) {
        if (o == Orb.NONE) return false;
        return (o == this);
    }
}
