package monitor;

public class Strain 
{
    private String name;
    private String background;
    private String matingType;
    private String shortGenotype;
    private String fullGenotype;
    private String freezerCode;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }
    public void setBackground(String background) {
        this.background = background;
    }

    public String getFreezerCode() {
        return freezerCode;
    }
    public void setFreezerCode(String freezerCode) {
        this.freezerCode = freezerCode;
    }

    public String getFullGenotype() {
        return fullGenotype;
    }
    public void setFullGenotype(String fullGenotype) {
        this.fullGenotype = fullGenotype;
    }

    public String getMatingType() {
        return matingType;
    }
    public void setMatingType(String matingType) {
        this.matingType = matingType;
    }

    public String getShortGenotype() {
        return shortGenotype;
    }
    public void setShortGenotype(String shortGenotype) {
        this.shortGenotype = shortGenotype;
    }
}
