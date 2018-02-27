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
	int[] countVWr;
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
		for (int i = 0; i < 2; ++i) {
			this.gamma[i] = modelParams.gamma;
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
		for (int i = 0; i < V; ++i) {
			this.countVWr[i] = 0;
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
						} else {
							t[i][j][k] = true;
							this.countTW[tp] ++;
							this.countTVW[tp][word] ++;
							this.countU2W[reply.author][1] ++;
						}
						// do sth
					}
				} else {
					this.countU2S[reply.author][0] ++;
					
					for (int k = 0; k < reply.content.length; ++k) {
						int word = reply.content[k];
						rand = Math.random();
						if (rand < 0.5) {
							t[i][j][k] = false;
							this.countU2W[reply.author][0] ++;
						} else {
							t[i][j][k] = true;
							this.countWr ++;
							this.countVWr[word] ++;
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
				ztheta[i][j] = (countPTW[i][j] + countPTR[i][j] + zalpha[j])
						/ (posts.get(i).contents.get(0).content.length + posts.get(i).contents.size() - 1 + zalphaSum);
			}
		}

		for (int i = 0; i < S; ++i) {
			for (int j = 0; j < V; ++j) {
				sphi[i][j] = (countSVW[i][j] + sbeta[j]) / (countSW[i] + sbetaSum);
			}
		}

		for (int i = 0; i < T; ++i) {
			for (int j = 0; j < V; ++j) {
				tphi[i][j] = (countTVW[i][j] + tbeta[j]) / (countTW[i] + tbetaSum);
			}
		}

		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < 2; ++j) {
				lambda[i][j] = (countU2R[i][j] + gamma[j]) / (countU2R[i][0] + countU2R[i][1] + gammaSum);
			}
		}

		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < S; ++j) {
				seta[i][j] = (countUSW[i][j] + salpha[j]) / (countU2W[i][0] + salphaSum);
			}
		}

		for (int i = 0; i < U; ++i) {
			for (int j = 0; j < T; ++j) {
				teta[i][j] = (countUTW[i][j] + talpha[j]) / (countU2W[i][1] + talphaSum);
			}
		}
		System.out.println("End update distribution.");

	}

	public void oneIter() {

		for (int i = 0; i < this.posts.size(); ++i) {
			System.out.println("sample post " + i);
			Post post = posts.get(i);
			Content rootPost = post.contents.get(0);
			for (int j = 0; j < rootPost.content.length; ++j) {
				sampleRootWords(i, j, rootPost.content[j]);
			}
			for (int j = 1; j < post.contents.size(); ++j) {
				// do sth
				sampleReply(i, j - 1, post.contents.get(j));
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
				countSVW[rstZ][word]--;
				countSW[rstZ]--;
				countU2W[content.author][0]--;
				countUSW[content.author][rstZ]--;
			} else {
				countTVW[rstZ][word]--;
				countTW[rstZ]--;
				countU2W[content.author][1]--;
				countUTW[content.author][rstZ]--;
			}
		}

		// -- sample
		if (rstX == false) {
			countU2R[content.author][0]--;
		} else {
			countU2R[content.author][1]--;
		}

		int rst = drawReply(p, w, content);

		if (rst < S) {
			rstX = false;
			rstZ = (short) rst;
		} else {
			rstX = true;
			rstZ = (short) (rst - S);
		}

		x[p][w] = rstX;
		zr[p][w] = rstZ;

		// System.out.println("r z == " + rstZ);

		// recover
		for (int i = 0; i < content.content.length; ++i) {
			int word = content.content[i];
			if (rstX == false) {
				countSVW[rstZ][word]++;
				countSW[rstZ]++;
				countU2W[content.author][0]++;
				countUSW[content.author][rstZ]++;
			} else {
				countTVW[rstZ][word]++;
				countTW[rstZ]++;
				countU2W[content.author][1]++;
				countUTW[content.author][rstZ]++;
			}
		}

		if (rstX == false) {
			countU2R[content.author][0]++;
		} else {
			countU2R[content.author][1]++;
		}

	}

	private int drawReply(int p, int w, Content content) {
		// TODO Auto-generated method stub
		int word;
		int[] pCount = new int[T + S];

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
		topicP = new double[S + T];
		int u = content.author;

		for (int i = 0; i < S; ++i) {
			topicP[i] = (countU2R[u][0] + gamma[0]) * (countUSW[u][i] + salpha[i]) / (countU2W[u][0] + salphaSum);
			// System.out.println("topic i == " + topicP[i]);

			// if(topicP[i] < 0) {
			// System.out.println(countU2R[u][0]);
			// System.out.println(countUSW[u][i]);
			// System.out.println(countU2W[u][0]);
			// System.out.println("topic i == " + topicP[i]);
			// }

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
					double value = (countSVW[i][word] + sbeta[word] + j) / (countSW[i] + sbetaSum + t);
					t++;
					// System.out.println("value == " + value);
					bufferP *= value;
					bufferP = isOverFlow(bufferP, pCount, i);
					// System.out.println("buffer P == " + bufferP);
				}
			}
			// System.out.println("buffer P == " + bufferP);
			topicP[i] *= Math.pow(bufferP, 1.0);
		}

		for (int i = 0; i < T; ++i) {
			// lost some thing
			topicP[S + i] = (countU2R[u][1] + gamma[1]) * (countUTW[u][i] + talpha[i]) / (countU2W[u][1] + talphaSum)
					* (countPTW[p][i] + countPTR[p][i] + zalpha[i])
					/ (posts.get(p).contents.get(0).content.length + posts.get(p).contents.size() - 1 - 1 + zalphaSum);
			// System.out.println("topic i == " + topicP[S+i]);

			int t = 0;
			Set s = wordCnt.entrySet();
			Iterator it = s.iterator();
			double bufferP = 1;
			while (it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				word = (Integer) m.getKey();
				int count = (Integer) m.getValue();
				for (int j = 0; j < count; ++j) {
					double value = (countTVW[i][word] + tbeta[word] + j) / (countTW[i] + tbetaSum + t);
					t++;
					bufferP *= value;
					bufferP = isOverFlow(bufferP, pCount, S + i);
				}
			}
			topicP[S + i] *= Math.pow(bufferP, 1.0);
		}

		reComputeProbs(topicP, pCount);

		// for (int i = 0; i < T+S; ++i) {
		// System.out.print(" " + topicP[i]);
		// }
		// System.out.println("");

		for (int i = 1; i < T + S; ++i) {
			topicP[i] += topicP[i - 1];
		}
		double rand = Math.random() * topicP[T + S - 1];
		int rst = 0;
		for (int i = 0; i < T + S; ++i) {
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
		countPTW[i][z]--;
		countTVW[z][word]--;
		countTW[z]--;

		z = drawZ(i, j, word);

		zw[i][j] = z;
		countPTW[i][z]++;
		countTVW[z][word]++;
		countTW[z]++;

	}

	private short drawZ(int p, int w, int word) {
		// TODO Auto-generated method stub
		double[] topicP;
		topicP = new double[T];

		for (int i = 0; i < T; ++i) {
			topicP[i] = (countPTW[p][i] + countPTR[p][i] + zalpha[i])
					/ (posts.get(p).contents.get(0).content.length + posts.get(p).contents.size() - 1 - 2 + zalphaSum)
					* (countTVW[i][word] + tbeta[i]) / (countTW[i] + tbetaSum);

		}

		for (int i = 1; i < T; ++i) {
			topicP[i] += topicP[i - 1];
		}

		double rand = Math.random() * topicP[T - 1];
		short topic = 0;
		for (short i = 0; i < T; ++i) {
			if (rand <= topicP[i]) {
				topic = i;
				break;
			}
		}

		return topic;
	}

	public void outputResult(String filename, ArrayList<String> wordList) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
		ArrayList<Integer> rankList = new ArrayList<Integer>();

		writer.write("Serious topics: \n");
		for (int i = 0; i < T; ++i) {
			writer.write("topic " + i + " -------------------- \n");
			rankList.clear();

			ComUtil.getTop(tphi[i], rankList, topNum);

			for (int j = 0; j < rankList.size(); ++j) {
				// System.out.println("ranklist " + j + " == " +
				// rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t" + tphi[i][rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}

		writer.write("Unserious topics: \n");
		for (int i = 0; i < S; ++i) {
			writer.write("topic " + i + " -------------------- \n");
			rankList.clear();

			ComUtil.getTop(sphi[i], rankList, topNum);

			for (int j = 0; j < rankList.size(); ++j) {
				// System.out.println("ranklist " + j + " == " +
				// rankList.get(j));
				String tmp = "\t" + wordList.get(rankList.get(j)) + "\t" + sphi[i][rankList.get(j)];
				writer.write(tmp + "\n");
			}
		}

		writer.flush();
		writer.close();

	}

}
