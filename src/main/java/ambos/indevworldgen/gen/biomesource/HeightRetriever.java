package ambos.indevworldgen.gen.biomesource;

public interface HeightRetriever {
	int getHeight(int x, int z);
	int getSeaLevelForGen();
	
	static HeightRetriever NONE = new HeightRetriever() {
		@Override
		public int getSeaLevelForGen() {
			return 64;
		}
		@Override
		public int getHeight(int x, int z) {
			return 0;
		}
	};
}
