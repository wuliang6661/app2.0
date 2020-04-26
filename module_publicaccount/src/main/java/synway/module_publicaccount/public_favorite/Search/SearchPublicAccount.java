package synway.module_publicaccount.public_favorite.Search;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import qyc.library.tool.main_thread.MainThread;
import synway.module_interface.db.SQLite;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.db.table_util.Table_PublicAccount;
import synway.module_publicaccount.db.table_util.Table_PublicAccountFocusedList;
import synway.module_publicaccount.db.table_util.Table_PublicConfigMsg;
import synway.module_publicaccount.public_list_new.adapter.UrlObj;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;


/**
 * Created by zjw on 2016/9/27.
 * 搜索公众号
 *  * 作用域:根据输入搜索公众号
 * 输入:待搜索字符或字母
 * 输出:公众号
 * 生命周期：开始 停止
 */
public class SearchPublicAccount {
    private ArrayList<Obj_PublicAccount> publicList;
    private ArrayList<Obj_PublicAccount> publicResultList;
    private String keyWord = "";
    private OnSearchPublicAccountListen lsn = null;

    public void setOnSearchPublicAccount(OnSearchPublicAccountListen lsn){
        this.lsn = lsn;
    }
    public void start(String keyWord){
        this.keyWord = keyWord;
        this.publicList = new ArrayList<>();
        new Thread(mRunnable).start();
    }
    public void stop(){
        this.lsn = null;
    }
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //public模块已加载的情况
//            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
//                initContactUserList:
//                {
//                    ArrayList<String> arrayList = new ArrayList<String>();
//                    String col = Table_PublicAccount._TABLE_NAME + "." + Table_PublicAccount.FC_ID + " as ID," + Table_PublicAccount.FC_NAME+","
//                   + Table_PublicAccount.FC_MOBILEPIC;
//                    String table = Table_PublicAccount._TABLE_NAME;
////                    String col = "PublicAccountList" + "." + "ID" + " as ID," + "Name";
////                    String table = "PublicAccountList";
//                    String sql = "select " + col + " from " + table;
//                    Cursor cursor = sqliteHelp.getWritableDatabase().rawQuery(sql,
//                            null);
//                    while (cursor.moveToNext()) {
//                        StringBuffer sb = new StringBuffer();
//                        sb.append(cursor.getString(cursor.getColumnIndex("ID")));
//                        sb.append("|");
//                        sb.append(cursor.getString(cursor.getColumnIndex("Name")));
//                        sb.append("|");
//                        sb.append(cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_MOBILEPIC)));
//                        arrayList.add(sb.toString());
//                    }
//                    if (arrayList.size() <= 0) {
//                        break initContactUserList;
//                    }
//                    Obj_PublicAccount objPublicAccount = null;
//                    for (int k = 0; k < arrayList.size(); k++) {
//                        String s = arrayList.get(k);
//                        String[] sList = s.split("\\|", -1);
//                        objPublicAccount = new Obj_PublicAccount();
//                        objPublicAccount.ID = sList[0];
//                        try {
//                            objPublicAccount.name = sList[1];
//                            objPublicAccount.fc_mobilepic=sList[2];
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                        objPublicAccount.namePinYin = PinYinDeal
//                                .getPinYin2(objPublicAccount.name);
//                        publicList.add(objPublicAccount);
//                    }
//                }
            SQLiteOpenHelper sqliteHelp = Main.instance().moduleHandle.getSQLiteHelp();
            SQLiteDatabase db = sqliteHelp.getReadableDatabase();
            String selectSql = "select * from " + Table_PublicAccount._TABLE_NAME;
            Cursor cursor = db.rawQuery(selectSql, null);
            while (cursor.moveToNext()) {
                Obj_PublicAccount item = new Obj_PublicAccount();
                item.ID = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_ID));
                item.name = cursor.getString(cursor.getColumnIndex(Table_PublicAccount.FC_NAME));
                if (cursor.isNull(cursor.getColumnIndex(Table_PublicAccount.FC_TYPE))) {
                    //兼容之前的数据,若是升级后此列没有值,则默认为0
                    item.type = 0;
                } else {
                    String typeStr = cursor.getString(cursor.getColumnIndex(Table_PublicAccount
                            .FC_TYPE));
                    item.type = Integer.parseInt(typeStr);
                    if (item.type == 1) {
                        //桌面应用暂不支持
                    } else if (item.type == 2) {
                        //
                        String strs[];
                        String columns[] = new String[]{Table_PublicConfigMsg.PAC_ID,
                                Table_PublicConfigMsg.PAM_PublicUrlType, Table_PublicConfigMsg
                                .PAM_PublicUrl, Table_PublicConfigMsg.PAC_SourceUrl,
                                Table_PublicConfigMsg.PAM_ISSHowTitle};
                        try {
                            strs = SQLite.query(sqliteHelp.getReadableDatabase(),
                                    Table_PublicConfigMsg._TABLE_NAME, columns, "|", null, null,
                                    null);
                        } catch (Exception e) {
                            return ;
                        }
                        for (int i = 0; i < strs.length; i++) {
                            String sList[] = strs[i].split("\\|", -1);
                            if (sList[0].equals(item.ID)) {
                                UrlObj urlObj = new UrlObj();
                                urlObj.urlType = Integer.parseInt(sList[1]);
                                urlObj.publicUrl = sList[2];
                                urlObj.urlParam = sList[3];
                                urlObj.isShowTitle = Integer.parseInt(sList[4]);
                                item.urlObj = urlObj;
                                break;
                            }
                        }
                    }
                }
                item.fc_mobilepic = cursor.getString(cursor.getColumnIndex(Table_PublicAccount
                        .FC_MOBILEPIC));
                item.fatherGroupID = cursor.getString(cursor.getColumnIndex(Table_PublicAccount
                        .PAM_FatherGroupID));
                item.fatherGroupName = cursor.getString(cursor.getColumnIndex(Table_PublicAccount
                        .PAM_FatherGroupName));
                item.namePinYin = PinYinDeal
                        .getPinYin2(item.name);
                publicList.add(item);
            }
                publicResultList = new ArrayList<>();
                for (Obj_PublicAccount s : publicList) {
                    // 查名字
                    if (s.name.contains(keyWord)) {
                        publicResultList.add(s);
                    }
                }
                for (Obj_PublicAccount s : publicList) {
                    // 查拼音，先判断首字母，列出
                    if (s.namePinYin.charAt(0) == keyWord.charAt(0)) {
                        if (s.namePinYin.contains(keyWord)) {
                            boolean isExit = false;
                            for (Obj_PublicAccount obj : publicResultList) {
                                if (obj.ID.equals(s.ID)) isExit = true;
                            }
                            if (!isExit) {
                                s.byPinYin = true;
                                publicResultList.add(s);
                            }
                        }
                    }
                }
                for (Obj_PublicAccount s : publicList) {
                    // 再查剩余拼音
                    if (s.namePinYin.charAt(0) != keyWord.charAt(0)
                            && s.namePinYin.contains(keyWord)) {
                        boolean isExit = false;
                        for (Obj_PublicAccount obj : publicResultList) {
                            if (obj.ID.equals(s.ID)) isExit = true;
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
    public interface OnSearchPublicAccountListen{
        void onResult(ArrayList<Obj_PublicAccount> resultList);
        void onError();
    }
}
