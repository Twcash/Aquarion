package aquarion.world.blocks.effect;

import arc.Core;
import arc.files.Fi;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ResearchServer extends Block {
    public static final String SAVE_KEY = "aquarion-research";

    public int researchCapacity = 1000;
    /** Global research pool: sectorId -> (item -> amount). Persisted to file. */
    public static final ObjectMap<Integer, ObjectMap<Item, Integer>> globalResearch = new ObjectMap<>();

    public ResearchServer(String name) {
        super(name);
        solid = true;
        update = true;
    }
    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.itemCapacity, researchCapacity*60, StatUnit.items);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("research", (ResearchServerBuild b) ->
                new Bar(
                        () -> Core.bundle.format("bar.research-progress", b.researchTotal()),
                        () -> Pal.accent,
                        () -> b.researchFill()
                )
        );
    }

    public static Fi researchFile() {
        return Vars.saveDirectory.child(SAVE_KEY + ".dat");
    }

    public static void saveGlobalResearch() {
        try {
            StringBuilder sb = new StringBuilder();
            boolean firstEntry = true;
            for (ObjectMap.Entry<Integer, ObjectMap<Item, Integer>> sectorEntry : globalResearch) {
                if (!firstEntry) sb.append("#");
                firstEntry = false;
                sb.append(sectorEntry.key);
                sb.append("=");
                boolean firstItem = true;
                for (ObjectMap.Entry<Item, Integer> itemEntry : sectorEntry.value) {
                    if (!firstItem) sb.append(",");
                    firstItem = false;
                    sb.append(itemEntry.key.id).append(":").append(itemEntry.value);
                }
            }
            researchFile().writeString(sb.toString(), false);
        } catch (Exception e) {
        }
    }

    public static void loadGlobalResearch() {
        globalResearch.clear();
        Fi file = researchFile();
        if (!file.exists()) return;
        try {
            String data = file.readString();
            if (data == null || data.isEmpty()) return;
            String[] sectors = data.split("#");
            for (String sectorStr : sectors) {
                String[] parts = sectorStr.split("=");
                if (parts.length != 2) continue;
                try {
                    int sectorId = Integer.parseInt(parts[0]);
                    ObjectMap<Item, Integer> items = new ObjectMap<>();
                    String[] itemStrs = parts[1].split(",");
                    for (String itemStr : itemStrs) {
                        String[] kv = itemStr.split(":");
                        if (kv.length == 2) {
                            int itemId = Integer.parseInt(kv[0]);
                            int amount = Integer.parseInt(kv[1]);
                            Item item = Vars.content.item(itemId);
                            if (item != null && amount > 0) {
                                items.put(item, amount);
                            }
                        }
                    }
                    if (!items.isEmpty()) {
                        globalResearch.put(sectorId, items);
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception e) {
        }
    }

    /** Get total research items across all sectors. */
    public static ObjectMap<Item, Integer> getAllResearch() {
        ObjectMap<Item, Integer> total = new ObjectMap<>();
        for (ObjectMap.Entry<Integer, ObjectMap<Item, Integer>> sectorEntry : globalResearch) {
            for (ObjectMap.Entry<Item, Integer> itemEntry : sectorEntry.value) {
                total.put(itemEntry.key, total.get(itemEntry.key, 0) + itemEntry.value);
            }
        }
        return total;
    }

    /** Remove research items across all sectors proportionally. */
    public static void removeResearch(Item item, int amount) {
        int total = getAllResearch().get(item, 0);
        if (total <= 0) return;
        double percentage = (double) amount / total;
        int remaining = amount;

        for (ObjectMap.Entry<Integer, ObjectMap<Item, Integer>> sectorEntry : globalResearch) {
            if (remaining <= 0) break;
            int have = sectorEntry.value.get(item, 0);
            if (have <= 0) continue;
            int toRemove = Math.min((int) Math.ceil(percentage * have), remaining);
            toRemove = Math.min(toRemove, have);
            sectorEntry.value.put(item, have - toRemove);
            remaining -= toRemove;
        }

        if (remaining < amount) saveGlobalResearch();
    }

    public static int getSectorResearchTotal(int sectorId) {
        ObjectMap<Item, Integer> sectorResearch = globalResearch.get(sectorId);
        if (sectorResearch == null) return 0;
        int total = 0;
        for (ObjectMap.Entry<Item, Integer> entry : sectorResearch) {
            total += entry.value;
        }
        return total;
    }

    public static void addResearch(int sectorId, Item item, int amount) {
        ObjectMap<Item, Integer> sectorResearch = globalResearch.get(sectorId);
        if (sectorResearch == null) {
            sectorResearch = new ObjectMap<>();
            globalResearch.put(sectorId, sectorResearch);
        }
        sectorResearch.put(item, sectorResearch.get(item, 0) + amount);
        saveGlobalResearch();
    }

    public class ResearchServerBuild extends Building {
        public int getSectorId() {
            if (Vars.state.isCampaign() && Vars.state.hasSector()) {
                return Vars.state.getSector().id;
            }
            return 0;
        }

        public int researchTotal() {
            return getSectorResearchTotal(getSectorId());
        }

        public float researchFill() {
            int total = researchTotal();
            return total <= 0 ? 0f : Math.min(1f, (float) total / (((ResearchServer) block).researchCapacity * 50f));
        }
    }
}
