package test.kth.xmpptestproject.datas;

/**
 * Created by 투덜이2 on 2016-08-04.
 */
public class ChatData {

    int mb_idx;
    int idx;
    int receive;
    int msg_read;
    String voice;
    String photo;
    String name;
    String regdate;
    int send = 1;
    int best;
    int msg_idx;
    private String saved_voice_file;
    private int totalChatListSize = 0;
    private String duration = null;

    public int getBest() {
        return best;
    }

    public void setBest(int best) {
        this.best = best;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }


    public int getReceive() {
        return receive;
    }

    public void setReceive(int receive) {
        this.receive = receive;
    }

    public int getMsg_read() {
        return msg_read;
    }

    public void setMsg_read(int msg_read) {
        this.msg_read = msg_read;
    }


    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getMb_idx() {
        return mb_idx;
    }

    public void setMb_idx(int mb_idx) {
        this.mb_idx = mb_idx;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public int getMsg_idx() {
        return msg_idx;
    }

    public void setMsg_idx(int msg_idx) {
        this.msg_idx = msg_idx;
    }

    public int getTotalChatListSize() {
        return totalChatListSize;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public ChatData setTotalChatListSize(int totalChatListSize) {
        this.totalChatListSize = totalChatListSize;
        return this;
    }

    public String getSaved_voice_file() {
        return saved_voice_file;
    }

    public ChatData setSaved_voice_file(String saved_voice_file) {
        this.saved_voice_file = saved_voice_file;
        return this;
    }

    @Override
    public String toString() {
        return "ChatData{" +
                "mb_idx=" + mb_idx +
                ", idx=" + idx +
                ", receive=" + receive +
                ", msg_read=" + msg_read +
                ", voice='" + voice + '\'' +
                ", photo='" + photo + '\'' +
                ", name='" + name + '\'' +
                ", regdate='" + regdate + '\'' +
                ", send=" + send +
                ", best=" + best +
                ", msg_idx=" + msg_idx +
                ", saved_voice_file='" + saved_voice_file + '\'' +
                ", totalChatListSize=" + totalChatListSize +
                '}';
    }
}
