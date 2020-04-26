package synway.module_publicaccount.public_chat.WebviewFn.newVideoVoice;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/*
 * 要让网络,异步播放等做到既稳定又操作无阻塞,关键在于定好一个播放源的生命周期.
 * 它什么时候属于开始,什么时候属于结束? 对外部来说就是  开始---->运行---->结束.
 * 这样一个简单的接口,中间无论多少异步操作都囊括在运行部分
 *"结束"来确保一切异步都清理干净:已经销毁  或  即将销毁并不可能再用  或  即将销毁并在"开始"时判定销毁是否彻底
 * 不干净则不允许"开始".
 * 这就不需要外部,比如UI进行配合约束,这样的封装才是逻辑清晰,运行稳定,性能高效的.
 * ==============
 * 在这个播放器里面,运行状态包括以下:  准备阶段  播放阶段  播放结束阶段
 * */

public class URLMediaPlayer extends VoicePlayer {
    // 界面传入的信息
    private VoiceReportObj reportItemObj;

    // 当前播放器是否正处于"播放完成"的状态
    private boolean isCompleting = false;

    // 本次播放时间
    private int allTime = 0;

    // 播放器是否正处于准备中,处于准备中则不允许操作播放器.如pause,result,start,stop之类的
    private boolean isPrepareing = false;

    // 播放器不向外反馈错误信息
    private boolean errorOut = true;

    // 播放器
    private MediaPlayer mediaPlayer = null;

    // 播放准备用的线程
    private Thread prepareThread = null;

    // 反馈播放状态的Timer
    private Timer progressWatchingTimer = null;
    private boolean isTimerRunning = false;

    // 观察者模式反馈的Handler
    private Handler notifHandler = new Handler();

    /** 不需要初始化,但要记得销毁 */
    public URLMediaPlayer() {
        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        this.mediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    /**
     * <p>
     * 销毁的干干净净
     * <p>
     * 不管它此刻在干什么,都可以直接去销毁.
     * */
    public void destory() {
        this.mediaPlayer.setOnBufferingUpdateListener(null);
        this.mediaPlayer.setOnCompletionListener(null);
        this.mediaPlayer.setOnPreparedListener(null);
        if (this.progressWatchingTimer != null) {
            this.progressWatchingTimer.cancel();
        }
        while (isTimerRunning) {
            // 等呀等呀等~~~~~~~~~~等到Timer线程结束,才能调用下一段.因为一旦release就必须重新初始化了,不初始化就崩给你看!
            // 好在这只是分分钟的事情
        }
        this.mediaPlayer.release();
    }

    /**
     * <p>
     * 播放一个新的报告,如果有报告尚未播放完毕,函数执行无效.
     * <p>
     * 一旦启动,便进入"运行"状态. 但真正放出声音的过程是异步的,它将在接口处反馈出来
     * <p>
     * 一个报告播放过程是 调用启动---->运行----->调用停止(或销毁)(或error),
     * 在运行过程中,无论是否已经放完,是否正在放音,都属于运行状态.
     */
    public void startPlay(VoiceReportObj reportItemObj) {
        if (this.reportItemObj != null) {
            return;
        }

        errorOut = true;// 如果出错播放器将向观察者反馈错误,它将在stop时被设为false
//        JSONObject json = null;
//        // 要流媒体服务转码,首先检查语音路径是否存在
//        boolean isMainCallExists = reportItemObj.voiceObj.mainCall != null
//                && !reportItemObj.voiceObj.mainCall.trim().equals("");
//        boolean isOhterCallExists = reportItemObj.voiceObj.otherCall != null
//                && !reportItemObj.voiceObj.otherCall.trim().equals("");
//
//        if (!isMainCallExists && !isOhterCallExists) {
//            // 两个都不存在
//            if (onStreamPlayerListen != null && errorOut) {
//                onStreamPlayerListen.onPlayerError("抱歉，这条语音报告没有“语音文件地址”因此无法播放，请尝试下一条。", reportItemObj);
//            }
//            return;
//        }
//        if (isMainCallExists && isOhterCallExists) {
//            // 两个都存在
//            json = getPostJson(reportItemObj.voiceObj.filePath, reportItemObj.voiceObj.mainCall,
//                    reportItemObj.voiceObj.otherCall);
//        } else if (isMainCallExists) {
//            // 存在主叫路径
//            json = getPostJson(reportItemObj.voiceObj.filePath, reportItemObj.voiceObj.mainCall, null);
//        } else if (isOhterCallExists) {
//            // 存在被叫路径
//            json = getPostJson(reportItemObj.voiceObj.filePath, null, reportItemObj.voiceObj.otherCall);
//        }

        // 这一步骤标志着播放器进入运行状态
        this.reportItemObj = reportItemObj;

        // 进入"准备阶段"
        isPrepareing = true;
        prepareThread = new Thread(new PrepareRunnable());
        prepareThread.start();
    }
    // 线程中准备播放的过程
    private class PrepareRunnable implements Runnable {

        @Override
        public void run() {

            String prepareError = null;
            prepare : {
//                JSONObject resultJson = getDecodeUrl(jsonStr);
//                final String[] result = HttpInThread.checkResult(resultJson);
//                if (result != null) {
//                    prepareError = "抱歉，请求解码失败。\n\n错误原因:\n" + result[0] + "\n" + result[1];
//                    break prepare;
//                }
//
//                String url = resultJson.optString("url");
//                if (url == null || "".equals(url)) {
//                    prepareError = "抱歉，请求解码失败。服务器没有返回解码后的URL。";
//                    break prepare;
//                }
//
//                // 替换IP地址和端口
//                NetConfig netConfig = Main.instance().moduleHandle.getAppConfig().net;
//                String ip_port = netConfig.streamIP + ":" + netConfig.streamPort;
//                String newUrl = replaceUrl(url, ip_port);
//                if (newUrl == null) {
//                    prepareError = "抱歉，请求解码失败。服务器返回的URL格式有误。\n\nURL=\n" + url;
//                    break prepare;
//                }
//                url = newUrl;

                // 测试
//                String url = "http://172.16.1.139:8081/SynWMS/sound/12345.mp3";
                    String  url=reportItemObj.mediaujrl;
                // "http://192.168.0.101:9004/synvoice/20050224/finish_byjava.pcm";
                // url = "http://192.168.0.101:9004/synvoice/20050224/2029.pcm";
                // 测试
                try {
                    mediaPlayer.setDataSource(url);
                }
                catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    // 异常后应该重新初始化,如果执行其他函数如start,stop等会导致播放器卡死.因此要退出运行状态
                    prepareError = "抱歉，播放器无法识别该语音地址。\n\n\nURL=\n" + url + "\n\n错误代码:\n" + e.toString();
                    break prepare;
                }

                try {
                    mediaPlayer.prepare();
                }
                catch (IllegalStateException | IOException e) {
                    // 异常后应该重新初始化,如果执行其他函数如start,stop等会导致播放器卡死.因此要退出运行状态
                    if (e instanceof IllegalStateException) {
                        prepareError = "抱歉，播放器操作异常。请尝试退出到主界面后，再重新进入。\n\n错误代码:\n" + e.toString();
                        break prepare;
                    } else if (e instanceof IOException) {
                        prepareError = "抱歉，播放器无法识别音频源。这可能由手机网络异常引起，或服务器上不存在该音频文件。\n\n\nURL=\n" + url + "\n\n\n错误代码:\n"
                                + e.toString();
                        break prepare;
                    }
                }
            }

            if (prepareError != null) {
                final VoiceReportObj sendToListenerObj = reportItemObj;// 复制对象以反馈给监听者
                reportItemObj = null;
                // 表示准备阶段的线程准备结束,允许立即重新初始化
                prepareThread = null;
                // 表示播放器不再处于准备阶段
                isPrepareing = false;
                // 由主线程来反馈到外部
                final String prepareErrorFinal = prepareError;
                notifHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onStreamPlayerListen != null && errorOut) {
                            onStreamPlayerListen.onPlayerError(prepareErrorFinal, sendToListenerObj);
                        }
                    }
                });
                return;
            }

            mediaPlayer.start();// 开始播放
            isPrepareing = false;// 表示播放器不再处于准备阶段

            // 反馈到外部
            notifHandler.post(new Runnable() {
                public void run() {
                    if (onStreamPlayerListen != null) {
                        allTime = mediaPlayer.getDuration() / 1000;
                        onStreamPlayerListen.onPlayerStart(allTime);
                    }
                }
            });

            // 0.5秒后开始反馈进度,然后1秒反馈1次.
            // 我本来想反馈的快一些,无奈seekBar这个烂控件快了也是跳跃式前进,我现在没时间去做个新控件做暂时就这样吧!
            progressWatchingTimer = new Timer(false);
            progressWatchingTimer.schedule(new MTimerTask(), 500, 1000);

            // 表示准备阶段已经完成
            prepareThread = null;
        }
    }

    /**
     * <p>
     * 继续播放,如果在已经放完的情况下,就从头开始继续放
     * <p>
     * 只有在运行状态下才有效,并且在准备过程中无效
     * */
    public void resumePlay() {
        if (this.reportItemObj == null) {// 未处于运行状态
            return;
        }

        if (this.isPrepareing) {
            return;
        }

        if (this.isCompleting) {
            this.mediaPlayer.seekTo(0);
            this.isCompleting = false;
        }

        this.mediaPlayer.start();
    }

    /**
     * <p>
     * 暂停播放
     * <p>
     * 只有在运行状态下才有效
     * */
    public void pausePlay() {
        if (this.reportItemObj == null) {
            return;
        }

        if (this.isPrepareing) {
            return;
        }

        this.mediaPlayer.pause();
    }

    /** 停止,停止后将结束运行状态 */
    public void stopPlay() {
        if (this.reportItemObj == null) {
            return;
        }

        errorOut = false;// 准备停止,播放器不再向外反馈错误

        if (this.progressWatchingTimer != null) {
            this.progressWatchingTimer.cancel();// 这里不像销毁,我没有等.因为stop时哪怕正好在执行TimerTask里的获取进度,应该也是不崩的,不受影响.
        }
        // 重置播放器
        this.mediaPlayer.stop();
        this.mediaPlayer.reset();

        // 如果正处于准备过程中,则需要锁至准备结束.以便立即重新开始
        if (prepareThread != null) {
            try {
                prepareThread.join();
            }
            catch (InterruptedException e) {
            }
        }

        // 重置标记变量
        this.isCompleting = false;
        this.isPrepareing = false;
        this.allTime = 0;
        // 重置报告对象
        this.reportItemObj = null;
    }

    public void setProgress(int progress) {
        if (this.reportItemObj == null) {// 未处于运行状态
            return;
        }

        if (this.isPrepareing) {
            return;
        }

        double a = (double) progress / 100;
        this.mediaPlayer.seekTo((int) (mediaPlayer.getDuration() * a));
    }

    /** 获取正在运行的报告,返回NULL表示没有在运行 */
    public VoiceReportObj getRunningReport() {
        return this.reportItemObj;
    }

    /** 播放器是否正处于放音的过程 */
    public boolean isPlaying() {
        if (this.reportItemObj == null) {
            return false;
        }
        if (this.isPrepareing) {
            return false;
        }
        return this.mediaPlayer.isPlaying();
    }

    /** 播放器是正处于"播放完成"的状态,可以resume重新播放. 除此之外都将返回false */
    public boolean isComplete() {
        return isCompleting;
    }

    /** 是否正在准备中 ,处于准备中则操作播放器无效.如pause,result,seek之类的 */
    public boolean isPrepareing() {
        return isPrepareing;
    }

    /** 获取播放时间,只有运行状态下才能获取到播放时间.除此之外都将返回0 */
    public int getAllTime() {
        return allTime;
    }

    // 进度反馈的Timer
    private class MTimerTask extends TimerTask {

        @Override
        public void run() {
            isTimerRunning = true;

            if (mediaPlayer.isPlaying()) {// 播放状态下才反馈
                if (onStreamPlayerListen != null) {
                    double a = mediaPlayer.getCurrentPosition();// 当前进度(毫秒)
                    double b = mediaPlayer.getDuration();// 总长度(毫秒)
                    double c = (a / b) * 100;// 百分比
                    onStreamPlayerListen.onPlayerProgress((int) c, (int) (a / 1000));

                    // System.out.println("progressTimerTask_Run:" + a + ":" +
                    // b);
                }
            }

            isTimerRunning = false;
        }
    }

    // 当播放器缓冲
    private OnBufferingUpdateListener onBufferingUpdateListener = new OnBufferingUpdateListener() {

        private int lastNotifPercent = 0;

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // System.out.println("onBufferingUpdate");
            if (onStreamPlayerListen != null) {
                if (percent != 100 || lastNotifPercent != 100)// 这段代码的意思就是100的进度只要给我报一次就行了别没完没了
                {
                    lastNotifPercent = percent;
                    onStreamPlayerListen.onPlayerLoading(percent);
                }
            }
        }
    };

    // 当播放器完成播放
    private OnCompletionListener onCompletionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            isCompleting = true;
            mediaPlayer.pause();// 暂停播放器
            if (onStreamPlayerListen != null) {
                onStreamPlayerListen.onPlayerComplete();
            }
        }
    };
    private OnPlayerStateListen onStreamPlayerListen = null;
    public void setOnPlayerListen(OnPlayerStateListen onStreamPlayerListen) {
        this.onStreamPlayerListen = onStreamPlayerListen;
    }

    public void delPlayerListen() {
        this.onStreamPlayerListen = null;
    }

}
