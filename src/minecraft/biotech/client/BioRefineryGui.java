package biotech.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import biotech.Biotech;
import biotech.container.BioRefineryContainer;
import biotech.tileentity.BioRefineryTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BioRefineryGui extends GuiContainer
{
	private BioRefineryTileEntity	tileEntity;
	
	private int						containerWidth;
	private int						containerHeight;
	private GuiButton				CheeseFuelB;
	private int						buttonX;
	private int						buttonY;
	private int						changeText = 1;
	
	public BioRefineryGui(InventoryPlayer playerInventory, BioRefineryTileEntity tileEntity)
	{
		super(new BioRefineryContainer(playerInventory, tileEntity));
		this.tileEntity = tileEntity;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		buttonX = (this.width - this.xSize) / 2 + 142;
		buttonY = (this.height - this.ySize) / 2 + 41;
		
		buttonList.clear();
		buttonList.add(CheeseFuelB = new GuiButton(0, buttonX, buttonY, 28, 12, this.tileEntity.ButtonText));
	}
	
	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(!guibutton.enabled)
		{
			return;
		}
		if (guibutton.id == 0)
		{
			this.changeText *= -1;
		}
	}
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j)
	{
		this.fontRenderer.drawString(this.tileEntity.getInvName(), 57, 4, 4210752);
		
		String displayText = "";
		
		if (this.tileEntity.isDisabled())
		{
			displayText = "Disabled!";
		}
		else if (this.tileEntity.checkRedstone())
		{
			displayText = "Working";
		}
		else
		{
			displayText = "Idle";
		}
		
		this.fontRenderer.drawString("Status: " + displayText, 28, 22, 0x00CD00);
		this.fontRenderer.drawString("Milk: " + this.tileEntity.getMilkStored() + "/" + this.tileEntity.getMaxMilk(), 28, 32, 0x00CD00);
		if (this.tileEntity.checkRedstone())
		{
			this.fontRenderer.drawString("Refining: " + this.tileEntity.working + "%", 28, 42, 0x00CD00);
		}
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	}
	
	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.renderEngine.bindTexture(this.getTexture());
		
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, xSize, ySize);
		
		int milkscale = (int) (((double) this.tileEntity.getMilkStored() / this.tileEntity.getMaxMilk()) * 100);
		
		this.drawTexturedModalRect(containerWidth + 108, containerHeight + 71 - milkscale, 176, 50 - milkscale, 8, milkscale);
		
		if (this.tileEntity.processTicks > 0)
		{
			int scale = (int) (((double) this.tileEntity.processTicks / (double) this.tileEntity.PROCESS_TIME_REQUIRED) * 28);
			this.drawTexturedModalRect(containerWidth + 122, containerHeight + 38, 176, 51, 12, 28 - scale);
		}
	}
	
	public static String getTexture()
	{
		return Biotech.GUI_PATH + "GUI_BioRefinery.png";
	}
	
}