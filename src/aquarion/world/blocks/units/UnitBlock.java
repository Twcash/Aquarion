package aquarion.world.blocks.units;

import aquarion.AquaSounds;
import aquarion.world.Uti.AquaStats;
import arc.*;
import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

//When placed and after a certain amount of time this block will be destroyed and produce a unit
public class UnitBlock extends Block {
    public UnitBlock(String name) {
        super(name);
        category = Category.units;
        destroySound = AquaSounds.start;
        destroyEffect = Fx.none;
        rebuildable = true;
        canOverdrive = false;
        commandable = true;
        configurable = true;
        saveConfig = true;
        saveData = true;
        update = true;
        ambientSound = AquaSounds.derrick;
        canPickup = false;
        hasShadow = false;
        createRubble = false;
        //Horrifically bad code.
        //Split the string the config spits back at me and individually set them
        config(String.class, (UnitBlockBuild build, String str) -> {
            if(str == null) return;

            for(String part : str.split(",")){
                if(part.startsWith("cmd=")){
                    String named = part.substring(4);
                    build.command = Vars.content.unitCommands().find(c -> c.name.equals(named));
                }else if(part.startsWith("pos=")){
                    String[] coords = part.substring(4).split(":");
                    if(coords.length == 2){
                        try{
                            float x = Float.parseFloat(coords[0]);
                            float y = Float.parseFloat(coords[1]);
                            build.commandPos = new Vec2(x, y);
                        }catch(NumberFormatException ignored){}
                    }
                }
            }
        });
        config(Integer.class, (UnitBlockBuild build, Integer i) -> {
            if(!configurable) return;
            build.progress = 0;
            if(build.command != null && (unit == null || !unit.commands.contains(build.command))){
                build.command = null;
            }
        });

        config(UnitType.class, (UnitBlockBuild build, UnitType val) -> {
            if(!configurable) return;
            if(build.command != null && !val.commands.contains(build.command)){
                build.command = null;
            }
        });

        config(UnitCommand.class, (UnitBlockBuild build, UnitCommand command) -> build.command = command);
        configClear((UnitBlockBuild build) -> build.command = null);
    }
    public float time = 60;
    public UnitType unit = UnitTypes.alpha;
    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(floating){
            return tile.floor().isLiquid;
        }
        return true;
    }
    @Override
    public void load(){
        super.load();
        region = unit.fullIcon;
    }
    @Override
    public void setStats(){
        stats.timePeriod = time;
        super.setStats();
        stats.remove(Stat.health);
        stats.add(Stat.health, health);
        stats.add(Stat.armor, armor);
        stats.add(Stat.speed, unit.speed * 60f / tilesize, StatUnit.tilesSecond);
        stats.add(Stat.size, StatValues.squared(unit.hitSize / tilesize, StatUnit.blocks));
        stats.add(Stat.itemCapacity, itemCapacity);
        stats.add(Stat.range, Strings.autoFixed(unit.maxRange / tilesize, 1), StatUnit.blocks);
        stats.add(Stat.targetsAir, unit.targetAir);
        stats.add(Stat.targetsGround, unit.targetGround);

        if(unit.abilities.any()){
            stats.add(Stat.abilities, StatValues.abilities(unit.abilities));
        }

        stats.add(Stat.flying, unit.flying);

        if(!unit.flying){
            stats.add(Stat.canBoost, unit.canBoost);
        }

        if(unit.mineTier >= 1){
            stats.addPercent(Stat.mineSpeed, unit.mineSpeed);
            stats.add(Stat.mineTier, StatValues.drillables(unit.mineSpeed, 1f, 1, null, b ->
                    b.itemDrop != null &&
                            (b instanceof Floor f && (((f.wallOre && unit.mineWalls) || (!f.wallOre && unit.mineFloor))) ||
                                    (!(b instanceof Floor) && unit.mineWalls)) &&
                            b.itemDrop.hardness <= unit.mineTier && (!b.playerUnmineable || Core.settings.getBool("doubletapmine"))));
        }
        if(unit.buildSpeed > 0){
            stats.addPercent(Stat.buildSpeed, unit.buildSpeed);
        }
        if(unit.sample instanceof Payloadc){
            stats.add(Stat.payloadCapacity, StatValues.squared(Mathf.sqrt(unit.payloadCapacity / (tilesize * tilesize)), StatUnit.blocks));
        }

        var reqs = unit.getFirstRequirements();

        if(reqs != null){
            stats.add(Stat.buildCost, StatValues.items(reqs));
        }

        if(unit.weapons.any()){
            stats.add(Stat.weapons, StatValues.weapons(unit, unit.weapons));
        }

        if(unit.immunities.size > 0){
            var imm = unit.immunities.toSeq().sort();
            //it's redundant to list wet for naval units
            if(unit.naval){
                imm.remove(StatusEffects.wet);
            }
            stats.add(Stat.immunities, StatValues.statusEffects(imm));
        }
    }
    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (UnitBlockBuild e) -> new Bar("bar.progress", Pal.ammo, e::totalProgress));

        addBar("units", (UnitBlockBuild e) ->
                new Bar(
                        () -> unit == null ? "[lightgray]" + Iconc.cancel :
                                Core.bundle.format("bar.unitcap",
                                        Fonts.getUnicodeStr(unit.name),
                                        e.team.data().countType(unit),
                                        unit == null ? Units.getStringCap(e.team) : (unit.useUnitCap ? Units.getStringCap(e.team) : "∞")
                                ),
                        () -> Pal.power,
                        () -> unit == null ? 0f : (unit.useUnitCap ? (float)e.team.data().countType(unit) / Units.getCap(e.team) : 1f)
                ));
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        if( unit.fullIcon != null) Draw.rect(unit.fullIcon, plan.drawx(), plan.drawy());
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{unit.fullIcon};
    }
    @Override
    public boolean outputsItems(){
        return false;
    }

    public class UnitBlockBuild extends Building {
        public float progress = 0;
        public float totProgress = 0;
        public @Nullable Vec2 commandPos;
        public @Nullable UnitCommand command;

        @Override
        public void draw() {
            //This is most likely unnecessary...
            Draw.z(Layer.groundUnit);
            if (unit.fullIcon != null) Draw.rect(unit.fullIcon, x, y);
            if (unit.flying) {
                float e =  Mathf.clamp(totProgress, unit.shadowElevation, 1f);

                Draw.z(Layer.groundUnit-2);
                Draw.color(Pal.shadow, Pal.shadow.a * totProgress);
                Draw.rect(unit.shadowRegion, x + -12 * e, y + -12 * e);
                Draw.color();
                Draw.alpha(1);
                Draw.z(Layer.groundUnit-1.1f);
                    for(var engine : unit.engines){
                        float rot = 0;

                        Tmp.v1.set(x, y).rotate(rot);
                        float ex = Tmp.v1.x, ey = Tmp.v1.y;
                        float rad = (engine.radius + Mathf.absin(Time.time, 2f, engine.radius / 4f)) * totProgress;
                        Draw.color(team.color);
                        Fill.circle(
                                engine.x + ex,
                                engine.y + ey,
                                rad * totProgress
                        );
                        Draw.z(Layer.groundUnit-1f);
                        Draw.color(Color.white);
                        Fill.circle(
                                engine.x + ex - Angles.trnsx(rot + engine.rotation, rad / 4f),
                                engine.y + ey - Angles.trnsy(rot + engine.rotation, rad / 4f),
                                rad / 2f * totProgress
                        );
                    }
            }
        }
        //This is horrific, god awful, inneficient
        @Override
        public Object config(){
            StringBuilder out = new StringBuilder();

            if(command != null){
                out.append("cmd=").append(command.name);
            }

            if(commandPos != null){
                if(out.length() > 0) out.append(",");
                out.append("pos=").append(commandPos.x).append(":").append(commandPos.y);
            }

            return out.length() == 0 ? null : out.toString();
        }
        public class UnitBuildConfig {
            public final UnitCommand command;
            public final Vec2 position;

            public UnitBuildConfig(UnitCommand command, Vec2 position){
                this.command = command;
                this.position = position == null ? null : new Vec2(position);
            }
        }

        @Override
        public void buildConfiguration(Table table){

            if(unit == null){
                deselect();
                return;
            }

            var group = new ButtonGroup<ImageButton>();
            group.setMinCheckCount(0);
            int i = 0, columns = 4;

            table.background(Styles.black6);

            var list = unit.commands;
            for(var item : list){
                ImageButton button = table.button(item.getIcon(), Styles.clearNoneTogglei, 40f, () -> {
                    configure(item);
                    deselect();
                }).tooltip(item.localized()).group(group).get();

                button.update(() -> button.setChecked(command == item || (command == null && unit.defaultCommand == item)));

                if(++i % columns == 0){
                    table.row();
                }
            }
        }

        @Override
        public void updateTile(){

            if(efficiency > 0 && !(this.team.data().countType(unit) >= Units.getCap(this.team))) {
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team) * efficiency;
                totProgress = progress/time;
            }
            if(progress >= time ){
                var b = unit.create(team);
                if(b.isCommandable()){
                    if(commandPos != null){
                        b.command().commandPosition(commandPos);
                    }
                    //this already checks if it is a valid command for the unit type
                    b.command().command(command == null && b.type.defaultCommand != null ? b.type.defaultCommand : command);
                }
                b.set(x + Mathf.range(0.001f), y + Mathf.range(0.001f));
                b.rotation = 90;
                Effect.shake(2f, 3f, this);
                Fx.producesmoke.at(this);
                b.add();
                kill();
            }
        }
        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }
        public boolean canSetCommand(){
            var output = unit;
            return output != null && output.commands.size > 1 && output.allowChangeCommands;
        }

        @Override
        public Cursor getCursor(){
            return canSetCommand() ? super.getCursor() : SystemCursor.arrow;
        }
        @Override
        public float totalProgress(){
            return totProgress;
        }
        @Override
        public boolean shouldShowConfigure(Player player){
            return canSetCommand();
        }
        @Override
        public byte version(){
            return 3;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);

            TypeIO.writeVecNullable(write, commandPos);
            TypeIO.writeCommand(write, command);
        }
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                progress = read.f();
            }

            if(revision >= 2){
              commandPos = TypeIO.readVecNullable(read);
            }

            if(revision >= 3){
                command = TypeIO.readCommand(read);
            }
        }

        /** Called when the block is destroyed. The tile is still intact at this stage. */
        @Override
        public void onDestroyed(){

            float explosiveness = block.baseExplosiveness;
            float flammability = 0f;
            float power = 0f;

            if(block.hasItems){
                for(Item item : content.items()){
                    int amount = Math.min(items.get(item), explosionItemCap());
                    flammability += item.flammability * amount;
                    power += item.charge * Mathf.pow(amount, 1.1f) * 150f;
                }
            }

            if(block.hasLiquids){
                flammability += liquids.sum((liquid, amount) -> liquid.flammability * amount / 2f);
            }

            if(block.consPower != null && block.consPower.buffered){
                power += this.power.status * block.consPower.capacity;
            }

            if(block.hasLiquids && state.rules.damageExplosions){

                liquids.each(this::splashLiquid);
            }

            //cap explosiveness so fluid tanks/vaults don't instakill units
            Damage.dynamicExplosion(x, y, flammability, explosiveness, power, tilesize * block.size / 2f, state.rules.damageExplosions, block.destroyEffect, block.baseShake);

            if(block.createRubble && !floor().solid && !floor().isLiquid){
                Effect.rubble(x, y, block.size);
            }
        }

    }

}
