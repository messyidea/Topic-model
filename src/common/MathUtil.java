package common;

public class MathUtil {
	public int sample(double[] raw) {
		for (int i = 1; i < raw.length; ++i) {
            raw[i] += raw[i] + raw[i - 1];
        }
        if (raw[raw.length - 1] == 0)
            return 0;
        double p = Math.random() * raw[raw.length - 1];
        int re;
        for (re = 0; re < raw.length; re++)
            if (p < raw[re])
                break;
        if (re > raw.length - 1)
            return -2;
        return re;
	}
}
