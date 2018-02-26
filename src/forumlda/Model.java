package forumlda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import common.ComUtil;

public class Model {
	ArrayList<Post> posts;
	
	int T, S, V, U, P;
	int nIter;
	int topNum;
	
	float[] talpha;
	float talphaSum;
	float[] salpha;
	float salphaSum;
	float[] zalpha;
	float zalphaSum;
	
	float[] tbeta;
	float tbetaSum;
	float[] sbeta;
	float sbetaSum;
	
	float[] gamma;
	float gammaSum;
	
	boolean[][] x;
	short[][] zw; // z word
	short[][] zr; // z reply
	short[][] y;
	
	float[][] ztheta;
	float[][] lambda;
	float[][] seta;
	float[][] teta;
	
	float[][] sphi;
	float[][] tphi;
	
	int[][] countPTW;  // only for root post
	int[][] countTVW;
	int[][] countPTR;
	int[] countTW;
	int[] countSW;
	int[][] countU2R;
	int[][] countUTW;
	int[][] countUSW;
	int[][] countU2W;
	int[][] countSVW;

	
	public Model(ModelParams modelParams, ArrayList<Post> posts) {
		// TODO Auto-generated constructor stub
		this.posts = posts;
		
		this.nIter = modelParams.iteration;
		this.topNum = modelParams.topNum;
		
		this.T = modelParams.T;
		this.V = modelParams.V;
		this.U = modelParams.U;
		this.P = this.posts.size();
		
		this.talpha = new float[T];
		this.talphaSum = 0;
		for (int i = 0; i < T; ++i) {
			this.talpha[i] = modelParams.alpha;
			this.talphaSum += this.talpha[i];
		}
		
		this.salpha = new float[S];
		this.salphaSum = 0;
		for (int i = 0; i < S; ++i) {
			this.salpha[i] = modelParams.alpha;
			this.salphaSum += this.salpha[i];
		}
		
		this.zalpha = new float[T];
		this.zalphaSum = 0;
		for (int i = 0; i < T; ++i) {
			this.zalpha[i] = modelParams.alpha;
			this.zalphaSum += this.zalpha[i];
		}
		
		this.tbeta = new float[V];
		this.tbetaSum = 0;
		for (int i = 0; i < V; ++i) {
			this.tbeta[i] = modelParams.beta;
			this.tbetaSum += this.tbeta[i];
		}
		
		this.sbeta = new float[V];
		this.sbetaSum = 0;
		for (int i = 0; i < V; ++i) {
			this.sbeta[i] = modelParams.beta;
			this.sbetaSum += this.sbeta[i];
		}
		
		this.gamma = new float[2];
		this.gammaSum = 0;
		for (int i = 0; i < 2; ++i) {
			this.gamma[i] = modelParams.gamma;
			this.gammaSum += this.gamma[i];
		}
		
		
		this.countUTW = new int[U][T];
		this.teta = new float[U][T];
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < T; ++j) {
				this.countUTW[i][j] = 0;
				this.teta[i][j] = 0;
			}
		}
		
		this.countUSW = new int[U][S];
		this.seta = new float[U][S];
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < S; ++j) {
				this.countUSW[i][j] = 0;
				this.seta[i][j] = 0;
			}
		}
		
		this.ztheta = new float[P][T];
		this.countPTW = new int[P][T];
		this.countPTR = new int[P][T];
		for (int i = 0; i < P; ++i) {
			for (int j = 0; j < T; ++j) {
				this.ztheta[i][j] = 0;
				this.countPTW[i][j] = 0;
				this.countPTR[i][j] = 0;
			}
		}
		
		this.sphi = new float[S][V];
		this.countSVW = new int[S][V];
		for (int i = 0; i < S ; ++i) {
			for (int j = 0; j < V; ++j) {
				this.sphi[i][j] = 0;
				this.countSVW[i][j] = 0;
			}
		}
		
		this.tphi = new float[T][V];
		this.countTVW = new int[T][V];
		for (int i = 0; i < T ; ++i) {
			for (int j = 0; j < V; ++j) {
				this.tphi[i][j] = 0;
				this.countTVW[i][j] = 0;
			}
		}
		
		this.countU2R = new int[U][2];
		this.countU2W = new int[U][2];
		this.lambda = new float[U][2];
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.countU2R[i][j] = 0;
				this.countU2W[i][j] = 0;
				this.lambda[i][j] = 0;
			}
		}
		
		this.countTW = new int[T];
		for (int i = 0; i < T; ++i) {
			this.countTW[i] = 0;
		}
		
		this.countSW = new int[T];
		for (int i = 0; i < S; ++i) {
			this.countSW[i] = 0;
		}
				
	}
	
	public void intialize() {
		System.out.println("Start init.");
		
		this.zw = new short[this.posts.size()][];
		this.zr = new short[this.posts.size()][];
		this.x = new boolean[this.posts.size()][];
		
		for (int i = 0; i < this.posts.size(); ++i) {
			Post post = posts.get(i);
			zw[i] = new short[post.contents.get(0).content.length];
			zr[i] = new short[post.contents.size() - 1];
			x[i] = new boolean[post.contents.size() - 1];
			
			Content rootPost = post.contents.get(0);
			for(int j = 0; j < rootPost.content.length; ++j) {
				double rand = Math.random();
				double thred = 0;
				short tp = 0;
				for(short a = 0; a < T; ++a) {
					thred += (double) 1.0 / T;
					if (thred >= rand) {
						tp = a;
						break;
					}
				}
				zw[i][j] = tp;
				
				countPTW[i][tp] ++;
				countTVW[tp][rootPost.content[j]] ++;
				countTW[tp] ++;
				countU2W[rootPost.author][0] ++;
				countUTW[rootPost.author][tp] ++;

			}
			 for (int j = 1; j < post.contents.size(); ++j) {
				 Content reply = post.contents.get(j);
				 double rand = Math.random();
				 boolean bufferX;
				 if (rand > 0.5) {
					 bufferX = true;
				 } else {
					 bufferX = false;
				 }
				 
				 x[i][j-1] = bufferX;
				 if (bufferX == true) {
					 rand = Math.random();
					 double thred = 0;
					 short tp = 0;
					 for (short a = 0; a < T; ++a) {
						 thred += (double) 1.0 / T;
						 if (thred > rand) {
							 tp = a;
							 break;
						 }
					 }
					 zr[i][j-1] = tp;
					 
					 countPTR[i][tp] ++;
					 countU2R[reply.author][1] ++;
					 
					 for (int k = 0; k < reply.content.length; ++k) {
						 int word = reply.content[k];
						 countTVW[tp][word] ++;
						 countTW[tp] ++;
						 countU2W[reply.author][1] ++;
						 countUTW[reply.author][tp] ++;
					 }
				 } else {
					 rand = Math.random();
					 double thred = 0;
					 short tp = 0;
					 for (short a = 0; a < S; ++a) {
						 thred += (double) 1.0 / S;
						 if (thred > rand) {
							 tp = a;
							 break;
						 }
					 }
					 zr[i][j-1] = tp;
					 
					 countU2R[reply.author][0] ++;
					 for (int k = 0; k < reply.content.length; ++k) {
						 int word = reply.content[k];
						 countSVW[tp][word] ++;
						 countSW[tp] ++;
						 countU2W[reply.author][0] ++;
						 countUSW[reply.author][tp] ++;
						 
					 }
				 }
			 }
			
		}
		System.out.println("End init.");
	}
	
	public void estimate() {
		System.out.println("Start estimate");
		int niter = 0;
		
		while (true) {
			System.out.println("iterator " + Integer.toString(niter));
			niter ++;
			oneIter();
			
			if (niter >= nIter) {
				
				updateDistribution();
				break;
			}
		}
	}
	
	private void updateDistribution() {
		System.out.println("Start update distribution.");
		for (int i = 0; i < P; ++i) {
			for (int j = 0; j < T; ++j) {
				ztheta[i][j] = (countPTW[i][j] + countPTR[i][j] + zalpha[j])
						/ (posts.get(i).contents.get(0).content.length + posts.get(i).contents.size() - 1 + zalphaSum);
			}
		}
		
		for (int i = 0; i < S; ++i) {
			for (int j = 0; j < V; ++j) {
				sphi[i][j] = (countSVW[i][j] + sbeta[j])
						/ (countSW[i] + sbetaSum);
			}
		}
		
		for (int i = 0; i < T; ++i) {
			for (int j = 0; j < V; ++j) {
				tphi[i][j] = (countTVW[i][j] + tbeta[j])
						/ (countTW[i] + tbetaSum);
			}
		}
		
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < 2; ++j) {
				lambda[i][j] = (countU2R[i][j] + gamma[j])
						/ (countU2R[i][0] + countU2R[i][1] + gammaSum);
			}
		}
		
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < S; ++j) {
				seta[i][j] = (countUSW[i][j] + salpha[j])
						/ (countU2W[i][0] + salphaSum);
			}
		}
		
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < T; ++j) {
				teta[i][j] = (countUTW[i][j] + talpha[j])
						/ (countU2W[i][1] + talphaSum);
			}
		}
		System.out.println("End update distribution.");
		
	}

	public void oneIter() {
		
		for (int i = 0; i < this.posts.size(); ++i) {
			System.out.println("sample post " + i);
			Post post = posts.get(i);			
			Content rootPost = post.contents.get(0);
			for(int j = 0; j < rootPost.content.length; ++j) {
				sampleRootWords(i, j, rootPost.content[j]);
			}
			for (int j = 1; j < post.contents.size(); ++j) {
				 // do sth
				sampleReply(i, j-1, post.contents.get(j));
			}
			
		}
	}

	private void sampleReply(int p, int w, Content content) {
		// TODO Auto-generated method stub
		boolean rstX = x[p][w];
		short rstZ = zr[p][w];
		
		for (int i = 0; i < content.content.length; ++i) {
			int word = content.content[i];
			if (rstX == false) {
				countSVW[rstZ][word] --;
				countSW[rstZ] --;
				countU2W[content.author][0] --;
				countUSW[content.author][rstZ] --;
			} else {
				countTVW[rstZ][word] --;
				countTW[rstZ] --;
				countU2W[content.author][1] --;
				countUTW[content.author][rstZ] --;
			}
		}
		
		// -- sample
		if (rstX == false) {
			countU2R[content.author][0] --;
		} else {
			countU2R[content.author][1] --;
		}
		
		int rst = drawReply(p, w, content);
		
		if (rst < S) {
			rstX = false;
			rstZ = (short)rst;
		} else {
			rstX = true;
			rstZ = (short)(rst - S);
		}
		
		x[p][w] = rstX;
		zr[p][w] = rstZ;
		
//		System.out.println("r z == " + rstZ);
		
		
		// recover
		for (int i = 0; i < content.content.length; ++i) {
			int word = content.content[i];
			if (rstX == false) {
				countSVW[rstZ][word] ++;
				countSW[rstZ] ++;
				countU2W[content.author][0] ++;
				countUSW[content.author][rstZ] ++;
			} else {
				countTVW[rstZ][word] ++;
				countTW[rstZ] ++;
				countU2W[content.author][1] ++;
				countUTW[content.author][rstZ] ++;
			}
		}
		
		if (rstX == false) {
			countU2R[content.author][0] ++;
		} else {
			countU2R[content.author][1] ++;
		}

	}

	private int drawReply(int p, int w, Content content) {
		// TODO Auto-generated method stub
		int word;
		int[] pCount = new int[T+S];
		
		HashMap<Integer, Integer> wordCnt = new HashMap<Integer, Integer>();
		for (int i = 0; i < content.content.length; ++i) {
			word = content.content[i];
			if (!wordCnt.containsKey(word)) {
				wordCnt.put(word, 1);
			} else {
				int count = wordCnt.get(word) + 1;
				wordCnt.put(word, count);
			}
		}
		
		double[] topicP;
		topicP = new double[S+T];
		int u = content.author;
		
		for(int i = 0; i < S; ++i) {
			topicP[i] = (countU2R[u][0] + gamma[0]) 
					* (countUSW[u][i] + salpha[i])
					/ (countU2W[u][0] + salphaSum);
//			System.out.println("topic i == " + topicP[i]);
			
//			if(topicP[i] < 0) {
//				System.out.println(countU2R[u][0]);
//				System.out.println(countUSW[u][i]);
//				System.out.println(countU2W[u][0]);
//				System.out.println("topic i == " + topicP[i]);
//			}
			
			int t = 0;
			Set s = wordCnt.entrySet();
			Iterator it = s.iterator();
			double bufferP = 1;
//			System.out.println("buffer P == " + bufferP);
			while(it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				word = (Integer) m.getKey();
				int count = (Integer) m.getValue();
				for (int j = 0; j < count; ++j) {
					double value = (countSVW[i][word] + sbeta[word] + j)
							/ (countSW[i] + sbetaSum + t);
					t ++;
//					System.out.println("value == " + value);
					bufferP *= value;
					bufferP = isOverFlow(bufferP, pCount, i);
//					System.out.println("buffer P == " + bufferP);
				}
			}
//			System.out.println("buffer P == " + bufferP);
			topicP[i] *= Math.pow(bufferP, 1.0);
		}
		
		for (int i = 0; i < T; ++i) {
			// lost some thing
			topicP[S + i] = (countU2R[u][1] + gamma[1]) 
					* (countUTW[u][i] + talpha[i])
					/ (countU2W[u][1] + talphaSum)
					* (countPTW[p][i] + countPTR[p][i] + zalpha[i])
					/ (posts.get(p).contents.get(0).content.length + posts.get(p).contents.size() - 1 - 1 + zalphaSum);
//			System.out.println("topic i == " + topicP[S+i]);
			
			int t = 0;
			Set s = wordCnt.entrySet();
			Iterator it = s.iterator();
			double bufferP = 1;
			while(it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				word = (Integer) m.getKey();
				int count = (Integer) m.getValue();
				for (int j = 0; j < count; ++j) {
					double value = (countTVW[i][word] + tbeta[word] + j)
							/ (countTW[i] + tbetaSum + t);
					t ++;
					bufferP *= value;
					bufferP = isOverFlow(bufferP, pCount, S+i);
				}
			}
			topicP[S + i] *= Math.pow(bufferP, 1.0);
		}
		
		reComputeProbs(topicP, pCount);
		
//		for (int i = 0; i < T+S; ++i) {
//			System.out.print("  " + topicP[i]);
//		}
//		System.out.println("");
		
		
		for (int i = 1; i < T+S; ++i) {
			topicP[i] += topicP[i-1];
		}
		double rand = Math.random() * topicP[T+S-1];
		int rst = 0;
		for (int i = 0; i < T+S; ++i) {
			if (topicP[i] >= rand) {
				rst = i;
				break;
			}
		}
		
		return rst;
	}

	private void reComputeProbs(double[] topicP, int[] pCount) {
		int max = pCount[0];
		// System.out.print(max + " ");
		for (int i = 1; i < pCount.length; ++i) {
			if (pCount[i] > max)
				max = pCount[i];
			// System.out.print(pCount[i] + " ");
		}
		
		for (int i = 0; i < pCount.length; i++) {
			topicP[i] = topicP[i] * Math.pow(1e150, pCount[i] - max);
		}
		
//		if (max > 0) {
//			System.out.print(pCount[0] + " ");
//			for (int i = 1; i < pCount.length; i++) {
//				System.out.print(pCount[i] + " ");
//			}
//			System.out.println();
//			// System.exit(0);
//		}
	}

	private double isOverFlow(double bufferP, int[] pCount, int i) {
		if (bufferP > 1e150) {
			pCount[i]++;
			return bufferP / 1e150;
		}
		if (bufferP < 1e-150) {
			pCount[i]--;
			return bufferP * 1e150;
		}
		return bufferP;
	}

	private void sampleRootWords(int i, int j, int word) {
		// TODO Auto-generated method stub
		short z = zw[i][j];
		countPTW[i][z] --;
		countTVW[z][word] --;
		countTW[z] --;
		
		z = drawZ(i, j, word);
		
		zw[i][j] = z;
		countPTW[i][z] ++;
		countTVW[z][word] ++;
		countTW[z] ++;

	}

	private short drawZ(int p, int w, int word) {
		// TODO Auto-generated method stub
		double[] topicP;
		topicP = new double[T];
		
		for (int i = 0; i < T; ++i) {
			topicP[i] = (countPTW[p][i] + countPTR[p][i] + zalpha[i]) 
					/ (posts.get(p).contents.get(0).content.length + posts.get(p).contents.size() - 1 - 2 + zalphaSum)
					* (countTVW[i][word] + tbeta[i]) 
					/ (countTW[i] + tbetaSum);
			
		}
		
		for (int i = 1; i < T; ++i) {
			topicP[i] += topicP[i-1];
		}
		
		double rand = Math.random() * topicP[T-1];
		short topic = 0;
		for (short i = 0; i < T; ++i) {
			if(rand <= topicP[i]) {
				topic = i;
				break;
			}
		}
		
		return topic;
	}

	public void outputResult(String filename, ArrayList<String> wordList) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				filename)));
		ArrayList<Integer> rankList = new ArrayList<Integer>();
		
		writer.write("Serious topics: \n");
		for (int i = 0; i < T; ++i) {
			writer.write("topic " + i + " -------------------- \n");
			rankList.clear();
			
			ComUtil.getTop(tphi[i], rankList, topNum);
			
			for (int j = 0; j < rankList.size(); ++j) {
//				System.out.println("ranklist " + j + " == " + rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t"
						+ tphi[i][rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}
		
		writer.write("Unserious topics: \n");
		for (int i = 0; i < S; ++i) {
			writer.write("topic " + i + " -------------------- \n");
			rankList.clear();
			
			ComUtil.getTop(sphi[i], rankList, topNum);
			
			for (int j = 0; j < rankList.size(); ++j) {
//				System.out.println("ranklist " + j + " == " + rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t"
						+ sphi[i][rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}
		
		writer.flush();
		writer.close();
		
	}

}
