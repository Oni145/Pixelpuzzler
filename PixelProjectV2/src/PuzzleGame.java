import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class PuzzleGame extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

    private final int ROWS = 3;
    private final int COLUMNS = 3;
    private final int TILE_SIZE = 150;
    private final int EMPTY_TILE = ROWS * COLUMNS - 1;
    private ArrayList<JButton> tiles;
    private JLabel timerLabel;
    private Timer timer;
    private int secondsElapsed;
    private JButton draggedTile;
    private Point initialPosition;

    public PuzzleGame() {
        setTitle("Freeform Sliding Puzzle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initializeGame();
        initializeTimer();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeGame() {
        setLayout(null); // Use null layout for freeform placement

        // Create the board panel
        tiles = new ArrayList<>();
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
            tile.addActionListener(this);
            tile.addMouseListener(this);
            tile.addMouseMotionListener(this);
            tiles.add(tile);
            add(tile);
        }
        shuffleTiles();
        tiles.get(EMPTY_TILE).setVisible(false); // Hide the empty tile

        // Create the timer panel
        timerLabel = new JLabel("Time: 0 seconds");
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        add(timerLabel, BorderLayout.SOUTH);
    }

    private void initializeTimer() {
        timer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + " seconds");
        });
        timer.start();
    }

    private void shuffleTiles() {
        Collections.shuffle(tiles);
        // Ensure solvability by swapping the tiles if necessary
        if (!isSolvable()) {
            Collections.swap(tiles, 0, 1);
        }
    }

    private boolean isSolvable() {
        int inversions = 0;
        for (int i = 0; i < ROWS * COLUMNS; i++) {
            for (int j = i + 1; j < ROWS * COLUMNS; j++) {
                if (!tiles.get(i).isVisible() || !tiles.get(j).isVisible()) {
                    continue; // Ignore the empty tile
                }
                int value1 = tiles.indexOf(tiles.get(i));
                int value2 = tiles.indexOf(tiles.get(j));
                if (value1 > value2) {
                    inversions++;
                }
            }
        }
        // If the number of inversions is even, the puzzle is solvable
        return inversions % 2 == 0;
    }

    private void checkForWin() {
        boolean win = true;
        for (int i = 0; i < ROWS * COLUMNS - 1; i++) {
            if (tiles.indexOf(tiles.get(i)) != i) {
                win = false;
                break;
            }
        }
        if (win) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Congratulations! You solved the puzzle in " + secondsElapsed + " seconds.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Tile action listener
        JButton clickedTile = (JButton) e.getSource();
        int clickedIndex = tiles.indexOf(clickedTile);
        int emptyIndex = tiles.indexOf(tiles.get(EMPTY_TILE));
        if (isAdjacent(clickedIndex, emptyIndex)) {
            Collections.swap(tiles, clickedIndex, emptyIndex);
            updateBoard();
            checkForWin();
        }
    }

    private boolean isAdjacent(int index1, int index2) {
        int row1 = index1 / ROWS;
        int col1 = index1 % ROWS;
        int row2 = index2 / ROWS;
        int col2 = index2 % ROWS;
        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

    private void updateBoard() {
        for (JButton tile : tiles) {
            remove(tile);
        }
        for (JButton tile : tiles) {
            add(tile);
        }
        revalidate();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Mouse press event
        draggedTile = (JButton) e.getSource();
        initialPosition = draggedTile.getLocation();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Mouse drag event
        if (draggedTile != null) {
            Point currentPoint = e.getLocationOnScreen();
            int deltaX = currentPoint.x - initialPosition.x;
            int deltaY = currentPoint.y - initialPosition.y;
            draggedTile.setLocation(draggedTile.getX() + deltaX, draggedTile.getY() + deltaY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Mouse release event
        if (draggedTile != null) {
            Point dropPoint = e.getPoint();
            for (JButton tile : tiles) {
                if (tile != draggedTile && tile.getBounds().contains(dropPoint)) {
                    swapTiles(draggedTile, tile);
                    break;
                }
            }
            draggedTile.setLocation(initialPosition);
            draggedTile = null;
            initialPosition = null;
        }
    }

    private void swapTiles(JButton tile1, JButton tile2) {
        Collections.swap(tiles, tiles.indexOf(tile1), tiles.indexOf(tile2));
        updateBoard();
    }

    // Unused mouse event methods
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PuzzleGame::new);
    }
}
