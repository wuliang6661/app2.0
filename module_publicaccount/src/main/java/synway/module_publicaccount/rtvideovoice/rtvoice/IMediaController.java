package synway.module_publicaccount.rtvideovoice.rtvoice;

/**
 * Created by sunliang on 2017/4/18.
 */

import android.view.View;

public interface IMediaController {
    void setMediaPlayer(IMediaController.MediaPlayerControl var1);

    void show();

    void show(int var1);

    void hide();

    boolean isShowing();

    void setEnabled(boolean var1);

    void setAnchorView(View var1);

    interface MediaPlayerControl {
        void start();

        void pause();

        long getDuration();

        long getCurrentPosition();

        void seekTo(long var1);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();
    }

    final class Proxy {
        private IMediaController mMediaController;

        private Proxy() {
        }

        public Proxy(IMediaController controller) {
            this.mMediaController = controller;
        }

        public void show() {
            this.mMediaController.show();
        }

        public void show(int timeOut) {
            this.mMediaController.show(timeOut);
        }

        public void hide() {
            this.mMediaController.hide();
        }

        public void setEnabled(boolean enable) {
            this.mMediaController.setEnabled(enable);
        }

        public void setAnchorView(View view) {
            this.mMediaController.setAnchorView(view);
        }

        public boolean isShowing() {
            return this.mMediaController.isShowing();
        }

        public void setMediaPlayer(IMediaController.MediaPlayerControl player) {
            this.mMediaController.setMediaPlayer(player);
        }
    }
}
