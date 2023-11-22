package src.solutions;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day01 {

    /**
     * Main solving method.
     * @param part1
     * The solver will solve part 1 if param is set to true.
     * The solver will solve part 2 if param is set to false.
     * @param in
     * The solver will read data from this Scanner.
     * @return
     * Returns answer in string format.
     * @throws FileNotFoundException
     */
    public String solve(boolean part1, Scanner in) throws FileNotFoundException{
        if(part1){
            return "hi";
        }
        else{
            return "bye";
        }
    }
}
