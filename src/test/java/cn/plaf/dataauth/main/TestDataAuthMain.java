package cn.plaf.dataauth.main;

import cn.plaf.dataauth.mapper.ArchDO;
import cn.plaf.dataauth.mapper.ArchDao;
import cn.plaf.dataauth.util.MybatisInterceptorHelper;
import cn.plaf.dataauth.util.MybatisPlusInterceptorHelper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.SqlSession;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataAuthMain {
	public static void getArchList() {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		PageHelper.startPage(1,2);
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getArchList(archDO);
			PageInfo<ArchDO> pageInfo  = new PageInfo(list);
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
            sqlSession.close();
        }
	}
	
	public static void getAreaSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getAreaSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
            sqlSession.close();
        }
	}

	public static void getCustomSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getCustomSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}

	public static void getExistsSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getExistsSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void getDeptSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getDeptSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void getDeptAndChildSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getDeptAndChildSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void getSelfSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getSelfSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void getFullMatchSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getFullMatchSqlList(archDO);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void getContainMatchSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		PageHelper.startPage(pageNo,pageSize);
		try {
			ArchDO archDO = new ArchDO();
			archDO.setArchName("数据");
			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.getContainMatchSqlList(archDO);
			PageInfo<ArchDO> pageInfo = new PageInfo<ArchDO>(list);
			System.out.println(pageInfo);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void getQueryWrapperSqlList(int pageNo,int pageSize) {
		SqlSession sqlSession = MybatisPlusInterceptorHelper.getSqlSession();
		ArchDao archDao = sqlSession.getMapper(ArchDao.class);
		try {
			QueryWrapper<ArchDO> qw= new QueryWrapper<ArchDO>();
			ArchDO archDO = new ArchDO();
			qw.setEntity(archDO);
			qw.in(true, "dept_id", Arrays.asList( new Integer[]{1,2,3}));
			qw.in(true, "crter", Arrays.asList( new String[]{"4","5","6"}));
			archDO.setArchName("数据");
//			List<Integer> deptIds = Arrays.asList( new Integer[]{1,2,3});
//			archDO.setDeptIds(deptIds);
			List<ArchDO>  list  = archDao.selectList(qw);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			sqlSession.close();
		}
	}
	public static void main(String[] args) {
//		getArchList();
//		getAreaSqlList(1,2);
//		getCustomSqlList(1,2);
//		getExistsSqlList(1,2);
//		getDeptSqlList(1,2);
//		getDeptAndChildSqlList(1,2);
//		getSelfSqlList(1,2);
//		getFullMatchSqlList(1,2);
		getContainMatchSqlList(1,2);
//		getQueryWrapperSqlList(1,2);
//		ew.paramNameValuePairs.MPGENVAL
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("array", (new String[] {"1","2","3"}));
//		map.put("list", Arrays.asList(new String[] {"1","2","3"}));
//		Map<String,Object> tmap = new HashMap<String,Object>();
//		tmap.put("111", "22222");
//		map.put("map", tmap);
//		System.out.println(map.get("array").getClass().isArray());
//		System.out.println(map.get("array") instanceof Arrays);
//		System.out.println(map.get("list") instanceof Arrays);
//		System.out.println(map.get("list") instanceof Collection);
		
//		System.out.println(map.get("map") instanceof Arrays);
//		System.out.println(map.get("map") instanceof Map);
		
		
	}


}
