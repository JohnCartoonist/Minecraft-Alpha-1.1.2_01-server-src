package net.minecraft.src;

import java.util.List;

public class EntitySnowball extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public int throwableShake = 0;
	private EntityLiving thrower;
	private int ticksInGround;
	private int ticksInAir = 0;

	public EntitySnowball(World var1) {
		super(var1);
		this.setSize(0.25F, 0.25F);
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.throwableShake > 0) {
			--this.throwableShake;
		}

		if(this.inGround) {
			int var1 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			if(var1 == this.inTile) {
				++this.ticksInGround;
				if(this.ticksInGround == 1200) {
					this.setEntityDead();
				}

				return;
			}

			this.inGround = false;
			this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
			this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
			this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
			this.ticksInGround = 0;
			this.ticksInAir = 0;
		} else {
			++this.ticksInAir;
		}

		Vec3D var15 = Vec3D.createVector(this.posX, this.posY, this.posZ);
		Vec3D var2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition var3 = this.worldObj.rayTraceBlocks(var15, var2);
		var15 = Vec3D.createVector(this.posX, this.posY, this.posZ);
		var2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
		if(var3 != null) {
			var2 = Vec3D.createVector(var3.hitVec.xCoord, var3.hitVec.yCoord, var3.hitVec.zCoord);
		}

		Entity var4 = null;
		List var5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double var6 = 0.0D;

		int var8;
		float var10;
		for(var8 = 0; var8 < var5.size(); ++var8) {
			Entity var9 = (Entity)var5.get(var8);
			if(var9.canBeCollidedWith() && (var9 != this.thrower || this.ticksInAir >= 5)) {
				var10 = 0.3F;
				AxisAlignedBB var11 = var9.boundingBox.expand((double)var10, (double)var10, (double)var10);
				MovingObjectPosition var12 = var11.calculateIntercept(var15, var2);
				if(var12 != null) {
					double var13 = var15.distanceTo(var12.hitVec);
					if(var13 < var6 || var6 == 0.0D) {
						var4 = var9;
						var6 = var13;
					}
				}
			}
		}

		if(var4 != null) {
			var3 = new MovingObjectPosition(var4);
		}

		if(var3 != null) {
			if(var3.entityHit != null && var3.entityHit.attackEntityFrom(this.thrower, 0)) {
							}

			for(var8 = 0; var8 < 8; ++var8) {
				this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
			}

			this.setEntityDead();
		}

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		float var16 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
		this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / (double)(float)Math.PI);

		for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var16) * 180.0D / (double)(float)Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
		}

		while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
			this.prevRotationPitch += 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}

		this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
		this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
		float var17 = 0.99F;
		var10 = 0.03F;
		if(this.handleWaterMovement()) {
			for(int var18 = 0; var18 < 4; ++var18) {
				float var19 = 0.25F;
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var19, this.posY - this.motionY * (double)var19, this.posZ - this.motionZ * (double)var19, this.motionX, this.motionY, this.motionZ);
			}

			var17 = 0.8F;
		}

		this.motionX *= (double)var17;
		this.motionY *= (double)var17;
		this.motionZ *= (double)var17;
		this.motionY -= (double)var10;
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setShort("xTile", (short)this.xTile);
		nbttagcompound.setShort("yTile", (short)this.yTile);
		nbttagcompound.setShort("zTile", (short)this.zTile);
		nbttagcompound.setByte("inTile", (byte)this.inTile);
		nbttagcompound.setByte("shake", (byte)this.throwableShake);
		nbttagcompound.setByte("inGround", (byte)(this.inGround?1:0));
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.xTile = nbttagcompound.getShort("xTile");
		this.yTile = nbttagcompound.getShort("yTile");
		this.zTile = nbttagcompound.getShort("zTile");
		this.inTile = nbttagcompound.getByte("inTile") & 255;
		this.throwableShake = nbttagcompound.getByte("shake") & 255;
		this.inGround = nbttagcompound.getByte("inGround") == 1;
	}

	public void onCollideWithPlayer(EntityPlayer entityPlayer) {
		if(this.inGround && this.thrower == entityPlayer && this.throwableShake <= 0 && entityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.arrow.shiftedIndex, 1))) {
			this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
			entityPlayer.onItemPickup(this, 1);
			this.setEntityDead();
		}

	}
}
