package mancala;

public class KalahRules extends GameRules {
    private static final long serialVersionUID = -7651314728917636985L;
    private boolean bonusTurn;

    /**
     * Move stones from the initial pit.
     *
     * @param startPit The inital pit that stones will be moved from.
     * @param playerNum The player number (1 or 2).
     * @return The number of stones added to the player's store.
     * @throws InvalidMoveException When a player attempts to move from a pit that isn't theirs.
     */
    @Override
    public int moveStones(final int startPit, final int playerNum) throws InvalidMoveException {
        if (!isValidMove(startPit, playerNum)) {
            throw new InvalidMoveException();
        }
        int preMoveStoreCount;

        final MancalaDataStructure dataStructure = getDataStructure();
        preMoveStoreCount = getStoreCount(dataStructure, playerNum);
        distributeStones(startPit);

        final int postCount = getStoreCount(playerNum, dataStructure);
        return postCount - preMoveStoreCount;
    } 

    private int getStoreCount(final MancalaDataStructure dataStructure, final int playerNum) {
        return dataStructure.getStoreCount(playerNum);
    }

    /**
     * Capture the stones from the pit and it's complement.
     *
     * @param stoppingPoint The last pit that a stone was distributed to.
     * @return The number of stones captured.
     */
    @Override
    public int captureStones(final int stoppingPoint)  {
        final int otherPit = 13-stoppingPoint;
        int stonesCaptured = 0;

        if (getNumStones(otherPit) != 0) {
            final MancalaDataStructure dataStructure = getDataStructure();
            stonesCaptured += removeStones(stoppingPoint, dataStructure);
            stonesCaptured += removeStones(otherPit, dataStructure);
            if (stoppingPoint <= PITS_PER_SIDE) {
                // add to player 1's store
                addToStore(1,stonesCaptured, dataStructure);
            } else {
                // add to player 2's store
                addToStore(2,stonesCaptured, dataStructure);
            }
        }

        // return number of stones captured
        return stonesCaptured;
    }

    /**
     * Distributes stones to the proper pits.
     *
     * @param startingPoint The inital pit that stones will be moved from.
     * @return Number of stones distributed or captured.
     */
    @Override
    public int distributeStones(final int startingPoint) {

        final MancalaDataStructure dataStructure = getDataStructure();
        final int initialStones = removeStones(startingPoint, dataStructure);
        int stonesToMove = initialStones;
        Countable currentPit;
        final int playerNum = getPlayerNum(startingPoint);

        setIterator(dataStructure, startingPoint, playerNum, false);
        


        while (stonesToMove > 0) {
            currentPit = dataStructure.next();
            currentPit.addStone();
            stonesToMove--;
        }

        int capturedStones;

        if (getCurrentPit(dataStructure) == -1) {
            setBonus(true);
        }

        if (isBonus()) {
            capturedStones = 0;
        } else {
            final int currentPitCount = getNumStones(getCurrentPit(dataStructure));
            capturedStones = doCapture(playerNum, dataStructure, currentPitCount);
        }


        return initialStones+capturedStones;
    }

    /**
     * Check if a bonus turn is applicable.
     *
     * @return Is bonus turn applicable (true or false).
     */    
    public boolean isBonus() {
        return bonusTurn;
    }

    /**
     * Set bonus turn.
     *
     * @param givenBonus State of bonus turn.
     */    
    public void setBonus(final boolean givenBonus) {
        bonusTurn = givenBonus;
    }
}
