package se.luppii.ladders.block;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.tile.TileEntitySturdyLadder;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSturdyLadder extends BlockGenericLadder implements ITileEntityProvider {
	
	private IIcon blockIIcon;
	private boolean enableLeftClick;
	public static int renderID;
	
	public BlockSturdyLadder(boolean par1Boolean) {
		
		super(Material.circuits);
		this.setHardness(0.4F);
		this.setStepSound(soundTypeLadder);
		this.setBlockName("lladders.block.sturdyladder");
		this.setCreativeTab(CreativeTabs.tabDecorations);
		enableLeftClick = par1Boolean;
	}
	
    public void updateLadderBounds(int par1) {
		
		int direction = par1 & 3;
		float f = 0.185F;
		
		if (direction == 0) {
			
			this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
		}
		if (direction == 1) {
			
			this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
		}
		if (direction == 2) {
			
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
		}
		if (direction == 3) {
			
			this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		
		return par1World.isSideSolid(par2, par3 - 1, par4, UP) ||
				par1World.isSideSolid(par2 - 1, par3, par4, EAST ) ||
				par1World.isSideSolid(par2 + 1, par3, par4, WEST ) ||
				par1World.isSideSolid(par2, par3, par4 - 1, SOUTH) ||
				par1World.isSideSolid(par2, par3, par4 + 1, NORTH) ||
				par1World.getBlock(par2, par3 - 1, par4) == this ||
				par1World.getBlock(par2, par3 + 1, par4) == this;
	}
	
	@Override
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9) {
		
		int j1 = par9 & 3;
		
		if (par1World.getBlock(par2, par3 + 1, par4) == this) {
			return par1World.getBlockMetadata(par2, par3 + 1, par4) & 3;
		}
		if (par1World.getBlock(par2, par3 - 1, par4) == this) {
			return par1World.getBlockMetadata(par2, par3 - 1, par4) & 3;
		}
		
		if (par5 == 2) {
			j1 = 0;
		}
		if (par5 == 3) {
			j1 = 2;
		}
		if (par5 == 4) {
			j1 = 3;
		}
		if (par5 == 5) {
			j1 = 1;
		}
		
		return j1;
	}
	
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {

		if (par5EntityLivingBase == null) {
			return;
		}
		
		int direction = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		
		// Use the same direction as ladders above or below.
		if (par1World.getBlock(par2, par3 + 1, par4) == this) {
			direction = par1World.getBlockMetadata(par2, par3 + 1, par4) & 3;
		}
		if (par1World.getBlock(par2, par3 - 1, par4) == this) {
			direction = par1World.getBlockMetadata(par2, par3 - 1, par4) & 3;
		}
		
		par1World.setBlockMetadataWithNotify(par2, par3, par4, direction, 2);
	}
	
	@Override
	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
		
		if (enableLeftClick) {
			if (par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().isItemEqual(new ItemStack(this))) {
				
				int meta = par1World.getBlockMetadata(par2, par3, par4) & 3;
							
				if (canSetLadder(par1World, par2, par3 + 1, par4, meta)) {
					
					setLadder(par1World, par2, par3 + 1, par4, meta, par5EntityPlayer);
				}
			}
		}
	}
	
	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block) {
		
		int metadata = par1World.getBlockMetadata(par2, par3, par4) & 3;
		boolean flag = false;
		
		if (par1World.isSideSolid(par2, par3 - 1, par4, UP) || par1World.getBlock(par2, par3 - 1, par4) == this) {
			
			flag = true;
		}
		
		// Placing ladders on the opposite of another, effectively making "flying" ladders - isn't allowed.
		if (metadata != 2 && par1World.isSideSolid(par2, par3, par4 + 1, NORTH)) {
			
			flag = true;
		}
		if (metadata != 3 && par1World.isSideSolid(par2 - 1, par3, par4, EAST)) {
			
			flag = true;
		}
		if (metadata != 0 && par1World.isSideSolid(par2, par3, par4 - 1, SOUTH)) {
			
			flag = true;
		}
		if (metadata != 1 && par1World.isSideSolid(par2 + 1, par3, par4, WEST)) {
			
			flag = true;
		}
		
		if (!flag) {
			
			this.dropBlockAsItem(par1World, par2, par3, par4, metadata, 0);
			par1World.setBlockToAir(par2, par3, par4);
		}
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5Block);
	}
	
	public TileEntity getBlockEntity(int par1) {
		return new TileEntitySturdyLadder();
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		
		try {
			
			TileEntitySturdyLadder te = TileEntitySturdyLadder.class.newInstance();
			return te;
		}
		catch (IllegalAccessException ex) {
			
			FMLLog.severe(References.MOD_NAME, "Unable to create TileEntitySturdyLadder instance.");
			return null;
		}
		catch (InstantiationException ex) {
			
			FMLLog.severe(References.MOD_NAME, "Unable to create TileEntitySturdyLadder instance.");
			return null;
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		
		return new TileEntitySturdyLadder();
	}
	
	protected boolean canSetLadder(World world, int x, int y, int z, int meta) {
		
		if (world.getBlock(x, y, z) == this) {
			
			return canSetLadder(world, x, y + 1, z, meta);
		}
		else if (!world.isAirBlock(x, y, z)) {
			
			return false;
		}
		return true;
	}
	
	protected boolean setLadder(World world, int x, int y, int z, int meta, EntityPlayer player) {
		
		Block block = world.getBlock(x, y, z);
		if (world.isAirBlock(x, y, z)) {
			
			ItemStack item = player.getCurrentEquippedItem();
			if (item.stackSize > 1) {
				
				player.setCurrentItemOrArmor(0, new ItemStack(item.getItem(), item.stackSize - 1, item.getItemDamage()));
			}
			else {
				
				player.setCurrentItemOrArmor(0, null);
			}
			world.setBlock(x, y, z, this, meta, 2);
			return true;
		}
		if (block == this) {
			
			return setLadder(world, x, y + 1, z, meta, player);
		}
		return false;
	}
}
