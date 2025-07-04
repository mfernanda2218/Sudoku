public class SudokuBoard {
    private int[][] board;
    private boolean[][] fixedNumbers;
    private String[] initialSetup;

    public SudokuBoard(String[] initialSetup) {
        this.initialSetup = initialSetup;
        newGame();
    }

    public void newGame() {
        board = new int[9][9];
        fixedNumbers = new boolean[9][9];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < 9; row++) {
            String[] numbers = initialSetup[row].split(",");
            for (int col = 0; col < 9; col++) {
                int num = Integer.parseInt(numbers[col].trim());
                board[row][col] = num;
                fixedNumbers[row][col] = (num != 0);
            }
        }
    }

    public void placeNumber(int row, int col, int number) {
        validatePosition(row, col);
        if (fixedNumbers[row][col]) {
            throw new IllegalArgumentException("Não é possível modificar um número fixo!");
        }
        if (number < 1 || number > 9) {
            throw new IllegalArgumentException("Número deve estar entre 1 e 9.");
        }
        board[row][col] = number;
    }

    public void removeNumber(int row, int col) {
        validatePosition(row, col);
        if (fixedNumbers[row][col]) {
            throw new IllegalArgumentException("Não é possível remover um número fixo!");
        }
        board[row][col] = 0;
    }

    public boolean checkBoard() {
        return !hasAnyConflicts();
    }

    public GameStatus checkGameStatus() {
        if (isBoardEmpty()) return new GameStatus("Not Started", false);

        boolean hasErrors = hasAnyConflicts();
        boolean isComplete = isBoardComplete();

        return new GameStatus(isComplete ? "Complete" : "Incomplete", hasErrors);
    }

    public void clearUserNumbers() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!fixedNumbers[row][col]) {
                    board[row][col] = 0;
                }
            }
        }
    }

    public boolean isFixed(int row, int col) {
        return fixedNumbers[row][col];
    }

    public int getValue(int row, int col) {
        return board[row][col];
    }

    private void validatePosition(int row, int col) {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            throw new IllegalArgumentException("Posição inválida!");
        }
    }

    private boolean isBoardEmpty() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != 0 && !fixedNumbers[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBoardComplete() {
        for (int[] row : board) {
            for (int num : row) {
                if (num == 0) return false;
            }
        }
        return true;
    }

    public boolean hasConflictAt(int row, int col) {
        int num = board[row][col];
        if (num == 0) return false;

        // Check row
        for (int c = 0; c < 9; c++) {
            if (c != col && board[row][c] == num) return true;
        }

        // Check column
        for (int r = 0; r < 9; r++) {
            if (r != row && board[r][col] == num) return true;
        }

        // Check 3x3 box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if ((r != row || c != col) && board[r][c] == num) return true;
            }
        }

        return false;
    }

    private boolean hasAnyConflicts() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (hasConflictAt(row, col)) return true;
            }
        }
        return false;
    }

    public static class GameStatus {
        private final String state;
        private final boolean hasErrors;

        public GameStatus(String state, boolean hasErrors) {
            this.state = state;
            this.hasErrors = hasErrors;
        }

        public String getState() {
            return state;
        }

        public boolean hasErrors() {
            return hasErrors;
        }
    }
}
