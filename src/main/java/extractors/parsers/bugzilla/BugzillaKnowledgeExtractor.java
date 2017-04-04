package extractors.parsers.bugzilla;

import framework.KnowledgeExtractor;
import framework.annotations.EntityDeclaration;
import framework.annotations.PropertyDeclaration;
import framework.annotations.RelationshipDeclaration;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import extractors.parsers.bugzilla.entity.*;

/**
 * Created by xiaohan on 2017/3/27.
 */
public class BugzillaKnowledgeExtractor implements KnowledgeExtractor {

    @EntityDeclaration
    public static final String BUGZILLAISSUE = "bugzilla_issue";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_BUGID = "bug_id";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_CREATIONTS = "creation_ts";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_SHORTDESC = "short_desc";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_DELTATS = "delta_ts";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_CLASSIFICATION = "classification";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_PRODUCT = "product";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_COMPONENT = "component";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_VERSION = "version";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_REPPLATFORM = "rep_platform";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_OPSYS = "op_sys";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_BUGSTATUS = "bug_status";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_RESOLUTION = "resolution";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_PRIORITY = "priority";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_BUGSEVERITIY = "bug_severity";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_REPROTER = "reporter";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_REPROTERNAME = "reporter_name";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_ASSIGNEDTO = "assigned_to";
    @PropertyDeclaration(parent = BUGZILLAISSUE)
    public static final String ISSUE_ASSIGNEENAME = "assignee_name";


    @EntityDeclaration
    public static final String ISSUECOMMENT = "bugzilla_comment";
    @PropertyDeclaration(parent = ISSUECOMMENT)
    public static final String COMMENT_ID = "commentid";
    @PropertyDeclaration(parent = ISSUECOMMENT)
    public static final String COMMENT_WHO = "who";
    @PropertyDeclaration(parent = ISSUECOMMENT)
    public static final String COMMENT_NAME = "name";
    @PropertyDeclaration(parent = ISSUECOMMENT)
    public static final String COMMENT_BUGWHEN = "bug_when";
    @PropertyDeclaration(parent = ISSUECOMMENT)
    public static final String COMMENT_THETEXT = "thetext";

    @EntityDeclaration
    public static final String BUGZILLAUSER = "bugzilla_user";
    @PropertyDeclaration(parent = BUGZILLAUSER)
    public static final String USER_ID = "user_id";
    @PropertyDeclaration(parent = BUGZILLAUSER)
    public static final String USER_NAME = "user_name";

    @RelationshipDeclaration
    public static final String HAVE_COMMENT = "bugzilla_have_comment";

    @RelationshipDeclaration
    public static final String IS_REPORTER_OF_ISSUE = "bugzilla_is_reporter_of_issue";

    @RelationshipDeclaration
    public static final String IS_ASSIGNEE_OF_ISSUE = "bugzilla_is_assignee_of_issue";

    @RelationshipDeclaration
    public static final String IS_CREATOR_OF_COMMENT = "bugzilla_is_creator_of_comment";

    GraphDatabaseService db = null;

    String folderPath = null;

    Map<String, Node> userNodeMap = new HashMap<>();

    public void setFolderPath(String path) {
        this.folderPath = path;
    }


    @Override
    public void run(GraphDatabaseService db) {
        this.db = db;
        File bugFolder = new File(folderPath);

        for (File oneBugFile : bugFolder.listFiles()){
            if (oneBugFile.getName().endsWith(".xml")) {
                System.out.println(oneBugFile.getName());
                BugInfo bugInfo = BugzillaParser.getBugInfo(folderPath + "/" + oneBugFile.getName());

                try (Transaction tx = db.beginTx()) {
                    Node node = db.createNode();
                    BugzillaUtils.creatBugzillaIssueNode(bugInfo, node);

                    if (userNodeMap.containsKey(bugInfo.getReporter())) {
                        userNodeMap.get(bugInfo.getReporter()).createRelationshipTo(node, RelationshipType.withName(IS_REPORTER_OF_ISSUE));
                    } else {
                        Node userNode = db.createNode();
                        BugzillaUtils.creatBugzillaUserNode(bugInfo.getReporter(), bugInfo.getReporterName(), userNode);
                        userNodeMap.put(bugInfo.getReporter(), userNode);
                        userNode.createRelationshipTo(node, RelationshipType.withName(IS_REPORTER_OF_ISSUE));
                    }
//
                    if (userNodeMap.containsKey(bugInfo.getAssignedTo())) {
                        userNodeMap.get(bugInfo.getAssignedTo()).createRelationshipTo(node, RelationshipType.withName(IS_ASSIGNEE_OF_ISSUE));
                    } else {
                        Node userNode = db.createNode();
                        BugzillaUtils.creatBugzillaUserNode(bugInfo.getAssignedTo(), bugInfo.getAssignedToName(), userNode);
                        userNodeMap.put(bugInfo.getAssignedTo(), userNode);
                        userNode.createRelationshipTo(node, RelationshipType.withName(IS_ASSIGNEE_OF_ISSUE));
                    }


                    for (BugCommentInfo comment : bugInfo.getComment()) {
                        Node commentNode = db.createNode();
                        BugzillaUtils.creatIssueCommentNode(comment, commentNode);
                        node.createRelationshipTo(commentNode, RelationshipType.withName(HAVE_COMMENT));

                        if (userNodeMap.containsKey(comment.getWho())) {
                            userNodeMap.get(comment.getWho()).createRelationshipTo(commentNode, RelationshipType.withName(IS_CREATOR_OF_COMMENT));
                        } else {
                            Node userNode = db.createNode();
                            BugzillaUtils.creatBugzillaUserNode(comment.getWho(), comment.getWhoName(), userNode);
                            userNodeMap.put(comment.getWho(), userNode);
                            userNode.createRelationshipTo(commentNode, RelationshipType.withName(IS_CREATOR_OF_COMMENT));
                        }

                    }
                    tx.success();
                }
            }
        }
    }

}
