package de.chafficplugins.mininglevels.api;

import com.google.gson.reflect.TypeToken;
import de.chafficplugins.mininglevels.MiningLevels;
import de.chafficplugins.mininglevels.io.FileManager;
import io.github.chafficui.CrucialAPI.io.Json;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;

public class MiningLevel {
    private final String name;
    private final int nextLevelXP;
    private final int ordinal;
    private float instantBreakProbability = 0;
    private float extraOreProbability = 0;
    private float maxExtraOre = 0;
    private int hasteLevel = 0;
    private final String[] commands = new String[0];
    private final Reward[] rewards = new Reward[0];

    public MiningLevel(String name, int nextLevelXP, int ordinal) {
        this.name = name;
        this.nextLevelXP = nextLevelXP;
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getRewards() {
        ItemStack[] items = new ItemStack[rewards.length];
        for (int i = 0; i < rewards.length; i++) {
            ItemStack item = rewards[i].getItemStack();
            if(item != null) {
                items[i] = item;
            } else {
                return new ItemStack[0];
            }
        }
        return items;
    }

    public int getNextLevelXP() {
        return nextLevelXP;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public float getInstantBreakProbability() {
        return instantBreakProbability;
    }

    public void setInstantBreakProbability(float instantBreakProbability) {
        this.instantBreakProbability = instantBreakProbability;
    }

    public float getExtraOreProbability() {
        return extraOreProbability;
    }

    public void setExtraOreProbability(float extraOreProbability) {
        this.extraOreProbability = extraOreProbability;
    }

    public float getMaxExtraOre() {
        return maxExtraOre;
    }

    public void setMaxExtraOre(float maxExtraOre) {
        this.maxExtraOre = maxExtraOre;
    }

    public int getHasteLevel() {
        return hasteLevel;
    }

    public void setHasteLevel(int hasteLevel) {
        this.hasteLevel = hasteLevel;
    }

    public MiningLevel getBefore() {
        if (ordinal == 0) return get(0);
        return get(ordinal - 1);
    }

    public MiningLevel getNext() {
        if (ordinal >= miningLevels.size() - 1)
            return get(miningLevels.size() - 1);
        return get(ordinal + 1);
    }

    public void levelUp(MiningPlayer miningPlayer, Player player) {
        if (ordinal + 1 >= MiningLevel.miningLevels.size()) {
            player.sendMessage(ChatColor.RED + "You reached the max level!");
            return;
        }
        miningPlayer.setXp(miningPlayer.getXp() - nextLevelXP);
        MiningLevel nextLevel = getNext();
        miningPlayer.setLevel(nextLevel);
        player.playSound(player.getLocation(), MiningLevels.lvlUpSound, 1, 1);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Your Mininglevel is now " + nextLevel.name + "!"));

        player.sendMessage(ChatColor.WHITE + "Level " + ChatColor.GREEN + nextLevel.name + ChatColor.WHITE + " unlocked!");
        player.sendMessage(ChatColor.WHITE + "-------------");

        int nextLevelXP = nextLevel.getNextLevelXP();
        double nextHasteLevel = nextLevel.getHasteLevel();
        double nextInstantBreakProbability = nextLevel.getInstantBreakProbability();
        double nextExtraOreProbability = nextLevel.getExtraOreProbability();
        double nextMaxExtraOre = nextLevel.getMaxExtraOre();
        if(nextHasteLevel != hasteLevel) {
            player.sendMessage(ChatColor.WHITE + "Haste Level: " + ChatColor.YELLOW + hasteLevel + ChatColor.WHITE + " -> " + ChatColor.GREEN + nextHasteLevel);
        }
        if(nextInstantBreakProbability != instantBreakProbability) {
            player.sendMessage(ChatColor.WHITE + "Instant Break Probability: " + ChatColor.YELLOW + instantBreakProbability + ChatColor.WHITE + " -> " + ChatColor.GREEN + nextInstantBreakProbability);
        }
        if(nextExtraOreProbability != extraOreProbability) {
            player.sendMessage(ChatColor.WHITE + "Extra Ore Probability: " + ChatColor.YELLOW + extraOreProbability + ChatColor.WHITE + " -> " + ChatColor.GREEN + nextExtraOreProbability);
        }
        if(nextMaxExtraOre != maxExtraOre) {
            player.sendMessage(ChatColor.WHITE + "Max Extra Ore: " + ChatColor.YELLOW + maxExtraOre + ChatColor.WHITE + " -> " + ChatColor.GREEN + nextMaxExtraOre);
        }
        if(nextLevel.rewards != null && nextLevel.rewards.length > 0) {
            player.sendMessage("");
            player.sendMessage(ChatColor.WHITE + "Rewards: ");
            for (Reward reward : nextLevel.rewards) {
                player.sendMessage(ChatColor.WHITE + "  " + ChatColor.YELLOW + reward.getName() + ChatColor.WHITE + ": " + ChatColor.GREEN + reward.getAmount());
            }
            miningPlayer.addRewards(nextLevel.getRewards());
            player.sendMessage("Claim your rewards with /miningrewards");
        }
        if(nextLevel.commands != null && nextLevel.commands.length > 0) {
            for (String command : nextLevel.commands) {
                player.performCommand(command);
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof MiningLevel) {
            return ((MiningLevel) object).ordinal == this.ordinal;
        }
        return false;
    }

    //Static
    public static ArrayList<MiningLevel> miningLevels = new ArrayList<>();

    public static void init() throws IOException {
        miningLevels = Json.fromJson(FileManager.LEVELS, new TypeToken<ArrayList<MiningLevel>>() {
        }.getType());
    }

    public static void save() throws IOException {
        if(miningLevels != null) {
            FileManager.saveFile(FileManager.LEVELS, miningLevels);
        }
    }

    public static void reload() throws IOException {
        init();
    }

    public static MiningLevel get(int index) {
        return miningLevels.get(index);
    }
}
