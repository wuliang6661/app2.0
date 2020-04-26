package synway.module_stack.utils;

import android.content.ClipboardManager;
import android.content.Context;

public class CopyUtils {

    public static void CopyToClipboard(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(text); // 复制
    }

}
