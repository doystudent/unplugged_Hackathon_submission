import java.io.IOException;
import java.util.Random;
import java.util.LinkedList;

public class SnakeGame {

    static int[] spawnFruit (int[][] board) {
        Random random = new Random();
        int x, y;
        while (true) {
            x = random.nextInt(board[0].length);
            y = random.nextInt(board.length);
           if (board[y][x] == 0) return new int[]{x, y};
        }
    }

    static boolean collides (LinkedList<int[]> snake, int x, int y, boolean grow) {
        int size = snake.size();
        for (int i = 0; i < size; i++) {
            if (!grow && i == size - 1) continue; // This is for when the snake is right behind its tail
            int [] coord = snake.get(i);
            if (coord[0] == x && coord[1] == y) return true;
        }
        return false;
    }

    static void print_board (int[][] board, int direction) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        char[] head = {'<', 'v', '^', '>'};
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x] == 1) System.out.print('.');
                else if (board[y][x] == 2) System.out.print('*');
                else if (board[y][x] == 3) System.out.print(head[direction]);
                else System.out.print('0');
            }
            System.out.print("\r\n");
        } 
        System.out.println("press x to quit\r\ncontrols: hjkl\r\n");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Initialize board
        int[][] board = new int[10][20];
        // Initialize snake
        LinkedList<int[]> snake = new LinkedList<>();
        snake.add(new int[] {board.length/2, board[0].length/2});
        board[board.length/2][board[0].length/2] = 1;
        // Directions
        int[][] dir = {{-1, 0}, {0, 1}, {0, -1}, {1, 0}}; // {left}, {down}, {up}, {right}
        int curr_dir = 3;
        int next_dir = 3;
        // Spawn fruit
        int[] fruit = spawnFruit(board);

        String[] toRaw = { "/bin/sh", "-c", "stty raw -echo < /dev/tty" };
        Runtime.getRuntime().exec(toRaw).waitFor();
        long lastTick = System.currentTimeMillis();
        boolean running = true;
        while (running) {
            // Update board values
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    board[y][x] = 0;
                    if (x == fruit[0] && y == fruit[1]) board[y][x] = 2;
                }
            }
            // Check for inputs
            if (System.in.available() > 0) {
                int key = System.in.read();
                char ch = (char) key;
                if (ch == 'x') running = false;
                if (ch == 'h' && curr_dir != 3) next_dir = 0; // left
                if (ch == 'j' && curr_dir != 2) next_dir = 1; // down
                if (ch == 'k' && curr_dir != 1) next_dir = 2; // up
                if (ch == 'l' && curr_dir != 0) next_dir = 3; // right
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTick >= 600) {
                // Calculate new position
                curr_dir = next_dir;
                int newX = (snake.getFirst()[0] + dir[curr_dir][0] + board[0].length) % board[0].length;
                int newY = (snake.getFirst()[1] + dir[curr_dir][1] + board.length) % board.length;

                // Check if snake grows
                boolean grow = (board[newY][newX] == 2);

                // Check if snake collides
                if (collides(snake, newX, newY, grow)) running = false;

                // Update head and tail
                snake.addFirst(new int[]{newX, newY});
                if (!grow) snake.removeLast();
                for (int[] coord : snake) board[coord[1]][coord[0]] = 1;
                board[snake.getFirst()[1]][snake.getFirst()[0]] = 3;

                // Update fruit position if eaten
                if (snake.size() != board.length * board[0].length && grow) fruit = spawnFruit(board);
                print_board(board, curr_dir);

                // Check if game is won
                if (snake.size() == board.length * board[0].length) {System.out.println("You won!\r\n"); running = false;}
                lastTick = currentTime; 
            }
            Thread.sleep(10); 
        }
        System.out.print("GGs\r\n");
        String[] toCooked = { "/bin/sh", "-c", "stty cooked echo < /dev/tty" };
        Runtime.getRuntime().exec(toCooked).waitFor();
    }
}

