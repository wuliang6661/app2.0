package synway.common.ReadAloud;


import java.util.UUID;

/**
 * 一次朗读任务<br>
 * 可以朗读文字,也可以播放语音.如果既朗读文字又播放语音,则先朗读文字,读完文字后播放语音.
 */
public class ReadAloudObj {

    public ReadAloudObj(String readText) {
        this.readText = readText;
        this.idPackage = UUID.randomUUID().toString();
    }

    public ReadAloudObj(String readText, String id) {
        this.readText = readText;
        this.id = id;
        this.idPackage = UUID.randomUUID().toString();
    }


    public ReadAloudObj(String readText, String playVoiceFilePath, String id) {
        this.id = id;
        this.readText = readText;
        this.playVoiceFilePath = playVoiceFilePath;
        this.idPackage = UUID.randomUUID().toString();
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    boolean canAdd = true;
    private Object tag;
    private String id;// 每一条朗读携带的tag.
    private String readText;//朗读的文本,为null表示不朗读文本
    private String playVoiceFilePath;//朗读的语音文件的路径,为null表示不朗读语音
    private String idPackage;//用于识别每一次任务

    String getIdPackage() {
        return idPackage;
    }

    public String getId() {
        return id;
    }

    public Object getTag() {
        return tag;
    }

    public String getReadText() {
        return readText;
    }

    public String getPlayVoiceFilePath() {
        return playVoiceFilePath;
    }

    public void setReadText(String text){
        this.readText = text;
    }
    public void setPlayVoiceFilePath(String path){
        this.playVoiceFilePath = path;
    }


}
