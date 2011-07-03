package monitor;

import java.util.*;

/**
 * Represents a mother cell set in an experiment.
 */
public class MotherCellSet
{
    private Integer id;
    private String reference;
    private String label;
    private String strainName;
    private String media;
    private Float temperature;
    private Integer cellCount;
    private Strain strain;
    private List<MotherCell> cells;

    public MotherCellSet() {
        cells = new ArrayList();
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getMedia() {
        return media;
    }
    public void setMedia(String media) {
        this.media = media;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStrainName() {
        return strainName;
    }
    public void setStrainName(String strainName) {
        this.strainName = strainName;
    }

    public Float getTemperature() {
        return temperature;
    }
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
    
    public Integer getCellCount() {
        return cellCount;
    }
    public void setCellCount(Integer cellCount) {
        this.cellCount = cellCount;
    }
    
    public void setStrain(Strain strain) {
        this.strain = strain;
    }
    public Strain getStrain() {
        return strain;
    }
    
    public void setCells(List<MotherCell> cells) {
        this.cells = cells;
    }
    public List<MotherCell> getCells() {
        return cells;
    }
    
    public MotherCell getCell(Integer i) {
        return cells.get(i);
    }
    
    public Boolean isComplete() {
        if (cells.isEmpty()) {
            return false;
        }
        for (MotherCell cell : cells) {
            if (! cell.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> getLifespans() {
        List<Integer> lifespans = new ArrayList();
        for (MotherCell cell : cells) {
            Integer lifespan = cell.getLifespan();
            lifespans.add(lifespan);
        }
        return lifespans;
    }
    
    public Integer getEndStateCount(MotherCell.EndState targetEndState) {
        Integer count = 0;
        for (MotherCell cell : cells) {
            MotherCell.EndState endState = cell.getEndState();
            if (endState != null && endState.equals(targetEndState)) {
                count++;
            }
        }
        return count;
    }

    public void setKey(MotherCellSetKey key) {
        if (key == null) {
            return;
        }
        this.id = key.getNumber();
        this.reference = key.getReference();
        this.label = key.getLabel();
        this.strainName = key.getStrainName();
        this.strain = key.getStrain();
        this.media = key.getMedia();
        this.temperature = key.getTemperature();
        this.cellCount = key.getCellCount();
    }
}
