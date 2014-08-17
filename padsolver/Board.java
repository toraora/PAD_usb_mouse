package padsolver;

import java.util.*;

class ActionScore {
    Action action;
    double score;
}

class Board {

    Orb[][] board;
    int rows, cols;

    Board() {
        this(GSettings.ROWS, GSettings.COLS);
    }

    Board(int r, int c) {
        rows = r;
        cols = c;
        board = new Orb[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = Orb.NONE;
            }
        }
    }

    Board(Board b) {
        rows = b.rows;
        cols = b.cols;
        board = new Orb[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = b.get(i, j);
            }
        }
    }


    void set(int i, int j, Orb orb) {
        board[i][j] = orb;
    }

    Orb get(int i, int j) {
        return board[i][j];
    }

    static Board randomBoard() {
        Board ret = new Board();
        for (int i = 0; i < ret.rows; i++) {
            for (int j = 0; j < ret.cols; j++) {
                ret.set(i, j, Orb.randomOrb());
            }
        }
        return ret;
    }   

    List<Move> validMoves(int i, int j) {
        ArrayList<Move> moves = new ArrayList<Move>();
        if (i != 0) moves.add(Move.UP);
        if (i != rows - 1) moves.add(Move.DOWN);
        if (j != 0) moves.add(Move.LEFT);
        if (j != cols - 1) moves.add(Move.RIGHT);
        return moves;
    }

    Board findMatches() throws NoMatchesException {
        Board matchBoard = new Board(rows, cols);
        boolean match = false;

        for (int i = 0; i < rows; i++) {
            Orb prev1 = Orb.NONE;
            Orb prev2 = Orb.NONE;
            for (int j = 0; j < cols; j++) {
                Orb cur = get(i, j);
                if (prev1.equal(prev2) && prev2.equal(cur)) {
                    match = true;
                    matchBoard.set(i, j, cur);
                    matchBoard.set(i, j-1, cur);
                    matchBoard.set(i, j-2, cur);
                }
                prev1 = prev2;
                prev2 = cur;
            }
        }

        for (int j = 0; j < cols; j++) {
            Orb prev1 = Orb.NONE;
            Orb prev2 = Orb.NONE;
            for (int i = 0; i < rows; i++) {
                Orb cur = get(i, j);
                if (prev1.equal(prev2) && prev2.equal(cur)) {
                    match = true;
                    matchBoard.set(i, j, cur);
                    matchBoard.set(i-1, j, cur);
                    matchBoard.set(i-2, j, cur);
                }
                prev1 = prev2;
                prev2 = cur;
            }
        }

        if (!match) throw new NoMatchesException();
        return matchBoard;
    }

    void findBestAction(int i, int j, int depth, List<Move> moves, ActionScore best) {
        for (Move m : validMoves(i, j)) {
            if (moves.size() != 0 && m.opposite(moves.get(moves.size() - 1))) continue;
            Board b = new Board(this);
            Orb temp = b.get(i, j);
            b.set(i, j, b.get(i + m.dx, j + m.dy));
            b.set(i + m.dx, j + m.dy, temp);
            if (GSettings.MAX_MOVES - depth > GSettings.MIN_MOVES) {
                double score = (double) depth / 1000. + BoardScore.score(new Board(b));
                if (score > best.score) {
                    best.action.moves = new ArrayList<Move>(moves);
                    best.action.moves.add(m);
                    best.score = score;
                }
            }
            if (depth != 0) {
                moves.add(m);
                b.findBestAction(i + m.dx, j + m.dy, depth - 1, moves, best);
                moves.remove(moves.size() - 1);
            }
        }
    }

    Action findBestAction() {
        ArrayList<Move> moves = new ArrayList<Move>();
        ActionScore best = new ActionScore();
        best.action = new Action();
        double maxScore = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                findBestAction(i, j, GSettings.MAX_MOVES, moves, best);
                if (best.score > maxScore) {
                    maxScore = best.score;
                    best.action.row = i;
                    best.action.col = j;
                }
            }
        }
        return best.action;
    }

    void gravityFill() {
        Orb[] orbs = new Orb[rows];
        int height;
        for (int j = 0; j < cols; j++) {
            height = 0;
            for (int i = 0; i < rows; i++) {
                if (get(i, j) == Orb.NONE) continue;
                orbs[height++] = get(i, j);
            }
            for (int i = 0; i < height; i++) {
                set(rows - 1 - i, j, orbs[height - 1 - i]);
            }
            for (int i = height; i < rows; i++) {
                set(rows - 1 - i, j, Orb.NONE);
            }
        }
    }

    void performAction(Action a) {
        int i = a.row;
        int j = a.col;
        int _i;
        int _j;
        for (Move m : a.moves) {
            _i = i + m.dx;
            _j = j + m.dy;
            Orb temp = get(i, j);
            set(i, j, get(_i, _j));
            set(_i, _j, temp);
            i = _i;
            j = _j;
        }
    }

    void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(get(i, j).toString() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Board b = randomBoard();
        b.printBoard();
        Action a = b.findBestAction();
        for (Move m : a.moves) {
            System.out.println(m);
        }
        b.performAction(a);
        b.printBoard();
        BoardScore.score(b);
        b.printBoard();
    }
}

class NoMatchesException extends Exception {
    NoMatchesException() {
        super();
    }
}
