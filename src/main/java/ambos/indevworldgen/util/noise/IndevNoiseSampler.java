package ambos.indevworldgen.util.noise;

import java.util.Random;

import net.minecraft.util.math.MathHelper;

public class IndevNoiseSampler {
	private int[] p = new int[512];
	
	private double offsetX;
	private double offsetY;
	private double offsetZ;

	public IndevNoiseSampler() {
		this(new Random());
	}

	public IndevNoiseSampler(Random rand) {
		this.offsetX = rand.nextDouble() * 256.0D;
		this.offsetY = rand.nextDouble() * 256.0D;
		this.offsetZ = rand.nextDouble() * 256.0D;
		for (int permIndex = 0; permIndex < 256; permIndex++) {
			this.p[permIndex] = permIndex;
		}
		for (int permIndex = 0; permIndex < 256; permIndex++) {
			int newPermIndex = rand.nextInt(256 - permIndex) + permIndex;
			int perm = this.p[permIndex];
			this.p[permIndex] = this.p[newPermIndex];
			this.p[newPermIndex] = perm;
			this.p[(permIndex + 256)] = this.p[permIndex];
		}
	}

	private double sampleInternal(double x, double y, double z) {
		double sampleX = x + offsetX;
		double sampleY = y + offsetY;
		double sampleZ = z + offsetZ;
		double floorX = MathHelper.floor(sampleX) & 0xff;
		double floorY = MathHelper.floor(sampleY) & 0xff;
		double floorZ =  MathHelper.floor(sampleZ) & 0xff;
		sampleX -=  MathHelper.floor(sampleX);
		sampleY -=  MathHelper.floor(sampleY);
		sampleZ -=  MathHelper.floor(sampleZ);
		double fadeX = fade(sampleX);
		double fadeY = fade(sampleY);
		double fadeZ = fade(sampleZ);
		double l = p[(int)floorX] + floorY;
		z = p[(int)l] + floorZ;
		l = p[(int)l + 1] + floorZ;
		floorX = p[(int)floorX + 1] + floorY;
		floorY = p[(int)floorX] + floorZ;
		floorX = p[(int)floorX + 1] + floorZ;
		return lerp(fadeZ, lerp(fadeY, lerp(fadeX, grad(p[(int)z], sampleX, sampleY, sampleZ), grad(p[(int)floorY], sampleX - 1.0D, sampleY, sampleZ)), lerp(fadeX, grad(p[(int)l], sampleX, sampleY - 1.0D, sampleZ), grad(p[(int)floorX], sampleX - 1.0D, sampleY - 1.0D, sampleZ))), lerp(fadeY, lerp(fadeX, grad(p[(int)z + 1], sampleX, sampleY, sampleZ - 1.0D), grad(p[(int)floorY + 1], sampleX - 1.0D, sampleY, sampleZ - 1.0D)), lerp(fadeX,  grad(p[(int)l + 1], sampleX, sampleY - 1.0D, sampleZ - 1.0D), grad(p[(int)floorX + 1], sampleX - 1.0D, sampleY - 1.0D, sampleZ - 1.0D))));
	}	

	private static double fade(double value) {
		return value * value * value * (value * (value * 6.0D - 15.0D) + 10.0D);
	}

	private static double lerp(double progress, double lower, double upper) {
		return lower + progress * (upper - lower);
	}

	private static double grad(int hash, double double1, double double2, double double3) {
		double d1 = (hash &= 15) < 8 ? double1 : double2;
		double d2 = (hash == 12) || (hash == 14) ? double1 : hash < 4 ? double2 : double3;
		return ((hash & 0x1) == 0 ? d1 : -d1) + ((hash & 0x2) == 0 ? d2 : -d2);
	}

	public final double sample(double x, double y) {
		return sampleInternal(x, y, 0.0D);
	}

	public final double sample(double x, double y, double z) {
		return sampleInternal(x, y, z);
	}
}