package forumlda;

import java.util.ArrayList;
import java.util.HashMap;

public class Content {
	int[] content;
	int position;
	int author;
	
	public Content(String line, HashMap<String, Integer> wordMap, 
			ArrayList<String> wordList, HashMap<String, Integer> authorMap, ArrayList<String> authorList, int pos) {
		int wordNum = wordMap.size();
		int authorNum = authorMap.size();
		
		this.position = pos;
		String[] str = line.split("\\s+");
		if (str.length <= 2) {
			this.position = -1;
			return;
		}
		
		// author
		String author = str[0];
		if (!authorMap.containsKey(author)) {
			authorList.add(author);
			authorMap.put(author, authorNum ++);
			this.author = authorNum - 1;
		} else {
			this.author = authorMap.get(author);
		}
		
		// content
		ArrayList<Integer> words = new ArrayList<Integer>();
		for(int i = 1; i < str.length; ++i) {
			if (!wordMap.containsKey(str[i])) {
				wordList.add(str[i]);
				wordMap.put(str[i], wordNum ++);
				words.add(wordNum - 1);
			} else {
				words.add(wordMap.get(str[i]));
			}
		}
		
		this.content = new int[words.size()];
		for(int i = 0; i < words.size(); ++i) {
			this.content[i] = words.get(i);
		}
		
		words.clear();
	}
	
}
