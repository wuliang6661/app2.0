package synway.module_publicaccount.publiclist;

import java.util.Comparator;
import java.util.Locale;

public class SortAccountComparator implements Comparator<Obj_PublicAccount> {

	@Override
	public int compare(Obj_PublicAccount obj1, Obj_PublicAccount obj2) {
		char c1 = obj1.namePinYin.charAt(0);
		char c2 = obj2.namePinYin.charAt(0);
		if (c1 == c2) {
			return 0;
		}
		if (c1 == '#') {
			return -1;
		} else if (c2 == '#') {
			return 1;
		} else if ((is_A2Z(c1) && is_A2Z(c2)) || (is_a2z(c1) && is_a2z(c2))) {
			if (c1 > c2) {
				return 1;
			} else if (c1 < c2) {
				return -1;
			} else {
				return 0;
			}
		} else if (is_A2Z(c1)) {
			c1 = obj1.namePinYin.toLowerCase(Locale.CHINA).charAt(0);
			if (c1 == c2) {
				return -1;
			} else if (c1 > c2) {
				return 1;
			} else {
				return -1;
			}
		} else if (is_A2Z(c2)) {
			c2 = obj2.namePinYin.toLowerCase(Locale.CHINA).charAt(0);
			if (c1 == c2) {
				return 1;
			} else if (c1 > c2) {
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}

	private boolean is_A2Z(char c) {
        return c >= 'A' && c <= 'Z';
    }

	private boolean is_a2z(char c) {
        return c >= 'a' && c <= 'z';
    }

}