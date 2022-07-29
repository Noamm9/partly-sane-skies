package me.partlysanestudios.partlysaneskies;

import me.partlysanestudios.partlysaneskies.keybind.KeyInit;
import me.partlysanestudios.partlysaneskies.rngdroptitle.DropBannerDisplay;
import me.partlysanestudios.partlysaneskies.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

@Mod(modid = Main.MODID, version = Main.VERSION, name = Main.NAME)
public class Main
{
    public static final String MODID = "PartlySaneSkies";
    public static final String NAME = "Partly Sane Skies";
    public static final String VERSION = "1.0";

    public static boolean isHypixel;
    public static boolean isSkyblock;
    public static boolean isDebugMode;

    
    public static Minecraft minecraft;
    
    @EventHandler
    public void init(FMLInitializationEvent evnt) {
        System.out.println("Hallo World!");
        isHypixel = false;
        isSkyblock = false;
        isDebugMode = false;
        minecraft = Minecraft.getMinecraft();


        MinecraftForge.EVENT_BUS.register(this);
        
        KeyInit.init();
        MinecraftForge.EVENT_BUS.register(new DropBannerDisplay());


        System.out.println("Partly Sane Skies has loaded.");
    }

    @SubscribeEvent
    public void joinServerEvent(ClientConnectedToServerEvent evnt) {
        if(minecraft.getCurrentServerData().serverIP.contains(".hypixel.net")) {
            isHypixel = true;
        }
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent evnt) {
        if(KeyInit.debugKey.isPressed()) {
            Utils.visPrint(Main.detectScoreboardName("§lSKYBLOCK"));
            Utils.visPrint(Main.minecraft.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1).getDisplayName());
            Utils.visPrint(Main.minecraft.displayWidth/2);
            Utils.visPrint(Main.minecraft.displayHeight/4);
            isDebugMode = !isDebugMode;
            Utils.visPrint("Debug mode: " + isDebugMode);
        }

        try {
            isSkyblock = Main.detectScoreboardName("§lSKYBLOCK");
            isHypixel = minecraft.getCurrentServerData().serverIP.contains(".hypixel.net");
        }
        catch(NullPointerException expt) {

        }
        finally {

        }
    }

    @SubscribeEvent
    public void chatAnalyzer(ClientChatReceivedEvent evnt) {
        if(isDebugMode) System.out.println(evnt.message.getFormattedText());
    }

    public static boolean detectScoreboardName(String desiredName) {
        String scoreboardName = minecraft.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1).getDisplayName();

        if(scoreboardName.contains(desiredName)) return true;

        return false;
    }
}
