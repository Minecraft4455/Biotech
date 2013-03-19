package biotech;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeRegistry 
{
	public static void Recipes()
	{
		/**
		 * Handle the items that will be used in recipes. Just use the string in
		 * the recipe like the milk manager recipe
		 */
		ItemStack itemStone = new ItemStack(Block.stone, 1);
		ItemStack FarmMachine = new ItemStack(Biotech.biotechBlockMachine, 1, 0);
		ItemStack WoodMachine = new ItemStack(Biotech.biotechBlockMachine, 1, 1);
		ItemStack FertMachine = new ItemStack(Biotech.biotechBlockMachine, 1, 2);
		ItemStack MineMachine = new ItemStack(Biotech.biotechBlockMachine, 1, 3);
		ItemStack CowMilker = new ItemStack(Biotech.biotechBlockMachine, 1, 4);
		ItemStack BioRefinery = new ItemStack(Biotech.biotechBlockMachine, 1, 5);
		ItemStack UnProgrammed = new ItemStack(Biotech.bioCircuit, 1, 0);
		ItemStack WheatSeeds = new ItemStack(Biotech.bioCircuit, 1, 1);
		ItemStack Carrots = new ItemStack(Biotech.bioCircuit, 1, 2);
		ItemStack Potatoes = new ItemStack(Biotech.bioCircuit, 1, 3);
		ItemStack RangeUpgrade = new ItemStack(Biotech.bioCircuit, 1, 4);
		//ItemStack TreeSappling = new ItemStack(Biotech.bioCircuit, 1, 5);
		
		//Machines
		GameRegistry.addRecipe(new ShapedOreRecipe(FarmMachine, new Object[] { "#%#", "@!@", "###", '@', Item.hoeStone, '!', "motor", '#', itemStone, '%', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(WoodMachine, new Object[] { "#%#", "@!@", "###", '@', Item.axeStone, '!', "motor", '#', itemStone, '%', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(CowMilker, new Object[] { "@$@", "@!@", "@@@", '@', Item.ingotIron, '!', "motor", '$', "basicCircuit" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(BioRefinery, new Object[] { "@$@", "%!%", "@@@", '@', Item.ingotIron, '!', "motor", '$', "basicCircuit", '%', Item.bucketEmpty }));
		
		//Items
		GameRegistry.addRecipe(new ShapelessOreRecipe(UnProgrammed, new Object[] { "basicCircuit", Item.redstone, "copperWire"}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(WheatSeeds, new Object[] { UnProgrammed, Item.seeds}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(Carrots, new Object[] { UnProgrammed, Item.carrot}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(Potatoes, new Object[] { UnProgrammed, Item.potato}));
		GameRegistry.addRecipe(new ShapelessOreRecipe(RangeUpgrade, new Object[] { UnProgrammed, Item.redstone, Item.compass}));
		
	}
	
}
