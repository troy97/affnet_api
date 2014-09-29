package eu.ibutler.affiliatenetwork.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.ibutler.affiliatenetwork.dao.ShopDao;
import eu.ibutler.affiliatenetwork.dao.exceptions.DbAccessException;
import eu.ibutler.affiliatenetwork.dao.exceptions.NoSuchEntityException;
import eu.ibutler.affiliatenetwork.entity.Shop;
import eu.ibutler.affiliatenetwork.jdbc.DbConnectionPool;
import eu.ibutler.affiliatenetwork.jdbc.JdbcUtils;

public class ShopDaoImpl implements ShopDao{
	
	private static Logger log = Logger.getLogger(ShopDaoImpl.class.getName());
	
	private DbConnectionPool connectionPool = null;
	
	/**
	 *
	 */
	public ShopDaoImpl() {
		this.connectionPool = DbConnectionPool.getInstance();
	}
	
	
	@Override
	public List<Shop> getAllShops() throws DbAccessException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_webshops";
			rs = stm.executeQuery(sql);
			return createShopsFromRs(rs);
		}
		catch(SQLException e){
			throw new DbAccessException("Error accessing DB", e);	
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}
	
	/**
	 *
	 * @param rs
	 * @return List<Shop> which is not null and size>0 or throws exception
	 * @throws NoSuchEntityException 
	 * @throws SQLException
	 */
	private List<Shop> createShopsFromRs(ResultSet rs) throws SQLException {
		List<Shop> toReturn=new ArrayList<Shop>();
		Shop freshShop=null;
		while(true){
			try {
				freshShop=createOneShopFromRs(rs);
			}
			catch (NoSuchEntityException e) {
				//SQL exception only thrown when reached the end of rs
				break;
			}
			toReturn.add(freshShop);
		};
		return toReturn;
	}	
	
	/**
	 *
	 * @param rs
	 * @return User object
	 * @throws NoSuchEntityException if failed to create new Shop instance
	 */
	private Shop createOneShopFromRs(ResultSet rs) throws SQLException, NoSuchEntityException {
		Shop toReturn = null;
		if(rs.next()){
			toReturn = new Shop(rs.getInt("id"), rs.getString("name"));
		}
		//if user wasn't created throw exception
		if(toReturn == null) {
			throw new NoSuchEntityException();
		}
		return toReturn;
	}


	@Override
	public Shop selectById(int dbId) throws DbAccessException, NoSuchEntityException {
		Connection conn = null;
		Statement stm=null;
		ResultSet rs = null;	
		try{
			conn=connectionPool.getConnection();
			stm = conn.createStatement();
			String sql = "SELECT * FROM tbl_webshops WHERE id=" + dbId;
			rs = stm.executeQuery(sql);
			return createOneShopFromRs(rs);
		}
		catch(SQLException e){
			throw new DbAccessException("Error accessing DB", e);	
		}
		finally{
			JdbcUtils.close(rs);
			JdbcUtils.close(stm);
			JdbcUtils.close(conn);
		}
	}	

}
