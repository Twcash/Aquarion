package aquarion.world.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawRadialEngine extends DrawBlock {
    public int pistonCount = 8;           // number of pistons
    public float radius = 15f;       // crank arm length
    public float rodLength = 40f;         // connecting rod length
    public float rodThickness = 3f;
    public float speed = 0.05f;
    public TextureRegion masterCrank;
    public TextureRegion crankRegion;
    public TextureRegion rodRegion;
    public TextureRegion pistonRegion;
    @Override
    public void load(Block block){
        crankRegion = Core.atlas.find(block.name + "-crank");
        masterCrank = Core.atlas.find(block.name + "-crank1");
        rodRegion = Core.atlas.find(block.name + "-rod");
        pistonRegion = Core.atlas.find(block.name + "-piston");
    }
    @Override
    public void draw(Building build) {
        float cx = build.x, cy = build.y;
        float time =build.totalProgress() * speed;


        float crankX = cx + Mathf.cos(time) * radius;
        float crankY = cy + Mathf.sin(time) * radius;

        for (int i = 0; i < pistonCount; i++) {
            float baseAngle = (Mathf.PI2 / pistonCount) * i;

            float ax = Mathf.cos(baseAngle), ay = Mathf.sin(baseAngle);

            float displacement = Mathf.cos(time - baseAngle) * radius;

            float px = cx + ax * (rodLength + displacement);
            float py = cy + ay * (rodLength + displacement);

            Lines.stroke(rodThickness);
            Lines.line(rodRegion, px, py, crankX, crankY, false);

            Draw.rect(pistonRegion, px, py, baseAngle * Mathf.radDeg);
        }

        // draw crankshaft
        Draw.rect(crankRegion, cx, cy, 0);
        Draw.rect(masterCrank, crankX, crankY, build.totalProgress());
    }
}
