package ambos.indevworldgen.util.noise;

import java.util.Random;

public class OctaveAlpha12NoiseSampler {

	public OctaveAlpha12NoiseSampler(Random random, int octaveCount) {
		octaves = octaveCount;
		octaveSamplers = new Alpha12NoiseSampler[octaveCount];
		for(int j = 0; j < octaveCount; j++)
		{
			octaveSamplers[j] = new Alpha12NoiseSampler(random);
		}

	}

	public double sample(double d, double d1)
	{
		double d2 = 0.0D;
		double d3 = 1.0D;
		for(int i = 0; i < octaves; i++)
		{
			d2 += octaveSamplers[i].sample(d * d3, d1 * d3) / d3;
			d3 /= 2D;
		}

		return d2;
	}

	public double[] sample(double ad[], double d, double d1, double d2, int i, int j, int k, double d3, double d4,  double d5) {
		if(ad == null)
		{
			ad = new double[i * j * k];
		} else
		{
			for(int l = 0; l < ad.length; l++)
			{
				ad[l] = 0.0D;
			}

		}
		double d6 = 1.0D;
		for(int i1 = 0; i1 < octaves; i1++)
		{
			octaveSamplers[i1].sample(ad, d, d1, d2, i, j, k, d3 * d6, d4 * d6, d5 * d6, d6);
			d6 /= 2D;
		}

		return ad;
	}

	public double[] sample(double ad[], int i, int j, int k, int l, double d,  double d1, double d2) {
		return sample(ad, i, 10D, j, k, 1, l, d, 1.0D, d1);
	}

	private Alpha12NoiseSampler[] octaveSamplers;
	private int octaves;
}