package extractors.parsers.bugzilla.entity;

/**
 * Created by xiaohan on 2017/3/27.
 */
public class BugCommentInfo {

    private String commentid = "";
    private String who = "";
    private String bug_when = "";
    private String thetext = "";

    public String getCommentId() {
        return commentid;
    }
    public void setCommentId(String commentid) {
        this.commentid = commentid;
    }

    public String getWho() {
        return who;
    }
    public void setWho(String who) {
        this.who = who;
    }

    public String getBugWhen() {
        return bug_when;
    }
    public void setBugWhen(String bug_when) {
        this.bug_when = bug_when;
    }

    public String getThetext() {
        return thetext;
    }
    public void setThetext(String thetext) {
        this.thetext = thetext;
    }

}
