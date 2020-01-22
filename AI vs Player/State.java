/**
 * Created by Ranakrc on 11-Dec-17.
 */
public class State {

    //binCount, blocks can be changed
    //value is the heristic value of the state
    //move is the move that will generate this state from its parent state
    public int binCount = 6, blocks = 4, maxScore = 0, minScore = 0, value, move = 0;
    int[] maxArr = new int[binCount];
    int[] minArr = new int[binCount];
    public State bestChild;


    public State() {

    }


    //copy the contents of a parent state's array into a child states's array
    public void copy(int[] arr, String str) {

        int i;
        if(str == "minArr") {

            for(i = 0; i < binCount; i++)
                minArr[i] = arr[i];
        }
        else {

            for(i = 0; i < binCount; i++)
                maxArr[i] = arr[i];

        }

    }


    //initialize only the root state
    public void initializeBoard() {

        int i;
        for(i = 0; i < binCount; i++) {

            maxArr[i] = blocks;
            minArr[i] = blocks;
        }
    }


    //count the total number of blocks in all the bins of a player
    public int countBlocks(int[] arr) {

        int i, count = 0;
        for(i = 0; i < binCount; i++)
            count += arr[i];
        return count;
    }


    //check whether an array has all zero elements
    //return true if any array has all zero elements, else return false
    public boolean checkZero(int[] arr) {

        int i, count = 0;
        for( i = 0; i < binCount; i++) {

            if(arr[i] == 0)
                count++;
        }

        if(count == binCount)
            return true;

        return false;
    }


    public void printState() {

        int i;
        System.out.print("MaxArray: ");
        for(i = 0; i < binCount; i++)
            System.out.print(maxArr[i] + " ");
        System.out.println();
        System.out.print("MinArray: ");
        for(i = 0; i < binCount; i++)
            System.out.print(minArr[i] + " ");
        System.out.println();

        System.out.println("Computer's Score : " + maxScore);
        System.out.println("Your Score : " + minScore);

    }

}
