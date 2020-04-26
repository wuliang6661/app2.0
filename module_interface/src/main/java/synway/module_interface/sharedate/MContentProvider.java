package synway.module_interface.sharedate;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * {@link SharedPreferencesQ}的ContentProvider
 * Created by 钱园超 on 2017/9/4.
 */

public class MContentProvider extends ContentProvider {

    /**
     * ContentProvider地址
     * <br><br>
     * ContentProvider的URI构成:host/path<br>
     * host俗称地址,一个ContentProvider就一个地址,需要在xml中注册,向外公开.<br>
     * path俗称路径,用于细分host下的具体业务.path可以有多层,就是好几个斜杠.path也可以携带数据,即path一部分为业务路径,一部分为业务数据<br>
     * <br><br>
     * 当path携带数据时,业务路径和数据之间需要通过内部约定来区分,无法仅通过uri来区分.这是因为path只有斜杠一个分隔符.<br>
     * 例如path的内容为 aaa/bbb/ccc 仅通过uri无法判断该path属于以下哪一种情况:<br>
     * 1.业务路径=aaa/bbb/ccc   数据=空<br>
     * 2.业务路径=aaa/bbb       数据=ccc<br>
     * 因此在开发中需要内部约定,并通过match进行匹配:<br>
     * 1.对一个业务定义好固定格式的path和data.<br>
     * 2.根据path的格式和data的格式,设定match的匹配规则,对于不符合格式的进行过滤.解析时根据定义好的格式进行解析.
     * 3.data最好在path后面,以便data具有良好的扩展性.<br>
     * 我们的内部约定是:业务路径/contextName/连接状态
     * <br><br>
     * Tip:Uri不仅用于ContentProvider,在其他应用场景中,还有更多的组合技巧.
     */
    public static Uri URI_HOST = Uri.parse("content://synway.osc.sharedate.forself");
    /**
     * 业务:read
     */
    public static String BUSINESS_READ = "read";
    /**
     * 业务:insert
     */
    public static String BUSINESS_INSERT_OR_UPDATE = "insertOrUpdate";
    /**
     * 业务:delete
     */
    public static String BUSINESS_DELETE = "delete";
    /**
     * 返回:错误
     */
    public static String BACK_ERROR = "error";
    /**
     * 返回:结果
     */
    public static String BACK_RESULT = "result";

    private UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DB db;

    @Override
    public boolean onCreate() {
        String uriHost = URI_HOST.getHost();
        //参数:host(不包含content://),path,id(通过int型的id来判断用户传的业务是哪个)
        //path匹配规则:
        //1.最前面的斜杠不需要,即host/path之间的这个斜杠不需要.
        //2./#表示斜杠后面带数字
        //3./*表示后面带任意字符(>0个),如同一个业务路径后面既有固定字符,又有任意字符,那么addURI时,任意字符要放在固定字符的后面,否则固定字符会永远匹配不到.
        matcher.addURI(uriHost, BUSINESS_READ, 1);
        matcher.addURI(uriHost, BUSINESS_INSERT_OR_UPDATE + "/*/*", 2);//插入或更新  /name/guid
        matcher.addURI(uriHost, BUSINESS_DELETE, 3);//删除
        matcher.addURI(uriHost, BACK_ERROR, 4);
        matcher.addURI(uriHost, BACK_RESULT, 5);
        db = new DB(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int result = matcher.match(uri);
        if (result == 1) {
            return db.getReadableDatabase().query("BaseTable", projection, selection, selectionArgs, null, null, null);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int result = matcher.match(uri);

        if (result == 2) {
            long replaceResult;
            try {
                replaceResult = db.getWritableDatabase().replace("BaseTable", null, values);
                //通知观察者,必须显式的调用notifyChange,才能被观察者获知.
                //第一个参数为具体的URI(基本URI+业务类型),这里光反馈基本URI也可以,这样不管哪个业务的观察者,只要是该基本URI的业务,都能收到.
                //但这样观察者那头就要额外进行业务区分,因此最好反馈具体的URI.而且如果观察者需要监听基本URI下的所有具体URI,通过参数一样可以收到这里的反馈.
                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(Uri.parse("content:" + uri.getSchemeSpecificPart()), null);
                }

                return Uri.parse(URI_HOST + "/" + BACK_RESULT + "/" + replaceResult);
            } catch (Exception e) {
                return Uri.parse(URI_HOST + "/" + BACK_ERROR + "/" + e.toString());
            }
        } else {
            return Uri.parse(URI_HOST + "/" + BACK_ERROR + "/不能识别的uri格式");
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = matcher.match(uri);
        if (result == 3) {
            return db.getWritableDatabase().delete("BaseTable", selection, selectionArgs);
        } else {
            return -1;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return -1;
    }
}
