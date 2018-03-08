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
import common.MathUtil;

public class Model {
	ArrayList<Post> posts;

	int T, V, U, P;
	int nIter;
	int topNum;

	float[] alpha;
	float alphaSum;

	float[] beta;
	float betaSum;
	float[] bbeta;
	float bbetaSum;
	float[] rbeta;
	float rbetaSum;

	float[] gamma;
	float gammaSum;
	float[] lambda;
	float lambdaSum;

	boolean[][] r;
	boolean[][][] t;
	short[][] z; // z word

	float[][] theta;

	float[][] phi;
	float[] bphi;
	float[] rphi;

	float[][] pi;
	float[][] eta;

	int[][] countU2S;
	int[][] countPTS;
	int[] countPS;
	int[] countTW;
	int[][] countTVW;
	int countWr;
	int countWb;
	int[] countVWr;
	int[] countVWb;
	int[][] countU2W;
	

	public Model(ModelParams modelParams, ArrayList<Post> posts) {
		// TODO Auto-generated constructor stub
		this.posts = posts;

		this.nIter = modelParams.iteration;
		this.topNum = modelParams.topNum;

		this.T = modelParams.T;
		this.V = modelParams.V;
		this.U = modelParams.U;
		this.P = this.posts.size();

		this.alpha = new float[T];
		this.alphaSum = 0;
		for (int i = 0; i < T; ++i) {
			this.alpha[i] = modelParams.alpha;
			this.alphaSum += this.alpha[i];
		}

		this.beta = new float[V];
		this.bbeta = new float[V];
		this.rbeta = new float[V];
		this.betaSum = 0;
		this.bbetaSum = 0;
		this.rbetaSum = 0;
		for (int i = 0; i < V; ++i) {
			this.beta[i] = modelParams.beta;
			this.betaSum += this.beta[i];
			this.bbeta[i] = modelParams.beta;
			this.bbetaSum += this.bbeta[i];
			this.rbeta[i] = modelParams.beta;
			this.rbetaSum += this.rbeta[i];
		}

		this.gamma = new float[2];
		this.lambda = new float[2];
		this.gammaSum = 0;
		this.lambdaSum = 0;
		this.gamma[0] = 1;
		this.gamma[1] = 10;
		for (int i = 0; i < 2; ++i) {
//			this.gamma[i] = modelParams.gamma;
			this.gammaSum += this.gamma[i];
			this.lambda[i] = modelParams.lambda;
			this.lambdaSum += this.lambda[i];
		}

		this.theta = new float[P][T];
		this.countPTS = new int[P][T];
		this.countPS = new int[P];
		for (int i = 0; i < P; ++i) {
			this.countPS[i] = 0;
			for (int j = 0; j < T; ++j) {
				this.theta[i][j] = 0;
				this.countPTS[i][j] = 0;
			}
		}

		this.phi = new float[T][V];
		this.countTW = new int[T];
		this.countTVW = new int[T][V];
		for (int i = 0; i < T; ++i) {
			this.countTW[i] = 0;
			for (int j = 0; j < V; ++j) {
				this.phi[i][j] = 0;
				this.countTVW[i][j] = 0;
			}
		}

		this.bphi = new float[V];
		this.rphi = new float[V];
		this.countVWr = new int[V];
		this.countVWb = new int[V];
		for (int i = 0; i < V; ++i) {
			this.countVWr[i] = 0;
			this.countVWb[i] = 0;
			this.bphi[i] = 0;
			this.rphi[i] = 0;
		}

		this.pi = new float[U][2];
		this.eta = new float[U][2];
		this.countU2S = new int[U][2];
		this.countU2W = new int[U][2];
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.pi[i][j] = 0;
				this.eta[i][j] = 0;
				this.countU2S[i][j] = 0;
				this.countU2W[i][j] = 0;
			}
		}
		
		this.countWr = 0;
		this.countWb = 0;

	}

	public void intialize() {
		System.out.println("Start init.");

		this.z = new short[this.posts.size()][];
		this.r = new boolean[this.posts.size()][];
		this.t = new boolean[this.posts.size()][][];

		for (int i = 0; i < this.posts.size(); ++i) {
			Post post = posts.get(i);
			z[i] = new short[post.contents.size()];
			r[i] = new boolean[post.contents.size()];
			t[i] = new boolean[post.contents.size()][];

			for (int j = 0; j < post.contents.size(); ++j) {
				Content reply = post.contents.get(j);
				t[i][j] = new boolean[reply.content.length];
				
				double rand = Math.random();
				boolean bufferX;
				if (rand > 0.5) {
					bufferX = true;
				} else {
					bufferX = false;
				}
				
				if (reply.fix == true) {
//					System.out.println("fix!!!!!!!!!!");
//					System.exit(1);
					bufferX = reply.type;
				}

				r[i][j] = bufferX;
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
					z[i][j] = tp;
					
					this.countU2S[reply.author][1] ++;
					this.countPTS[i][tp] ++;
					this.countPS[i] ++;

					for (int k = 0; k < reply.content.length; ++k) {
						int word = reply.content[k];
						rand = Math.random();
						if (rand < 0.5) {
							t[i][j][k] = false;
							// background word
							this.countU2W[reply.author][0] ++;
							this.countVWb[word] ++;
							this.countWb ++;
						} else {
							t[i][j][k] = true;
							this.countTW[tp] ++;
							this.countTVW[tp][word] ++;
							this.countU2W[reply.author][1] ++;
						}
						// do sth
					}
				} else {
					z[i][j] = -1;
					this.countU2S[reply.author][0] ++;
					
					for (int k = 0; k < reply.content.length; ++k) {
						int word = reply.content[k];
						rand = Math.random();
						if (rand < 0.5) {
							t[i][j][k] = false;
							this.countU2W[reply.author][0] ++;
							this.countVWb[word] ++;
							this.countWb ++;
						} else {
							t[i][j][k] = true;
							this.countWr ++;
							this.countVWr[word] ++;
							this.countU2W[reply.author][1] ++;
						}

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
			niter++;
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
				this.theta[i][j] = (countPTS[i][j] + alpha[j])
						/ (countPS[i] + alphaSum);
			}
		}
		
		for (int i = 0; i < T; ++i) {
			for (int j = 0; j < V; ++j) {
				this.phi[i][j] = (countTVW[i][j] + beta[j]) / (countTW[i] + betaSum);
			}
		}
		
		for (int i = 0; i < V; ++i) {
			this.rphi[i] = (countVWr[i] + rbeta[i]) / (countWr + rbetaSum);
			this.bphi[i] = (countVWb[i] + bbeta[i]) / (countWb + bbetaSum);
		}
		
		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.pi[i][j] = (countU2S[i][j] + gamma[j]) / (countU2S[i][0] + countU2S[i][1] + gammaSum);
				this.eta[i][j] = (countU2W[i][j] + lambda[j]) / (countU2W[i][0] + countU2W[i][1] + lambdaSum);
			}
		}
		
		System.out.println("End update distribution.");

	}

	public void oneIter() {

		for (int i = 0; i < this.posts.size(); ++i) {
//			System.out.println("sample post " + i);
			Post post = posts.get(i);
			for (int j = 0; j < post.contents.size(); ++j) {
				Content reply = post.contents.get(j);
				if (!reply.fix) {
					sampleReply(i, j, reply);
				}
				for (int k = 0; k < reply.content.length; ++k) {
					int word = reply.content[k];
					sampleWords(i, j, k, word, reply);
				}
			}
		}
	}

	private void sampleWords(int p, int s, int n, int word, Content reply) {
		boolean rstT = t[p][s][n];
		boolean rstR = r[p][s];
		int rstZ = z[p][s];
		int u = reply.author;
		
		
		if (rstT == true) {
			this.countU2W[u][1] --;
			if (rstR == true) {
				this.countTVW[rstZ][word] --;
				this.countTW[rstZ] --;
			} else {
				this.countVWr[word] --;
				this.countWr --;
			}
		} else {
			this.countU2W[u][0] --;
			this.countVWb[word] --;
			this.countWb --;
		}
		
		double[] topicP;
		topicP = new double[2];
		
		topicP[0] = (countU2W[u][0] + lambda[0])
				* (countVWb[word] + bbeta[word])
				/ (countWb + bbetaSum);
		
		if (rstR == true) {
			topicP[1] = (countU2W[u][1] + lambda[1])
					* (countTVW[rstZ][word] + beta[word])
					/ (countTW[rstZ] + betaSum);
		} else {
			topicP[1] = (countU2W[u][1] + lambda[1])
					* (countVWr[word] + rbeta[word])
					/ (countWr + rbetaSum);
		}
		
		if (topicP[0] > topicP[1]) {
			rstT = false;
		} else {
			rstT = true;
		}
		
		t[p][s][n] = rstT;
		
		// recover
		if (rstT == true) {
			this.countU2W[u][1] ++;
			if (rstR == true) {
				this.countTVW[rstZ][word] ++;
				this.countTW[rstZ] ++;
			} else {
				this.countVWr[word] ++;
				this.countWr ++;
			}
		} else {
			this.countU2W[u][0] ++;
			this.countVWb[word] ++;
			this.countWb ++;
		}
		
	}

	private void sampleReply(int p, int w, Content content) {
		// TODO Auto-generated method stub
		boolean rstR = r[p][w];
		short rstZ = z[p][w];

		if (rstR) {
			this.countU2S[content.author][1] --;
			this.countPTS[p][rstZ] --;
			this.countPS[p] --;
		} else {
			this.countU2S[content.author][0] --;
		}
		
		for (int i = 0; i < content.content.length; ++i) {
			int word = content.content[i];
			if (t[p][w][i] == true) {
				if (rstR) {
					this.countTW[rstZ] --;
					this.countTVW[rstZ][word] --;
					if (countTVW[rstZ][word] < 0) {
						System.out.println("???????????????");
						System.out.println(rstZ);
						System.out.println(word);
						System.out.println(this.countTVW[rstZ][word]);
						System.exit(1);
					}
				} else {
					this.countWr --;
					this.countVWr[word] --;
				}
			}
			
		}
		
		

		int rst = drawReply(p, w, content);
//		System.out.println("rst == " + rst);
		
		if (rst == T) {
			rstR = false;
			rstZ = -1;
		} else {
			rstR = true;
			rstZ = (short)rst;
		}
		
//		System.out.println(rst);
//		System.out.println(rstR);
//		System.out.println(rstZ);
		r[p][w] = rstR;
		z[p][w] = rstZ;

		// recover
//		System.out.println("rstR: " + rstR);
//		System.out.println("rstZ: " + rstZ);
//		System.out.println("author: " + content.author);
		if (rstR) {
			this.countU2S[content.author][1] ++;
			this.countPTS[p][rstZ] ++;
			this.countPS[p] ++;
		} else {
			this.countU2S[content.author][0] ++;
		}
		
		for (int i = 0; i < content.content.length; ++i) {
			int word = content.content[i];
			if (t[p][w][i] == true) {
				if (rstR) {
					this.countTW[rstZ] ++;
					this.countTVW[rstZ][word] ++;
				} else {
					this.countWr ++;
					this.countVWr[word] ++;
				}
			}
		}

	}

	private int drawReply(int p, int w, Content content) {
		// TODO Auto-generated method stub
		int word;
		int[] pCount = new int[T+1];

		HashMap<Integer, Integer> wordCnt = new HashMap<Integer, Integer>();
		for (int i = 0; i < content.content.length; ++i) {
			word = content.content[i];
			if (t[p][w][i] == true) {
				if (!wordCnt.containsKey(word)) {
					wordCnt.put(word, 1);
				} else {
					int count = wordCnt.get(word) + 1;
					wordCnt.put(word, count);
				}
			}
		}

		double[] topicP;
		topicP = new double[T+1];
//		double[] topicPpre = new double[T+1];
//		double[] mess = new double[T+1];
//		double[] mess1 = new double[T+1];
//		double[] mess2 = new double[T+1];
//		double[] mess3 = new double[T+1];
		int u = content.author;

		for (int i = 0; i < T; ++i) {
			topicP[i] = (countU2S[u][1] + gamma[1])
					* (countPTS[p][i] + alpha[i])
					/ (countPS[p] + alphaSum);
//			mess[i] = topicP[i];
//			mess1[i] = countU2S[u][1];
//			mess2[i] = countPTS[p][i];
//			mess3[i] = countPS[p];
//			System.out.println("------");
//			System.out.println("  " + topicP[i]);

			int t = 0;
			Set s = wordCnt.entrySet();
			Iterator it = s.iterator();
			double bufferP = 1;
			// System.out.println("buffer P == " + bufferP);
			while (it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				word = (Integer) m.getKey();
				int count = (Integer) m.getValue();
				for (int j = 0; j < count; ++j) {
					double value = (countTVW[i][word] + beta[word] + j) / (countTW[i] + betaSum + t);
//					if (value < 0) {
//						System.out.println("value < 0");
//						System.out.println(countTW[i]);
//						System.out.println(countTVW[i][word]);
//						System.out.println(beta[word]);
//						System.out.println(betaSum);
//					}
					t++;
					// System.out.println("value == " + value);
//					System.out.println("----" + value);
//					System.out.println("====" + bufferP);
//					double bef = bufferP;
					bufferP *= value;
//					double bef2 = bufferP;
					bufferP = isOverFlow(bufferP, pCount, i);
//					if (!Double.isFinite(bufferP)) {
//						System.out.println("----value  " + value);
//						System.out.println("----bef  " + bef);
//						System.out.println("----bef2  " + bef2);
//						System.out.println("====bufferP  " + bufferP);
//					}
					// System.out.println("buffer P == " + bufferP);
				}
			}
			// System.out.println("buffer P == " + bufferP);
			topicP[i] *= Math.pow(bufferP, 1.0);
		}

		for (int i = 0; i < 1; ++i) {
			// lost some thing
			topicP[T] = (countU2S[u][0] + gamma[0]);
//			mess[T] = topicP[T];
//			mess1[i] = countU2S[u][0];
//			mess2[i] = countU2S[u][0];
//			mess3[i] = countU2S[u][0];

			int t = 0;
			Set s = wordCnt.entrySet();
			Iterator it = s.iterator();
			double bufferP = 1;
			while (it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				word = (Integer) m.getKey();
				int count = (Integer) m.getValue();
				for (int j = 0; j < count; ++j) {
					double value = (countVWr[word] + rbeta[word] + j) / (countWr + rbetaSum + t);
					t++;
//					System.out.println("----" + value);
//					System.out.println("----" + bufferP);
					bufferP *= value;
					bufferP = isOverFlow(bufferP, pCount, T);
				}
			}
			topicP[T] *= Math.pow(bufferP, 1.0);
		}
//		for (int i = 0; i < T + 1; ++i) {
//			System.out.print(" " + topicP[i]);
//		}
//		System.out.println();
//		for (int i = 0; i < topicP.length; ++i) {
//			topicPpre[i] = topicP[i];
//		}
		reComputeProbs(topicP, pCount);

		// for (int i = 0; i < T+S; ++i) {
		// System.out.print(" " + topicP[i]);
		// }
		// System.out.println("");
		int rst = MathUtil.sample(topicP);
//		if (rst == -2) {
//			for(int i = 0; i < mess.length; ++i) {
//				System.out.print("  " + mess[i]);
//			}
//			System.out.println();
//			for(int i = 0; i < mess1.length; ++i) {
//				System.out.print("  " + mess1[i]);
//			}
//			System.out.println();
//			for(int i = 0; i < mess2.length; ++i) {
//				System.out.print("  " + mess2[i]);
//			}
//			System.out.println();
//			for(int i = 0; i < mess3.length; ++i) {
//				System.out.print("  " + mess3[i]);
//			}
//			System.out.println();
//			for(int i = 0; i < pCount.length; ++i) {
//				System.out.print("  " + pCount[i]);
//			}
//			System.out.println();
//			for(int i = 0; i < topicPpre.length; ++i) {
//				System.out.print("  " + topicPpre[i]);
//			}
//			System.out.println();
//			for(int i = 0; i < topicP.length; ++i) {
//				System.out.print("  " + topicP[i]);
//			}
//			System.out.println();
//		}
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

		// if (max > 0) {
		// System.out.print(pCount[0] + " ");
		// for (int i = 1; i < pCount.length; i++) {
		// System.out.print(pCount[i] + " ");
		// }
		// System.out.println();
		// // System.exit(0);
		// }
	}

	private double isOverFlow(double bufferP, int[] pCount, int i) {
//		System.out.println("before  " + bufferP);
		if (bufferP > 1e150) {
			pCount[i]++;
			return bufferP / 1e150;
		}
		if (bufferP < 1e-150) {
			pCount[i]--;
			return bufferP * 1e150;
		}
//		System.out.println("after  " + bufferP);
		return bufferP;
	}

	public void outputResult(String filename, ArrayList<String> wordList) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
		ArrayList<Integer> rankList = new ArrayList<Integer>();

		writer.write("topics: \n");
		for (int i = 0; i < T; ++i) {
			writer.write("topic " + i + " -------------------- \n");
			rankList.clear();

			ComUtil.getTop(phi[i], rankList, topNum);

			for (int j = 0; j < rankList.size(); ++j) {
				System.out.println("ranklist " + j + " == " + rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t" + phi[i][rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}

		writer.write("rubbish words: \n");
		for (int i = 0; i < 1; ++i) {
			rankList.clear();

			ComUtil.getTop(rphi, rankList, 50);

			for (int j = 0; j < rankList.size(); ++j) {
				// System.out.println("ranklist " + j + " == " +
				// rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t" + rphi[rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}
		
		writer.write("background words: \n");
		for (int i = 0; i < 1; ++i) {
			rankList.clear();

			ComUtil.getTop(bphi, rankList, 50);

			for (int j = 0; j < rankList.size(); ++j) {
				// System.out.println("ranklist " + j + " == " +
				// rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t" + bphi[rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}
		
//		for (int i = 0; i < P; ++i) {
//			Post post = posts.get(i);
//			for (int j = 0; j < post.contents.size(); ++j) {
////				System.out.print("  " + z[i][j]);
//				Content reply = post.contents.get(j);
//				for (int k = 0; k < reply.content.length; ++k) {
//					System.out.println("  " + t[i][j][k]);
//				}
//			}
//			System.out.println();
//		}

		writer.flush();
		writer.close();

	}
	
	public void outputTopicResult(String filename, ArrayList<String> wordList) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));

		for (int i = 0; i < posts.size(); ++i) {
			Post post = posts.get(i);
			for (int j = 0; j < post.contents.size(); ++j) {
				Content cc = post.contents.get(j);
				writer.write("" + z[i][j] + "  :");
				for (int k = 0; k < cc.content.length; ++k) {
					if (t[i][j][k]) {
						writer.write("  " + wordList.get(cc.content[k]) + "1");
					} else {
						writer.write("  " + wordList.get(cc.content[k]) + "0");
					}
				}
				writer.write("\n");
			}
			writer.write("\n------------------------------------------\n");
		}

		writer.flush();
		writer.close();

	}

}
