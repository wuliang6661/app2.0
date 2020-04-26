package synway.module_interface.config.userConfig;

import java.io.Serializable;

public class LoginUser  implements Serializable{


	private static final long serialVersionUID = 1L;
	/** 卡号 */
	//public String IMSI = null;
	/** 机身码 */
	//public String IMEI = null;

	/**ID,OPCode,返回的UserID*/
	public String ID = null;
	/** 姓名 */
	public String name = null;
	/** 用户名 */
	public String LoginCode = null;
	/** 密码 */
	public String LoginPass = null;
	/** 登陆用户的省份 */
	public String province = null;
	/** 登陆用户的地区 */
	public String area = null;
	/** 登陆用户的手机号 */
	public String telNumber=null;
	/** 人员角色 默认普通人员 ：1  管理员 ：2 */
	public int userRole = 1;
	/** 用户信息(Json的toString形式)*/
	public String userInfoJson = null;
	/** 用户机构*/
	public String userOragian = null;
    /** 用户机构Id*/
	public String userOragianId = null;

}
