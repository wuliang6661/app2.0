package synway.common.watermaker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/6/714:25
 * desc   :
 * version: 1.0
 */
public class WaterText extends AppCompatTextView {

    public WaterText(Context context) {
        super(context);
    }

    public WaterText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(WaterMarkUtil.mWaterMarkDesc, type);
    }
}
