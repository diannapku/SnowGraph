package framework;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class KnowledgeGraphBuilder {

    List<KnowledgeExtractor> extractors = new ArrayList<KnowledgeExtractor>();
    public String graphPath = null;
    public String baseGraphPath = null;

    public void setExtractors(List<KnowledgeExtractor> extractors) {
        this.extractors = new ArrayList<KnowledgeExtractor>(extractors);
    }

    public void setGraphPath(String graphPath) {
        this.graphPath = graphPath;
    }

    public void setBaseGraphPath(String baseGraphPath) {
        this.baseGraphPath = baseGraphPath;
    }

    public void buildGraph() {
        File f = new File(graphPath);
        try {
            FileUtils.deleteDirectory(f);
            if (baseGraphPath != null)
                FileUtils.copyDirectory(new File(baseGraphPath), f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(f);
        for (KnowledgeExtractor extractor : extractors) {
            extractor.run(db);
            System.out.println(extractor.getClass().getName() + " finished. [" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]");
        }
        db.shutdown();
    }

}
