package net.minecraft.src;

class RedstoneUpdateInfo {
	int x;
	int y;
	int z;
	long updateTime;

	public RedstoneUpdateInfo(int x, int y, int z, long updateTime) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.updateTime = updateTime;
	}
}
