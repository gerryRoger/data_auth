package cn.plaf.dataauth.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.plaf.dataauth.annotation.DataAuth;
import cn.plaf.dataauth.config.DataAuthType;
public interface ArchDao extends BaseMapper<ArchDO>{
//	@DataAuth(dataAuthType="2,3")
    List<ArchDO> getArchList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_FULL_MATCH,DataAuthType.DATA_SCOPE_CUSTOM})
    List<ArchDO> getList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_AREA})
    List<ArchDO> getAreaSqlList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_CUSTOM})
    List<ArchDO> getCustomSqlList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_CUSTOM_EXISTS})
    List<ArchDO> getExistsSqlList(@Param("archDTO") ArchDO archDO);
    
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_DEPT})
    List<ArchDO> getDeptSqlList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_DEPT_AND_CHILD})
    List<ArchDO> getDeptAndChildSqlList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_SELF})
    List<ArchDO> getSelfSqlList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_FULL_MATCH})
    List<ArchDO> getFullMatchSqlList(@Param("archDTO") ArchDO archDO);
    
    @DataAuth(dataAuthType= {DataAuthType.DATA_SCOPE_CONTAIN_MATCH})
    List<ArchDO> getContainMatchSqlList(@Param("archDTO") ArchDO archDO);
    

    
}
