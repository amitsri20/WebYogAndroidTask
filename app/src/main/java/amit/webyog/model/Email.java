package amit.webyog.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by amit on 10/15/2016.
 */

public class Email {
    @SerializedName("subject")
    private String subject;
    @SerializedName("participants")
    private List<String> participants;
    @SerializedName("preview")
    private String preview;
    @SerializedName("isRead")
    private String isRead;
    @SerializedName("isStarred")
    private String isStarred;
    @SerializedName("ts")
    private String ts;
    @SerializedName("id")
    private String id;

    public Email(String subject, List<String> participants, String preview, String isRead, String isStarred, String ts, String id) {
        this.subject = subject;
        this.participants = participants;
        this.preview = preview;
        this.isRead = isRead;
        this.isStarred = isStarred;
        this.ts = ts;
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(String isStarred) {
        this.isStarred = isStarred;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
