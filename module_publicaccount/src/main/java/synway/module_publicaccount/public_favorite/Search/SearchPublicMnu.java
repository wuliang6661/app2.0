package synway.module_publicaccount.public_favorite.Search;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import qyc.library.tool.main_thread.MainThread;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicMenu;
import synway.module_publicaccount.public_chat.Obj_Menu;


/**
 * Created by zjw on 2016/9/27.
 * 搜索公众号
 *  * 作用域:根据输入搜索公众号
 * 输入:待搜索字符或字母
 * 输出:公众号
 * 生命周期：开始 停止
 */
public class SearchPublicMnu {
    private ArrayList<Obj_Menu> menulist;
    private ArrayList<Obj_Menu> publicResultList;
    private String keyWord = "";
    private OnSearchMenuListen lsn = null;

    public void setOnSearchPublicAccount(OnSearchMenuListen lsn){
        this.lsn = lsn;
    }
    public void start(String keyWord){
        this.keyWord = keyWord;
        this.menulist = new ArrayList<>();
        new Thread(mRunnable).start();
    }
    public void stop(){
        this.lsn = null;
    }
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //public模块已加载的情况
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
                initContactUserList:
                {
                    ArrayList<String> arrayList = new ArrayList<String>();
                    String col = "a." + Table_PublicMenu.PAM_menuGUID + " as MenuID," +
                            "a."+Table_PublicMenu.PAM_menuName +" as Name, "
                            + "a."+Table_PublicMenu.PAM_menuPicId+", "
                           +"a."+Table_PublicMenu.PAM_menuType +", "
                           +"a."+Table_PublicMenu.PAM_menuFather+", "
                           +"a."+Table_PublicMenu.PAM_ID+", "
                            +"a."+Table_PublicMenu.PAM_menuUrl+","
                           + "b."+Table_PublicAccount.FC_NAME+" as publicname"+","
                            + "a."+Table_PublicMenu.PAM_menuUrlType+","
                            +"a."+Table_PublicMenu.PAM_menuKey;
                    String sql = "select " + col + " from " + Table_PublicMenu._TABLE_NAME+" a left join "+Table_PublicAccount._TABLE_NAME+
                            " b on "+"b."+Table_PublicAccount.FC_ID+"="+
                          "a."+Table_PublicMenu.PAM_ID;
                    Log.i("testy","得到的查询语句+："+sql);
                    Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(sql,
                            null);
                    while (cursor.moveToNext()) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(cursor.getString(cursor.getColumnIndex("MenuID")));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex("Name")));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuPicId)));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuType)));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuFather)));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_ID)));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrl)));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex("publicname")));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuKey)));
                        sb.append("|");
                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicMenu.PAM_menuUrlType)));
                        arrayList.add(sb.toString());
                    }
                    if (arrayList.size() <= 0) {
                        break initContactUserList;
                    }
                    Obj_Menu obj_menu = null;
                    for (int k = 0; k < arrayList.size(); k++) {
                        String s = arrayList.get(k);
                        String[] sList = s.split("\\|", -1);
                        obj_menu = new Obj_Menu();
                        obj_menu.menuGUID = sList[0];
                        try {
                            obj_menu.menuName = sList[1];
                            obj_menu.menuPicUrl=sList[2];
                            obj_menu.menuType= Integer.parseInt(sList[3]);
                            obj_menu.menuFather=sList[4];
                            obj_menu.ID=sList[5];
                            obj_menu.menuUrl=sList[6];
                            obj_menu.Name=sList[7];
                            obj_menu.menuKey=sList[8];
                            obj_menu.menuUrlType = Integer.parseInt(sList[9]);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        obj_menu.namePinYin = PinYinDeal
                                .getPinYin2(obj_menu.menuName);
                        menulist.add(obj_menu);
                    }
                }
               menulist=getMenulist(menulist);
                publicResultList = new ArrayList<>();
                for (Obj_Menu s : menulist) {
                    // 查名字
                    if (s.menuName.contains(keyWord)) {
                        publicResultList.add(s);
                    }
                }
                for (Obj_Menu s : menulist) {
                    // 查拼音，先判断首字母，列出
                    if (s.namePinYin.charAt(0) == keyWord.charAt(0)) {
                        if (s.namePinYin.contains(keyWord)) {
                            boolean isExit = false;
                            for (Obj_Menu obj : publicResultList) {
                                if (obj.ID.equals(s.ID)) isExit = true;
                            }
                            if (!isExit) {
                                s.byPinYin = true;
                                publicResultList.add(s);
                            }
                        }
                    }
                }
                for (Obj_Menu s : menulist) {
                    // 再查剩余拼音
                    if (s.namePinYin.charAt(0) != keyWord.charAt(0)
                            && s.namePinYin.contains(keyWord)) {
                        boolean isExit = false;
                        for (Obj_Menu obj : publicResultList) {
                            if (obj.menuGUID.equals(s.menuGUID)) isExit = true;
                        }
                        if (!isExit) {
                            s.byPinYin = true;
                            publicResultList.add(s);
                        }

                    }
                }
                MainThread.joinMainThread(new MainThread.Runnable_MainThread() {
                    @Override
                    public void run() {
                        if (null != lsn) {
                            lsn.onResult(publicResultList);
                        }
                    }
                });

            }
    };

    /****
     * 判斷需要顯示的菜單
     * 有二级菜单的一级菜单不显示   其余类型的一级菜单显示（TYPE为0）
     *
     * @param menulist
     * @return obj_menus
     */
    public  ArrayList<Obj_Menu> getMenulist(ArrayList<Obj_Menu> menulist){
        ArrayList<Obj_Menu> obj_menus=new ArrayList<>();
        for(int i=0;i<menulist.size();i++){
            if(menulist.get(i).menuType!=0){
                obj_menus.add((menulist.get(i)));
            }
        }
        return  obj_menus;
    }
    public interface OnSearchMenuListen{
        void onResult(ArrayList<Obj_Menu> resultList);
        void onError();
    }
}
