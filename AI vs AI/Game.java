import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Ranakrc on 11-Dec-17.
 */
public class Game {

    //maxDepth, heuristicType1 , heuristicType2, W, binCount, blocks, number of games per pair can be changed
    public static int player = 0, computer = 1, heuristicType1, heuristicType2;
    //initial value of turn is the player with the first move
    public static int turn, binCount = 6, flag = 0, treeTurn, treeFlag = 0;
    public static int game = 1, simulation = 2, winner;
    public static int numberOfGamesPerPair = 100;
    public static int[][] stat = new int[4][4];
    public static int[][] reverseStat = new int[4][4];
    public static int maxDepth = 10;
    public static State root = new State();
    public static State bestState;
    public static int stonesCapPlayer = 0, stonesCapCom = 0, addMovesPlayer = 0, addMovesCom = 0;
    public static int[] W = {1, 1, 1, 1};
    //public static ArrayList<State> gameState = new ArrayList<>();
    //public static ArrayList<Integer> moves = new ArrayList<>();


    //Heristic
    public static int heuristic(State state, int type, int stonesCap, int addMoves) {

        int val = 0, a = state.minScore - state.maxScore, b = state.countBlocks(state.minArr) - state.countBlocks(state.maxArr);

        if(type == 0) val = a;
        else if(type == 1) val = W[0] * a + W[1] * b;
        else if (type == 2) val = W[0] * a + W[1] * b + W[2] * addMoves;
        else if(type == 3) val = W[0] * a + W[1] * b + W[2] * addMoves + W[3] * stonesCap;

        return val;
    }


    //create a new State, given a move
    public static State makeState(int id, State currState, int num, int turn, int depth) {

        State newState = new State();
        newState.maxScore = currState.maxScore;
        newState.minScore = currState.minScore;
        newState.value = currState.value;
        newState.copy(currState.maxArr, "maxArr");
        newState.copy(currState.minArr, "minArr");
        int sum = 0;
        //For player's turn
        if((turn == player && id == game) || (treeTurn == player && id == simulation)) {

            int count = newState.minArr[num];
            newState.minArr[num] = 0;
            int j = 1;
            while(sum != count) {

                //player's array
                while(sum < count && j + num < binCount) {

                    newState.minArr[num + j] += 1;
                    sum++;

                    //if the last block ends up in player's array's empty bin
                    if(sum == count && newState.minArr[num + j] == 1) {

                        newState.minScore += 1 + newState.maxArr[num + j];
                        stonesCapPlayer += newState.maxArr[num + j];
                        newState.minArr[num + j] = 0;
                        newState.maxArr[num + j] = 0;

                    }

                    j++;

                }

                //player's store
                if(sum < count) {

                    newState.minScore++;
                    sum++;

                    //if the last block ends up in player's store
                    if(sum == count) {

                        if(turn == player && id == game) flag = 1;
                        if(treeTurn == player && id == simulation) {

                            treeFlag = 1;
                            addMovesCom += 1;

                        }
                    }

                }

                //computer's array
                int k = binCount - 1;
                while(sum < count && k >= 0) {

                    newState.maxArr[k] += 1;
                    k--;
                    sum++;

                }
                num = 0;
                j = 0;

            }
        }

        //For computer's turn
        else if((turn == computer && id == game) || (treeTurn == computer && id == simulation)) {

            int count = newState.maxArr[num];
            newState.maxArr[num] = 0;
            int j = 1;
            while(sum != count) {

                //computer's array
                while(sum < count && num - j >= 0) {

                    newState.maxArr[num - j] += 1;
                    sum++;

                    //if the last block ends up in computer's array's empty bin
                    if(sum == count && newState.maxArr[num - j] == 1) {

                        newState.maxScore += 1 + newState.minArr[num - j];
                        stonesCapCom += newState.minArr[num - j];
                        newState.minArr[num - j] = 0;
                        newState.maxArr[num - j] = 0;

                    }
                    j++;

                }

                //computer's store
                if(sum < count) {

                    newState.maxScore++;
                    sum++;

                    //if the last block ends up in computer's store
                    if(sum == count) {

                        if(turn == computer && id == game) flag = 1;
                        if(treeTurn == computer && id == simulation) {

                            treeFlag = 1;
                            addMovesCom += 1;
                        }
                    }

                }

                //player's array
                int k = 0;
                while(sum < count && k < binCount) {

                    newState.minArr[k] += 1;
                    k++;
                    sum++;

                }
                num = binCount;
                j = 1;

            }
        }

        //System.out.println("Treeturn: " + treeTurn + ", depth: " + depth);
        if((treeTurn == player && id == 2 && depth == maxDepth) || ((currState.checkZero(currState.maxArr) || currState.checkZero(currState.minArr)) && id == simulation))  {
            newState.value = heuristic(newState, heuristicType1, stonesCapPlayer, addMovesPlayer);
          //  System.out.println("Heuristic value is: " + newState.value);
        }
        if((treeTurn == computer && id == 2 && depth == maxDepth) || ((currState.checkZero(currState.maxArr) || currState.checkZero(currState.minArr)) && id == simulation)) {
            newState.value = heuristic(newState, heuristicType2, stonesCapCom, addMovesCom);
            //System.out.println("Heuristic value is: " + newState.value);
        }
        //System.out.println("does it?");
        return newState;

    }


    //check Game Over Condition
    public static boolean gameOver(State currState) {

        if (currState.checkZero(currState.maxArr) || currState.checkZero(currState.minArr)) {

            int count = currState.countBlocks(currState.maxArr);
            if (count != 0)
                currState.maxScore += count;

            count = currState.countBlocks(currState.minArr);
            if (count != 0)
                currState.minScore += count;

            //System.out.println("Your final score: " + currState.minScore);
            //System.out.println("Computer's final score: " + currState.maxScore);
            if (currState.maxScore < currState.minScore) {
                winner = player;
                //System.out.println("Congratulations! You won");
            }
            else if (currState.minScore < currState.maxScore) {
                winner = computer;
                //System.out.println("Sorry! You lost");
            }
            else {
                winner = -1;
                //System.out.println("Game drawn");
            }

            return true;

        }

        return false;
    }


    public static int minValue(State currState, int depth, int alpha, int beta) {

        if(currState.checkZero(currState.maxArr) || currState.checkZero(currState.minArr) || depth == maxDepth) {
            //System.out.println("minValue: 1st return, depth: " + depth);
            bestState = currState;
            return currState.move;

        }

        currState.value = Integer.MAX_VALUE;
        int i;
        State newState = null;

        //generate all possible child of a parent
        for(i = 0; i < binCount; i++) {

            //if a bin has zero blocks, continue with the next bin
            if(currState.minArr[i] == 0) continue;

            newState = makeState(2, currState, i, treeTurn, depth);
            newState.move = i;
            //System.out.println("Inside minValue For Loop: " + newState.move);
            //player or minimizing agent didn't get an extra turn
            if(treeFlag == 0) {

                treeTurn = (treeTurn + 1) % 2;
                newState.value = Math.min(newState.value, maxValue(newState, depth + 1, alpha, beta));
              //  System.out.println("minValue calls maxValue");
            }

            //player or minimizing agent gets an extra turn
            if(treeFlag == 1) {
                newState.value = Math.min(newState.value, minValue(newState, depth + 1, alpha, beta));
                treeFlag = 0;
            }

            if(newState.value <= alpha) {
                //System.out.println("minValue: 2nd return, depth: " + depth);
                bestState = newState;
                return newState.value;
            }
            beta = Math.min(beta, newState.value);

        }
        //System.out.println("minValue: 3rd return, depth: " + depth);
        bestState = newState;
        return newState.value;
    }


    public static int maxValue(State currState, int depth, int alpha, int beta) {

        if(currState.checkZero(currState.maxArr) || currState.checkZero(currState.minArr) || depth == maxDepth) {
            //System.out.println("maxValue: 1st return, depth: " + depth);
            bestState = currState;
            return currState.value;

        }

        currState.value = Integer.MIN_VALUE;
        int i;
        State newState = null;
        //System.out.println("Inside maxValue");
        //generate all possible child of a parent
        for(i = 0; i < binCount; i++) {

            //if a bin has zero blocks, continue with the next bin
            if(currState.maxArr[i] == 0) continue;

            newState = makeState(2, currState, i, treeTurn, depth);
            newState.move = i;
            //System.out.println("Inside maxValue For Loop: " + newState.move);
            //computer or maximizing agent didn't get an extra turn
            if(treeFlag == 0) {

                treeTurn = (treeTurn + 1) % 2;
                newState.value = Math.max(newState.value, minValue(newState, depth + 1, alpha, beta));
                //System.out.println("maxValue calls minValue");

            }

            //computer or maximizing agent gets an extra turn
            if(treeFlag == 1) {

                newState.value = Math.max(newState.value, maxValue(newState, depth + 1, alpha, beta));
                treeFlag = 0;

            }

            if(newState.value >= beta) {
                //System.out.println("maxValue: 2nd return, depth: " + depth);
                bestState = newState;
                return newState.value;

            }
            alpha = Math.max(alpha, newState.value);

        }
        //System.out.println("maxValue: 3rd return, depth: " + depth);
        bestState = newState;
        return newState.value;
    }


    //takes the current state as a parameter and returns the best possible move
    public static void miniMax(State currState) {

        maxValue(currState, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        //System.out.println("Inside miniMax: " + bestState.move);
    }


    //The Game Algorithm
    public static void gameAlgo(State currState) {

        while (!gameOver(currState)) {

            /*if(turn == 0)
                System.out.println("Your turn");
            else if(turn == 1)
                System.out.print("Computer's turn: ");
            */

            int num = 0;

            //1st AI's turn
            if(turn == player) {

                //make a copy of the current State and use it
                State intelligentState = new State();
                intelligentState.maxScore = currState.maxScore;
                intelligentState.minScore = currState.minScore;
                intelligentState.value = currState.value;
                intelligentState.copy(currState.minArr, "minArr");
                intelligentState.copy(currState.maxArr, "maxArr");
                //Scanner in = new Scanner(System.in);
                //num = in.nextInt() - 1;
                treeTurn = player;
                miniMax(intelligentState);
                num = bestState.move;
                //System.out.println(num + 1);

            }

            //2nd AI's turn
            if(turn == computer) {

                //make a copy of the current State and use it
                State intelligentState = new State();
                intelligentState.maxScore = currState.maxScore;
                intelligentState.minScore = currState.minScore;
                intelligentState.value = currState.value;
                intelligentState.copy(currState.minArr, "minArr");
                intelligentState.copy(currState.maxArr, "maxArr");
                //Scanner in = new Scanner(System.in);
                //num = in.nextInt() - 1;
                treeTurn = computer;
                miniMax(intelligentState);
                num = bestState.move;
                //System.out.println(num + 1);

            }


            //moves.add(num);
            State newState;
            newState = makeState(1, currState, num, turn, 0);
            currState.bestChild = newState;
            newState.move = num;
            //newState.printState();
            //gameState.add(newState);
            currState = newState;

            //System.out.println("Before Flag: " + flag);
            if(flag == 0)
                turn = (turn + 1) % 2;
            flag = 0;
            //System.out.println("After Flag: " + flag);
            //System.out.println("Turn: " + turn);
        }
    }


    //initialize the game
    public static void initState() {

        root.initializeBoard();
        root.value = 0;
        //root.printState();
        //gameState.add(root);

    }


    public static void match(int p1, int p2) {

        int i;
        for(i = 1; i <= numberOfGamesPerPair; i++) {

            //heuristicType 1(p1) is giving the first move
            if(i >= 1 && i <= numberOfGamesPerPair/2) turn = player;

            //heuristicType 2(p2) is giving the first move
            if(i >= numberOfGamesPerPair/2 && i <= numberOfGamesPerPair) turn = computer;

            initState();
            gameAlgo(root);

            //First move by player & won by player
            if(i >= 1 && i <= numberOfGamesPerPair/2 && winner == player)
                stat[p1][p2]++;

            //First move by player, won by computer
            if(i >= numberOfGamesPerPair/2 && i <= numberOfGamesPerPair && winner == computer)
                reverseStat[p1][p2]++;

            //First move by computer, won by computer
            if(i >= numberOfGamesPerPair/2 && i <= numberOfGamesPerPair && winner == computer)
                stat[p2][p1]++;

            //First move by computer, won by player
            if(i >= numberOfGamesPerPair/2 && i <= numberOfGamesPerPair && winner == player)
                reverseStat[p2][p1]++;
        }

        if(stat[p1][p2] != 0) stat[p1][p2]--;
        if(stat[p2][p1] != 0) stat[p2][p1]--;
        if(reverseStat[p1][p2] != 0) reverseStat[p1][p2]--;
        if(reverseStat[p2][p1] != 0) reverseStat[p2][p1]--;

    }


    public static void main(String[] args) {

        int i;

        //round decider
        for(i = 0; i < 6; i++) {

            if(i == 0) {
                match(i, i + 1); //0 vs 1
                heuristicType1 = 0;
                heuristicType2 = 1;
            }
            else if(i == 1) {
                match(i - 1, i + 1); //0 vs 2
                heuristicType1 = 0;
                heuristicType2 = 2;
            }
            else if(i == 2) {
                match(i - 2, i + 1); //0 vs 3
                heuristicType1 = 0;
                heuristicType2 = 3;
            }
            else if(i == 3) {
                match(i - 2, i - 1); //1 vs 2
                heuristicType1 = 1;
                heuristicType2 = 2;
            }
            else if(i == 4) {
                match(i - 3, i - 1); //1 vs 3
                heuristicType1 = 1;
                heuristicType2 = 3;
            }
            else {
                match(i - 3, i - 2); //2 vs 3
                heuristicType1 = 2;
                heuristicType2 = 3;
            }

        }

        int j;
        System.out.println("First move by player, won by player");
        for(i = 0; i < 4; i++) {

            for(j = 0; j < 4; j++)
                System.out.print(stat[i][j] + " ");
            System.out.println();
        }

        System.out.println("Second move by player, won by player");
        for(i = 0; i < 4; i++) {

            for(j = 0; j < 4; j++)
                System.out.print(reverseStat[j][i] + " ");
            System.out.println();
        }

    }
}
