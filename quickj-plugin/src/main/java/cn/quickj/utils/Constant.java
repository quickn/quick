package cn.quickj.utils;

public class Constant {

	// 页面发布历史状态 1:待测试,2:待审核,3:已发布,4:已取消
	public static final Integer PUBLISH_STATUS_TEST = 1;// 待测试
	public static final Integer PUBLISH_STATUS_AUDIT = 2;// 待审核
	public static final Integer PUBLISH_STATUS_PUBLISH = 3;// 待发布
	public static final Integer PUBLISH_STATUS_FINISH = 4;// 已经发布
	public static final Integer PUBLISH_STATUS_CANCEL = 5;// 已取消
	// 杂志专题是否删除 0:正常,1:已删除
	public static final Integer SPECIAL_STATUS_NORMAL = 0;
	public static final Integer SPECIAL_STATUS_DELETE = 1;
	//专题字典名称
	public static final String SPECIAL_TYPE="专题类型";
	//初始账户用户名
	public static final String THEINIT_NAME="admin";	
}
