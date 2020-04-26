package cn.synway.app.bean;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.synway.app.base.SynApplication;

public class MyAreaDO {

    public static String SP_NAME_KEY = "down_map";
    private final boolean isHead;

    public String percentage;//下载进度
    private List<MyAreaDO> child;
    private boolean hasChild;
    private boolean isExpand;
    Set<String> set;
    /**
     * father : 浙江省
     * name : 杭州市
     */
    private String name;
    private String father;
    private int state;//1:准备中(获取下再地址)，2：开始下载(下载中) 3:出现错误 4：下载完成
    private String pYName;


    public MyAreaDO(String name, boolean b) {
        this.name = name;
        this.isHead = b;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setChild(List<MyAreaDO> citys) {
        this.child = citys;
    }

    public List<MyAreaDO> getChild() {
        return child;
    }

    public boolean hasChild() {
        return getChild() != null && getChild().size() > 0;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }


    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(int i) {
        this.state = i;
    }

    /**
     * @return 1:准备中(获取下再地址)，2：开始下载(下载中) 3:出现错误 4：下载完成,5 解压中 ， 6解压完成（已下载） ， 7,解压异常 8 写入本地，
     */
    public int getState() {
        if (state == 0) {
            if (set == null) {
                set = SynApplication.spUtils.getStringSet(SP_NAME_KEY, new HashSet<>());

            }

            if (set.contains(getPingYinName())) {
                this.state = 6;
            }
        }
        return state;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    /**
     * @return 下载进度
     */
    public String getPercentage() {
        return percentage;
    }

    public String getPingYinName() {
        if (this.pYName == null) {
            String pingYin = getPingYin(getName());
            this.pYName = pingYin;
        }
        return this.pYName;
    }

    public String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        String output = "";

        try {
            for (int i = 0; i < input.length; i++) {
                if (java.lang.Character.toString(input[i]).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(
                            input[i], format);
                    output += temp[0];
                }
                else
                    output += java.lang.Character.toString(input[i]);
            }
        }
        catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return null;
        }
        return output;
    }
}
