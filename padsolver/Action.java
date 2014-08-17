package padsolver;

import java.util.*;

enum Move {
    UP(-1, 0, "Up"),
    RIGHT(0, 1, "Right"),
    DOWN(1, 0, "Down"),
    LEFT(0, -1, "Left");

    int dx, dy;
    String disp;

    Move(int x, int y, String str) {
        dx = x;
        dy = y;
        disp = str;
    }

    boolean opposite(Move m) {
        return (m.dx == -dx || m.dy == -dy);
    }

    public String toString() {
        return disp;
    }
}

class Action {
    int row, col;
    List<Move> moves;
}

