package extractors.parsers.bugzilla.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohan on 2017/3/27.
 */
public class BugInfo {

    private String bug_id = "";
    private String creation_ts = "";
    private String short_desc = "";
    private String delta_ts = "";
    private String component = "";
    private String version = "";
    private String priority = "";
    private String bug_severity = "";
    private String reporter = "";
    private List<BugCommentInfo> comment = new ArrayList<>();

    public String getBugId() {
        return bug_id;
    }
    public void setBugId(String bug_id) {
        this.bug_id = bug_id;
    }

    public String getCreationTs() {
        return creation_ts;
    }
    public void setCreationTs(String creation_ts) {
        this.creation_ts = creation_ts;
    }

    public String getShortDesc() {
        return short_desc;
    }
    public void setShortDesc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getDeltaTs() {
        return delta_ts;
    }
    public void setDeltaTs(String delta_ts) {
        this.delta_ts = delta_ts;
    }

    public String getComponent() {
        return component;
    }
    public void setComponent(String component) {
        this.component = component;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getBugSeverity() {
        return bug_severity;
    }
    public void setBugSeverity(String bug_severity) {
        this.bug_severity = bug_severity;
    }

    public String getReporter() {
        return reporter;
    }
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public List<BugCommentInfo> getComment() {
        return comment;
    }
    public void setComment(List<BugCommentInfo> comment) {
        this.comment = comment;
    }
    public void addComment(BugCommentInfo comment) { this.comment.add(comment); }

}
