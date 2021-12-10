package project1;

public class Main {

    /**
     * Main Execution Method. Runs simulations at the fixed density 30%.
     *
     * @param args Input arguments X - Grid X size, Y - Grid Y size, Iterations - Number of Iterations (defaults to 100)
     */
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int iterations = args.length > 2 ? Integer.parseInt(args[2]): 100;

        SearchAlgo algo = new AStarSearch(Heuristics::manhattanDistance);
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(x-1, y-1);

        for(int i = 0; i < iterations; i++) {
            Grid world = getSolvableMaze(x, y, algo, 30);
            Robot rob = new Robot(start, end, false, world, algo, true);
            rob.run();
        }
    }

    /**
     * This method returns a solvable maze for given inputs. It uses a search algo to make sure there is a path to the
     * end.
     *
     * @param xDim Dimension of the Grid's X coord
     * @param yDim Dimension of the Grid's Y coord
     * @param algo Algorithm to use for searching
     * @param prob Probability of a space being blocked
     * @return returns a solvable maze
     */
    public static Grid getSolvableMaze(int xDim, int yDim, SearchAlgo algo, int prob){
        Tuple<Integer, Integer> start = new Tuple<>(0, 0);
        Tuple<Integer, Integer> end = new Tuple<>(xDim-1, yDim-1);
        GridWorldInfo completeResult;
        Grid grid;

        do{
            grid = new Grid(xDim, yDim, prob);
            completeResult = algo.search(start, end, grid, cell -> cell.isBlocked());
        }while (completeResult.getPath() == null);

        return grid;
    }


}
