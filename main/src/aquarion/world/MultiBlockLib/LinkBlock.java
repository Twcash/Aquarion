package aquarion.world.MultiBlockLib;


import arc.Core;
import arc.func.Cons;
import arc.func.Func;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.input.Binding;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.modules.ItemModule;
import mindustry.world.modules.LiquidModule;
//Credit to https://github.com/LaoHuaJiOfficial/MultiBlockLib/blob/master/src/multiblock/extend/LinkBlock.java
/**
 * Inner building that are linked to a specific building.
 * Handle items, liquids, damage and so on a passed to the main building to handle.
 * NEVER SUPPOSED TO USE OUTSIDE MULTI BLOCK!
 */
public class LinkBlock extends Block {
    public LinkBlock(String name) {
        super(name);

        update = true;
        squareSprite = false;

        destructible = true;
        breakable = false;
        solid = true;
        rebuildable = false;
        canOverdrive = false;

        instantDeconstruct = true;

        hasItems = true;
        hasLiquids = true;
        hasPower = false;

        buildVisibility = BuildVisibility.hidden;
        inEditor = false;

        drawCracks = false;
        drawArrow = false;
        drawTeamOverlay = false;

        ambientSound = Sounds.none;
        breakSound = Sounds.none;
        destroySound = Sounds.none;
        placeSound = Sounds.none;

        localizedName = Core.bundle.get(getContentType() + ".new-horizon-inner-entity.name", this.name);
        description = Core.bundle.getOrNull(getContentType() + ".new-horizon-inner-entity.description");
        details = Core.bundle.getOrNull(getContentType() + ".new-horizon-inner-entity.details");
    }

    @Override
    public boolean canBreak(Tile tile) {
        return tile.build instanceof LinkBuild link && link.linkedBuilding() != null;
    }

    public static void pickLinkedBlock() {
        if (Vars.headless || !Vars.state.isGame() || !Core.input.keyTap(Binding.pick)
            || Vars.player == null || !Vars.player.isBuilder() || Core.scene.hasDialog() || Core.scene.hasMouse()) return;

        var input = Vars.control.input;
        if (input.commandMode) return;

        var mouse = Core.input.mouseWorld();
        if (!(Vars.world.buildWorld(mouse.x, mouse.y) instanceof LinkBuild link) || link.inFogTo(Vars.player.team())) return;

        Building target = link.linkedBuilding();
        if (target == null || target.inFogTo(Vars.player.team())) return;

        Block block = target.block;
        if (!Vars.state.rules.editor && (!block.isVisible() || !block.unlockedNowHost() || !block.placeablePlayer
            || !block.environmentBuildable() || !block.supportsEnv(Vars.state.rules.env))) return;

        // Let the normal picker handle queued plans that overlap the cursor.
        for (BuildPlan plan : Vars.player.unit().plans()) {
            if (!plan.breaking && plan.block.bounds(plan.x, plan.y, Tmp.r1).contains(mouse)) return;
        }

        input.block = block;
        block.lastConfig = block.copyConfig ? target.config() : null;
        block.onPicked(target.tile);
        var placement = Vars.ui.hudfrag.blockfrag;
        if (block.isVisible() && placement.currentCategory != block.category) {
            placement.currentCategory = block.category;
            placement.rebuild();
        }
    }

    public static void redirectDeconstructionPlans() {
        if (!Vars.state.isGame() || Vars.player == null || !Vars.player.isBuilder()) return;

        var plans = Vars.player.unit().plans();
        for (int i = plans.size - 1; i >= 0; i--) {
            BuildPlan plan = plans.get(i);
            if (!plan.breaking || !(plan.build() instanceof LinkBuild link)) continue;

            Building target = link.linkedBuilding();
            if (target == null || !Build.validBreak(Vars.player.team(), target.tileX(), target.tileY())) {
                plans.removeIndex(i);
                continue;
            }

            int x = target.tileX(), y = target.tileY();
            boolean alreadyQueued = false;
            for (BuildPlan queued : plans) {
                if (queued.breaking && queued.x == x && queued.y == y) {
                    alreadyQueued = true;
                    break;
                }
            }
            if (alreadyQueued) {
                plans.removeIndex(i);
            } else {
                plan.x = x;
                plan.y = y;
                plan.block = target.block;
            }
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find("status-blasted");
    }

    @Override
    public void loadIcon() {
        fullIcon = uiIcon = Core.atlas.find("status-blasted");
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class LinkBuild extends Building {
        public Building linkBuild;

        public Building linkedBuilding() {
            return linkBuild != null && linkBuild.isValid() && linkBuild.team == team ? linkBuild : null;
        }

        public void updateLink(Building link) {
            if (link instanceof MultiBlockEntity) {
                linkBuild = link;
                items = link.items;
                liquids = link.liquids;
                //might not a good idea if do so
                //block = link.block;
            } else {
                linkBuild = null;
                tile.remove();
            }

            //create empty module to avoid null
            if (items == null) items = new ItemModule();
            if (liquids == null) liquids = new LiquidModule();
        }

        @Override
        public void updateTile() {
            if (linkBuild == null || !linkBuild.isValid()) {
                kill();
            }
        }

        //skip draw
        @Override
        public void draw() {
        }

        @Override
        public void drawSelect() {
            if (linkBuild != null) {
                linkBuild.drawSelect();
            }
        }

        @Override
        public TextureRegion getDisplayIcon() {
            return linkBuild == null ? this.block.uiIcon : linkBuild.getDisplayIcon();
        }

        @Override
        public String getDisplayName() {
            String name = linkBuild == null ? this.block.localizedName : linkBuild.block.localizedName;
            return this.team == Team.derelict ? name + "\n" + Core.bundle.get("block.derelict") : name + (this.team != Vars.player.team() && !this.team.emoji.isEmpty() ? " " + this.team.emoji : "");
        }

        @Override
        public void displayBars(Table table) {
            if (linkBuild != null) {
                for (Func<Building, Bar> buildingBarFunc : linkBuild.block.listBars()) {
                    Bar result = buildingBarFunc.get(linkBuild);
                    if (result != null) {
                        table.add(result).growX();
                        table.row();
                    }
                }
            }
        }

        @Override
        public void displayConsumption(Table table) {
            if (linkBuild != null) {
                table.left();
                for (Consume cons : linkBuild.block.consumers) {
                    if (cons.optional && cons.booster) continue;
                    cons.build(linkBuild, table);
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (linkBuild != null) {
                return linkBuild.acceptItem(source, item);
            } else {
                return false;
            }
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            if (linkBuild != null) {
                return linkBuild.acceptStack(item, amount, source);
            } else {
                return 0;
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (linkBuild != null) {
                return linkBuild.acceptLiquid(source, liquid);
            } else {
                return false;
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            //todo
            return super.acceptPayload(source, payload);
        }

        @Override
        public float handleDamage(float amount) {
            if (linkBuild != null) {
                return linkBuild.handleDamage(amount);
            } else {
                return 0;
            }
        }

        @Override
        public void damage(float damage) {
            if (linkBuild != null) {
                linkBuild.damage(damage);
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (linkBuild != null) {
                linkBuild.handleItem(source, item);
            }
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            if (linkBuild != null) {
                linkBuild.handleStack(item, amount, source);
            }
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            if (linkBuild != null) {
                linkBuild.handleLiquid(source, liquid, amount);
            }
        }

        @Override
        public void handlePayload(Building source, Payload payload) {
            if (linkBuild != null) {
                linkBuild.handlePayload(source, payload);
            }
        }

        @Override
        public void handleString(Object value) {
            if (linkBuild != null) {
                linkBuild.handleString(value);
            }
        }

        @Override
        public void handleUnitPayload(Unit unit, Cons<Payload> grabber) {
            if (linkBuild != null) {
                linkBuild.handleUnitPayload(unit, grabber);
            }
        }

        @Override
        public void onProximityUpdate() {
            if (linkBuild != null) ((MultiBlockEntity) linkBuild).updateLinkProximity();
            super.onProximityUpdate();
        }

        @Override
        public void onDestroyed() {
        }

        @Override
        public void remove() {
            super.remove();
        }

        @Override
        public boolean canPickup() {
            return false;
        }
    }
}
