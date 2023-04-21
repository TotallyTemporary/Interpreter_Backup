package interpreter;

import java.util.Scanner;

public class IO {
    
    public static Scanner scanner = new Scanner(System.in);

    static {
        scanner.useDelimiter(System.lineSeparator());
    }

    public static void close() {
        scanner.close();
    }

}
