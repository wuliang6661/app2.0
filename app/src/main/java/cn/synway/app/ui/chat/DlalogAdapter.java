package cn.synway.app.ui.chat;

import android.widget.CheckBox;

import java.util.List;

import cn.synway.app.R;
import cn.synway.app.bean.MsgSelectFcBo;
import cn.synway.app.widget.lgrecycleadapter.LGRecycleViewAdapter;
import cn.synway.app.widget.lgrecycleadapter.LGViewHolder;

/**
 * author : wuliang
 * e-mail : wuliang6661@163.com
 * date   : 2019/8/115:11
 * desc   :   弹窗列表
 * version: 1.0
 */
public class DlalogAdapter extends LGRecycleViewAdapter<MsgSelectFcBo> {


    public DlalogAdapter(List<MsgSelectFcBo> dataList) {
        super(dataList);
    }


    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_app;
    }

    @Override
    public void convert(LGViewHolder holder, MsgSelectFcBo companysBean, int position) {
        holder.setText(R.id.complny_name, companysBean.getName());
    }
}
