package biotech.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import biotech.Biotech;
import biotech.helpers.Util;


public class FarmMachineTileEntity extends BasicMachineTileEntity implements IInventory, ISidedInventory, IPacketReceiver
{
	
	public static final double	WATTS_PER_ACTION	= 500;

	Random random;
	
	private Block				tilledField			= Block.tilledField;
	private Block				wheatseedsField		= Block.crops;
	private Block				melonStemField		= Block.melonStem;
	private Block				pumpkinStemField	= Block.pumpkinStem;
	private Block				carrotField			= Block.carrot;
	private Block				potatoField			= Block.potato;
	
	protected Item[]			resourceStacks		= new Item[] { Item.seeds, Item.carrot, Item.potato, };
	protected Block[]			cropStacks			= new Block[] { Block.crops, Block.carrot, Block.potato, };
	protected ItemStack[]		harvestStacks		= new ItemStack[] { new ItemStack(Item.wheat, 1), new ItemStack(Item.carrot, 1), new ItemStack(Item.potato, 1), };
	
	public FarmMachineTileEntity()
	{
		super();
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!worldObj.isRemote)
		{
			if(this.checkRedstone())
			{
				/* Per 40 Tick Processes */
				if (this.ticks % 40 == 0 && this.wattsReceived >= WATTS_PER_ACTION && this.inventory[1] != null)
				{
					this.workArea();
					this.wattsReceived = Math.max(this.wattsReceived - WATTS_PER_ACTION / 4, 0);
				}
			}
		}
	}
	
	public int AreaSize()
	{
		if(this.inventory[2] != null)
		{
			return (3 * this.inventory[2].stackSize);
		}
		return 3;
	}
	
	/**
	 * Calculates the work area base on the AreaSize
	 */
	public void workArea()
	{
		int xmin = this.xCoord + 2;
		int xmax = this.xCoord + 2 + AreaSize();
		int zmin = this.zCoord + 2;
		int zmax = this.zCoord + 2 + AreaSize();
		for (int i = 0; i < resourceStacks.length; i++)
		{
			if (this.inventory[1].itemID == resourceStacks[i].itemID)
			{
				for (int xx = xmin; xx < xmax; xx++)
				{
					for (int zz = zmin; zz < zmax; zz++)
					{
						if (worldObj.getBlockId(xx, this.yCoord, zz) != Block.tilledField.blockID)
						{
							tillLand(xx, this.yCoord, zz);
						}
						if (worldObj.getBlockId(xx, this.yCoord, zz) == Block.tilledField.blockID)
						{
							PlantSeed(xx, this.yCoord + 1, zz, cropStacks[i].blockID);
						}
						else if (worldObj.getBlockId(xx, this.yCoord, zz) == Block.tilledField.blockID && worldObj.getBlockId(xx, this.yCoord + 1, zz) == cropStacks[i].blockID)
						{
							HarvestPlant(xx, this.yCoord + 1, zz, harvestStacks[i]);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Makes dirt tilledDirt at given position
	 * 
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param z
	 *            position
	 */
	public void tillLand(int x, int y, int z)
	{
		worldObj.setBlock(x, y, z, Block.tilledField.blockID, 0, 2);
	}
	
	/**
	 * Plants the given seed at the given position
	 * 
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param z
	 *            position
	 * @param seed
	 *            that has to be planted
	 */
	public void PlantSeed(int x, int y, int z, int seed)
	{
		worldObj.setBlock(x, y, z, seed, 0, 3);
	}
	
	/**
	 * Harvests the plant at the given position
	 * 
	 * @param x
	 *            position
	 * @param y
	 *            position
	 * @param z
	 *            position
	 * @param plant
	 *            that has to be harvested
	 */
	public void HarvestPlant(int x, int y, int z, ItemStack plant)
	{
		worldObj.setBlock(x, y, z, 0, 0, 2);
		
		for (int i = 3; i < 8; i++)
		{
			if (this.inventory[i] == null)
			{
				this.inventory[i] = (plant);
			}
			if(this.inventory[i] != null && this.inventory[i].stackSize <= 60)
			{
				int plantRandom = random.nextInt(3);
				this.inventory[i].stackSize += plantRandom;
			}
		}
		if(this.inventory[1] != null && this.inventory[1].stackSize <= 60)
		{
			int plantRandom = random.nextInt(3);
			this.inventory[1].stackSize += plantRandom;
		}
		else if(this.inventory[1].stackSize > 64)
		{
			ItemStack added = Util.addToRandomInventory(plant, worldObj, xCoord, yCoord, zCoord, ForgeDirection.UNKNOWN);
		}
		else
		{
			float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;

			EntityItem entityitem = new EntityItem(worldObj, xCoord + f, yCoord + f1 + 0.5F, zCoord + f2, plant);
			
			entityitem.delayBeforeCanPickup = 10;

			float f3 = 0.05F;
			entityitem.motionX = (float) worldObj.rand.nextGaussian() * f3;
			entityitem.motionY = (float) worldObj.rand.nextGaussian() * f3 + 1.0F;
			entityitem.motionZ = (float) worldObj.rand.nextGaussian() * f3;
			worldObj.spawnEntityInWorld(entityitem);
		}
	}
	
	@Override
	public String getInvName()
	{
		return "Farmer";
	}
	
}
