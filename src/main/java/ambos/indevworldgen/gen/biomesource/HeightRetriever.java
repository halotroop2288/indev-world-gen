package ambos.indevworldgen.gen.biomesource;

public interface HeightRetriever {
	int getHeight(int x, int z);
	int getSeaLevelForBiomeGen();
	
	static HeightRetriever NONE = new HeightRetriever() {
		@Override
		public int getSeaLevelForBiomeGen() {
			return 63;
		}
		@Override
		public int getHeight(int x, int z) {
			return 0;
		}
	};
}
