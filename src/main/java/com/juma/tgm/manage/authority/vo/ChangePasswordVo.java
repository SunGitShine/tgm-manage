/**
 * 
 */
package com.juma.tgm.manage.authority.vo;

import java.io.Serializable;

/**
 * @author vencent.lu
 *
 */
public class ChangePasswordVo implements Serializable{
	
    private static final long serialVersionUID = -6611250551016384570L;
    private String password;
	private String newPassword;
	private String confirmPassword;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChangePasswordVo [password=");
		builder.append(password);
		builder.append(", newPassword=");
		builder.append(newPassword);
		builder.append(", confirmPassword=");
		builder.append(confirmPassword);
		builder.append("]");
		return builder.toString();
	}
	
	

}
