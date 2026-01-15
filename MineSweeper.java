import java.util.*;

public class MineSweeper {

    // Cell class to store state of each cell
    static class Cell {
        boolean isRevealed;
        boolean isFlagged;
        boolean isBomb;
        int surroundingBombs;

        public Cell(boolean isBomb) {
            this.isBomb = isBomb;
            this.isRevealed = false;
            this.isFlagged = false;
            this.surroundingBombs = 0;
        }
    }

    // Map to store the cells, key is the "row-col" string
    private Map<String, Cell> fieldVisible = new HashMap<>();
    private Map<String, Cell> fieldHidden = new HashMap<>();

    // Method to display the visible field
    public void displayVisible() {
        System.out.print("\t ");
        for (int i = 0; i < 10; i++) {
            System.out.print(" " + i + "  ");
        }
        System.out.print("\n");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + "\t| ");
            for (int j = 0; j < 10; j++) {
                String key = i + "-" + j;
                Cell cell = fieldVisible.get(key);
                if (!cell.isRevealed) {
                    System.out.print("?");
                } else if (cell.isBomb) {
                    System.out.print("X");
                } else {
                    System.out.print(cell.surroundingBombs);
                }
                System.out.print(" | ");
            }
            System.out.print("\n");
        }
    }

    // Method to display the hidden field (for game over)
    public void displayHidden() {
        System.out.print("\t ");
        for (int i = 0; i < 10; i++) {
            System.out.print(" " + i + "  ");
        }
        System.out.print("\n");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + "\t| ");
            for (int j = 0; j < 10; j++) {
                String key = i + "-" + j;
                Cell cell = fieldHidden.get(key);
                if (cell.isBomb) {
                    System.out.print("X");
                } else {
                    System.out.print(cell.surroundingBombs);
                }
                System.out.print(" | ");
            }
            System.out.print("\n");
        }
    }

    public void fixVisible(int i, int j) {
        String key = i + "-" + j;
        Cell cell = fieldHidden.get(key);
        if (cell == null || cell.isRevealed) {
            return;  // If cell is null or already revealed, exit
        }
    
        // Reveal the cell in both visible and hidden fields
        fieldVisible.put(key, cell);
        cell.isRevealed = true;  // Mark the cell as revealed
    
        // If the cell has no surrounding bombs, reveal adjacent cells
        if (cell.surroundingBombs == 0) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (i + di >= 0 && i + di < 10 && j + dj >= 0 && j + dj < 10) {
                        String neighborKey = (i + di) + "-" + (j + dj);
                        if (!fieldVisible.containsKey(neighborKey)) {
                            fixVisible(i + di, j + dj);  // Recursively reveal surrounding cells
                        }
                    }
                }
            }
        }
    }
    

    // Properly reveal cells recursively like real Minesweeper
public void floodReveal(int i, int j) {
    String key = i + "-" + j;

    // If out of bounds, or already revealed, or flagged, stop
    if (i < 0 || i >= 10 || j < 0 || j >= 10) return;
    Cell cell = fieldHidden.get(key);
    if (cell == null || cell.isRevealed || cell.isBomb) return;

    // Reveal the cell
    cell.isRevealed = true;
    fieldVisible.put(key, cell);

    // If there are no surrounding bombs, recurse to neighbors
    if (cell.surroundingBombs == 0) {
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di != 0 || dj != 0) {
                    floodReveal(i + di, j + dj);
                }
            }
        }
    }
}



    // Method to fix the neighbours when a cell is revealed
    public void fixNeighbours(int i, int j) {
        String key = i + "-" + j;
        fieldVisible.put(key, fieldHidden.get(key));

        Random random = new Random();
        int x = random.nextInt(4);

        if (x == 0) {
            if (i > 0) fixVisible(i - 1, j);
            if (j > 0) fixVisible(i, j - 1);
            if (i > 0 && j > 0) fixVisible(i - 1, j - 1);
        } else if (x == 1) {
            if (i > 0) fixVisible(i - 1, j);
            if (j < 9) fixVisible(i, j + 1);
            if (i > 0 && j < 9) fixVisible(i - 1, j + 1);
        } else if (x == 2) {
            if (i < 9) fixVisible(i + 1, j);
            if (j < 9) fixVisible(i, j + 1);
            if (i < 9 && j < 9) fixVisible(i + 1, j + 1);
        } else {
            if (i < 9) fixVisible(i + 1, j);
            if (j > 0) fixVisible(i, j - 1);
            if (i < 9 && j > 0) fixVisible(i + 1, j - 1);
        }
    }

    public void buildHidden() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String key = i + "-" + j;
                Cell cell = fieldHidden.getOrDefault(key, new Cell(false));
                int count = 0;
    
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        int ni = i + di, nj = j + dj;
                        String neighborKey = ni + "-" + nj;
                        if (ni >= 0 && ni < 10 && nj >= 0 && nj < 10) {
                            Cell neighbor = fieldHidden.get(neighborKey);
                            if (neighbor != null && neighbor.isBomb) {
                                count++;
                            }
                        }
                    }
                }
    
                cell.surroundingBombs = count;
                fieldHidden.put(key, cell);  // Update the cell in case it wasn't added yet
            }
        }
    }
    

    public void setupField() {
        Random random = new Random();
        int var = 0;
        while (var != 10) {
            int i = random.nextInt(10);
            int j = random.nextInt(10);
            if (!fieldHidden.containsKey(i + "-" + j)) {
                fieldHidden.put(i + "-" + j, new Cell(true));  // Place a bomb
                fieldVisible.put(i + "-" + j, new Cell(false));  // Initialize the visible field with non-bomb cells
                var++;
            }
        }
    
        // Now fill the rest of the field with empty cells
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (!fieldHidden.containsKey(i + "-" + j)) {
                    fieldHidden.put(i + "-" + j, new Cell(false));  // Add non-bomb cells to fieldHidden
                    fieldVisible.put(i + "-" + j, new Cell(false));  // Add non-bomb cells to fieldVisible
                }
            }
        }
    
        buildHidden();  // Now call buildHidden to calculate the surrounding bombs
    }
    

    // Method to check if the player won
    public boolean checkWin() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String key = i + "-" + j;
                Cell cell = fieldVisible.get(key);
                if (!cell.isRevealed && !cell.isBomb) {
                    return false; // Not all non-bomb cells are revealed
                }
            }
        }
        return true; // All non-bomb cells revealed
    }

    public boolean playMove() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Row Number: ");
        int i = sc.nextInt();
        System.out.print("Enter Column Number: ");
        int j = sc.nextInt();
    
        if (i < 0 || i > 9 || j < 0 || j > 9 || fieldVisible.get(i + "-" + j).isRevealed) {
            System.out.print("\nIncorrect Input!!! Please type in a number from 0 - 9 for both rows and columns!\n");
            return true;
        }
    
        if (fieldHidden.get(i + "-" + j).isBomb) {
            displayHidden();
            System.out.print("Oops! You stepped on a mine!\n============GAME OVER============");
            return false;
        } else {
            floodReveal(i, j);  // Reveal the selected cell and its neighbors if applicable
        }
    
        return true;
    }
    

    // Method to start the game
    public void startGame() {
        System.out.println("\n\n================Welcome to Minesweeper! ================\n");
        setupField();

        boolean flag = true;
        while (flag) {
            displayVisible();
            flag = playMove();
            if (checkWin()) {
                displayHidden();
                System.out.println("\n================You WON!!!================");
                break;
            }
        }
    }

    // Main method
    public static void main(String[] args) {
        MineSweeper M = new MineSweeper();
        M.startGame();
    }
}
