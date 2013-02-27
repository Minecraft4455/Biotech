package biotech.slots;

import biotech.Biotech;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class bioFuelSlot extends Slot {

	public bioFuelSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);

	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack.itemID == Biotech.BioFuel.itemID;
	}
}