package similarquestions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import crawlers.qa.QaExtractor;
import discretgraphs.code.CodeGraphBuilder;
import discretgraphs.qa.QaGraphDbBuilder;

public class GraphPreparation {

	String projectName="";
	
	public static void main(String[] args){
		GraphPreparation p=new GraphPreparation("apache-poi");
		try {
			p.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GraphPreparation(String projectName){
		this.projectName=projectName;
	}
	
	public void run() throws IOException{
		SimilarQuestionTaskConfig config=new SimilarQuestionTaskConfig(projectName);
		if (!new File(config.qPath).exists()||!new File(config.aPath).exists()||
				!new File(config.cPath).exists()||!new File(config.uPath).exists()
				||!new File(config.plPath).exists()){
			FileUtils.cleanDirectory(new File(config.qaPath));
			QaExtractor.extract(projectName,config.qPath, config.aPath, config.cPath, config.uPath, config.plPath);
		}
		FileUtils.cleanDirectory(new File(config.graphPath));
		
		FileUtils.cleanDirectory(new File(config.tmpPath));
		CodeGraphBuilder codeGraphBuilder=new CodeGraphBuilder(config.tmpPath, config.srcPath, config.binPath);
		codeGraphBuilder.run();
		codeGraphBuilder.migrateTo(config.graphPath);
		
		FileUtils.cleanDirectory(new File(config.tmpPath));
		QaGraphDbBuilder qaGraphDbBuilder=new QaGraphDbBuilder(config.tmpPath, config.qPath, config.aPath, config.cPath, config.uPath, config.plPath);
		qaGraphDbBuilder.run();
		qaGraphDbBuilder.migrateTo(config.graphPath);
	}
	
}
