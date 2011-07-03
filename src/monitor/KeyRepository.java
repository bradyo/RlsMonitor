package monitor;

import java.sql.*;
import java.util.*;

public class KeyRepository {

    private Connection connection;
    
    public KeyRepository(Connection connection) {
        this.connection = connection;
    }
    
    public Map<Integer,MotherCellSetKey> getCellSetKeyMap(Integer experimentNumber)
            throws Exception {
        Map<Integer,MotherCellSetKey> map = new HashMap();
            
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM yeast_rls_experiment e"
                + " INNER JOIN yeast_rls_cell_set s ON s.experiment_id = e.id"
                + " LEFT JOIN yeast_strain y ON y.id = s.strain_id"
                + " WHERE e.number = ?");
        stmt.setInt(1, experimentNumber);
                
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            MotherCellSetKey key = new MotherCellSetKey();
            key.setNumber(results.getInt("s.number"));
            key.setReference(results.getString("s.reference"));
            key.setLabel(results.getString("s.label"));
            key.setStrainName(results.getString("s.strain_name"));
            key.setMedia(results.getString("s.media"));
            key.setTemperature(results.getFloat("s.temperature"));
            key.setCellCount(results.getInt("s.cell_count"));

            Strain strain = new Strain();
            strain.setName(results.getString("y.name"));
            strain.setBackground(results.getString("y.background"));
            strain.setMatingType(results.getString("y.mating_type"));
            strain.setFreezerCode(results.getString("y.freezer_code"));
            strain.setShortGenotype(results.getString("y.genotype_short"));
            strain.setFullGenotype(results.getString("y.genotype"));
            key.setStrain(strain);

            map.put(key.getNumber(), key);
        }       
        stmt.close();
        return map;
    }
}
