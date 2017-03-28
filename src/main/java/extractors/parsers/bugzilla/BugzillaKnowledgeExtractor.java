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
import java.io.IOException;

import extractors.parsers.bugzilla.entity.*;

/**
 * Created by xiaohan on 2017/3/27.
 */
public class BugzillaKnowledgeExtractor implements KnowledgeExtractor {

    @EntityDeclaration
    public static final String BUGZILLA = "bugzilla";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_BUGID = "bug_id";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_CREATIONTS = "creation_ts";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_SHORTDESC = "short_desc";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_DELTATS = "delta_ts";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_COMPONENT = "component";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_VERSION = "version";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_PRIORITY = "priority";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_BUGSEVERITIY = "bug_severity";
    @PropertyDeclaration(parent = BUGZILLA)
    public static final String BUGZILLA_REPROTER = "reporter";


    @EntityDeclaration
    public static final String BUGZILLECOMMENT = "bugzilla_comment";
    @PropertyDeclaration(parent = BUGZILLECOMMENT)
    public static final String COMMENTID = "commentid";
    @PropertyDeclaration(parent = BUGZILLECOMMENT)
    public static final String COMMENTWHO = "who";
    @PropertyDeclaration(parent = BUGZILLECOMMENT)
    public static final String COMMENTBUGWHEN = "bug_when";
    @PropertyDeclaration(parent = BUGZILLECOMMENT)
    public static final String COMMENTTHETEXT = "thetext";


    @RelationshipDeclaration
    public static final String HAVE_COMMENT = "bugzilla_have_comment";

    GraphDatabaseService db = null;

    String folderPath = null;

    public void setFolderPath(String path) {
        this.folderPath = path;
    }


    @Override
    public void run(GraphDatabaseService db) {
        this.db = db;
        File bugFolder = new File(folderPath);
        for (File oneBugFile : bugFolder.listFiles()){
            if (oneBugFile.getName().endsWith(".xml")) {
                BugInfo bugInfo = BugzillaParser.getBugInfo(oneBugFile.getName());
                try (Transaction tx = db.beginTx()) {
                    Node node = db.createNode();
                    BugzillaUtils.creatBugzillaNode(bugInfo, node);
                    for (BugCommentInfo comment : bugInfo.getComment()) {
                        Node commentNode = db.createNode();
                        BugzillaUtils.creatBugCommentNode(comment, commentNode);
                        node.createRelationshipTo(commentNode, RelationshipType.withName(HAVE_COMMENT));
                    }
                    tx.success();
                }
            }
        }
    }

}
