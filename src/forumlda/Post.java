package forumlda;

import java.util.ArrayList;
import java.util.HashMap;

public class Post {
	int id;
	boolean fix;
	boolean type;
	ArrayList<Content> contents = new ArrayList<Content>();
	
	public Post(ArrayList<String> tPost, HashMap<String, Integer> wordMap, 
			ArrayList<String> wordList, HashMap<String, Integer> authorMap, ArrayList<String> authorList) {
		int pos = 1;
		this.id = 1;
		
		for (int i = 0; i < tPost.size(); ++i) {
			Content c = new Content(tPost.get(i), wordMap, wordList, authorMap, authorList, pos ++);
			if (c.position == -1) {
				pos --;
				continue;
			}
			this.contents.add(c);
		}
		
		if(pos == 1) {
			this.id = -1;
		}
	}
	
}
