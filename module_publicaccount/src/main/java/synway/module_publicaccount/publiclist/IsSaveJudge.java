package synway.module_publicaccount.publiclist;

import java.util.List;

import synway.module_publicaccount.public_favorite.PublicDBUtils;
import synway.module_publicaccount.public_favorite.obj.Obj_PublicAccount_Favorite;

/**
 * Created by admin on 2017/1/3.
 */

public class IsSaveJudge {
    /**
     * 判断是否加入了常用应用
     * @param publicAccountID
     * @return ture表示加入了，false则表示还没有加入
     */
    public static boolean isSavePublicFavorite(String publicAccountID) {
        boolean result = false;

        List<Obj_PublicAccount_Favorite> favorites = PublicDBUtils.read();
        if (favorites != null) {


            //表示常用应用个数为0,也返回false
            if (favorites.size() == 0) {
                result = false;
                //System.out.println("--->"+"常用应用表无数据");
            } else {
                for (int i = 0; i < favorites.size(); i++) {
                    if (publicAccountID.equals(favorites.get(i).ID) &&
                            "".equals(favorites.get(i).menuName)) {

                        result = true;
                        break;
                    }
                }
            }


        }else {
            System.out.println("--->"+"常用应用表 返回null");
        }

        return result;
    }
}
