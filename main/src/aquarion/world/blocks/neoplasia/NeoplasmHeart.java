package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Env;
import static mindustry.content.Blocks.air;
import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.NeoplasiaBuild;

/** A real team core with neoplasia mass growth and heart connectivity. */
public class NeoplasmHeart extends CoreBlock {
    public float techStep = 100f, heartCost = 150f, dissolveRate = 0.005f;
    public int techMax = 5;
    public float maxAmount = 1000f, startMass = 0.001f, selfGrowRate = 0.04f, baseSize = 14f;
    public Color colFrom = Color.valueOf("701e1e"), colTo = Color.valueOf("cf5a3b");

    public NeoplasmHeart(String name){
        super(name);
        update = true;
        solid = true;
        destructible = true;
        rebuildable = false;
        requiresCoreZone = false;
        placeableLiquid = true;
        floating = true;
        envEnabled = Env.any;
        envDisabled = Env.none;
        itemCapacity = 1000;
    }

    @Override public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return tile != null;
    }

    public class NeoplasmHeartBuild extends CoreBuild {
        public boolean isPrime, dissolving;
        public float amount;
        float spawnTime, liquidRequestTimer;
        private boolean destroyedNormally;
        final Seq<NeoplasiaBuild> adjacent = new Seq<>();

        Seq<NeoplasiaBuild> neighbors(){
            adjacent.clear();
            if(tile != null) for(var offset : Geometry.d8){
                Tile other = Vars.world.tile(tile.x + offset.x, tile.y + offset.y);
                if(other != null && other.build instanceof NeoplasiaBuild tissue && tissue.team == team && !tissue.dead()) adjacent.addUnique(tissue);
            }
            return adjacent;
        }

        @Override public void created(){
            super.created();
            amount = startMass;
            health = amount;
            maxHealth = Math.max(amount, maxAmount);
            Vars.state.teams.registerCore(this);
            NeoplasiaGraph.addHeart(this);
        }

        @Override public void onRemoved(){
            NeoplasiaGraph.removeHeart(this);
            adjacent.clear();
            super.onRemoved();
        }

        @Override public void onDestroyed(){
            // Overwriting/removing a heart is not combat destruction or a core capture.
            if(dissolving || tile == null || tile.build != this) return;
            destroyedNormally = true;
            super.onDestroyed();
        }

        @Override public void afterDestroyed(){
            if(destroyedNormally) super.afterDestroyed();
        }

        @Override public void changeTeam(Team next){
            if(next == team) return;
            super.changeTeam(next);
            NeoplasiaGraph.addHeart(this);
        }

        @Override public float handleDamage(float damage){
            float actual = super.handleDamage(damage);
            amount = Math.max(0f, amount - actual);
            if(actual > 0f) NeoplasmTentaclePit.alertDamage(this);
            NeoplasiaDamage.bleed(this, actual);
            return actual;
        }

        @Override public void updateTile(){
            super.updateTile();
            if(tile == null || tile.build != this) return;
            spawnTime = Math.min(90f, spawnTime + delta());
            if(amount <= 0f || dissolving && amount <= 1f){ kill(); return; }
            if(dissolving) amount -= amount * dissolveRate * delta();
            else{
                amount = Math.max(amount, Math.min(maxAmount, amount + selfGrowRate * delta()));
                tryLiquidSlurper();
            }
            health = amount;
            maxHealth = Math.max(amount, maxAmount);
        }

        void tryLiquidSlurper(){
            NeoplasmLiquidSlurper type = GenericNeoplasiaBlock.liquidSlurperBlock;
            if(type == null) return;
            liquidRequestTimer += delta();
            if(liquidRequestTimer < 120f || amount < type.cost + Math.max(1f, maxAmount * 0.05f)) return;
            liquidRequestTimer = 0f;
            if(!type.hasLiquid(tile)) return;
            for(var offset : Geometry.d8){
                Tile other = Vars.world.tile(tile.x + offset.x, tile.y + offset.y);
                if(other != null && other.block() == type && other.team() == team) return;
            }
            for(var offset : Geometry.d8){
                Tile site = Vars.world.tile(tile.x + offset.x, tile.y + offset.y);
                if(site == null || site.block() != air || site.floor().isLiquid || !site.floor().placeableOn || !type.hasLiquid(site)) continue;
                site.setBlock(type, team);
                if(site.build instanceof NeoplasiaBuild gland){ gland.amount = type.cost; amount -= type.cost; }
                return;
            }
        }

        final Cons<Vec2> wiggle = vec -> vec.add(Mathf.sin(vec.y * 3 + Time.time, 25f, 0.4f), Mathf.cos(vec.x * 3 + Time.time, 31f, 0.44f));

        @Override public void draw(){
            float previousZ = Draw.z();
            float scale = Interp.smooth.apply(Mathf.clamp(spawnTime / 90f));
            Draw.color(colFrom, colTo, Mathf.clamp(amount / maxAmount));
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, baseSize * 0.5f * scale);
            Draw.z(Renderer.Layer.neoplasiaBase);
            Fill.circle(x, y, baseSize * 0.5f * scale);
            Draw.color();
            Draw.z(Renderer.Layer.blockOver + 2f);
            Draw.rectv(region, x, y, region.width * region.scl() * scale, region.height * region.scl() * scale, 0f, wiggle);
            Draw.reset();
            Draw.z(previousZ);
        }

        @Override public byte version(){ return 32; }
        @Override public void write(Writes write){
            super.write(write);
            write.f(amount);
            write.f(spawnTime);
            write.bool(isPrime);
            write.bool(dissolving);
        }
        @Override public void read(Reads read, byte revision){
            // Old hearts had the generic mass prefix and no core command position.
            super.read(read, (byte)(revision >= 32 ? 1 : 0));
            amount = read.f();
            spawnTime = read.f();
            if(revision < 32){
                read.f();
                read.f();
                if(revision >= GenericNeoplasiaBlock.transportSaveRevision) read.f();
            }
            isPrime = read.bool();
            if(revision >= 32) read.bool();
            dissolving = false; // Connected hearts are no longer retired when their colonies merge.
            health = amount;
            maxHealth = Math.max(amount, maxAmount);
        }
        public int techLevel(){ return Math.min(techMax, 1 + (int)(amount / techStep)); }
    }
}
