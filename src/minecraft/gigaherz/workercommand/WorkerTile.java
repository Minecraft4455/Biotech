package gigaherz.workercommand;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.EnumSet;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.core.electricity.ElectricityConnections;
import universalelectricity.core.electricity.ElectricityNetwork;
import universalelectricity.core.implement.IConductor;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.tile.TileEntityElectricityReceiver;
import cpw.mods.fml.common.network.PacketDispatcher;

public class WorkerTile extends TileEntityElectricityReceiver implements IInventory, ISidedInventory
{
	// The amount of watts required by the
	// electric furnace per tick
	public static final double MAX_WATTS_PER_TICK = 1000;
	public static final double WATTS_PER_ACTION = 5000;
	
    private ItemStack[] inventory;
    
    public int powerAccum = 0;
    public int currentX = 0;
    public int currentZ = 0;
    
    public int minX, maxX;
    public int minZ, maxZ;

    public WorkerTile()
    {
		super();
        this.inventory = new ItemStack[24];
		ElectricityConnections.registerConnector(this, EnumSet.noneOf(ForgeDirection.class));
    }

	@Override
	public void initiate()
	{
		refreshConnectorsAndWorkArea();
	}

	public void refreshConnectorsAndWorkArea()
	{
		int orientation = this.getBlockMetadata() & 7;
		ForgeDirection direction = ForgeDirection.getOrientation(orientation);
		ElectricityConnections.registerConnector(this, EnumSet.of(direction));
		
		System.out.println("Orientation: " + orientation);
		
		if(direction.offsetZ != 0)
		{
			this.minX = -2;
			this.maxX =  2;
			this.minZ = -1 * direction.offsetZ;
			this.maxZ = -5 * direction.offsetZ;
		}
		else if(direction.offsetX != 0)
		{
			this.minZ = -2;
			this.maxZ =  2;
			this.minX = -1 * direction.offsetX;
			this.maxX = -5 * direction.offsetX;
		}
		
		if(this.currentX < this.minX || this.currentX > this.maxX)
			this.currentX = this.minX;

		if(this.currentZ < this.minZ || this.currentZ > this.maxZ)
			this.currentZ = this.minZ;
		
	}
	
    @Override
    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex)
    {
    	if(slotIndex >= this.inventory.length)
    	{
    		System.out.println("Tried to access slot " + slotIndex);
    		return null;
    	}
        return this.inventory[slotIndex];
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        this.inventory[slot] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize(int slotIndex, int amount)
    {
        ItemStack stack = getStackInSlot(slotIndex);

        if (stack != null)
        {
            if (stack.stackSize <= amount)
            {
                setInventorySlotContents(slotIndex, null);
            }
            else
            {
                stack = stack.splitStack(amount);

                if (stack.stackSize == 0)
                {
                    setInventorySlotContents(slotIndex, null);
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotIndex)
    {
        ItemStack stack = getStackInSlot(slotIndex);

        if (stack != null)
        {
            setInventorySlotContents(slotIndex, null);
        }

        return stack;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public void openChest() {}

    @Override
    public void closeChest() {}

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        
        //this.progressTime = tagCompound.getShort("Progress");
        //this.powerTicks = tagCompound.getShort("PowerTicks");
        
        NBTTagList tagList = tagCompound.getTagList("Inventory");

        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        
        //tagCompound.setShort("Progress", (short)this.progressTime);
        //tagCompound.setShort("PowerTicks", (short)this.powerTicks);
        
        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];

            if (stack != null)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }

        tagCompound.setTag("Inventory", itemList);
    }

    @Override
    public String getInvName()
    {
        return "GrinderInventory";
    }

    @Override
    public void updateEntity()
    {
    	super.updateEntity();
    	
        if (this.worldObj.isRemote)
        	return;
        
        boolean stateChanged = false;
        
        if(this.ticks == 1)
        {
    		this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, this.blockMetadata);        	
        }

		ForgeDirection inputDirection = ForgeDirection.getOrientation(this.getBlockMetadata() & 7);
		TileEntity inputTile = Vector3.getTileEntityFromSide(this.worldObj, new Vector3(this), inputDirection);

		if (inputTile != null)
		{
			if (inputTile instanceof IConductor)
			{
				IConductor conductor = (IConductor) inputTile;
				ElectricityNetwork network = conductor.getNetwork();
				
				if (this.hasWorkToDo())
				{
					network.startRequesting(this, MAX_WATTS_PER_TICK / this.getVoltage(), this.getVoltage());
					
					int received = (int)Math.floor(network.consumeElectricity(this).getWatts());
					
					this.powerAccum = received;
				}
				else
				{
					network.stopRequesting(this);
				}
			}
		}
		
		this.powerAccum += 1000;

		if (this.powerAccum >= this.WATTS_PER_ACTION && !this.isDisabled() && this.hasWorkToDo())
		{
			System.out.println("Has work to do!");
			
			boolean workDone = false;
			for(int i=21; i < 24; i++)
			{
				ItemStack stack = getStackInSlot(i);
				
				if(stack == null)
					continue;
				
				Item item = stack.getItem();
				
				if(item == null || !(item instanceof CommandCircuit))
					continue;
				
				CommandCircuit circuit = (CommandCircuit)item;
				
				if(!circuit.canDoWork(this, stack.getItemDamage()))
					continue;
									
				if(circuit.doWork(this, stack.getItemDamage()))
				{
					this.powerAccum -= this.WATTS_PER_ACTION;
					workDone = true;
					stateChanged = true;
					
					this.currentX++;
					if(this.currentX > this.maxX)
					{
						this.currentX = this.minX;
						this.currentZ++;
						if(this.currentZ > this.maxZ)
						{
							this.currentZ = this.minZ;
						}
					}
					
					System.out.println("New location: " + this.currentX + ", " + this.currentZ);
					
					break;
				}
			}
			
			if(!workDone)
			{
				System.out.println("But nothing can be done!");
			}
		}
        
        if (stateChanged)
        {
            this.onInventoryChanged();
        }
        
        if(this.ticks % 20 == 0)
        {
            sendProgressBarUpdate(0, this.powerAccum);
            sendProgressBarUpdate(1, this.currentX);
            sendProgressBarUpdate(2, this.currentZ);
        }
    }

	@Override
    public int getStartInventorySide(ForgeDirection side)
    {
    	ForgeDirection left, right;
    	switch(this.getBlockMetadata() & 7)
    	{
    	case 2: // North
    		left = ForgeDirection.WEST;
    		right = ForgeDirection.EAST;
    		break;
    	case 3: // South
    		left = ForgeDirection.EAST;
    		right = ForgeDirection.WEST;
    		break;
    	case 4: // West
    		left = ForgeDirection.NORTH;
    		right = ForgeDirection.SOUTH;
    		break;
    	case 5: // East
    		left = ForgeDirection.SOUTH;
    		right = ForgeDirection.NORTH;
    		break;
    	default:
    		left = ForgeDirection.WEST;
    		right = ForgeDirection.EAST;
    		break;
    	}
    			
        if (side == left)
        {
            return 0;
        }
        
        if(side == right)
        {
        	return 9;
        }

        if(side == ForgeDirection.UP)
        {
        	return 18;
        }

        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
    	ForgeDirection left, right;
		switch(this.getBlockMetadata() & 7)
		{
		case 2: // North
			left = ForgeDirection.WEST;
			right = ForgeDirection.EAST;
			break;
		case 3: // South
			left = ForgeDirection.EAST;
			right = ForgeDirection.WEST;
			break;
		case 4: // West
			left = ForgeDirection.NORTH;
			right = ForgeDirection.SOUTH;
			break;
		case 5: // East
			left = ForgeDirection.SOUTH;
			right = ForgeDirection.NORTH;
			break;
		default:
			left = ForgeDirection.WEST;
			right = ForgeDirection.EAST;
			break;
		}
				
	    if (side == left)
	    {
	        return 9;
	    }
	    
	    if(side == right)
	    {
	    	return 9;
	    }
	
	    if(side == ForgeDirection.UP)
	    {
	    	return 3;
	    }

        return 0;
    }

    private void sendProgressBarUpdate(int bar, int value) {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
    	DataOutputStream outputStream = new DataOutputStream(bos);
    	try {
    		outputStream.writeInt(this.worldObj.getWorldInfo().getDimension());
    		outputStream.writeInt(this.xCoord);
    		outputStream.writeInt(this.yCoord);
    		outputStream.writeInt(this.zCoord);
    		outputStream.writeInt(bar);
    		outputStream.writeInt(value);
		} catch (Exception ex) {
    		ex.printStackTrace();
    	}

    	Packet250CustomPayload packet = new Packet250CustomPayload();
    	packet.channel = "WorkerCommand";
    	packet.data = bos.toByteArray();
    	packet.length = bos.size();
    	
    	PacketDispatcher.sendPacketToAllAround(this.xCoord, this.yCoord, this.zCoord, 12, this.worldObj.provider.dimensionId, packet);
	}
    
	public void updateProgressBar(int par1, int par2) {
        //Worker.updateBlockState(this.isPowered(), this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        if (par1 == 0)
        {
            this.powerAccum = par2;
        }
        if (par1 == 1)
        {
            this.currentX = par2;
        }
        if (par1 == 0)
        {
            this.currentZ = par2;
        }
	}

    private boolean hasWorkToDo() {

    	for(int i=21; i<24; i++)
		{
			ItemStack stack = inventory[i];
			
			if(stack == null)
				continue;
			
			Item item = stack.getItem();
			
			if(item == null || !(item instanceof CommandCircuit))
				continue;
			
			CommandCircuit circuit = (CommandCircuit)item;
			
			if(circuit.canDoWork(this, stack.getItemDamage()))
			{
				return true;
			}
		}
    	
		return false;
	}

	public boolean hasItemInInputArea(ItemStack itemStack) 
	{
		for(int i=0;i<9;i++)
		{
			ItemStack slot = inventory[i];
			if(slot == null)
				continue;
			
			if(slot.itemID != itemStack.itemID)
				continue;
			
			int damage = itemStack.getItemDamage();
			
			if(damage < 0 || slot.getItemDamage() == damage)
				return true;
		}
		return false;
	}

	public boolean hasAnyBlockInInputArea()
	{
		for(int i=0;i<9;i++)
		{
			ItemStack slot = inventory[i];
			if(slot == null)
				continue;
			
			Item item = slot.getItem();
			
			if(item instanceof ItemBlock)
				return true;
		}
		return false;
	}

	public boolean hasSpaceInOutputAreaForItem(ItemStack itemStack) 
	{
		for(int i=9;i<18;i++)
		{
			ItemStack slot = inventory[i];
			if(slot == null)
				return true;
			
			if(slot.itemID != itemStack.itemID)
				continue;
			
			int damage = itemStack.getItemDamage();
			
			if(damage >= 0 && slot.getItemDamage() != damage)
				continue;
			
			if(slot.stackSize + itemStack.stackSize <= slot.getMaxStackSize())
				return true;
		}
		return false;
	}

	public boolean hasToolInToolArea(ItemStack itemStack)
	{
		for(int i=18;i<21;i++)
		{
			ItemStack slot = inventory[i];
			if(slot == null)
				continue;
			
			if(slot.itemID == itemStack.itemID)
				return true;
		}
		return false;
	}

	public int getTopY() 
	{
		for(int y=0;y<4;y++)
		{
			if(this.worldObj.getBlockMaterial(this.xCoord + this.currentX, this.yCoord + y, this.zCoord + this.currentZ) == Material.air)
				return y;
		}
		return -1;
	}
}
