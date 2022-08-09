package com.slackow.explore;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.slackow.explore.mixin.LevelStorageAccessor;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiMCManager {

    private static Instance selected;

    public MultiMCManager() {

    }

    private static final boolean hasMultiMC = Files.exists(MinecraftClient.getInstance().runDirectory.toPath().resolveSibling("instance.cfg"));

    public static boolean hasMultiMC() {
        return hasMultiMC;
    }


    public void setSaves(Path path) {
        ((LevelStorageAccessor) MinecraftClient.getInstance().getLevelStorage()).setSavesDirectory(path);
    }

    public List<Instance> getInstances() {
        Path currInstancePath = MinecraftClient.getInstance().runDirectory.toPath().getParent();
        Path mmcPath = currInstancePath.getParent();
        Path instances = mmcPath.resolve("instgroups.json");
        Map<String, String> groupMsp = new HashMap<>();
        if (Files.exists(instances)) {
            try (Reader reader = Files.newBufferedReader(instances)) {
                JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("groups").entrySet()) {
                    String group = entry.getKey();
                    for (JsonElement string : entry.getValue().getAsJsonObject().getAsJsonArray("instances")) {
                        groupMsp.put(string.getAsString(), group);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (Stream<Path> paths = Files.list(mmcPath)) {
            return paths.filter(path -> Files.isDirectory(path.resolve(".minecraft")) &&
                            Files.exists(path.resolve("instance.cfg")))
                    .map(path -> new Instance(nameFromPath(path), path.getFileName().toString(), groupMsp.get(path.getFileName().toString()), worldCountFromPath(path)))
                    .sorted(Comparator.comparing(Instance::getGroup)
                            .thenComparing(Instance::getName)
                            .thenComparing(Instance::getPath))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int worldCountFromPath(Path path) {
        try(Stream<Path> dirs = Files.list(path.resolve(".minecraft/saves"))) {
            return (int) dirs.filter(world -> Files.isDirectory(world) && Files.exists(world.resolve("level.dat"))).count();
        } catch (IOException e) {
            return 0;
        }
    }

    private static String nameFromPath(Path path) {
        try (Stream<String> lines = Files.lines(path.resolve("instance.cfg"))) {
            return lines.filter(line -> line.startsWith("name=")).findAny().map(str -> str.substring(5)).orElseGet(() -> path.getFileName().toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config", e);
        }

    }

    public void setSelected(Instance instance) {
        selected = instance;
    }

    public Instance getSelected() {
        return selected;
    }
}