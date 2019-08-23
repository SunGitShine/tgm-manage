/**
 * 
 */
package com.juma.tgm.manage.authority.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author vencent.lu
 *
 */
public class RoleVo implements Serializable {
	
	private static final long serialVersionUID = -5164807093964220026L;
	
	private Integer roleId;
	private String roleKey;
	private String roleName;
	private String roleDescription;
	private List<Integer> resourceIds;
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}	
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}	
	public String getRoleKey() {
		return roleKey;
	}
	public void setRoleKey(String roleKey) {
		this.roleKey = roleKey;
	}
	public List<Integer> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Integer> resourceIds) {
		this.resourceIds = resourceIds;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleVo [roleId=");
		builder.append(roleId);
		builder.append(", roleName=");
		builder.append(roleName);
		builder.append(", roleDescription=");
		builder.append(roleDescription);
		builder.append(", isSysRole=");
		builder.append(", resourceIds=");
		builder.append(resourceIds);
		builder.append("]");
		return builder.toString();
	}
	
	

}
