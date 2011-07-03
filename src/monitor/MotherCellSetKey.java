package monitor;

public class MotherCellSetKey 
{
    private Integer number;
    private String reference;
    private String label;
    private String strainName;
    private Strain strain;
    private String media;
    private Float temperature;
    private Integer cellCount;

    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer id) {
        this.number = id;
    }

    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    public Integer getCellCount() {
        return cellCount;
    }
    public void setCellCount(Integer cellCount) {
        this.cellCount = cellCount;
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

    public String getStrainName() {
        return strainName;
    }
    public void setStrainName(String strainName) {
        this.strainName = strainName;
    }

    public Strain getStrain() {
        return strain;
    }
    public void setStrain(Strain strain) {
        this.strain = strain;
    }
    
    public Float getTemperature() {
        return temperature;
    }
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }
}
