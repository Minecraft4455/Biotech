package biotech.block;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.prefab.block.BlockAdvanced;
import biotech.Biotech;
import biotech.tileentity.BasicMachineTileEntity;
import biotech.tileentity.BioRefineryTileEntity;
import biotech.tileentity.CowMilkerTileEntity;
import biotech.tileentity.CuttingMachineTileEntity;
import biotech.tileentity.FarmMachineTileEntity;
import biotech.tileentity.PlantingMachineTileEntity;
import biotech.tileentity.TillingMachineTileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BiotechBlockMachine extends BlockAdvanced {
	// 0 == Farm
	// 1 == Woodcutter
	// 2 == Fertilizer
	// 3 == Miner
	// 4 == Filler
	// 5 == Cow Milker
	// 6 == BioRefinery

	public static final int FARM_METADATA = 0;
	public static final int WOODCUTTER_METADATA = 1;
	public static final int FERTILIZER_METADATA = 2;
	public static final int MINER_METADATA = 3;
	public static final int FILLER_METADATA = 4;
	public static final int COW_MILKER_METADATA = 5;
	public static final int BIO_REFINERY_METADATA = 6;

	public BiotechBlockMachine(int id, int textureIndex) {
		super(id, UniversalElectricity.machine);
		this.setCreativeTab(Biotech.tabBiotech);
		//this.blockIndexInTexture = textureIndex;
	}

	@Override
	public String getTextureFile() {
		return Biotech.BLOCK_TEXTURE_FILE;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		if (meta == 0 || meta == 1 || meta == 2 || meta == 3 || meta == 4) {
			switch (side) {
			case 0:
				return 3;
			case 1:
				return 18;
			case 2:
				return 32;
			case 3:
				return 3;
			default:
				return 3;
			}
		}
		else if (meta == 5) {
			switch (side) {
			case 0:
				return 3;
			case 1:
				return 18;
			case 2:
				return 32;
			case 3:
				return 3;
			default:
				return 3;
			}
		} else if (meta == 6) {
			switch (side) {
			case 0:
				return 3;
			case 1:
				return 18;
			case 2:
				return 32;
			case 3:
				return 3;
			default:
				return 3;
			}
		} else {
			return 3;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
		int metadata = world.getBlockMetadata(x, y, z);

		BasicMachineTileEntity tileEntity = (BasicMachineTileEntity) world
				.getBlockTileEntity(x, y, z);

		// System.out.println("getBlockTexture: " + tileEntity.getFacing());

		int front = 3;
		int back = 2;
		int left = 5;
		int right = 4;
		int top = 1;
		int bottom = 0;

		switch (tileEntity.getFacing()) {
		case 2:
			front = 3;
			back = 2;
			left = 5;
			right = 4;
			break;
		case 3:
			front = 2;
			back = 3;
			left = 4;
			right = 5;
			break;
		case 4:
			front = 5;
			back = 4;
			left = 2;
			right = 3;
			break;
		case 5:
			front = 4;
			back = 5;
			left = 3;
			right = 2;
			break;

		default:
			break;
		}

		if (metadata == 0 || metadata == 1 || metadata == 2 || metadata == 3 || metadata == 4) {
			if (side == front) {
				return tileEntity.isPowered ? 48 : 32;
			} else if (side == back) {
				return 3;
			} else if (side == bottom) {
				return 3;
			} else if (side == top) {
				return 18;
			} else {
				return 3;
			}
		} 
		else if (metadata == 5) {
			if (side == front) {
				return tileEntity.isPowered ? 48 : 32;
			} else if (side == back) {
				return 3;
			} else if (side == bottom) {
				return 19;
			} else if (side == top) {
				return 18;
			} else {
				return 3;
			}
		} else if (metadata == 6) {
			if (side == front) {
				return tileEntity.isPowered ? 48 : 32;
			} else if (side == back) {
				return 3;
			} else if (side == bottom) {
				return 19;
			} else if (side == top) {
				return 18;
			} else {
				return 3;
			}
		} else {
			return 3;
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z,
			Random random) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof BasicMachineTileEntity) {
			BasicMachineTileEntity tileEntity = (BasicMachineTileEntity) tile;

			if (tileEntity.isPowered) {
				float sx = (float) x + 0.5F;
				float sy = (float) y + 0.0F + random.nextFloat() * 6.0F / 16.0F;
				float sz = (float) z + 0.5F;
				float o1 = 0.52F;
				float o2 = random.nextFloat() * 0.6F - 0.3F;
				world.spawnParticle("reddust", (double) (sx - o1), (double) sy,
						(double) (sz + o2), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("reddust", (double) (sx + o1), (double) sy,
						(double) (sz + o2), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("reddust", (double) (sx + o2), (double) sy,
						(double) (sz - o1), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("reddust", (double) (sx + o2), (double) sy,
						(double) (sz + o1), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile instanceof BasicMachineTileEntity) {
			BasicMachineTileEntity tileEntity = (BasicMachineTileEntity) tile;

			int side = MathHelper
					.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			int change = 3;

			switch (side) {
			case 0:
				change = 3;
				break;
			case 1:
				change = 4;
				break;
			case 2:
				change = 2;
				break;
			case 3:
				change = 5;
				break;
			}
			// System.out.println("onBlockPlacedBy: " + change);

			tileEntity.setFacing((short) change);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int i, CreativeTabs creativetabs, List list) {
		list.add(new ItemStack(i, 1, 0));
		list.add(new ItemStack(i, 1, 1));
		list.add(new ItemStack(i, 1, 2));
		list.add(new ItemStack(i, 1, 3));
		list.add(new ItemStack(i, 1, 4));
		list.add(new ItemStack(i, 1, 5));
		list.add(new ItemStack(i, 1, 6));
	}

	@Override
	public boolean onUseWrench(World world, int x, int y, int z,
			EntityPlayer par5EntityPlayer, int side, float hitX, float hitY,
			float hitZ) {
		int meta = world.getBlockMetadata(x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity == null
				|| !(tileEntity instanceof BasicMachineTileEntity)) {
			return false;
		}

		BasicMachineTileEntity basicEntity = (BasicMachineTileEntity) tileEntity;

		// Re-orient the block
		switch (basicEntity.getFacing()) {
		case 2:
			basicEntity.setFacing((short) 5);
			break;

		case 5:
			basicEntity.setFacing((short) 3);
			break;

		case 3:
			basicEntity.setFacing((short) 4);
			break;

		case 4:
			basicEntity.setFacing((short) 2);
			break;
		}

		world.setBlockMetadataWithNotify(x, y, z, meta, 3);

		return true;
	}

	@Override
	public boolean onMachineActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		int metadata = world.getBlockMetadata(x, y, z);

		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!world.isRemote && !player.isSneaking()) {
			switch (metadata) {
			case 0:
				player.openGui(Biotech.instance, 0, world, x, y, z);
				return true;
			case 1:
			case 2:
				player.openGui(Biotech.instance, 3, world, x, y, z);
				return true;
			case 3:
			case 4:
			case 5:
				player.openGui(Biotech.instance, 1, world, x, y, z);
				return true;
			case 6:
				player.openGui(Biotech.instance, 2, world, x, y, z);
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
		case 0:
			return new FarmMachineTileEntity();
		case 1:
			return new CuttingMachineTileEntity();
		case 2:
		case 3:
		case 4:
		case 5:
			return new CowMilkerTileEntity();
		case 6:
			return new BioRefineryTileEntity();
		default:
			return new BasicMachineTileEntity();
		}
	}
}
