package gigaherz.workercommand.client;

import gigaherz.workercommand.WorkerContainer;
import gigaherz.workercommand.WorkerTile;

import org.lwjgl.opengl.GL11;
import net.minecraft.src.*;

public class WorkerGui extends GuiContainer
{
    protected WorkerTile worker;

    public WorkerGui(InventoryPlayer playerInventory, WorkerTile tileEntity)
    {
        super(new WorkerContainer(tileEntity, playerInventory));

    	this.worker = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j)
    {
        fontRenderer.drawString("Worker", 60, 6, 4210752);
        //fontRenderer.drawString("Power: " + grinder.powerFlow + " J / T", 70, 60, 0xFFFFFF);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2 , 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {    	
        int picture = mc.renderEngine.getTexture("/gigaherz/workercommand/worker.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.renderEngine.bindTexture(picture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        /*
        if (this.grinder.isPowered())
        {
            this.drawTexturedModalRect(x + 86, y + 54, 176, 0, 8, 8);
        }

        int progress = this.grinder.getProgressTimeScaled(24);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, progress + 1, 16);
        */
    }
}