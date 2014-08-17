package padsolver;

import java.util.*;

class BoardScore {

    static int countConnected(Board b, int i, int j, Orb orb) {
        int connected = 2; // orb elim. is 1x for first 3, then +.25 for each orb after. this takes care of that
        for (Move m : b.validMoves(i, j)) {
            if (b.get(i, j) == orb) {
                connected++;
                b.set(i, j, Orb.NONE);
                countConnected(b, i + m.dx, j + m.dy, orb);
            }
        }
        return connected;
    }

    static double score(Board b) {
        HashMap<Orb, Integer> numOrbs = new HashMap<Orb, Integer>();
        for (Orb o : Orb.values()) {
            numOrbs.put(o, 0);
        }
        int combo = 0;

        while (true) {
            try {
                Board matches = b.findMatches();
                for (int i = 0; i < b.rows; i++) {
                    for (int j = 0; j < b.cols; j++) {
                        if (matches.get(i, j) == Orb.NONE) continue;
                        b.set(i, j, Orb.NONE);
                    }
                }
                for (int i = 0; i < b.rows; i++) {
                    for (int j = 0; j < b.cols; j++) {
                        Orb o = matches.get(i, j);
                        if (o == Orb.NONE) continue;
                        combo++;
                        int orbs = countConnected(matches, i, j, o);
                        numOrbs.put(o, numOrbs.get(o) + orbs);
                    }
                }
                b.gravityFill(); 
            } catch (NoMatchesException e) {
                break;
            }
        }

        double score = 0;
        for (Orb o : Orb.values()) {
            score += o.weight * numOrbs.get(o);
        }
        score *= (0.75 + combo * GSettings.COMBO_BONUS);

        return score;
    }

}
