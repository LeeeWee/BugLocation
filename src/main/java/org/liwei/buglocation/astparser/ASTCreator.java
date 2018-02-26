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
	/**
	 * Code file content
	 */
	private String content;

	public ASTCreator() {
		content = null;
	}
	
	public void getFileContent(File file) {
		getFileContent(file.getAbsolutePath());
	}
	
	/**
	 * get file content for given java file
	 * @param absoluteFilePath input java file path
	 * @return
	 */
	public void getFileContent(String absoluteFilePath) {
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
	}

	/**
	 * get compilation unit
	 * @param content input code content
	 * @return
	 */
	public CompilationUnit getCompilationUnit() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(content.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		return compilationUnit;
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	
}

