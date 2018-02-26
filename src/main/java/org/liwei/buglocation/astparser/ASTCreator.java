package org.liwei.buglocation.astparser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.liwei.buglocation.utils.FileUtil;

public class ASTCreator {
	
	public static String getFileContent(File file) {
		return getFileContent(file.getAbsolutePath());
	}
	
	/**
	 * get file content for given java file
	 * @param absoluteFilePath input java file path
	 * @return
	 */
	public static String getFileContent(String absoluteFilePath) {
		String content = null;
		try {
			StringBuffer contentBuffer = new StringBuffer();
			String line = null;
			BufferedReader reader = new BufferedReader(new FileReader(
					absoluteFilePath));
			while ((line = reader.readLine()) != null)
				contentBuffer.append((new StringBuilder(String.valueOf(line)))
						.append("\r\n").toString());
			content = contentBuffer.toString();
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return content;
	}

//	public static CompilationUnit getCompilationUnit() {
//		ASTParser parser = ASTParser.newParser(AST.JLS8);
//		parser.setSource(content.toCharArray());
//		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
//		return compilationUnit;
//	}
	
	/**
	 * get methods names and comments for given java code
	 */
	public static String getMethodsAndComments(String str) throws Exception{
		String resultStr = new String();
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		final CompilationUnit result = (CompilationUnit) parser.createAST(null);
 
		TypeDeclaration typeDec = (TypeDeclaration) result.types().get(0);
		//System.out.println("class:" + typeDec.getName());
		//System.out.println("class doc:" + typeDec.getJavadoc());
		Javadoc classComment = typeDec.getJavadoc();
		if (classComment == null)
			resultStr = resultStr + typeDec.getName() + "\n";
		else
			resultStr = resultStr + typeDec.getName() + "\n" + classComment + "\n";
		MethodDeclaration methodDec[] = typeDec.getMethods();

		for (MethodDeclaration method : methodDec) {
			//System.out.println(method);
			//get method name
			SimpleName methodName = method.getName();
			//System.out.println("method:" + methodName);	
			//get method comment
			Javadoc methodComment = method.getJavadoc();
			//System.out.println("method doc:" + methodComment);
			if (methodComment == null)
				resultStr = resultStr + methodName + "\n";
			else 
				resultStr = resultStr + methodName + "\n" + methodComment;
//			//get method parameters
//			List param = method.parameters();
//			System.out.println("method parameters:" + param);
//			//get method return type
//			Type returnType=method.getReturnType2();  
//            System.out.println("method return type:"+returnType);
		}
		//System.out.println(resultStr);
		return resultStr;
	}

	public static void main(String[] args) {
		try {
			String filePath = "/Users/liwei/Documents/defect-prediction/open-source/eclipse.jdt.ui/org.eclipse.jdt.ui/ui/org/eclipse/jdt/ui/text/java/AbstractProposalSorter.java";
			getMethodsAndComments(FileUtil.readFileToString(filePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.print("ParseFilesInDir();");
	}
}

