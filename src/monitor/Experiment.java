package monitor;

import java.util.*;
import java.util.Map.*;

/**
 * Holds experiment data.
 */
public class Experiment
{
    private Integer number;
    private String comment;
    private Map<Integer,MotherCellSet> cellSetMap = new HashMap();
    
    private boolean isComplete = false;
    
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Integer getMaxCellSetId() {
        Integer maxId = 0;
        for (Integer id : cellSetMap.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    public void setCellSet(Integer id, MotherCellSet dataSet) {
        cellSetMap.put(id, dataSet);
    }
    public MotherCellSet getCellSet(Integer id) {
        return cellSetMap.get(id);
    }
    
    public void setComplete(boolean value) {
        this.isComplete = value;
    }
    public boolean isComplete() {
        return isComplete;
    }
    
    public Map<Integer,MotherCellSet> getCellSetMap() {
        return cellSetMap;
    }
}
