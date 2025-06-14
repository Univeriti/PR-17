import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    static class Settings {
        public String player1;
        public String player2;
        public int size;

        public Settings(String p1, String p2, int sz) {
            player1 = p1;
            player2 = p2;
            size = sz;
        }
    }

    static class GameStats {
        public String winner;
        public String datetime;
        public int size;

        public GameStats(String w, String dt, int sz) {
            winner = w;
            datetime = dt;
            size = sz;
        }
    }

    static class Board {
        public char[][] cells;

        public Board(int size) {
            cells = new char[size][size];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    cells[i][j] = ' ';
        }
    }

    static Settings config = new Settings("Player1", "Player2", 3);

    public static void main(String[] args) {
        loadConfig();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1.Грати");
            System.out.println("2.Налаштування");
                    System.out.println("3.Статистика");
                            System.out.println("4.Вихід");
            String ch = sc.nextLine();
            if (ch.equals("1")) playGame();
            else if (ch.equals("2")) changeConfig(sc);
            else if (ch.equals("3")) showStats();
            else if (ch.equals("4")) break;
        }
    }

    static void playGame() {
        Scanner sc = new Scanner(System.in);
        int size = config.size;
        Board board = new Board(size);
        boolean turn = true;
        int moves = 0;

        while (true) {
            printBoard(board);
            System.out.println("Хід: " + (turn ? config.player1 : config.player2));
            int x = sc.nextInt() - 1;
            int y = sc.nextInt() - 1;
            sc.nextLine();
            if (x < 0 || y < 0 || x >= size || y >= size || board.cells[x][y] != ' ') continue;
            board.cells[x][y] = turn ? 'X' : 'O';
            moves++;

            if (checkWin(board, turn ? 'X' : 'O')) {
                printBoard(board);
                String winner = turn ? config.player1 : config.player2;
                System.out.println("Переможець: " + winner);
                saveStats(winner);
                break;
            }

            if (moves == size * size) {
                printBoard(board);
                System.out.println("Нічия");
                saveStats("Нічия");
                break;
            }

            turn = !turn;
        }
    }

    static void printBoard(Board board) {
        for (int i = 0; i < board.cells.length; i++) {
            for (int j = 0; j < board.cells[i].length; j++) {
                System.out.print(board.cells[i][j]);
                if (j < board.cells.length - 1) System.out.print("|");
            }
            System.out.println();
        }
    }

    static boolean checkWin(Board board, char s) {
        int n = board.cells.length;
        for (int i = 0; i < n; i++) {
            boolean row = true, col = true;
            for (int j = 0; j < n; j++) {
                if (board.cells[i][j] != s) row = false;
                if (board.cells[j][i] != s) col = false;
            }
            if (row || col) return true;
        }

        boolean d1 = true, d2 = true;
        for (int i = 0; i < n; i++) {
            if (board.cells[i][i] != s) d1 = false;
            if (board.cells[i][n - i - 1] != s) d2 = false;
        }

        return d1 || d2;
    }

    static void changeConfig(Scanner sc) {
        System.out.print("Ім'я першого гравця: ");
        config.player1 = sc.nextLine();
        System.out.print("Ім'я другого гравця: ");
        config.player2 = sc.nextLine();
        System.out.print("Розмір поля (3-9): ");
        try {
            int s = Integer.parseInt(sc.nextLine());
            if (s >= 3 && s <= 9) config.size = s;
        } catch (Exception e) {}
        saveConfig();
    }

    static void loadConfig() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("config.txt"));
            String p1 = br.readLine();
            String p2 = br.readLine();
            int size = Integer.parseInt(br.readLine());
            config = new Settings(p1, p2, size);
            br.close();
        } catch (Exception e) {
            saveConfig();
        }
    }

    static void saveConfig() {
        try {
            PrintWriter pw = new PrintWriter("config.txt");
            pw.println(config.player1);
            pw.println(config.player2);
            pw.println(config.size);
            pw.close();
        } catch (Exception e) {}
    }

    static void saveStats(String winner) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String now = LocalDateTime.now().format(fmt);
            GameStats stat = new GameStats(winner, now, config.size);
            FileWriter fw = new FileWriter("stats.txt", true);
            fw.write(stat.datetime + " - " + stat.winner + " - Розмір: " + stat.size + "\n");
            fw.close();
        } catch (Exception e) {}
    }

    static void showStats() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("stats.txt"));
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
            br.close();
        } catch (Exception e) {
            System.out.println("Немає записів.");
        }
    }
}
