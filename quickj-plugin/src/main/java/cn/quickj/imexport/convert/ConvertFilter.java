package cn.quickj.imexport.convert;

import cn.quickj.hibernate.HibernateTemplate;


/**
 * 当将数据从xml或者csv导入的时候，在实体Bean被创建前
 * 或者创建后可以通过此filter来对实体Bean进行处理
 * 
 * @author lbj
 *
 */
public interface ConvertFilter {
	/**
	 * 
	 * @param ht
	 * @param o
	 * @return 如果不需要导入Service进行Hibernate的保存操作，则返回false,否则返回true。
	 */
	public boolean save(HibernateTemplate ht,Object o,ImportContext importContext);
}
