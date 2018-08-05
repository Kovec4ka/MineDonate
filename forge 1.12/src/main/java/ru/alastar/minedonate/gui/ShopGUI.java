package ru.alastar.minedonate.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import ru.alastar.minedonate.MineDonate;
import ru.alastar.minedonate.gui.categories.*;
import ru.alastar.minedonate.merch.info.ShopInfo;
import ru.alastar.minedonate.network.packets.CodePacket;
import ru.alastar.minedonate.proxies.ClientProxy;
import ru.alastar.minedonate.rtnl.ModNetworkRegistry;
import ru.log_inil.mc.minedonate.gui.*;
import ru.log_inil.mc.minedonate.gui.context.ContextMenuManager;
import ru.log_inil.mc.minedonate.gui.frames.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alastar on 18.07.2017.
 */
public class ShopGUI extends MCGuiAccessible {

    public static ShopGUI instance;
    public static boolean dbgFlag = false;
    public static boolean confirmFlag = false;
    public static int m_Page = 0;
    private static int buttonLastId = 3;
    public boolean can_process = true;
    public boolean needNetUpdate = true;
    public boolean loading = false;
    public int currentShop = 0;
    public int m_Selected_Category = 0;
    public int defaultCategory = 0;

    public int lastCategory = -1;
    public List<GuiGradientTextField> listTextFields = new ArrayList<>();
    public ScaledResolution resolution;
    public GuiMoneyArea moneyArea;
    public GuiGradientTextField searchField;
    public PreviousButton pb;
    public NextButton nb;
    public GuiGradientButton exitButton;
    public GuiGradientButton searchButton;
    public Map<String, GuiEntry> gEntries = new LinkedHashMap<>();
    public GuiButton rightButton;
    public boolean contextMenuClickCallFlag = true;
    GuiEntry lastEntry;
    GuiFrameLoading gfl;
    boolean show = true;
    int a = 0;
    int tmpH;
    private ShopCategory[] cats;

    public ShopGUI() {

        cats = new ShopCategory[]{new ItemNBlockCategory("cat.items.base"), new PrivilegieCategory("cat.privilegies"), new RegionsCategory("cat.regions"), new EntitiesCategory("cat.entities"), new UsersShopsCategory("cat.shops")};

        for (ShopCategory sc : cats) {

            sc.preShow(this);

        }

        gEntries.put("frame.shop.create", new GuiFrameCreateShop("frame.shop.create", MineDonate.cfgUI.frames.createShop));
        gEntries.put("frame.shop.rename", new GuiFrameRenameShop("frame.shop.rename", MineDonate.cfgUI.frames.renameShop));
        gEntries.put("frame.shop.delete", new GuiFrameDeleteShop("frame.shop.delete", MineDonate.cfgUI.frames.deleteShop));
        gEntries.put("frame.shop.freeze", new GuiFrameFreezeShop("frame.shop.freeze", MineDonate.cfgUI.frames.freezeShop));

        gEntries.put("frame.item.add", new GuiFrameAddItem("frame.item.add", MineDonate.cfgUI.frames.addItem));
        gEntries.put("frame.item.edit", new GuiFrameEditItem("frame.item.edit", MineDonate.cfgUI.frames.editItem));
        gEntries.put("frame.item.delete", new GuiFrameDeleteItem("frame.item.delete", MineDonate.cfgUI.frames.deleteItem));

        gEntries.put("frame.entity.add", new GuiFrameAddEntity("frame.entity.add", MineDonate.cfgUI.frames.addEntity));
        gEntries.put("frame.entity.edit", new GuiFrameEditEntity("frame.entity.edit", MineDonate.cfgUI.frames.editEntity));
        gEntries.put("frame.entity.delete", new GuiFrameDeleteEntity("frame.entity.delete", MineDonate.cfgUI.frames.deleteEntity));

        gEntries.put("frame.acc.freeze", new GuiFrameFreezeAccount("frame.acc.freeze", MineDonate.cfgUI.frames.freezeAccount));

        gEntries.put("frame.loading", (gfl = new GuiFrameLoading("frame.loading")));

        gfl.setText(MineDonate.cfgUI.loadingText);

    }

    public static int getNextButtonId() {

        return buttonLastId++;

    }

    public GuiEntry showEntry(String k, boolean v) {

        if (gEntries.containsKey(k)) {

            if (v && lastEntry != null) {

                lastEntry.show(false);
                lastEntry.unShow(this);

                if (lastEntry.needReloadOnUnShow()) {

                    initGui();

                }

            }

            gEntries.get(k).show(v);
            gEntries.get(k).postShow(this);

            if (v) {

                lastEntry = gEntries.get(k);

            }
            /*
    		if ( reload ) {

    			initGui ( ) ;

    		}*/

            return gEntries.get(k);

        }

        return null;

    }

    public ShopCategory[] getCurrentShopCategories() { // #LOG

        return cats;

    }

    public ShopCategory getCurrentCategory() {

        return cats[m_Selected_Category];

    }

    public int getCurrentShopId() {

        return currentShop;

    }

    public void lockProcess() {

        can_process = false;

    }

    public boolean isLoading() {

        return loading;

    }

    public void setLoading(boolean _loading) {

        loading = _loading;

        showEntry("loading", loading);

    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {

        for (ru.log_inil.mc.minedonate.gui.GuiEntry ge : gEntries.values()) {

            if (ge.isVisible() && ge.onKey(p_73869_1_, p_73869_2_)) {

                return;

            }

        }

        if (searchField != null && searchField.isFocused()) {

            searchField.textboxKeyTyped(p_73869_1_, p_73869_2_);

            //updateGrid ( ) ;
            //updateBtns ( ) ;

            if ("!DBGE".equals(searchField.getText())) {

                dbgFlag = !dbgFlag;

                searchField.setText("");

            }

            if ("!CNFRM".equals(searchField.getText())) {

                confirmFlag = !confirmFlag;

                searchField.setText("");

            }

            getCurrentCategory().search(searchField.getText());

            getCurrentCategory().unShow(this);
            getCurrentCategory().preShow(this);
            getCurrentCategory().postShow(this);

            return;

        }

        for (GuiGradientTextField ggtf : listTextFields) {

            if (ggtf.isFocused()) {

                ggtf.textboxKeyTyped(p_73869_1_, p_73869_2_);

            }

        }

        if (dbgFlag && p_73869_2_ == 63) {

            MineDonate.loadClientConfig();
            initGui();

        }


        // 32 205 -> d
        // 30 203 <- a

        if ((30 == p_73869_2_ || 203 == p_73869_2_) && pb.enabled) {

            actionPerformed(pb);

        } else if ((32 == p_73869_2_ || 205 == p_73869_2_) && nb.enabled) {

            actionPerformed(nb);

        } else if (ClientProxy.openShop.getKeyCode() == p_73869_2_) {

            Minecraft.getMinecraft().displayGuiScreen(null);

        } else {

            try {
                super.keyTyped(p_73869_1_, p_73869_2_);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {

        if (dbgFlag) {

            System.err.println("Mouse click: x" + p_73864_1_ + ", y" + p_73864_2_ + ", k" + p_73864_3_);

        }

        contextMenuClickCallFlag = true;

        for (ru.log_inil.mc.minedonate.gui.GuiEntry ge : gEntries.values()) {

            if (ge.isVisible() && ge.coordContains(p_73864_1_, p_73864_2_)) {

                if (ge.onClick(p_73864_1_, p_73864_2_, p_73864_3_)) {

                    return;

                }

                if (ge.lockContextMenuUnderEntry()) {

                    contextMenuClickCallFlag = false;

                }

            } else if (ge.isVisible() && ge.lockContextMenuUnderEntry()) {

                contextMenuClickCallFlag = false;

            }

        }

        if (contextMenuClickCallFlag) {

            if (ContextMenuManager.click(this, p_73864_1_, p_73864_2_, p_73864_3_)) {

                return;

            }

        }

        for (GuiGradientTextField ggtf : listTextFields) {

            ggtf.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);

        }

        try {
            super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void actionPerformed(GuiButton button) {

        if (dbgFlag) {

            System.err.println("");

        }

        for (ru.log_inil.mc.minedonate.gui.GuiEntry ge : gEntries.values()) {

            if (ge.isOwnerButton(button)) {

                if (ge.actionPerformed(this, button)) {

                    return;

                }

            } else if (ge.lockButtonsUnderEntry()) {

                return;

            }

        }

        if (button.id == 0) {

            button.enabled = false;
            this.mc.player.closeScreen();

        }

        if (can_process) {

            can_process = false;

            if (button instanceof PreviousButton) {

                button.enabled = false;
                m_Page = m_Page - 1;
                updateGrid();
                updateButtons(true);

            } else if (button instanceof NextButton) {

                button.enabled = false;
                m_Page = m_Page + 1;
                updateGrid();
                updateButtons(true);

            } else if (button instanceof BuyButton) {

                ((BuyButton) button).buy();

            } else if (button instanceof CategoryButton) {

                if (currentShop != 0) {

                    if (cats[m_Selected_Category] instanceof UsersShopsCategory) {

                        ((UsersShopsCategory) cats[m_Selected_Category]).selectedShop = null;
                        ((UsersShopsCategory) cats[m_Selected_Category]).updateUserShopCategory(this, null, false);

                    }

                }

                if (lastCategory != -1) {

                    cats[lastCategory].unShow(this);

                }

                currentShop = 0;
                m_Page = 0;
                lastCategory = m_Selected_Category = ((CategoryButton) button).getCategory();

                ModNetworkRegistry.sendToServerNeedShopCategoryPacket(getCurrentShopId(), m_Selected_Category);

                loading = true;

                resolution = new ScaledResolution(this.mc);

                if (searchField != null) {

                    getCurrentCategory().search(searchField.getText());

                }

                updateGrid();
                updateButtons(true);

            } else if (button instanceof GoButton) {

                ((UsersShopsCategory) getCurrentCategory()).selectedShop = (ShopInfo) MineDonate.shops.get(0).cats[(getCurrentCategory()).getCatId()].getMerch(((GoButton) button).shopId);
                ModNetworkRegistry.sendToServerNeedShopCategoryPacket(((GoButton) button).shopId, 0);

                loading = true;


            } else if (button instanceof GuiGradientButton) {

                if (button.id == searchButton.id) {

                    if (searchField != null) {

                        searchButton.pressed = !searchField.getVisible();

                        if (searchField.getVisible()) {

                            searchField.setVisible(false);
                            searchField.setEnabled(false);

                            getCurrentCategory().search(null);

                            getCurrentCategory().unShow(this);
                            getCurrentCategory().preShow(this);
                            getCurrentCategory().postShow(this);

                            updateGrid();

                        } else {

                            searchField.setVisible(true);
                            searchField.setEnabled(true);
                            searchField.setFocused(true);

                            getCurrentCategory().search(searchField.getText());

                            getCurrentCategory().unShow(this);
                            getCurrentCategory().preShow(this);
                            getCurrentCategory().postShow(this);

                            updateGrid();

                        }

                    }

                }

            }

            getCurrentCategory().actionPerformed(this, button);

        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 1258291200);

        if (!needNetUpdate) {

            moneyArea.drawBalanceArea((int) resolution.getScaledWidth() - 20, (int) (resolution.getScaledHeight() * 0.1 + 29), mouseX, mouseY);

            if (!loading) {

                can_process = true;

                getCurrentCategory().draw(this, m_Page, mouseX, mouseY, partialTicks, DrawType.BG);
                getCurrentCategory().draw(this, m_Page, mouseX, mouseY, partialTicks, DrawType.PRE);

                super.drawScreen(mouseX, mouseY, partialTicks);

                for (GuiGradientTextField ggtf : listTextFields) {

                    ggtf.drawTextBox();

                }

                getCurrentCategory().draw(this, m_Page, mouseX, mouseY, partialTicks, DrawType.POST);

                getCurrentCategory().draw(this, m_Page, mouseX, mouseY, partialTicks, DrawType.OVERLAY);


            } else {

                can_process = false;

                getCurrentCategory().draw(this, m_Page, mouseX, mouseY, partialTicks, DrawType.BG);

                super.drawScreen(mouseX, mouseY, partialTicks);

                getCurrentCategory().draw(this, m_Page, mouseX, mouseY, partialTicks, DrawType.POST);

            }

        } else {

            if (loading) {

                can_process = false;


            }

        }

        GL11.glTranslatef(0, 0, 1000f);

        ContextMenuManager.draw(this, mouseX, mouseY);

        if (dbgFlag) {

            ContextMenuManager.drawDebug(this, mouseX, mouseY);

        }
        GL11.glTranslatef(0, 0, -1000f);

        for (ru.log_inil.mc.minedonate.gui.GuiEntry ge : gEntries.values()) {

            if (ge.isVisible()) {

                ge.draw(this, 0, mouseX, mouseY, partialTicks, DrawType.POST);

            }

        }

    }

    public void drawHoveringText(List<String> list, int mouseX, int mouseY, FontRenderer fontRenderer) {
        super.drawHoveringText(list, mouseX, mouseY, fontRenderer);
    }

    @Override
    public void initGui() {

        super.initGui();
        instance = this;

        resolution = new ScaledResolution(this.mc);

        m_Page = 0;

        getCurrentCategory().unShow(this);

        updateGrid();
        updateButtons(false);

        getCurrentCategory().postShow(this);

        //getCurrentCategory ( ) . updateButtons ( this, m_Page ) ;

        //

        if (moneyArea == null) {

            moneyArea = new GuiMoneyArea();

        }

        moneyArea.initGui(this);

        //

        if (needNetUpdate && !loading) {

            ModNetworkRegistry.sendToServerNeedUpdatePacket(CodePacket.Code.CLIENT_NEED_FULL_INFO);

            loading = true;

        }

        for (ru.log_inil.mc.minedonate.gui.GuiEntry ge : gEntries.values()) {

            if (ge.isVisible()) {

                ge.postShow(this);

            }

        }

    }

    @Override
    public void onGuiClosed() {

        ContextMenuManager.clean();

        if (lastEntry != null && lastEntry.needUnShowWhenGuiClose()) {

            lastEntry.show(false);
            lastEntry.unShow(this);

            if (lastEntry.needReloadOnUnShow()) {

                initGui();

            }

        }

    }

    public void onGuiClosed(boolean byBacked) {

        if (byBacked) {

            ContextMenuManager.clean();

        }

    }

    private void addCategories() {

        int posX = 30;

        for (int i = 0; i < getCurrentShopCategories().length; ++i) {

            if (getCurrentShopCategories()[i].getEnabled()) { //#LOG

                CategoryButton btn = new CategoryButton(i, getNextButtonId(), posX, (int) (resolution.getScaledHeight() * 0.1) + 19, getCurrentShopCategories()[i].getButtonText());

                btn.width = getCurrentShopCategories()[i].getButtonWidth(); // 75
                this.addButton(btn, true);

                posX += btn.width;

                if (i == m_Selected_Category) { // #LOG

                    btn.enabled = false;

                } else {

                    btn.enabled = true;

                }

            }

        }
    }

    public void addButton(GuiButton b, boolean noHide) {

        this.buttonList.add(b);

    }

    public void removeButton(GuiButton but) {

        this.buttonList.remove(but);

    }

    public void updateButtons(boolean updateInCat) {

        buttonList.clear();
        addCategories();

        if (MineDonate.cfgUI.addSearchButton) {

            buttonList.add(searchButton = new GuiGradientButton(ShopGUI.getNextButtonId(), 30, (int) ((resolution.getScaledHeight()) - (resolution.getScaledHeight() * 0.1)) - 5, MineDonate.cfgUI.searchButton.width, MineDonate.cfgUI.searchButton.height, MineDonate.cfgUI.searchButton.text, false, true));

            if (searchField == null) {

                listTextFields.add(searchField = new GuiGradientTextField(this.fontRenderer, 30 + MineDonate.cfgUI.searchButton.width, (int) ((resolution.getScaledHeight()) - (resolution.getScaledHeight() * 0.1)) - 5, MineDonate.cfgUI.searchField.width, MineDonate.cfgUI.searchField.height, true));

                searchField.setText(MineDonate.cfgUI.searchField.text);
                searchField.setTextHolder(MineDonate.cfgUI.searchField.textHolder);

                searchField.setVisible(false);
                searchField.setEnabled(false);

            }

            searchField.xPosition = 30 + MineDonate.cfgUI.searchButton.width;
            searchField.yPosition = (int) ((resolution.getScaledHeight()) - (resolution.getScaledHeight() * 0.1)) - 5;
            searchButton.pressed = searchField.getVisible();

        }

        buttonList.add(rightButton = exitButton = new GuiGradientButton(0, (int) (resolution.getScaledWidth() - 30) - MineDonate.cfgUI.exitButton.width, (int) ((resolution.getScaledHeight()) - (resolution.getScaledHeight() * 0.1) - 5), MineDonate.cfgUI.exitButton.width, MineDonate.cfgUI.exitButton.height, MineDonate.cfgUI.exitButton.text, false));
        // buttonList . add ( returnButton = new GuiGradientButton ( ShopGUI . getNextButtonId ( ), exitButton.xPosition -  MineDonate . cfgUI . returnButton . width, exitButton . yPosition, MineDonate . cfgUI . returnButton . width, MineDonate . cfgUI . returnButton . height, MineDonate . cfgUI . returnButton . text, false ) ) ;

        buttonList.add(pb = new PreviousButton(ShopGUI.getNextButtonId(), (int) (resolution.getScaledWidth() * 0.5) - 20 - 2, (int) ((resolution.getScaledHeight()) - (resolution.getScaledHeight() * 0.1)) - 20 - 10, 20, 20, "<"));

        buttonList.add(nb = new NextButton(ShopGUI.getNextButtonId(), pb.x + pb.width + 4, pb.y, 20, 20, ">"));

        if (getCurrentCategory().elementsOnPage() != 0 && getCurrentCategory().getSourceCount(getCurrentShopId()) > getCurrentCategory().elementsOnPage()) {

            pb.enabled = (m_Page > 0);

            nb.enabled = getCurrentCategory().elementsOnPage() > 0 && m_Page < (int) Math.ceil(getCurrentCategory().getSourceCount(getCurrentShopId()) / (getCurrentCategory().elementsOnPage()));

        } else {

            pb.enabled = nb.enabled = false;
            pb.visible = nb.visible = false;

        }

        if (updateInCat) {

            getCurrentCategory().updateButtons(this, m_Page);

        }

        for (ru.log_inil.mc.minedonate.gui.GuiEntry ge : gEntries.values()) {

            if (ge.isVisible()) {

                ge.postShow(this);

            }

        }

    }

    public void updateGrid() {

        int tmpW;

        if (getCurrentCategory().getItemWidth() > 0) {

            tmpH = resolution.getScaledHeight() - 50 - 25;

            tmpW = resolution.getScaledWidth() - 50 - 50;

            getCurrentCategory().setColCount(tmpW / getCurrentCategory().getItemWidth());

            getCurrentCategory().setRowCount(tmpH / getCurrentCategory().getItemHeight());

        }

    }

    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public RenderItem getItemRender() {
        return this.itemRender;
    }

    public void drawGradientRectAccess(int par1, int par2, int par3, int par4, int par5, int par6) {
        drawGradientRect(par1, par2, par3, par4, par5, par6);
    }


    public void renderToolTipAccess(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_) {
        renderToolTip(p_146285_1_, p_146285_2_, p_146285_3_);
    }

    public void drawTexturedModalRectNormal(int x, int y, int width, int height) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        bufferbuilder.pos(x, y + height, 0.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(0.0D, 0.0D).endVertex();

        tessellator.draw();
        /*tessellator.addVertexWithUV(x, y + height, 0, 0.0, 1.0);
        tessellator.addVertexWithUV(x + width, y + height, 0, 1.0, 1.0);
        tessellator.addVertexWithUV(x + width, y, 0, 1.0, 0.0);
        tessellator.addVertexWithUV(x, y, 0, 0.0, 0.0);
        tessellator.draw();*/

    }

    public List<GuiButton> getButtonList() {

        return this.buttonList;

    }

    public ScaledResolution getScaledResolution() {

        return resolution;

    }

    public void drawHoveringTextAccess(List<String> list, int mouseX, int mouseY, FontRenderer fontRenderer) {
        this.drawHoveringText(list, mouseX, mouseY, fontRenderer);
    }

    @Override
    public boolean doesGuiPauseGame() {

        return false;

    }

    @Override
    public void drawDefaultBackground() {

    }

    public void refresh() {

        if (Minecraft.getMinecraft().currentScreen == this) {

            initGui();

        }

    }

}