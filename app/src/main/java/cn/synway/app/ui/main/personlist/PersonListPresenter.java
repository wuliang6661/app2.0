package cn.synway.app.ui.main.personlist;

import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.synway.app.api.HttpResultSubscriber;
import cn.synway.app.api.HttpServerImpl;
import cn.synway.app.bean.PersonInPsListBO;
import cn.synway.app.bean.PersonInPsListBO.OrganAndUserInfoBO;
import cn.synway.app.bean.PersonInPsListBO.TagList;
import cn.synway.app.bean.request.PersonRequest;
import cn.synway.app.mvp.BasePresenterImpl;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class PersonListPresenter extends BasePresenterImpl<PersonListContract.View> implements PersonListContract.Presenter {

    private HashMap<String, List<OrganAndUserInfoBO>> localHistoryData = new HashMap();//记录历史数据
    private HashMap<String, List<TagList>> localHistoryTagData = new HashMap();//记录历史数据 Tags
    private HashMap<String, Integer> charIndexMap;//记录字符位置
    private HashMap<String, HashMap<String, Integer>> HistoryCharIndexMap = new HashMap();


    @Override
    public void getPeopleList(String fatherId, boolean fresh) {
        if (fresh) {
            //清除缓存,重新请求
            localHistoryData.clear();
            localHistoryTagData.clear();
            HistoryCharIndexMap.clear();
            if (charIndexMap != null)
                charIndexMap.clear();
        }
        else {
            //读取缓存
            List<OrganAndUserInfoBO> organAndUserInfoBOS = localHistoryData.get(fatherId);
            List<TagList> tags = localHistoryTagData.get(fatherId);
            HashMap<String, Integer> charIndexMap2 = HistoryCharIndexMap.get(fatherId);
            if (tags != null && organAndUserInfoBOS != null && mView != null && charIndexMap2 != null) {
                mView.getPeoleList(organAndUserInfoBOS, tags);
                this.charIndexMap = charIndexMap2;
                return;
            }
        }


        PersonRequest request = new PersonRequest();
        request.fatherId = fatherId;
        HttpServerImpl.getPeopleList(request).subscribe(new HttpResultSubscriber<PersonInPsListBO>() {
            @Override

            public void onSuccess(PersonInPsListBO person) {
                if (mView != null) {
                    //处理数据
                    if ("0".equals(fatherId)) {
                        OrganAndUserInfoBO organAndUserInfoBO = person.getOrganList().get(0);
                        organAndUserInfoBO.setName("(我的)" + organAndUserInfoBO.getName());
                    }
                    //排序
                    sortChinesss(person);
                    //获取全部数据
                    List<OrganAndUserInfoBO> list = person.getList();
                    //缓存
                    localHistoryData.put(fatherId, list);
                    HistoryCharIndexMap.put(fatherId, PersonListPresenter.this.charIndexMap);
                    //添加默认的tag
                    person.getFatherOrganList().add(0, new PersonInPsListBO().new TagList("0", "通讯录"));
                    List<TagList> tagList = person.getFatherOrganList();
                    localHistoryTagData.put(fatherId, tagList);
                    //通知界面
                    mView.getPeoleList(list, tagList);
                }
            }

            @Override
            public void onFiled(String message) {
                if(mView!=null){
                    mView.onFaild();
                }
                ToastUtils.showShort(message);
            }
        });

    }

    @Override
    public void getGroupPerson(String id) {
        getPeopleList(id, false);
    }


    /**
     * 获取当前人员机构列表 与 字母 对应的索引
     *
     * @param letter
     * @return
     */
    @Override
    public Integer getCharPosition(String letter) {
        if (charIndexMap == null) return null;
        Integer integer = charIndexMap.get(letter);

        return integer;
    }

    /**
     * 排序
     *
     * @param person
     */
    private void sortChinesss(PersonInPsListBO person) {

        this.charIndexMap=  new HashMap<String, Integer>();
        List<OrganAndUserInfoBO> userInfoList = person.getUserInfoList();
        int organSize = person.getOrganList().size();
        Collections.sort(userInfoList, new Comparator<OrganAndUserInfoBO>() {
            @Override
            public int compare(OrganAndUserInfoBO o1, OrganAndUserInfoBO o2) {

                char chart1 = getFirstChar(o1);
                o1.setFirstChar(String.valueOf(chart1));
                char chart2 = getFirstChar(o2);
                o2.setFirstChar(String.valueOf(chart2));
                int i = chart1 - chart2;
                //LogUtil.e(chart1 + ",chart2=" + chart2);
                return i;
            }
        });
        if (userInfoList.size() == 1) {
            OrganAndUserInfoBO userInfoBO = userInfoList.get(0);
            char chart1 = getFirstChar(userInfoBO);
            userInfoBO.setFirstChar(String.valueOf(chart1));
        }

        String charIndex = "";
        ArrayList<OrganAndUserInfoBO> list = new ArrayList<>();
        int letterNum = 0;
        for (int i = 0; i < userInfoList.size(); i++) {
            OrganAndUserInfoBO organAndUserInfoBO = userInfoList.get(i);
            String firstChar = organAndUserInfoBO.getFirstChar();

            if (!charIndex.equals(firstChar)) {
                list.add(new PersonInPsListBO().new OrganAndUserInfoBO(firstChar, true));
                charIndex = firstChar;
                this.charIndexMap.put(firstChar, (organSize + i + letterNum));
                letterNum++;
            }

            list.add(organAndUserInfoBO);

        }
        person.setUserInfoList(list);
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public char getFirstChar(OrganAndUserInfoBO inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.getUserName().trim().toCharArray();// 把字符串转化成字符数组

        char output = "#".charAt(0);

        try {
            char c = input[0];
            //u4E00是unicode编码，判断是不是中文
            if (java.lang.Character.toString(c).matches(
                    "[\\u4E00-\\u9FA5]+")) {
                // 将汉语拼音的全拼存到temp数组
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                        c, format);
                // 取拼音的第一个读音
                output = temp[0].toUpperCase().charAt(0);
            }
            else if (c >= 'a' && c <= 'z') {
                output = String.valueOf(c).toUpperCase().charAt(0);
            }
            else if (c >= 'A' && c <= 'Z') {
                output = c;
            }

        }
        catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return output;
    }


}

