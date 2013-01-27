package gigaherz.biotech;

import gigaherz.biotech.client.MilkingMachineGui;
import gigaherz.biotech.client.MilkingManagerGui;
import gigaherz.biotech.client.PlantingMachineGui;
import gigaherz.biotech.client.TillingMachineGui;
import gigaherz.biotech.client.WorkerGui;
import gigaherz.biotech.container.BasicWorkerContainer;
import gigaherz.biotech.container.MilkingMachineContainer;
import gigaherz.biotech.container.MilkingManagerContainer;
import gigaherz.biotech.container.PlantingMachineContainer;
import gigaherz.biotech.container.TillingMachineContainer;
import gigaherz.biotech.tileentity.BasicWorkerTileEntity;
import gigaherz.biotech.tileentity.MilkingMachineTileEntity;
import gigaherz.biotech.tileentity.PlantingMachineTileEntity;
import gigaherz.biotech.tileentity.TillingMachineTileEntity;
import gigaherz.biotech.tileentity.MilkingManagerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	public void preInit()
	{
		// Preload textures
		MinecraftForgeClient.preloadTexture(Biotech.BLOCK_TEXTURE_FILE);
		MinecraftForgeClient.preloadTexture(Biotech.ITEM_TEXTURE_FILE);
		
		MinecraftForgeClient.preloadTexture(Biotech.FILE_PATH + "tillingmachine.png");
		MinecraftForgeClient.preloadTexture(Biotech.FILE_PATH + "plantingmachine.png");
		MinecraftForgeClient.preloadTexture(Biotech.FILE_PATH + "cowmilker.png");
		
	}
	
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if(tileEntity != null)
	    {
	    	switch(id)
	    	{
	    	case 0:
	    		return new TillingMachineContainer(player.inventory, (TillingMachineTileEntity) tileEntity);
	    	case 1:
	    		return new MilkingManagerContainer(player.inventory, (MilkingManagerTileEntity) tileEntity);
	    	case 2:
	    		return new PlantingMachineContainer(player.inventory, (PlantingMachineTileEntity) tileEntity);
	    	case 3:
	    		return new MilkingMachineContainer(player.inventory, (MilkingMachineTileEntity) tileEntity);
	    	}
	    	
	    }
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
	    TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
	    
	    if(tileEntity != null)
	    {
	    	switch(id)
	    	{
	    	case 0:
	    		return new TillingMachineGui(player.inventory, (TillingMachineTileEntity) tileEntity);
	    	case 1:
	    		return new MilkingManagerGui(player.inventory, (MilkingManagerTileEntity) tileEntity);
	    	case 2:
	    		return new PlantingMachineGui(player.inventory, (PlantingMachineTileEntity) tileEntity);
	    	case 3:
	    		return new MilkingMachineGui(player.inventory, (MilkingMachineTileEntity) tileEntity);
	    	}
	    	
	    }
        return null;
    }
}
