package monitor;

import java.util.*;

/**
 * Represents a mother cell in an experiment.
 */
public class MotherCell 
{
    public enum EndState {DEAD, LOST, OMIT, FLAGGED, NO_BUD, SMALL_BUD, LARGE_BUD, CLUSTER};
    
    private Integer id;
    private List<Integer> divisionCounts;
    private EndState endState;
    private String comment;

    public MotherCell() {
        divisionCounts = new ArrayList();
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getDivisionCounts() {
        return divisionCounts;
    }
    public void setDivisionCounts(List<Integer> divisionCounts) {
        this.divisionCounts = divisionCounts;
    }
    
    public EndState getEndState() {
        return endState;
    }
    public void setEndState(EndState endState) {
        this.endState = endState;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public boolean isLost() {
        return (endState == EndState.LOST);
    }
    
    public boolean isOmitted() {
        return (endState == EndState.OMIT);
    }
    
    public boolean isFlagged() {
        return (endState == EndState.FLAGGED);
    }
    
    public Boolean isComplete() {
        return (endState != null);
    }

    public Integer getLifespan() {
        if (divisionCounts.isEmpty() || isLost() || isOmitted()) {
            return null;
        }
        Integer sum = 0;
        for (Integer count : divisionCounts) {
            sum += count;
        }
        return sum;
    }
}
