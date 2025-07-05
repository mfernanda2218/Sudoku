import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SudokuGame extends JFrame {
    private final SudokuBoard board;
    private JButton[][] gridButtons;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private JLabel statusLabel;

    public SudokuGame(String[] initialNumbers) {
        setTitle("Sudoku");
        setSize(700, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        board = new SudokuBoard(initialNumbers);
        initializeUI();
    }

    private void initializeUI() {
        // Título
        JLabel title = new JLabel("Jogo de Sudoku", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Painel do tabuleiro
        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        gridButtons = new JButton[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createMatteBorder(
                        row % 3 == 0 ? 2 : 1,
                        col % 3 == 0 ? 2 : 1,
                        1,
                        1,
                        Color.BLACK));

                int value = board.getValue(row, col);
                if (value != 0) {
                    button.setText(String.valueOf(value));
                    button.setEnabled(false);
                    button.setBackground(Color.LIGHT_GRAY);
                } else {
                    button.setBackground(Color.WHITE);
                }

                final int r = row, c = col;
                button.addActionListener(e -> {
                    selectedRow = r;
                    selectedCol = c;
                    updateSelection();
                });

                gridButtons[row][col] = button;
                boardPanel.add(button);
            }
        }

        // Painel de controle
        JPanel controlPanel = new JPanel(new GridLayout(1, 7));
        String[] labels = {
                "Novo Jogo", "Número a inserir", "Remover",
                "Verificar quadro", "Status do Jogo", "Limpar jogo", "Finalizar"
        };

        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.addActionListener(new ButtonClickListener());
            controlPanel.add(btn);
        }

        // Painel de números
        JPanel numberPanel = new JPanel(new GridLayout(1, 9));
        for (int i = 1; i <= 9; i++) {
            JButton numButton = new JButton(String.valueOf(i));
            numButton.addActionListener(e -> {
                if (selectedRow != -1 && selectedCol != -1) {
                    placeNumber(selectedRow, selectedCol, Integer.parseInt(numButton.getText()));
                }
            });
            numberPanel.add(numButton);
        }

        // Status bar
        statusLabel = new JLabel("Bem-vindo ao Sudoku!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(numberPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void updateSelection() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (row == selectedRow && col == selectedCol) {
                    gridButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                } else {
                    gridButtons[row][col].setBorder(BorderFactory.createMatteBorder(
                            row % 3 == 0 ? 2 : 1,
                            col % 3 == 0 ? 2 : 1,
                            1,
                            1,
                            Color.BLACK));
                }
            }
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText().toLowerCase();

            switch (command) {
                case "novo jogo" -> {
                    board.newGame();
                    updateBoard();
                    updateStatus("Novo jogo iniciado.");
                }
                case "número a inserir" -> {
                    if (selectedRow != -1 && selectedCol != -1) {
                        String input = JOptionPane.showInputDialog("Escolha um número (1-9):");
                        try {
                            int num = Integer.parseInt(input);
                            placeNumber(selectedRow, selectedCol, num);
                        } catch (NumberFormatException ex) {
                            updateStatus("Entrada inválida.");
                        }
                    }
                }
                case "remover" -> {
                    if (selectedRow != -1 && selectedCol != -1) {
                        removeNumber(selectedRow, selectedCol);
                    }
                }
                case "verificar quadro" -> {
                    boolean valid = board.checkBoard();
                    updateStatus(valid ? "Tabuleiro válido." : "Conflitos encontrados.");
                    highlightConflicts();
                }
                case "status do jogo" -> showGameStatus();
                case "limpar jogo" -> {
                    board.clearUserNumbers();
                    updateBoard();
                    updateStatus("Jogo limpo.");
                }
                case "finalizar" -> finishGame();
            }
        }
    }

    private void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void placeNumber(int row, int col, int number) {
        try {
            board.placeNumber(row, col, number);
            updateCell(row, col);
            updateStatus("Número " + number + " colocado.");
        } catch (IllegalArgumentException ex) {
            updateStatus(ex.getMessage());
        }
    }

    private void removeNumber(int row, int col) {
        try {
            board.removeNumber(row, col);
            updateCell(row, col);
            updateStatus("Número removido.");
        } catch (IllegalArgumentException ex) {
            updateStatus(ex.getMessage());
        }
    }

    private void updateCell(int row, int col) {
        int value = board.getValue(row, col);
        JButton button = gridButtons[row][col];
        button.setText(value == 0 ? "" : String.valueOf(value));
        button.setEnabled(!board.isFixed(row, col));
        button.setBackground(board.isFixed(row, col) ? Color.LIGHT_GRAY : Color.WHITE);
    }

    private void updateBoard() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                updateCell(row, col);
            }
        }
        updateSelection();
    }

    private void showGameStatus() {
        SudokuBoard.GameStatus status = board.checkGameStatus();
        String msg = "Status do jogo: " + status.getState() + " — ";
        msg += status.hasErrors() ? "conflitos detectados." : "sem conflitos.";
        updateStatus(msg);
    }

    private void finishGame() {
        SudokuBoard.GameStatus status = board.checkGameStatus();
        if ("Complete".equals(status.getState()) && !status.hasErrors()) {
            JOptionPane.showMessageDialog(null, "Parabéns! Sudoku resolvido corretamente.");
            updateStatus("Jogo finalizado com sucesso!");
        } else if ("Complete".equals(status.getState())) {
            updateStatus("Jogo completo mas com erros.");
            JOptionPane.showMessageDialog(null, "Existem erros no tabuleiro!");
        } else {
            updateStatus("Jogo incompleto.");
            JOptionPane.showMessageDialog(null, "Complete o tabuleiro antes de finalizar.");
        }
    }

    private void highlightConflicts() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (!board.isFixed(row, col)) {
                    int temp = board.getValue(row, col);
                    board.removeNumber(row, col);
                    board.placeNumber(row, col, temp);
                    if (!board.checkBoard()) {
                        gridButtons[row][col].setBackground(Color.PINK);
                    } else {
                        gridButtons[row][col].setBackground(Color.WHITE);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] sampleInitial = {
                "5,3,0,0,7,0,0,0,0",
                "6,0,0,1,9,5,0,0,0",
                "0,9,8,0,0,0,0,6,0",
                "8,0,0,0,6,0,0,0,3",
                "4,0,0,8,0,3,0,0,1",
                "7,0,0,0,2,0,0,0,6",
                "0,6,0,0,0,0,2,8,0",
                "0,0,0,4,1,9,0,0,5",
                "0,0,0,0,8,0,0,7,9"
        };

        SwingUtilities.invokeLater(() -> {
            SudokuGame game = new SudokuGame(sampleInitial);
            game.setVisible(true);
        });
    }
}
