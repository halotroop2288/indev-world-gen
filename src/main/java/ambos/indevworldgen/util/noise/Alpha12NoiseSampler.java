package ambos.indevworldgen.util.noise;

import java.util.Random;

public class Alpha12NoiseSampler {

	public Alpha12NoiseSampler() {
		this(new Random());
	}

	public Alpha12NoiseSampler(Random rand)
	{
		p = new int[512];
		xOffset = rand.nextDouble() * 256D;
		yOffset = rand.nextDouble() * 256D;
		zOffset = rand.nextDouble() * 256D;
		for(int permIndex = 0; permIndex < 256; permIndex++)
		{
			p[permIndex] = permIndex;
		}

		for(int permIndex = 0; permIndex < 256; permIndex++)
		{
			int newPermIndex = rand.nextInt(256 - permIndex) + permIndex;
			int value = p[permIndex];
			p[permIndex] = p[newPermIndex];
			p[newPermIndex] = value;
			p[permIndex + 256] = p[permIndex];
		}

	}
	
	// I can't be bothered to map every local var AGAIN
	// Already mapped them all in the indev one
	public double sample(double x, double y, double z) {
		double sampleX = x + xOffset;
		double sampleY = y + yOffset;
		double sampleZ = z + zOffset;
		int floorX = (int)sampleX;
		int floorY = (int)sampleY;
		int floorZ = (int)sampleZ;
		if(sampleX < (double)floorX)
		{
			floorX--;
		}
		if(sampleY < (double)floorY)
		{
			floorY--;
		}
		if(sampleZ < (double)floorZ)
		{
			floorZ--;
		}
		int l = floorX & 0xff;
		int i1 = floorY & 0xff;
		int j1 = floorZ & 0xff;
		sampleX -= floorX;
		sampleY -= floorY;
		sampleZ -= floorZ;
		double fadeX = sampleX * sampleX * sampleX * (sampleX * (sampleX * 6D - 15D) + 10D);
		double fadeY = sampleY * sampleY * sampleY * (sampleY * (sampleY * 6D - 15D) + 10D);
		double fadeZ = sampleZ * sampleZ * sampleZ * (sampleZ * (sampleZ * 6D - 15D) + 10D);
		int k1 = p[l] + i1;
		int l1 = p[k1] + j1;
		int i2 = p[k1 + 1] + j1;
		int j2 = p[l + 1] + i1;
		int k2 = p[j2] + j1;
		int l2 = p[j2 + 1] + j1;
		return lerp(fadeZ, lerp(fadeY, lerp(fadeX, grad(p[l1], sampleX, sampleY, sampleZ), grad(p[k2], sampleX - 1.0D, sampleY, sampleZ)), lerp(fadeX, grad(p[i2], sampleX, sampleY - 1.0D, sampleZ), grad(p[l2], sampleX - 1.0D, sampleY - 1.0D, sampleZ))), lerp(fadeY, lerp(fadeX, grad(p[l1 + 1], sampleX, sampleY, sampleZ - 1.0D), grad(p[k2 + 1], sampleX - 1.0D, sampleY, sampleZ - 1.0D)), lerp(fadeX, grad(p[i2 + 1], sampleX, sampleY - 1.0D, sampleZ - 1.0D), grad(p[l2 + 1], sampleX - 1.0D, sampleY - 1.0D, sampleZ - 1.0D))));
	}

	public final double lerp(double d, double d1, double d2)
	{
		return d1 + d * (d2 - d1);
	}

	public final double grad(int hash, double d, double d1)
	{
		int j = hash & 0xf;
		double d2 = (double)(1 - ((j & 8) >> 3)) * d;
		double d3 = j >= 4 ? j != 12 && j != 14 ? d1 : d : 0.0D;
		return ((j & 1) != 0 ? -d2 : d2) + ((j & 2) != 0 ? -d3 : d3);
	}

	public final double grad(int i, double d, double d1, double d2)
	{
		int j = i & 0xf;
		double d3 = j >= 8 ? d1 : d;
		double d4 = j >= 4 ? j != 12 && j != 14 ? d2 : d : d1;
		return ((j & 1) != 0 ? -d3 : d3) + ((j & 2) != 0 ? -d4 : d4);
	}

	public double sample(double x, double y)
	{
		return sample(x, y, 0.0D);
	}

	public void sample(double ad[], double d, double d1, double d2, int i, int j, int k, double d3, double d4, double d5, double d6) {
		if(j == 1)
		{
			int j4 = 0;
			double d17 = 1.0D / d6;
			for(int k4 = 0; k4 < i; k4++)
			{
				double d19 = (d + (double)k4) * d3 + xOffset;
				int i5 = (int)d19;
				if(d19 < (double)i5)
				{
					i5--;
				}
				int j5 = i5 & 0xff;
				d19 -= i5;
				double d21 = d19 * d19 * d19 * (d19 * (d19 * 6D - 15D) + 10D);
				for(int i6 = 0; i6 < k; i6++)
				{
					double d23 = (d2 + (double)i6) * d5 + zOffset;
					int k6 = (int)d23;
					if(d23 < (double)k6)
					{
						k6--;
					}
					int l6 = k6 & 0xff;
					d23 -= k6;
					double d25 = d23 * d23 * d23 * (d23 * (d23 * 6D - 15D) + 10D);
					int l = p[j5] + 0;
					int j1 = p[l] + l6;
					int l1 = p[j5 + 1] + 0;
					int j2 = p[l1] + l6;
					double d9 = lerp(d21, grad(p[j1], d19, d23), grad(p[j2], d19 - 1.0D, 0.0D, d23));
					double d12 = lerp(d21, grad(p[j1 + 1], d19, 0.0D, d23 - 1.0D), grad(p[j2 + 1], d19 - 1.0D, 0.0D, d23 - 1.0D));
					double d27 = lerp(d25, d9, d12);
					ad[j4++] += d27 * d17;
				}

			}

			return;
		}
		int i1 = 0;
		double d7 = 1.0D / d6;
		int i2 = -1;
		double d13 = 0.0D;
		double d15 = 0.0D;
		double d16 = 0.0D;
		double d18 = 0.0D;
		for(int l4 = 0; l4 < i; l4++)
		{
			double d20 = (d + (double)l4) * d3 + xOffset;
			int k5 = (int)d20;
			if(d20 < (double)k5)
			{
				k5--;
			}
			int l5 = k5 & 0xff;
			d20 -= k5;
			double d22 = d20 * d20 * d20 * (d20 * (d20 * 6D - 15D) + 10D);
			for(int j6 = 0; j6 < k; j6++)
			{
				double d24 = (d2 + (double)j6) * d5 + zOffset;
				int i7 = (int)d24;
				if(d24 < (double)i7)
				{
					i7--;
				}
				int j7 = i7 & 0xff;
				d24 -= i7;
				double d26 = d24 * d24 * d24 * (d24 * (d24 * 6D - 15D) + 10D);
				for(int k7 = 0; k7 < j; k7++)
				{
					double d28 = (d1 + (double)k7) * d4 + yOffset;
					int l7 = (int)d28;
					if(d28 < (double)l7)
					{
						l7--;
					}
					int i8 = l7 & 0xff;
					d28 -= l7;
					double d29 = d28 * d28 * d28 * (d28 * (d28 * 6D - 15D) + 10D);
					if(k7 == 0 || i8 != i2)
					{
						i2 = i8;
						int k2 = p[l5] + i8;
						int i3 = p[k2] + j7;
						int j3 = p[k2 + 1] + j7;
						int k3 = p[l5 + 1] + i8;
						int l3 = p[k3] + j7;
						int i4 = p[k3 + 1] + j7;
						d13 = lerp(d22, grad(p[i3], d20, d28, d24), grad(p[l3], d20 - 1.0D, d28, d24));
						d15 = lerp(d22, grad(p[j3], d20, d28 - 1.0D, d24), grad(p[i4], d20 - 1.0D, d28 - 1.0D, d24));
						d16 = lerp(d22, grad(p[i3 + 1], d20, d28, d24 - 1.0D), grad(p[l3 + 1], d20 - 1.0D, d28, d24 - 1.0D));
						d18 = lerp(d22, grad(p[j3 + 1], d20, d28 - 1.0D, d24 - 1.0D), grad(p[i4 + 1], d20 - 1.0D, d28 - 1.0D, d24 - 1.0D));
					}
					double d30 = lerp(d29, d13, d15);
					double d31 = lerp(d29, d16, d18);
					double d32 = lerp(d26, d30, d31);
					ad[i1++] += d32 * d7;
				}

			}

		}

	}

	private int p[];
	public double xOffset;
	public double yOffset;
	public double zOffset;
}