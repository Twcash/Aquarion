package aquarion.world.entities.bullet;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class NeoplasmGlobBulletType extends BasicBulletType {
    public GenericNeoplasiaBlock neoplasiaBlock;
    public float blobAmount = 100f;

    public NeoplasmGlobBulletType(float speed, float damage) {
        super(speed, damage);
        collides = false;
        hittable = false;
        keepVelocity = false;
        lifetime = 90f;
        drag = 0.03f;
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);
        placeOrFeed(b);
    }

    protected void placeOrFeed(Bullet b) {
        if (neoplasiaBlock == null) return;
        var tile = Vars.world.tileWorld(b.x, b.y);
        if (tile == null || tile.solid() || tile.floor().isDeep()) return;
        if (tile.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild nb && nb.team == b.team) {
            nb.amount = Math.min(nb.block().maxAmount, nb.amount + blobAmount);
            return;
        }
        if (tile.block() != Blocks.air) return;
        tile.setBlock(neoplasiaBlock, b.team);
        if (tile.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild nb) {
            nb.amount = blobAmount + Mathf.random(blobAmount * 0.2f);
        }
    }
}
