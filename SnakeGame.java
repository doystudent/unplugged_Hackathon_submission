import java.io.IOException;
import java.util.*;

public class SnakeGame {
    static Random random = new Random();
    static int[] spawnFruit (int[][] board) {
        int x, y;
        do {x = random.nextInt(board[0].length);y = random.nextInt(board.length);} while (board[y][x]!= 0);
        return new int[]{x, y};
    }
    static boolean collides (LinkedList<int[]> snake, int x, int y, boolean grow) {
        for (int i = 0; i < snake.size() - (grow ? 0 : 1); i++) { // This is for when the snake is right behind its tail
            if (snake.get(i)[0] == x && snake.get(i)[1] == y) return true;
        }
        return false;
    }
    static void print_board (int[][] board, int dir) {
        System.out.print("\033[H\033[2J"); System.out.flush(); // Refresh terminal
        char[] head = {'<', 'v', '^', '>'};
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) System.out.print(board[y][x] == 1 ? '.' : board[y][x] == 2 ? '*' : board[y][x] == 3 ? head[dir] : '0');
            System.out.print("\r\n");
        } 
        System.out.println("press x to quit\r\ncontrols: hjkl\r\n");
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        int[][] board = new int[4][8]; // Initialize board
        LinkedList<int[]> snake = new LinkedList<>(List.of(new int[] {board[0].length/2, board.length/2})); // Initialize snake
        board[board.length/2][board[0].length/2] = 1;
        int[][] dir = {{-1, 0}, {0, 1}, {0, -1}, {1, 0}}; // {left}, {down}, {up}, {right}
        int curr_dir = 3, next_dir = 3; // Initialize directions
        int[] fruit = spawnFruit(board); // Spawn Fruit
        Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "stty raw -echo < /dev/tty"}).waitFor(); // Swap to raw
        long lastTick = System.currentTimeMillis();
        boolean running = true;
        while (running) {
            for (int[] row : board) Arrays.fill(row, 0); board[fruit[1]][fruit[0]] = 2; // Update board values
            if (System.in.available() > 0) {
                char ch = (char) System.in.read();
                if (ch == 'x') running = false;
                if (ch == 'h' && curr_dir != 3) next_dir = 0; // left
                if (ch == 'j' && curr_dir != 2) next_dir = 1; // down
                if (ch == 'k' && curr_dir != 1) next_dir = 2; // up
                if (ch == 'l' && curr_dir != 0) next_dir = 3; // right
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTick >= 600) {
                curr_dir = next_dir; // Update direction
                int newX = (snake.getFirst()[0] + dir[curr_dir][0] + board[0].length) % board[0].length;
                int newY = (snake.getFirst()[1] + dir[curr_dir][1] + board.length) % board.length;
                boolean grow = (board[newY][newX] == 2); // Check if snake grows
                if (collides(snake, newX, newY, grow)) running = false; // Check if snake collides
                snake.addFirst(new int[]{newX, newY}); // Update head and tail
                if (!grow) snake.removeLast();
                for (int[] coord : snake) board[coord[1]][coord[0]] = 1;
                board[snake.getFirst()[1]][snake.getFirst()[0]] = 3;
                if (snake.size() != board.length * board[0].length && grow) fruit = spawnFruit(board); // Update fruit position
                print_board(board, curr_dir);
                if (snake.size() == board.length * board[0].length) {System.out.println("You won!\r\n"); running = false;} // Check if won
                lastTick = currentTime; 
            }
            Thread.sleep(10); 
        }
        System.out.print("GGs\r\n");
        Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "stty cooked echo < /dev/tty"}).waitFor();
    }
}

