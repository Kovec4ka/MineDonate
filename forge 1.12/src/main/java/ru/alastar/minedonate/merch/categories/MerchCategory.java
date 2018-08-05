package ru.alastar.minedonate.merch.categories;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.alastar.minedonate.merch.Merch;
import ru.alastar.minedonate.rtnl.ModDataBase;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Alastar on 21.07.2017.
 */
public abstract class MerchCategory {

    public int shopId;
    public int catId;
    public String moneyType;
    public Map<Integer, Merch> m_Merch = new LinkedHashMap<>();

    public MerchCategory(int _shopId, int _catId, String _moneyType) {

        shopId = _shopId;
        catId = _catId;
        moneyType = _moneyType;

    }

    @SideOnly(Side.SERVER)
    public boolean canReverse() {

        return false;

    }

    @SideOnly(Side.SERVER)
    public void reverseFor(int merchId, UUID player, String[] data) {

    }

    @SideOnly(Side.SERVER)
    public abstract void loadMerchFromDB(ResultSet rs);

    @SideOnly(Side.SERVER)
    public abstract String getDatabaseTable();

    public void addMerch(Merch merch) {

        m_Merch.put(merch.getId(), merch);

    }

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean _enabled);

    @SideOnly(Side.SERVER)
    public abstract void giveMerch(EntityPlayerMP player, Merch merch, int amount);

    public boolean merchExists(int id) {

        return m_Merch.containsKey(id);

    }

    public Merch getMerch(int id) {

        return m_Merch.get(id);

    }

    public int getNextMerchId() {

        Statement stat = null;

        try {

            stat = ModDataBase.getNewStatement("main");
            ResultSet rs = stat.executeQuery("SHOW TABLE STATUS LIKE '" + getDatabaseTable() + "';");

            int r = -1;
            while (rs.next()) {

                r = rs.getInt("Auto_increment");

            }

            rs.close();
            ModDataBase.closeStatementAndConnection(stat);

            return r;

        } catch (Exception ex) {

            ex.printStackTrace();

        }

        ModDataBase.closeStatementAndConnection(stat);

        return -1;

    }

    public Merch[] getMerch() {

        Merch[] m = new Merch[m_Merch.size()];

        return m_Merch.values().toArray(m);
    }

    public abstract Merch constructMerch();

    public void removeMerch(int id) {

        m_Merch.remove(id);

    }

    @SideOnly(Side.SERVER)
    public void removeMerch(Merch info) {

        m_Merch.remove(info.getId());

    }

    public void updateMerch(int id, Merch info) {

        m_Merch.put(id, info);

    }

    public String getMoneyType() {

        return moneyType;

    }

    public abstract Type getCatType();

    @Override
    public String toString() {

        return getClass().getName() + "@" + hashCode() + "{shopId=" + shopId + ", catId=" + catId + ", catType=" + getCatType() + ", moneyType=" + getMoneyType() + "}";

    }

    public enum Type {

        ITEMS, PRIVELEGIES, REGIONS, ENTITIES, SHOPS

    }

}
