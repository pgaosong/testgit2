package cn.smbms.dao.role;

import java.sql.Connection;
import java.util.List;
import cn.smbms.pojo.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao {
	
	public List<Role> getRoleList()throws Exception;

}
