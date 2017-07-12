package com.trs.om.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.trs.om.bean.ICPinfo;
import com.trs.om.bean.ICPinfoCriterion;
import com.trs.om.bean.ICPinfoGroup;
import com.trs.om.bean.OffsetLimit;
import com.trs.om.bean.UserGroup;
import com.trs.om.common.ObjectContainer;
import com.trs.om.dao.ICPinfoDao;
import com.trs.om.dao.ICPinfoGroupDao;
import com.trs.om.dao.UserGroupDao;
import com.trs.om.dbconfig.DbConfigManager;
import com.trs.om.dbconfig.MySqlBaseDbConfig;
import com.trs.om.exception.TRSOMException;
import com.trs.om.service.EncryptService;
import com.trs.om.service.ICPinfoService;
import com.trs.om.util.DataSourceManager;
import com.trs.om.util.PagedArrayList;

public class ICPinfoServiceImpl implements ICPinfoService {

	private ICPinfoDao icpinfoDao;
	private ICPinfoGroupDao icpInfoGroupDao;
	private DbConfigManager dbConfigManager;

	public ICPinfoServiceImpl() {
		EncryptService obj;
		try {
			obj = EncryptService.getInstance();
			obj.checkLicence();
		} catch (Exception e) {
			throw new TRSOMException("无法获得或者校验注册码：" + e.getMessage(), e);
		}
	}

	public ICPinfoDao getIcpinfoDao() {
		return icpinfoDao;
	}

	public void setIcpinfoDao(ICPinfoDao icpinfoDao) {
		this.icpinfoDao = icpinfoDao;
	}

	@Transactional
	public void add(ICPinfo temp) {
		icpinfoDao.makePersistent(temp);
	}

	@Transactional
	public void delete(Long id) {
		ICPinfo entity = icpinfoDao.findById(id, false);
		icpinfoDao.makeTransient(entity);
	}

	@Transactional
	public PagedArrayList<ICPinfo> findByName(String siteName, int dbPage,
			int limit) {
		return icpinfoDao.findByName(siteName, dbPage, limit);
	}

	@Transactional
	public PagedArrayList<ICPinfo> findByName(int dbPage, int limit) {
		return icpinfoDao.findByName(dbPage, limit);
	}

	@Transactional
	public void update(ICPinfo temp) {
		icpinfoDao.makePersistent(temp);
	}

	/**
	 * 按照站点类型查询站点信息
	 */
	@Transactional
	public PagedArrayList<ICPinfo> list(int type, int dbpage, int limit) {
		return icpinfoDao.list(type, dbpage, limit);
	}

	@Transactional
	public PagedArrayList<ICPinfo> list(int type, String enemy, int dbpage,
			int limit) {
		return icpinfoDao.list(type, enemy, dbpage, limit);
	}

	/**
	 * 列出某个责任辖区下的所有站点
	 */
	@Transactional
	public PagedArrayList<ICPinfo> listDesignate(List<Long> userGroups,
			int dbpage, int limit) {
		return icpinfoDao.listDesignate(userGroups, dbpage, limit);
	}

	/**
	 * 列出某个地区的所有站点
	 */
	@Transactional
	public PagedArrayList<ICPinfo> listLocal(List<Long> userGroups, int dbpage,
			int limit) {
		return icpinfoDao.listLocal(userGroups, dbpage, limit);
	}

	/**
	 * 列出属于某个准属地的所有站点
	 */
	@Transactional
	public PagedArrayList<ICPinfo> ListAffiliate(List<Long> userGroups,
			int dbpage, int limit) {
		return icpinfoDao.ListAffiliate(userGroups, dbpage, limit);
	}

	/**
	 * 列出站点的准属地信息
	 */
	@Transactional
	public List<Long> getAffiliate(Long siteId) {
		// return icpinfoDao.getAffiliate(siteId);
//		StringBuffer sb = new StringBuffer();
		ICPinfo icpinfo = icpinfoDao.findById(siteId, false);
		Set<UserGroup> groupSet = icpinfo.getAffiliate();
		Iterator<UserGroup> it = groupSet.iterator();
		List<Long> rList = new ArrayList<Long>();
		while (it.hasNext()) {
			UserGroup g = it.next();
			rList.add(g.getId());
		}
		return rList;
	}

	// 配置站点的准属地信息
	@Transactional
	public void configAffiliate(Long siteId, String groupIds) {
		UserGroupDao userGroupDao = (UserGroupDao) ObjectContainer
				.getBean("userGroupDao");
		ICPinfo icpinfo = this.icpinfoDao.findById(siteId, false);
		Set<UserGroup> set = new HashSet<UserGroup>();
		String[] ids = groupIds.split(",");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] != null && ids[i].trim().length() > 0) {
				Long id = Long.valueOf(ids[i]);
				UserGroup ug = userGroupDao.findById(id, false);
				set.add(ug);
			}
		}
		icpinfo.setAffiliate(set);
		icpinfoDao.makePersistent(icpinfo);
	}

	@Transactional
	public ICPinfo findBySid(Long sid, boolean lock) {

		return icpinfoDao.findById(sid, lock);

	}

	@Transactional
	public PagedArrayList<ICPinfo> searchIcpinfo(ICPinfoCriterion criterion,
			OffsetLimit offsetLimit) {
		if (criterion.eqLocals != null && criterion.eqLocals.length == 0)
			return new PagedArrayList<ICPinfo>();
		else
			return icpinfoDao.searchIcpinfo(criterion, offsetLimit);

	}

	@Transactional
	public List<ICPinfo> searchIcpinfo(ICPinfoCriterion criterion) {
		if (criterion.eqLocals != null && criterion.eqLocals.length == 0)
			return new ArrayList<ICPinfo>();
		else
			return icpinfoDao.searchAllIcpinfo(criterion);

	}

	@Transactional
	public ICPinfo findByUrl(String url) {
		/*
		 * String domain=url.substring(0,url.indexOf("/")); String
		 * root2=domain.substring(domain.lastIndexOf("."),domain.length());
		 * String root1=domain.substring(0,domain.lastIndexOf(".")); String
		 * root0=root1.substring(root1.lastIndexOf(".")+1,root1.length());
		 * String root=root0+root2; return
		 * this.icpinfoDao.findByRootDomainName(root);
		 */
		Matcher m = Pattern.compile("(http://)?.*?((\\w+.)?\\w+.\\w+)(?=\\/)")
				.matcher(url);
		String root = null;
		if (m.find()) {
			String temp = m.group(1) == null ? "" : m.group(1);
			String temp2 = m.group(2) == null ? "" : m.group(2);
			// String temp3 = m.group(3)== null ? "":m.group(3);
			root = temp + temp2;// +temp3;
		}
		// if(root.endsWith("com.cn")||root.endsWith("org.cn")||root.endsWith("gov.cn"))

		return this.icpinfoDao.findByRootDomainName(root);
	}

	@Override
	@Transactional
	public List<ICPinfoGroup> listUsedGroup() {
		return icpInfoGroupDao.findUsedGroups();
	}

	public void setIcpInfoGroupDao(ICPinfoGroupDao icpInfoGroupDao) {
		this.icpInfoGroupDao = icpInfoGroupDao;
	}

	@Override
	@Transactional
	//TODO 用触发器完成该功能，然后去掉改方法
	@Deprecated
	public List<ICPinfo> countIcpinfos() {
		return icpinfoDao.countAndAddIcpInfos();
	}

	public DbConfigManager getDbConfigManager() {
		return dbConfigManager;
	}

	public void setDbConfigManager(DbConfigManager dbConfigManager) {
		this.dbConfigManager = dbConfigManager;
	}

	public ICPinfoGroupDao getIcpInfoGroupDao() {
		return icpInfoGroupDao;
	}
	@Override
	public void updateSiteRank(String siteName, int siteRank) {
		//获取舆情主库的配置
		final MySqlBaseDbConfig dbConfig=dbConfigManager.getMySqlBaseDbConfig();
		Object[] dsTriples=DataSourceManager.getMySQLDataSourceTriples("om_data", dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUser(), dbConfig.getPwd());
		DataSource ds=(DataSource) dsTriples[0];
		final NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(ds);
		HashMap<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("sitename", siteName);
		String sql = "select count(sitename) from " + dbConfig.getDb() + ".siteinfo where sitename='"+siteName+"';";
		String sql1 = "insert into " + dbConfig.getDb() + ".siteinfo (sitename,siterank) values ('"+siteName+"','"+siteRank+"');";

		int count = jdbcTemplate.queryForInt(sql, paramMap);
		if(count==0){
			paramMap.put("siterank", siteRank);
		}else{
			sql1 = "update " + dbConfig.getDb() + ".siteinfo set siterank="+siteRank+" where  sitename='"+siteName+"';";
		}
		jdbcTemplate.update(sql1, paramMap);
	}
	@Override
	//TODO 用触发器完成该功能，然后去掉改方法
	@Deprecated
	public void batchAddOrUpdate(List<ICPinfo> infos) {
		List<String> all=new ArrayList<String>();
		Map<String,ICPinfo> site2info=new HashMap<String,ICPinfo>();
		for(ICPinfo info:infos){
			site2info.put(info.getSiteName(),info);
			all.add(info.getSiteName());
		}
		List<ICPinfo> existInfos=new ArrayList<ICPinfo>();
		if(!all.isEmpty()){
			final List<String> exists= this.listExistedSitenames(all);
			for(String exist:exists){
				existInfos.add(site2info.get(exist));
			}

			this.batchUpdate(existInfos);
			infos.removeAll(existInfos);
			if(!all.isEmpty()){
				this.batchAdd(infos);
			}
		}
	}


	public List<String> listExistedSitenames(final List<String> infos) {
		//获取舆情主库的配置
		final MySqlBaseDbConfig dbConfig=dbConfigManager.getMySqlBaseDbConfig();
		Object[] dsTriples=DataSourceManager.getMySQLDataSourceTriples("om_data", dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUser(), dbConfig.getPwd());
		DataSource ds=(DataSource) dsTriples[0];
		TransactionTemplate txTemplate=(TransactionTemplate) dsTriples[2];
		final NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);
		// 检索
		return txTemplate.execute(new TransactionCallback<List<String>>() {
			public List<String> doInTransaction(TransactionStatus status) {
				StringBuilder whereBuilder = new StringBuilder();
				HashMap<String, Object> paramMap = new HashMap<String, Object>(infos.size());
				for (String info : infos) {
					String name = "infos" + paramMap.size();
					if (whereBuilder.length() > 0)
						whereBuilder.append(',');
					whereBuilder.append(':').append(name);
					paramMap.put(name, "'"+info+"'");
				}
				String sql="select distinct IR_SITENAME from "+dbConfig.getDb()+".siteinfo in ("+whereBuilder.toString()+")";
				return jdbcTemplate.queryForList(sql, paramMap, String.class);
			}
		});
	}

	private  int[] batchUpdate(final List<ICPinfo> infos)   {
		final MySqlBaseDbConfig dbConfig=dbConfigManager.getMySqlBaseDbConfig();
		Object[] dsTriples=DataSourceManager.getMySQLDataSourceTriples("om_data", dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUser(), dbConfig.getPwd());
		DataSource ds=(DataSource) dsTriples[0];
		final JdbcTemplate jdbcTemplate=new JdbcTemplate(ds);
		TransactionTemplate txTemplate=(TransactionTemplate) dsTriples[2];
        final String sql ="update  siteinfo set siterank=? where sitename=?";
        return txTemplate.execute(new TransactionCallback<int[]>() {
			public int[] doInTransaction(TransactionStatus status) {
		        return jdbcTemplate.batchUpdate(sql,  new BatchPreparedStatementSetter(){
		            public void setValues(PreparedStatement ps,int i) throws SQLException{
		           	 ICPinfo info = infos.get(i);
		                ps.setInt(1, new Long(info.getSid()).intValue() );
		                ps.setString(2,"'"+info.getSiteName()+"'");
		             }
		             public int getBatchSize(){
		                return infos.size();
		             }
		       });
			}
		});
    }

	private  int[] batchAdd(final List<ICPinfo> infos)   {
		final MySqlBaseDbConfig dbConfig=dbConfigManager.getMySqlBaseDbConfig();
		Object[] dsTriples=DataSourceManager.getMySQLDataSourceTriples("om_data", dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUser(), dbConfig.getPwd());
		DataSource ds=(DataSource) dsTriples[0];
		final JdbcTemplate jdbcTemplate=new JdbcTemplate(ds);
		TransactionTemplate txTemplate=(TransactionTemplate) dsTriples[2];
		final String sql = "insert into " + dbConfig.getDb() + ".siteinfo (sitename,siterank) values (?,?)";
        return txTemplate.execute(new TransactionCallback<int[]>() {
			public int[] doInTransaction(TransactionStatus status) {
		        return jdbcTemplate.batchUpdate(sql,  new BatchPreparedStatementSetter(){
		            public void setValues(PreparedStatement ps,int i) throws SQLException{
		           	 	ICPinfo info = infos.get(i);
		           	 	ps.setInt(2, new Long(info.getSid()).intValue() );
		           	 	ps.setString(1,"'"+info.getSiteName()+"'");
		             }
		             public int getBatchSize(){
		                return infos.size();
		             }
		       });
			}
		});
    }

	@Override
	public int getSiteRank(final ICPinfo icpinfo) {

		final MySqlBaseDbConfig dbConfig=dbConfigManager.getMySqlBaseDbConfig();
		Object[] dsTriples=DataSourceManager.getMySQLDataSourceTriples("om_data", dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUser(), dbConfig.getPwd());
		DataSource ds=(DataSource) dsTriples[0];

		final NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(ds);
		HashMap<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("sitename", icpinfo.getSiteName());
		String sql = "select count(sitename) from " + dbConfig.getDb() + ".siteinfo where sitename='"+icpinfo.getSiteName()+"';";
		int count =  jdbcTemplate.queryForInt(sql, paramMap);
		int siteRank = new Long(icpinfo.getSid()).intValue();
		if(count==0){
			paramMap.put("siterank", siteRank);
			String sql1 = "insert into " + dbConfig.getDb() + ".siteinfo (sitename,siterank) values ('"+icpinfo.getSiteName()+"','"+siteRank+"');";
			jdbcTemplate.update(sql1, paramMap);
		}else{
			sql= "select siterank from " + dbConfig.getDb() + ".siteinfo where sitename='"+icpinfo.getSiteName()+"';";
			siteRank = jdbcTemplate.queryForInt(sql, paramMap);
		}
		return siteRank;
	}

	@Override
	public void deleteSiteRank(String siteName) {
		final MySqlBaseDbConfig dbConfig=dbConfigManager.getMySqlBaseDbConfig();
		Object[] dsTriples=DataSourceManager.getMySQLDataSourceTriples("om_data", dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUser(), dbConfig.getPwd());
		DataSource ds=(DataSource) dsTriples[0];
		final NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(ds);
		HashMap<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("sitename", siteName);
		String sql="delete from " + dbConfig.getDb() + ".siteinfo where sitename='"+siteName+"';";
		jdbcTemplate.update(sql, paramMap);
	}
}
