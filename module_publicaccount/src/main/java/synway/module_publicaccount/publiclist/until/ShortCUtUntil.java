package synway.module_publicaccount.publiclist.until;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import synway.module_publicaccount.R;
import synway.module_publicaccount.public_chat.PublicAccountChatActNormal;
import synway.module_publicaccount.until.DialogTool;
/**
 * Created by admin on 2017/8/25.
 * 快捷方式工具类
 */

public class ShortCUtUntil {
    public static void addShortcut(Activity context, String name, Bitmap bitmap, Intent actionIntent, boolean allowRepeat) {
        // ComponentName yourAlias = new ComponentName(context.getPackageName(), "synway.module_publicaccount.public_chat.PublicAccountChatAct");
        //
        // int flag = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        //
        // context.getPackageManager().setComponentEnabledSetting(yourAlias, flag, PackageManager.DONT_KILL_APP);
//        Intent intent =  new Intent(ACTION_QUICK_LAUNCH_SETTINGS);
//        context.startActivityForResult(intent,1);
//        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//        context.startActivity(intent);
        //设置快捷方式动作
        actionIntent.setAction("android.intent.action.MAIN");
        actionIntent.addCategory("android.intent.category.LAUNCHER");
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        Uri uri=getUriFromLauncher(context);
//        Boolean ifexit=hasShortcut(context,uri,name);
//        if(ifexit){
//            updateShortcutIcon(uri,context,name,actionIntent,bitmap);
//            return;
//        }
//        Log.i("testy","结果"+ifexit);
        if (DialogTool.dialog != null && DialogTool.dialog.isShowing()) {
            DialogTool.dialog.dismiss();
        }
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //是否允许重复创建
        shortcutintent.putExtra("duplicate", allowRepeat);
        //快捷方式的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //设置快捷方式图片
        if (bitmap != null) {
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        } else {
            Parcelable icon = Intent.ShortcutIconResource.fromContext(context.getApplicationContext(), R.drawable.publicaccountpic_default);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        }

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        //向系统发送广播
        context.sendBroadcast(shortcutintent);

    }

    public static void addShortcut2(Activity context, String name, Bitmap bitmap, Intent actionIntent, boolean allowRepeat,int type) {
        // ComponentName yourAlias = new ComponentName(context.getPackageName(), "synway.module_publicaccount.public_chat.PublicAccountChatAct");
        //
        // int flag = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        //
        // context.getPackageManager().setComponentEnabledSetting(yourAlias, flag, PackageManager.DONT_KILL_APP);
        //        Intent intent =  new Intent(ACTION_QUICK_LAUNCH_SETTINGS);
        //        context.startActivityForResult(intent,1);
        //        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        //        context.startActivity(intent);
        //设置快捷方式动作
        actionIntent.setAction("android.intent.action.MAIN");
        actionIntent.addCategory("android.intent.category.LAUNCHER");
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //        Uri uri=getUriFromLauncher(context);
        //        Boolean ifexit=hasShortcut(context,uri,name);
        //        if(ifexit){
        //            updateShortcutIcon(uri,context,name,actionIntent,bitmap);
        //            return;
        //        }
        //        Log.i("testy","结果"+ifexit);
        if (DialogTool.dialog != null && DialogTool.dialog.isShowing()) {
            DialogTool.dialog.dismiss();
        }
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //是否允许重复创建
        shortcutintent.putExtra("duplicate", allowRepeat);
        //快捷方式的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //设置快捷方式图片
        if (bitmap != null) {
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        } else {
            Parcelable icon;
            if (type == 0) {
                //公众号
                icon = Intent.ShortcutIconResource.fromContext(context.getApplicationContext(), R.drawable.publicaccountpic_default);
            } else {
                //公众号菜单
                icon = Intent.ShortcutIconResource.fromContext(context.getApplicationContext(), R.drawable.publicmenupic_default);
            }

            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        }

        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        //向系统发送广播
        context.sendBroadcast(shortcutintent);

    }
    public class InstallShortcutReceiver extends BroadcastReceiver {
        private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
        public void onReceive(Context context, Intent data) {
            if (!ACTION_INSTALL_SHORTCUT.equals(data.getAction())) {
                return;
            }
           Log.i("shortcut","添加结果");
        }
    }

    /**
     * 删除快捷方式
     */
    public static void deleteShortCut(Activity activity, String shortcutName) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
       /*改成以下方式能够成功删除，估计是删除和创建需要对应才能找到快捷方式并成功删除**/
        //调整启动zzw
        Intent intent = new Intent(activity, PublicAccountChatActNormal.class);
//        Intent intent = new Intent(activity.getApplication(), PublicAccountChatAct.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        activity.sendBroadcast(shortcut);
        Toast.makeText(activity.getApplicationContext(),"您已被取消该公众号授权，请联系管理员",Toast.LENGTH_SHORT).show();
    }
//    /**
//     * 更新桌面快捷方式图标，不一定所有图标都有效<br/>
//     * 如果快捷方式不存在，则不更新<br/>.
//     */
//    public static void updateShortcutIcon(Uri uri,Context context, String title, Intent intent,Bitmap bitmap) {
//            final ContentResolver cr = context.getContentResolver();
//             Cursor c = cr.query(uri, null, "title=?", new String[] { title }, null);
//            if (c != null && c.getCount() > 0) {
//                c.moveToFirst();
//                ContentValues cv=new ContentValues();
//                cv.put("icon", flattenBitmap(bitmap));
//                int i=cr.update(uri, cv, null,null);
//                context.getContentResolver().notifyChange(uri,null);//此处不能用uri2，是个坑
//            }else{
//                //
//            }
//            if (c != null && !c.isClosed()) {
//                c.close();
//            }
//
//    }
    /**
     * 判断是否存在快捷方式（升级版本）
     * */
    private static boolean hasShortcut(Context context,Uri url,String title) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(url, null, "title=?", new String[] { title }, null);
        if (cursor == null) {
            return false;
        }
        if (cursor.getCount()>0) {
            cursor.close();
            return true;
        }else {
            cursor.close();
            return false;
        }
    }
    private static Uri getUriFromLauncher(Context context) {
        StringBuilder uriStrBuilder = new StringBuilder();
        //为了速度考虑，这里我们先查找默认的 看是否能查找到 因为多数手机的rom还是用的默认的lanucher
        String authority = getAuthorityFromPermissionDefault(context);
        //如果找不到的话 就说明这个rom一定是用的其他的自定义的lanucher。那就拼一下 这个自定义的lanucher的permission再去查找一次
        if (authority == null || authority.trim().equals("")) {
            authority = getAuthorityFromPermission(context,getCurrentLanucherPackageName(context) + ".permission.READ_SETTINGS");
        }
        uriStrBuilder.append("content://");
        //如果连上面的方法都查找不到这个authority的话 那下面的方法 就肯定查找到了 但是很少有情况会是如下这种
        //多数都是else里面的逻辑
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                uriStrBuilder.append("com.android.launcher.settings");
            } else if (sdkInt < 19) {// Android 4.4以下
                uriStrBuilder.append("com.android.launcher2.settings");
            } else {// 4.4以及以上
                uriStrBuilder.append("com.android.launcher3.settings");
            }
        } else {
            uriStrBuilder.append(authority);

        }
        uriStrBuilder.append("/favorites?notify=true");
        return Uri.parse(uriStrBuilder.toString());
    }
    private static String getCurrentLanucherPackageName(Context context)

    {
        //这个intent很好理解 就是启动lanucher的intent
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        //getPackageManager().resolveActivity 这个函数就是查询是否有符合条件的activity的
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        //为避免空指针 我们要判定下空，虽然你我都知道这种情况不会发生
        if (res == null || res.activityInfo == null) {
            return "";
        }
        return res.activityInfo.packageName;
    }


    private static String getAuthorityFromPermission(Context context, String permission) {
        //返回安装的app的 provider的信息
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        //遍历获取到的安装包的信息
        for (PackageInfo pack : packs) {
            //每个安装包提供的provider 都在这个数组里面
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                //遍历每个provider 看需要的权限是否与我们传进来的权限参数相等
                for (ProviderInfo providerInfo : providers) {
                    if (permission.equals(providerInfo.readPermission) || permission.equals(providerInfo.writePermission)) {
                        return providerInfo.authority;
                    }

                }

            }

        }
        return "";
    }

    private static String getAuthorityFromPermissionDefault(Context context) {

        return getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");

    }
    private static byte[] flattenBitmap(Bitmap bitmap) {
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
