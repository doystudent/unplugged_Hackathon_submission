import java.io.IOException;
import java.util.Random;
import java.util.LinkedList;

public class SnakeGame {

    static int[] spawnFruit (int[][] board) {
        Random random = new Random();
        int x, y;
        while (true) {
            x = random.nextInt(10);
            y = random.nextInt(10);
            if (board[y][x] == 0) return new int[]{x, y};
        }
    }

    static boolean collides (LinkedList<int[]> snake, int x, int y, boolean grow) {
        int size = snake.size();
        for (int i = 0; i < size; i++) {
            if (!grow && i == size - 1) continue;
            int [] coord = snake.get(i);
            if (coord[0] == x && coord[1] == y) return true;
        }
        return false;
    }

    static void print_board (int[][] board) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x] == 1) System.out.print('.');
                else if (board[y][x] == 2) System.out.print('*');
                else System.out.print('0');
            }
            System.out.print("\r\n");
        } 
        System.out.println("press x to quit\r\ncontrols: hjkl\r\n");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        LinkedList<int[]> snake = new LinkedList<>();
        snake.add(new int[] {3, 3});
        int[][] dir = {{-1, 0}, {0, 1}, {0, -1}, {1, 0}}; // {left}, {down}, {up}, {right}
        int curr_dir = 3;
        int[][] board = new int[10][10];
        board[3][3] = 1;
        int[] fruit = spawnFruit(board);

        String[] toRaw = { "/bin/sh", "-c", "stty raw -echo < /dev/tty" };
        Runtime.getRuntime().exec(toRaw).waitFor();
        long lastTick = System.currentTimeMillis();
        boolean running = true;
        while (running) {
            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 10; x++) {
                    board[y][x] = 0;
                    if (x == fruit[0] && y == fruit[1]) board[y][x] = 2;
                }
            }

            if (System.in.available() > 0) {
                int key = System.in.read();
                char ch = (char) key;
                if (ch == 'x') running = false;
                if (ch == 'h' && curr_dir != 3) curr_dir = 0; // left
                if (ch == 'j' && curr_dir != 2) curr_dir = 1; // down
                if (ch == 'k' && curr_dir != 1) curr_dir = 2; // up
                if (ch == 'l' && curr_dir != 0) curr_dir = 3; // right
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTick >= 600) {
                int newX = (snake.getFirst()[0] + dir[curr_dir][0] + 10) % 10;
                int newY = (snake.getFirst()[1] + dir[curr_dir][1] + 10) % 10;

                boolean grow = (board[newY][newX] == 2);
                if (collides(snake, newX, newY, grow)) running = false;
                snake.addFirst(new int[]{newX, newY});
                if (grow) fruit = spawnFruit(board);
                else snake.removeLast();
                for (int[] coord : snake) board[coord[1]][coord[0]] = 1;
                print_board(board);
                lastTick = currentTime; 
            }
            Thread.sleep(10); 
        }
        System.out.print("GGs\r\n");
        String[] toCooked = { "/bin/sh", "-c", "stty cooked echo < /dev/tty" };
        Runtime.getRuntime().exec(toCooked).waitFor();
    }
}

