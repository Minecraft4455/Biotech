package gigaherz.biotech.tileentity;

import gigaherz.biotech.Biotech;

import gigaherz.biotech.item.CommandCircuit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import cpw.mods.fml.common.network.PacketDispatcher;

// Default machine TileEntity
// Has a power connection at the back
// Has a powered state
// Has an inventory

public class TillingMachineTileEntity extends BasicMachineTileEntity implements IInventory, ISidedInventory, IPacketReceiver
{
	public static final double WATTS_PER_ACTION = 500;
	public static final double WATTS_PER_IDLE_ACTION = 25;
	
	// Time idle after a tick
	public static final int IDLE_TIME_AFTER_ACTION = 80;
	public static final int IDLE_TIME_NO_ACTION = 40;
	
    public int currentX = 0;
    public int currentZ = 0;
    public int currentY = 0;

    public int minX, maxX;
    public int minZ, maxZ;

    //TODO Add variables to indicate maximum workarea size. Should be based on CommandItem usage?
    
    final ItemStack[] resourceStacks = new ItemStack[]
    {
        new ItemStack(Item.seeds, 1),
        new ItemStack(Item.melonSeeds, 1),
        new ItemStack(Item.pumpkinSeeds, 1),
        new ItemStack(Item.carrot, 1),
        new ItemStack(Item.potato, 1),
    };
    
	private int idleTicks;
    
    public TillingMachineTileEntity()
    {
        super();
      
       
    }
    
    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (this.worldObj.isRemote)
        {
            return;
        }
        
        if(this.idleTicks > 0)
        {
            if (this.ticks % 20 == 0)
            	this.setElectricityStored(this.getElectricityStored() - this.WATTS_PER_IDLE_ACTION);
        	
        	--this.idleTicks;
        	return;
        }
        
        while(canDoWork())
        {
	        this.setPowered(true);

	        if(doWork())
	        {
	        	this.setElectricityStored(this.getElectricityStored() - this.WATTS_PER_ACTION);
	        	this.idleTicks = this.IDLE_TIME_AFTER_ACTION;
	        	advanceLocation();
	        	this.setPowered(false);
	        	break;
	        }
	        else
	        {
	        	this.idleTicks = this.IDLE_TIME_NO_ACTION;
	        	advanceLocation();
	        	break;
	        }
        }

    	return;
    }
    

    public int getTopY()
    {/*
        for (int y = 0; y < 4; y++)
        {
            if (this.worldObj.getBlockMaterial(this.xCoord + this.currentX, this.yCoord + y, this.zCoord + this.currentZ) == Material.air)
            {
                return y - 1;
            }
        }

        return -1;
        */
    	return this.yCoord - 1;
    }

    
    public boolean doWork()
    {
    	
		Block tilledField = Block.tilledField;
		Block cropsField = Block.crops;
		
		int blockid = worldObj.getBlockId(xCoord + currentX, getTopY(), zCoord + currentZ);
		
		if((worldObj.getBlockId(xCoord + currentX, getTopY(), zCoord + currentZ) == Block.dirt.blockID || worldObj.getBlockId(currentX, getTopY(),  zCoord +  currentZ) == Block.grass.blockID) && worldObj.isAirBlock(xCoord + currentX, getTopY() + 1,  zCoord + currentZ))
		{
            if (!worldObj.isRemote)
            {
            	worldObj.setBlock(xCoord + currentX, getTopY(),  zCoord + currentZ, tilledField.blockID);
            	return true;
            	//damageTool(hoeToolStacks);
            }
		}
		else if(worldObj.getBlockId(xCoord + currentX, getTopY(), zCoord +  currentZ) == tilledField.blockID && worldObj.getBlockId(xCoord + currentX, getTopY() + 1, zCoord +  currentZ) != cropsField.blockID && worldObj.isAirBlock(xCoord + currentX, getTopY() + 1, zCoord +  currentZ))
		{
            if (!worldObj.isRemote)
            {
            	worldObj.setBlock(xCoord + currentX, getTopY() + 1, zCoord +  currentZ, cropsField.blockID);
            	return true;
            }
		}
			
		//this.tillingTimeTicks = 0;
		//this.active = false;
    	
    	return false;
    }
    
    public boolean canDoWork()
    {
    	ItemStack circuit = this.inventory[2];
    	ItemStack resource = this.inventory[1];
    	
    	if(this.getElectricityStored() >= this.WATTS_PER_ACTION)
    	{
	    	if(hasBioCircuitInSlot() && hasResourcesInSlot())
	    	{
	    		if(hasBioCircuitOfType(Biotech.bioCircuitWheatSeeds) && hasResourceOfType(resourceStacks[0]))
	    		{
	    			return true;
	    		}
	    		else if(hasBioCircuitOfType(Biotech.bioCircuitMelonSeeds) && hasResourceOfType(resourceStacks[1]))
	    		{
	    			return true;
	    		}
	    		else if(hasBioCircuitOfType(Biotech.bioCircuitPumpkinSeeds) && hasResourceOfType(resourceStacks[3]))
	    		{
	    			return true;
	    		}
	    		else if(hasBioCircuitOfType(Biotech.bioCircuitCarrots) && hasResourceOfType(resourceStacks[4]))
	    		{
	    			return true;
	    		}
	    		else if(hasBioCircuitOfType(Biotech.bioCircuitPotatoes) && hasResourceOfType(resourceStacks[5]))
	    		{
	    			return true;
	    		}
	    	}
    	}
    	
    	return false;
    }
    
    
    
    private boolean hasResourcesInSlot()
    {
    	ItemStack slot = this.inventory[1];
    	
    	if (slot == null)
        {
            return false;
        }
    	
        for (int i = 0; i < resourceStacks.length; i++)
        {
            if (slot.itemID == resourceStacks[i].itemID)
            {
                return true;
            }
        }

        return false;
    }
    
    public boolean hasBioCircuitInSlot()
    {
        ItemStack slot = this.inventory[2];

        if (slot == null)
        {
            return false;
        }

        if (slot.itemID == Biotech.bioCircuitEmpty.itemID)
        {
            return true;
        }

        return false;
    }
    
    public boolean hasResourceOfType(ItemStack itemStack)
    {
        ItemStack slot = this.inventory[1];

        if (slot == null)
        {
            return false;
        }

        if (slot.itemID == itemStack.itemID)
        {
            if(slot.getItemDamage() == itemStack.getItemDamage())
            {
            	return true;
            }
        }

        return false;
    }
    
    public boolean hasBioCircuitOfType(ItemStack itemStack)
    {
        ItemStack slot = this.inventory[2];

        if (slot == null)
        {
            return false;
        }

        if (slot.itemID == itemStack.itemID)
        {
            if(slot.getItemDamage() == itemStack.getItemDamage())
            {
            	return true;
            }
        }

        return false;
    }
    
    @Override
    public void refreshConnectorsAndWorkArea()
    {
    	super.refreshConnectorsAndWorkArea();
    	
    	ForgeDirection direction = ForgeDirection.getOrientation(getFacing());
    	
        if (direction.offsetZ > 0)
        {
            this.minX = -2;
            this.maxX =  2;
            this.minZ = -5 * direction.offsetZ;
            this.maxZ = -1 * direction.offsetZ;
        }
        else if (direction.offsetZ < 0)
        {
            this.minX = -2;
            this.maxX =  2;
            this.minZ = -1 * direction.offsetZ;
            this.maxZ = -5 * direction.offsetZ;
        }
        else if (direction.offsetX > 0)
        {
            this.minZ = -2;
            this.maxZ =  2;
            this.minX = -5 * direction.offsetX;
            this.maxX = -1 * direction.offsetX;
        }
        else if (direction.offsetX < 0)
        {
            this.minZ = -2;
            this.maxZ =  2;
            this.minX = -1 * direction.offsetX;
            this.maxX = -5 * direction.offsetX;
        }

        if (this.currentX < this.minX || this.currentX > this.maxX)
        {
            this.currentX = this.minX;
        }

        if (this.currentZ < this.minZ || this.currentZ > this.maxZ)
        {
            this.currentZ = this.minZ;
        }
    }
    
    private void advanceLocation()
    {
        this.currentX++;

        if (this.currentX > this.maxX)
        {
            this.currentX = this.minX;
            this.currentZ++;

            if (this.currentZ > this.maxZ)
            {
                this.currentZ = this.minZ;
            }
        }
    }
    
    
    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        //this.progressTime = tagCompound.getShort("Progress");

    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        //tagCompound.setShort("Progress", (short)this.progressTime);

    }

    @Override
    public String getInvName()
    {
        return "TillingMachine";
    }

}
