package extractors.parsers.bugzilla;

import extractors.parsers.bugzilla.entity.BugCommentInfo;
import extractors.parsers.bugzilla.entity.BugInfo;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 * Created by xiaohan on 2017/3/27.
 */
public class BugzillaUtils {
    public static void creatBugzillaNode(BugInfo bugInfo, Node node) {
        node.addLabel(Label.label(BugzillaKnowledgeExtractor.BUGZILLA));
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_BUGID, bugInfo.getBugId());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_CREATIONTS, bugInfo.getCreationTs());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_SHORTDESC, bugInfo.getShortDesc());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_DELTATS, bugInfo.getDeltaTs());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_COMPONENT, bugInfo.getComponent());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_VERSION, bugInfo.getVersion());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_PRIORITY, bugInfo.getPriority());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_BUGSEVERITIY, bugInfo.getBugSeverity());
        node.setProperty(BugzillaKnowledgeExtractor.BUGZILLA_REPROTER, bugInfo.getReporter());
    }

    public static void creatBugCommentNode(BugCommentInfo commentInfo, Node node) {
        node.addLabel(Label.label(BugzillaKnowledgeExtractor.BUGZILLECOMMENT));
        node.setProperty(BugzillaKnowledgeExtractor.COMMENTID, commentInfo.getCommentId());
        node.setProperty(BugzillaKnowledgeExtractor.COMMENTWHO, commentInfo.getWho());
        node.setProperty(BugzillaKnowledgeExtractor.COMMENTBUGWHEN, commentInfo.getBugWhen());
        node.setProperty(BugzillaKnowledgeExtractor.COMMENTTHETEXT, commentInfo.getThetext());
    }



}
