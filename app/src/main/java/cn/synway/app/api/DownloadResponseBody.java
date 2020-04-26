package cn.synway.app.api;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.math.BigDecimal;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private DownloadListener downloadListener;
    private BufferedSource bufferedSource;


    public DownloadResponseBody(ResponseBody responseBody, DownloadListener downloadListener) {
        this.responseBody = responseBody;
        this.downloadListener = downloadListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                if (null != downloadListener) {
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                    BigDecimal decimal = new BigDecimal(totalBytesRead * 100F / responseBody.contentLength());
                    String progress = decimal.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
                      LogUtils.d("已经下载的：" + totalBytesRead + "共有：" + responseBody.contentLength()+"进度"+progress+",,"+(totalBytesRead * 100L / responseBody.contentLength()));
                    downloadListener.onProgress(progress);
                }
                return bytesRead;
            }
        };
    }

    public interface DownloadListener {
        void onProgress(String progress);
    }
}

