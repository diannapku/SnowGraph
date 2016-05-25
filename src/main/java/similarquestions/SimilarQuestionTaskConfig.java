package similarquestions;

import java.io.File;

import utils.Config;

public class SimilarQuestionTaskConfig {

	public String projectName="";
	public String projectPath="";
	public String qaPath="";
	public String qPath="",aPath="",cPath="",uPath="",plPath="";
	public String graphPath="";
	public String tmpPath="";
	
	public String srcPath="";
	public String binPath="";
	
	public SimilarQuestionTaskConfig(String projectName){
		this.projectName=projectName;
		projectPath=Config.getValue("datastore", "")+"/"+projectName;
		File file=new File(projectPath);
		if (!file.exists())
			file.mkdir();
		tmpPath=projectPath+"/tmp";
		file=new File(tmpPath);
		if (!file.exists())
			file.mkdir();
		qaPath=projectPath+"/qa";
		file=new File(qaPath);
		if (!file.exists())
			file.mkdir();
		qPath=qaPath+"/Questions.xml";
		aPath=qaPath+"/Answers.xml";
		cPath=qaPath+"/Comments.xml";
		uPath=qaPath+"/Users.xml";
		plPath=qaPath+"/PostLinks.xml";
		graphPath=projectPath+"/simquestion-graphdb";
		file=new File(graphPath);
		if (!file.exists())
			file.mkdir();
		srcPath=projectPath+"/src";
		binPath=projectPath+"/bin";
		file=new File(srcPath);
		if (!file.exists())
			file.mkdir();
		file=new File(binPath);
		if (!file.exists())
			file.mkdir();
	}
	
}
