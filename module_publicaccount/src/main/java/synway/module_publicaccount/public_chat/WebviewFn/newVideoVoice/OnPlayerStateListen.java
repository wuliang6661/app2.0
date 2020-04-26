package synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice;

/**
 * Created by 钱园超 on 2016/12/14.
 */

public interface OnPlayerStateListen {
    /**
     * 播放发生错误,在整个运行过程中都可能发生,当它触发,播放器已经重置回初始状态,你只要操作View即可
     *
     * @param error
     *            错误信息,如果等于null表示没有错误信息
     */
    void onPlayerError(String error, VoiceReportObj voiceReportObj);
    /**
     * 播放器开始播放声音了
     *
     * @param allTime
     *            一共有多少秒
     */
    void onPlayerStart(int allTime);

    /**
     * 播放器的播放进度
     *
     * @param progress
     *            百分比
     * @param playTime
     *            播放了几秒
     */
    void onPlayerProgress(int progress, int playTime);

    /**
     * <p>
     * 播放器放完这段声音了,处于暂停状态,并没有释放资源.
     * <p>
     * 播放完不代表运行完,这表示isComplete=true,你可以通过resume重新播放. 一旦start开始,只有stop才表示运行完毕.
     * <p>
     * 播放器放完这段声音时,不一定进度到了100%,也可能是99%,它最后一小段数据有可能丢弃.所以你最好自己通过控件去设置成100%.是的,
     * 确实不太负责任,我也觉得很生气.
     *
     * */
    void onPlayerComplete();

    /** 播放器下载进度 */
    void onPlayerLoading(int progress);
}
