package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	public void listObjects(Map<Integer, ArtObject> idMap) {
		
		String sql = "SELECT * from objects";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				if(!idMap.containsKey(res.getInt("object_id"))) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
					idMap.put(artObj.getId(), artObj);
				}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Approccio 1 --> non giunge al termine perchè ci sono troppi vertici e facciamo troppe query al database
	/*public int getPeso(ArtObject a1, ArtObject a2) {
		String sql="SELECT COUNT(*) AS peso" + 
				"FROM exhibition_objects AS eo1, exhibition_objects AS eo2 " + 
				"WHERE eo1.exhibition_id=eo2.exhibition_id AND eo1.object_id=? AND eo2.object_id=?";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a2.getId());
			ResultSet res = st.executeQuery();
			if(res.next()) {
				int peso= res.getInt("peso");
				conn.close();
				return peso;
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}*/
	
	//query metodo 3
	
	//metto id1> id2 o viceversa così risparmio un controllo nel model: non prende in considerazione le coppie multiple
	public List<Adiacenza> getAdiacenze(){
		String sql="SELECT eo1.object_id AS obj1, eo2.object_id AS obj2, COUNT(*) AS peso "  + 
				"FROM exhibition_objects AS eo1, exhibition_objects AS eo2 " + 
				"WHERE eo1.exhibition_id=eo2.exhibition_id AND eo2.object_id < eo1.object_id " + 
				"GROUP BY eo1.object_id, eo2.object_id";
		
		Connection conn = DBConnect.getConnection();
		List<Adiacenza> adiacenze = new ArrayList<Adiacenza>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while(res.next()){
				adiacenze.add(new Adiacenza(res.getInt("obj1"), res.getInt("obj2"), res.getInt("peso")));
			}
			
			conn.close();
			return adiacenze;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
			
		
	}
	
}
