package extractors.parsers.mail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.MimeConfig;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import framework.KnowledgeExtractor;
import framework.annotations.EntityDeclaration;
import framework.annotations.PropertyDeclaration;
import framework.annotations.RelationshipDeclaration;
import extractors.parsers.mail.utils.CharBufferWrapper;
import extractors.parsers.mail.utils.MboxHandler;
import extractors.parsers.mail.utils.MboxIterator;

public class MailListKnowledgeExtractor implements KnowledgeExtractor {

    @EntityDeclaration
    public static final String MAIL = "Mail";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_ID = "mailId";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_SUBJECT = "subject";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_SENDER_NAME = "senderName";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_SENDER_MAIL = "senderMail";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_RECEIVER_NAMES = "receiverNames";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_RECEIVER_MAILS = "receiverMails";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_DATE = "date";
    @PropertyDeclaration(parent = MAIL)
    public static final String MAIL_BODY = "body";

    @EntityDeclaration
    public static final String MAILUSER = "MailUser";
    @PropertyDeclaration(parent = MAILUSER)
    public static final String MAILUSER_NAMES = "names";
    @PropertyDeclaration(parent = MAILUSER)
    public static final String MAILUSER_MAIL = "mail";

    @RelationshipDeclaration
    public static final String MAIL_IN_REPLY_TO = "mailInReplyTo";
    @RelationshipDeclaration
    public static final String MAIL_SENDER = "mailSender";
    @RelationshipDeclaration
    public static final String MAIL_RECEIVER = "mailReceiver";

    public String mboxPath = null;
    GraphDatabaseService db = null;
    MimeStreamParser parser = null;
    private static Charset charset = Charset.forName("UTF-8");
    private final static CharsetDecoder DECODER = charset.newDecoder();

    public void setMboxPath(String path) {
        this.mboxPath = path;
    }

    public void run(GraphDatabaseService db) {
        this.db = db;
        MboxHandler myHandler = new MboxHandler();
        myHandler.setDb(db);
        parser = new MimeStreamParser(new MimeConfig());
        parser.setContentHandler(myHandler);
        parse(new File(mboxPath));
        try (Transaction tx = db.beginTx()) {
            for (String address : myHandler.getMailUserNameMap().keySet()) {
                Node node = myHandler.getMailUserMap().get(address);
                node.setProperty(MAILUSER_NAMES, String.join(", ", myHandler.getMailUserNameMap().get(address)));
            }
            tx.success();
        }
        try (Transaction tx = db.beginTx()) {
            for (String mailId : myHandler.getMailReplyMap().keySet()) {
                Node mailNode = myHandler.getMailMap().get(mailId);
                Node replyNode = myHandler.getMailMap().get(myHandler.getMailReplyMap().get(mailId));
                if (mailNode != null & replyNode != null)
                    mailNode.createRelationshipTo(replyNode, RelationshipType.withName(MailListKnowledgeExtractor.MAIL_IN_REPLY_TO));
            }
            tx.success();
        }
    }

    public void parse(File mboxFile) {
        if (mboxFile.isDirectory()) {
            for (File f : mboxFile.listFiles())
                parse(f);
            return;
        }
        if (!mboxFile.getName().endsWith(".mbox"))
            return;
        MboxIterator iterator;
        try {
            iterator = MboxIterator.fromFile(mboxFile).charset(DECODER.charset()).build();
        } catch (IOException e) {
            return;
        }
        try (Transaction tx = db.beginTx()) {
            for (CharBufferWrapper message : iterator) {
                if (message.toString().contains("Subject: svn commit"))
                    continue;
                if (message.toString().contains("Subject: cvs commit"))
                    continue;
                if (message.toString().contains("Subject: ["))
                    continue;
                parse(message);
            }
            tx.success();
        }
    }

    public void parse(CharBufferWrapper message) {
        try {
            parser.parse(new ByteArrayInputStream(message.toString().trim().getBytes()));
        } catch (MimeException | IOException e) {
            e.printStackTrace();
        }
    }

}
