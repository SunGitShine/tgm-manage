/**
 * 
 */
package com.juma.tgm.manage.authority.vo;

import java.io.Serializable;

/**
 * @author vencent.lu
 *
 */
public class UserLoginVo implements Serializable{
	
    private static final long serialVersionUID = 5465445131755281875L;
    private String loginName;
	private String password;
	
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserLoginVo [loginName=");
		builder.append(loginName);
		builder.append(", password=");
		builder.append(password);
		builder.append("]");
		return builder.toString();
	}

}
