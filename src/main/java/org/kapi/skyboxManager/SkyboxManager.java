package org.kapi.skyboxManager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SkyboxManager implements ModInitializer {
    public static final String MOD_ID = "skybox-manager";
    public static Path configDir;
    public static List<String> skyboxFolders = new ArrayList<>();
    public static boolean lockTime = false;
    public static long lockedTime = 6000L; // midi par défaut

    @Override
    public void onInitialize() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("myskyboxes");
        File dir = configDir.toFile();

        System.out.println("Config dir: " + configDir.toAbsolutePath());
        System.out.println("Dir exists: " + dir.exists());

        if (!dir.exists()) {
            dir.mkdirs();
        }
        scanSkyboxFolders();
    }

    public static void scanSkyboxFolders() {
        skyboxFolders.clear();
        File[] files = configDir.toFile().listFiles(File::isDirectory);
        if (files != null) {
            for (File file : files) {
                skyboxFolders.add(file.getName());
            }
        }
    }
}