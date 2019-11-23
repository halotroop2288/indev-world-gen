package ambos.indevworldgen.util.noise;

import java.util.Random;

public class OctaveIndevNoiseSampler {

	public OctaveIndevNoiseSampler(Random rand, int octaveCount) {
		octaves = octaveCount;
		indevNoiseSamplers = new IndevNoiseSampler[octaveCount];
		for(int j = 0; j < octaveCount; j++) {
			indevNoiseSamplers[j] = new IndevNoiseSampler(rand);
		}
	}
	
	private IndevNoiseSampler indevNoiseSamplers[];
	private int octaves;

	public final double sample(double x, double y) {
		double result = 0.0D;
		double freqAmpl = 1.0D;
		for (int i = 0; i < this.octaves; i++) {
			result += this.indevNoiseSamplers[i].sample(x / freqAmpl, y / freqAmpl) * freqAmpl;
			freqAmpl *= 2.0D;
		}
		return result;
	}

	public final double sample(double x, double y, double z) {
		double result = 0.0D;
		double freqAmpl = 1.0D;
		for (int i = 0; i < this.octaves; i++) {
			result += this.indevNoiseSamplers[i].sample(x / freqAmpl, 0.0D / freqAmpl, z / freqAmpl) * freqAmpl;
			freqAmpl *= 2.0D;
		}
		return result;
	}
}