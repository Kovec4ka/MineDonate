package ru.log_inil.mc.minedonate.gui;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import ru.alastar.minedonate.gui.ShopGUI;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModContainer;

public class GuiItems extends GuiScrollingList
{
    public ShopGUI parent;
    public List<GuiEntry> entrs;

    public GuiItems(ScaledResolution sr, ShopGUI parent, List<GuiEntry> _entr, int listWidth)
    {
        super(Minecraft.getMinecraft(),sr.getScaledWidth()-60, 100,  (int) (sr.getScaledHeight() * 0.1) + 15+25, (int) ( (sr.getScaledHeight()) - (sr.getScaledHeight() * 0.1) ) - 5, 30, 30);
        this.parent=parent;
        this.entrs=_entr;
    }

    @Override
    protected int getSize()
    {
        return entrs.size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2)
    {
    }

    @Override
    protected boolean isSelected(int var1)
    {
    	return false ;
    }

    @Override
    protected void drawBackground()
    {
    }

    @Override
    protected int getContentHeight()
    {
        return (this.getSize()) * 35 + 1;
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
    	
    	entrs . get ( listIndex ) . draw ( this, var2, var3, var4, var5 ) ;
  
    }
    
    
    @Override
    protected void undrawSlot ( int listIndex ) {
    	
    	entrs . get ( listIndex ) . undraw ( ) ;
  
    }
    
    public FontRenderer getFontRenderer ( ) {
    	
    	return this.parent.getFontRenderer();
    	
    }

}