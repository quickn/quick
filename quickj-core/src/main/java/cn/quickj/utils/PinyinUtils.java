package cn.quickj.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.log4j.lf5.util.ResourceUtils;


/**
 * 有关拼音的工具包。
 * @author Administrator
 *
 */
public class PinyinUtils {
	static private String [] pinyin;
	static public void Initialize(){
		try {
			InputStream is = PinyinUtils.class.getResourceAsStream("/cn/quickj/utils/py.txt");
			BufferedReader br=new BufferedReader(new InputStreamReader(is));   
			String pyTxt = br.readLine();
			pinyin = pyTxt.split(",");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getPinyinCode(String text,boolean onlyFirst){
		if(pinyin==null)
			Initialize();
		StringBuffer result = new StringBuffer();
		try {
			byte []buffer = text.getBytes("GBK");
			for (int i = 0; i < buffer.length;) {
				//只处理汉字部分，英文字符忽略掉。
				if((buffer[i] &0x80)!=0){
					long high = (256+buffer[i])- 0x81;
					long low =(( buffer[i+1]<0)?(256+buffer[i+1]): buffer[i+1]) - 0x40;
					long offset = (high<<8)+low-(high*0x40);
					if(offset>0 && offset<pinyin.length){
						if(onlyFirst)
							result.append(pinyin[(int)offset].charAt(0));
						else
							result.append(pinyin[(int)offset]);
					}
					i+=2;
				}else
					i++;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	public static void main(String[] args) throws Exception {
		System.out.println(PinyinUtils.getPinyinCode("中华人们共和国,朱镕基",true));
		System.out.println(PinyinUtils.getPinyinCode("中华人们共和国,朱镕基", false));
	}

}
