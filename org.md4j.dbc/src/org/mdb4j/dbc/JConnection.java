package org.mdb4j.dbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JConnection {
	private final Logger log = LoggerFactory.getLogger("SQL");
	
	private final Connection con;
	
	public JConnection(final Driver driver, final String url, final String user, final String password) {
		final Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
		
		try {
			con = driver.connect(url, props);
			con.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void execute(final String sql, final Object ...params) {
		try {
			final PreparedStatement stmt = con.prepareStatement(sql);
			setParams(stmt, params);
			
			log.trace("{}; {}", sql, params);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JResult select(final String sql, final Object ...params) {
		try {
			final PreparedStatement stmt = con.prepareStatement(sql);
			setParams(stmt, params);
			
			log.trace("{}; {}", sql, params);
			final ResultSet set = stmt.executeQuery();
			
			return new JResult(set);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void update(final String sql, final Object ...params) {
		try {
			final PreparedStatement stmt = con.prepareStatement(sql);
			setParams(stmt, params);
			
			log.trace("{}; {}", sql, params);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public long insert(final String sql, final Object ...params) {
		try {
			final PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			setParams(stmt, params);
			
			log.trace("{}; {}", sql, params);
			stmt.executeUpdate();
			
			final ResultSet rs1 = stmt.getGeneratedKeys();
			rs1.next();
			return rs1.getLong(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void delete(final String table, final long ...ids) {
		try {
			final StringBuilder sb = new StringBuilder("DELETE ");
			sb.append(table);
			sb.append(" WHERE id IN (");
			
			for(int i = 0; i < ids.length; ++i) {
				sb.append("?");
				if(i < ids.length-1) sb.append(", ");
			}
				
			sb.append(")");
			final String sql = sb.toString();
			
			log.trace("{}; {}", sql, ids);
			final PreparedStatement stmt = con.prepareStatement(sql);
			setParams(stmt, ids);
			stmt.execute();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	

	public void commit() {
		try {
			log.trace("COMMIT");
			con.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void rollback() {
		try {
			log.trace("ROLLBACK");
			con.rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void close() {
		try {
			con.rollback();
			con.close();
		} catch (SQLException e) {
			//ignore => unable to do anything!!
		}
	}
	
	static void setParams(final PreparedStatement stmt, final Object ...params) throws SQLException {
		int i = 1;
		for(final Object obj: params) {
			stmt.setObject(i, obj);
			i++;
		}
	}
	
}
