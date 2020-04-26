package synway.module_publicaccount.publiclist;

import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinYinDeal {

	private static final boolean isZiMu(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

	static final String[] getPinYin(char cc) {
		if (isZiMu(cc)) {
			return new String[] { String.valueOf(cc) };
		}

		String result[] = PinyinHelper.toHanyuPinyinStringArray(cc);
		if (null != result) {
			return result;
		}

		return new String[] { "#" };
	}
	
	static final String[] getPinYin(String c){
		if(null == c || c.length() == 0){
			return new String[] { "#" };
		}
		return getPinYin(c.charAt(0));
	}


	static final String getPinYin2(char cc) {
		if (isZiMu(cc)) {
			return String.valueOf(cc).toLowerCase(Locale.CHINA);
		}

		String result[] = PinyinHelper.toHanyuPinyinStringArray(cc);
		if (null != result) {
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < result.length; i++){
				builder.append(result[i].toLowerCase(Locale.CHINA));
			}
			return builder.toString();
		}

		return "#";
	}
	public static final String getPinYin2(String str){
		if(null == str || str.length() == 0){
			return "#" ;
		}
		return getPinYin2(str.charAt(0));
	}
	
	

}