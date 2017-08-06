import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Test {
	   public static void main(String[] args) {
		   Properties props = new Properties();  
		   props.put("annotators", "tokenize,ssplit,pos,lemma");  
		   StanfordCoreNLP pipeline = new StanfordCoreNLP(props); 
		   
		   String text = "made changed created";               // 输入文本  
	      
		   Annotation document = new Annotation(text);    // 利用text创建一个空的Annotation  
		   pipeline.annotate(document);                   // 对text执行所有的Annotators（七种）  
	      
	    // 下面的sentences 中包含了所有分析结果，遍历即可获知结果。  
		   List<CoreMap> sentences = document.get(SentencesAnnotation.class);  
		   System.out.println("lemma");  
	      
           CoreMap sentence = sentences.get(0);

		   for (CoreLabel token: sentence.get(TokensAnnotation.class)) {  
              
			   String word = token.get(TextAnnotation.class);            // 获取分词  
//	           String pos = token.get(PartOfSpeechAnnotation.class);     // 获取词性标注  
//	           String ne = token.get(NamedEntityTagAnnotation.class);    // 获取命名实体识别结果  
	           String lemma = token.get(LemmaAnnotation.class);          // 获取词形还原结果  
	             
	           System.out.println(lemma);  
		   }  
	   }
}
