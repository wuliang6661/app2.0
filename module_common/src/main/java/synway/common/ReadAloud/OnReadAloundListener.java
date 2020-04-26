package synway.common.ReadAloud;


/**
 * Created by 钱园超 on 2018/7/10.
 */
public interface OnReadAloundListener {


    /**
     * 准备开始朗读
     *
     * @return true表示准备成功, 如果这个过程没有被中断, 接下来将触发{@link #onStartRead(ReadAloudObj)}.<br>
     * false表示准备失败,则直接结束这条朗读.
     */
    boolean onPrepare(ReadAloudObj readAloudObj);

    /**
     * 朗读开始
     */
    void onStartRead(ReadAloudObj readAloudObj);

    /**
     * 朗读结束
     */
    void onFinishRead(ReadAloudObj readAloudObj);
}
