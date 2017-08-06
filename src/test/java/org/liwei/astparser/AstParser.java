package org.liwei.astparser;


import org.liwei.util.Util;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class AstParser {
	public static String parse(String str) throws Exception{
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
			parse(Util.readFileToString(filePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.print("ParseFilesInDir();");
	}
}

