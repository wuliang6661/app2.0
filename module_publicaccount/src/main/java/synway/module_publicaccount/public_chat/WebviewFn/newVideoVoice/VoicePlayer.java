package synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice;


/**
 * <p>
 * 播放器的生命周期为:对象初始化-->destory() 销毁后应该立即停止播放并销毁清理和重置所有资源
 * <p>
 * 一条VoiceReportObj可以理解为一个播放任务,一个播放任务的生命周期为: startPlay()-->
 * Prepareing-->Playing(Resuming)-->isComplete-->stopPlay()
 * 播放生命周期结束后,要清理播放任务相关的资源.
 */
public abstract class VoicePlayer {


    /**
     * 销毁播放器
     */
    public abstract void destory();

    /**
     * 开始播放
     */
    public abstract void startPlay(VoiceReportObj reportItemObj);

    /**
     * 继续播放
     */
    public abstract void resumePlay();

    /**
     * 暂停播放
     */
    public abstract void pausePlay();

    /**
     * 停止播放
     */
    public abstract void stopPlay();

    /**
     * 设置进度
     */
    public abstract void setProgress(int progress);

    /**
     * 获取当前正在播放的VoiceReport,如果没有播放任务,则返回NULL
     */
    public abstract VoiceReportObj getRunningReport();

    /**
     * 是否正在准备阶段
     */
    public abstract boolean isPrepareing();

    /**
     * 是否正在放音
     */
    public abstract boolean isPlaying();

    /**
     * 是否已经播放完成
     */
    public abstract boolean isComplete();

    /**
     * 获取语音播放时间(秒)
     */
    public abstract int getAllTime();

    public abstract void setOnPlayerListen(OnPlayerStateListen onPlayerStateListen);

    public abstract void delPlayerListen();


}
