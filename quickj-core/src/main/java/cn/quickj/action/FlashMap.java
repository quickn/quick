package cn.quickj.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cn.quickj.guice.SessionScoped;
/**
 * 用于实现类似于rails的Flash功能，放入Flash中的值每次只有在下一次Action才生效。
 * @author lbj
 * FIXME: Flash放到Application内部在均衡部署的时候会产生两次请求不在同一台服务器时候出现问题。
 * 放入Session的话则会造成较多的Session同步操作,目前放入Session实现。
 * @param <K>
 * @param <V>
 */
@SessionScoped
public class FlashMap<K extends Object,V extends Object> implements Map<K,V> {

	private HashSet<K>lastValue = new HashSet<K>();
	private ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
	private boolean onlyNow = false;
	public void clear() {
		map.clear();
		lastValue.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V put(K key, V value) {
		//更新一个key，则认为是当前Action的内容，应该予以保存
		//所以要清除上次lastValue中的值。
		lastValue.remove(key);
		return map.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		Set<? extends K> keys = t.keySet();
		for (K key : keys) {
			put(key,t.get(key));
		}
	}

	public V remove(Object key) {
		lastValue.remove(key);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}
	/**
	 * 保留一个指定的值到下一个Action中。
	 * @param key
	 */
	public void keep(K key){
		lastValue.remove(key);
	}
	public void keepAll(){
		lastValue.clear();
	}
	/**
	 * 指定Flash的值只在本次Action中有效。
	 */
	public void now(){
		onlyNow = true;
	}
	/**
	 * 供框架内部调用，用于删除上一次Action带过来的值，并调整这次的Action值。
	 */
	public void updateStatus(){
		if(onlyNow)//如果只有本次Action生效，则清除所有的内容。
			clear();
		else{
			for (K key : lastValue) {
				map.remove(key);
			}
			lastValue.clear();
			lastValue.addAll(map.keySet());			
		}
		onlyNow = false;
	}
}
