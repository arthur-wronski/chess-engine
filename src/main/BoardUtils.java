package main;

public class BoardUtils {
    private static final char[] columnNames = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};

    public static String getSquareNameFromIndex(int squareIndex){
        // e.g. square 38 -> 36 % 8 = 4 -> E and 36 / 8 = 4.5 -> 4 + 1, so return E5

        char columnName = columnNames[squareIndex % 8];
        int rowNumber = squareIndex / 8 + 1;

        return "" + columnName + rowNumber;
    }
}
