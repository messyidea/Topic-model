package forumlda;

import java.util.ArrayList;

import common.FileUtil;

public class ModelParams {
	int T;
	int U;
	int V;
	int P;

	float alpha;
	float beta;
	float gamma;
	float lambda;
	
	int iteration;
	int topNum;
	
	public void parseFromFile(String filename) {
		System.out.println("Start parse params from file.");
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(filename, lines);
		for(int i = 0; i < lines.size(); i ++) {
			String[] strArr = lines.get(i).split(":", 2);
			String key = strArr[0].trim();
			String value = strArr[1].trim();
//			System.out.println("key == " + key);
//			System.out.println("value == " + value);
			switch(key) {
			case "topics":
				this.T = Integer.parseInt(value);
				break;
			case "alpha":
				this.alpha = Float.parseFloat(value);
				break;
			case "beta":
				this.beta = Float.parseFloat(value);
				break;
			case "gamma":
				this.gamma = Float.parseFloat(value);
				break;
			case "iteration":
				this.iteration = Integer.parseInt(value);
				break;
			case "top_num":
				this.topNum = Integer.parseInt(value);
				break;
			default:
				System.out.println("Unknow param: " + key);
			}
		}
		System.out.println("End parse params from file.");
	}

	public void getExtraParams(int wordNum, int authorNum, int postNum) {
		// TODO Auto-generated method stub
		this.U = authorNum;
		this.V = wordNum;
		this.P = postNum;
	}
	
	public void showParams() {
		System.out.println("==================================");
		System.out.println("topics: " + Integer.toString(this.T));
		System.out.println("alpha: " + Float.toString(this.alpha));
		System.out.println("beta: " + Float.toString(this.beta));
		System.out.println("gamma: " + Float.toString(this.gamma));
		System.out.println("iteration: " + Integer.toString(this.iteration));
		System.out.println("top_num: " + Integer.toString(this.topNum));
		System.out.println("authorNum: " + Integer.toString(this.U));
		System.out.println("wordNum: " + Integer.toString(this.V));
		System.out.println("postNum: " + Integer.toString(this.P));
		System.out.println("==================================");
	}
}
