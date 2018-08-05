package ru.log_inil.mc.minedonate.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.alastar.minedonate.MineDonate;
import ru.alastar.minedonate.gui.ShopGUI;
import ru.log_inil.mc.minedonate.localData.ui.DataOfUIMoneyGroup;

import javax.imageio.ImageIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiMoneyArea {

    List<String> linesRemoved = new ArrayList<>();
    List<String> types = new ArrayList<String>();
    Map<String, MoneyLine> lines = new HashMap<>();
    int i = 0, s = 0;
    ShopGUI gui;

    public GuiMoneyArea() {


    }

    public GuiMoneyArea updateDrawData() {

        // lines . clear ( ) ;

        for (String type : lines.keySet()) {

            if (!MineDonate.getAccount().moneys.containsKey(type)) {

                linesRemoved.add(type);

            }

        }

        for (String ml : linesRemoved) {

            lines.remove(ml);

        }

        linesRemoved.clear();

        MoneyLine ml;

        for (String type : MineDonate.getAccount().moneys.keySet()) {

            if (!lines.containsKey(type)) {

                ml = new MoneyLine(type);
                ml.load();

                lines.put(type, ml);

            }


        }

        s = lines.size();

        return this;

    }

    public void drawBalanceArea(int offsetX, int offsetY, int mouseX, int mouseY) {

        i = 0;

        for (MoneyLine moneyLine : lines.values()) {

            moneyLine.drawBalance(i, s, MineDonate.getAccount().getMoney(moneyLine.type), offsetX, offsetY, mouseX, mouseY);

            i++;

        }

    }

    public void drawPriceArea(int offsetX, int offsetY, int price, String mtype) {

        if (lines.containsKey(mtype)) {

            lines.get(mtype).drawPrice(price, offsetX, offsetY);

        }

    }

    public void initGui(ShopGUI _gui) {

        gui = _gui;

    }

    class MoneyLine {

        String type;

        String pricePref;
        String priceSuff;
        String balancePref;
        String balanceSuff;

        ResourceLocation texture;
        int tmpX;
        int tmpY;
        int tmp = 0;
        public MoneyLine(String _type) {

            type = _type;

        }

        public void load() {

            for (DataOfUIMoneyGroup mg : MineDonate.cfgUI.moneyGroups) {

                if (type.equalsIgnoreCase(mg.id)) {

                    if (!"".equals(mg.image)) {

                        if (mg.imageIsResource) {

                            if (mg.image.contains(":")) {

                                texture = new ResourceLocation(mg.image.split(":")[0], mg.image.split(":")[1]);

                            } else {

                                texture = new ResourceLocation(mg.image);

                            }

                        } else {

                            try {

                                texture = Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation("minedonate", new DynamicTexture(ImageIO.read(new URL(mg.image))));

                            } catch (Exception ex) {

                                ex.printStackTrace();

                            }

                        }

                    }

                    pricePref = mg.pricePref;
                    priceSuff = mg.priceSuff;
                    balancePref = mg.balancePref;
                    balanceSuff = mg.balanceSuff;

                }

            }

        }

        public void drawBalance(int i, int max, int count, int offsetX, int offsetY, int mouseX, int mouseY) {

            tmpX = offsetX - 20 - (gui.getFontRenderer().getStringWidth(count + (balanceSuff != null ? balanceSuff : "")));
            tmpY = offsetY - ((i) * 10); // - ( max * 3 )

            tmp = 0;

            if (balancePref != null) {

                gui.getFontRenderer().drawString(balancePref, tmpX - gui.getFontRenderer().getStringWidth(balancePref), tmpY, 14737632);

            }

            if (texture != null) {

                GL11.glEnable(GL11.GL_BLEND);

                Minecraft.getMinecraft().renderEngine.bindTexture(texture);
                gui.drawTexturedModalRectNormal(tmpX, tmpY, 8, 8);

                GL11.glDisable(GL11.GL_BLEND);

            }

            tmp += 10;

            gui.getFontRenderer().drawString(count + (balanceSuff != null ? balanceSuff : ""), tmpX + tmp, tmpY, 14737632);

        }

        public void drawPrice(int price, int offsetX, int offsetY) {

            tmpX = (gui.getFontRenderer().getStringWidth((pricePref != null ? pricePref : "") + price + (priceSuff != null ? priceSuff : "")));
            gui.getFontRenderer().drawString((pricePref != null ? pricePref : "") + price + (priceSuff != null ? priceSuff : ""), offsetX - tmpX / 2, offsetY, 14737632);

            if (texture != null) {

                GL11.glEnable(GL11.GL_BLEND);

                Minecraft.getMinecraft().renderEngine.bindTexture(texture);
                gui.drawTexturedModalRectNormal(offsetX + 5 + tmpX / 2, offsetY, 9, 9);

                GL11.glDisable(GL11.GL_BLEND);

            }

			/*
			tmp = 0 ;

			if ( pricePref != null ) {
				
				gui . getFontRenderer ( ) . drawString ( pricePref, offsetX - gui . getFontRenderer ( ) . getStringWidth ( pricePref ), offsetY, 14737632 ) ;
				
			}

			if ( texture != null ) {
				
			 	RenderHelper . enableGUIStandardItemLighting ( ) ;
				
			 	Minecraft . getMinecraft ( ) . renderEngine . bindTexture ( texture ) ;
			 	gui . drawTexturedModalRectNormal ( offsetX, offsetY, 8, 8 ) ;
								
				tmp += 10 ;
				
			}

			gui . getFontRenderer ( ) . drawString ( price + ( priceSuff != null ? priceSuff : "" ), offsetX + tmp, offsetY, 14737632 ) ;
			*/
        }

    }

}
