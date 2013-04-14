package biotech.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class itemBioBlock extends ItemBlock
{
	public itemBioBlock(int id)
	{
		super(id);
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		int metadata = itemstack.getItemDamage();
		
		return Block.blocksList[this.getBlockID()].getUnlocalizedName() + "." + metadata;
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return Block.blocksList[this.getBlockID()].getUnlocalizedName() + ".0";
	}
}