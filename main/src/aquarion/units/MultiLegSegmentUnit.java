package aquarion.units;

import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.entities.Leg;
import mindustry.gen.Legsc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.MultiPacker;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class MultiLegSegmentUnit extends UnitType {
    private static final Vec2 legOffset = new Vec2();
    public int legSegments = 5;
    public float segmentLength = 15f;
    transient float baseRotation;

    private TextureRegion[] segmentSprites;
    private TextureRegion[] segmentSpritesOutline;

    public MultiLegSegmentUnit(String name) {
        super(name);
    }
    @Override
    public void load() {
        super.load();

        segmentSprites = new TextureRegion[legSegments];
        segmentSpritesOutline = new TextureRegion[legSegments];

        for (int i = 0; i < legSegments; i++) {
            String segmentName = String.format("%s-leg-%d", this.name, i + 1);
            String outlineName = segmentName + "-outline";

            segmentSprites[i] = Core.atlas.find(segmentName);
            segmentSpritesOutline[i] = Core.atlas.find(outlineName);

            if (!segmentSprites[i].found()) {
                throw new IllegalArgumentException("dumbass (no leg segment texture found): " + segmentName);
            }
            if (!segmentSpritesOutline[i].found()) {
                throw new IllegalArgumentException("Oi what the fuck are you on yeh fucker? (missing segment outline): " + outlineName);
            }
        }
    }


//    @Override
//    public void createIcons(MultiPacker packer) {
//        super.createIcons(packer);
//
//        for (int i = 0; i < legSegments; i++) {
//            String segmentName = name + "-leg-" + (i + 1);
//            if (!Core.atlas.has(segmentName + "-outline")) {
//                var region = Core.atlas.find(segmentName);
//
//                // sanity check
//                if (region == Core.atlas.find("error") || region.width <= 0 || region.height <= 0) {
//                    Log.err("Missing or invalid leg segment sprite for unit @: @", name, segmentName);
//                    continue;
//                }
//
//                PixmapRegion base = Core.atlas.getPixmap(region);
//                if (base == null) {
//                    Log.err("Null pixmap for region @ in unit @", segmentName, name);
//                    continue;
//                }
//
//                Pixmap outlined = Pixmaps.outline(base, outlineColor, outlineRadius);
//                Drawf.checkBleed(outlined);
//                packer.add(MultiPacker.PageType.main, segmentName + "-outline", outlined);
//                outlined.dispose();
//            }
//        }
//        for (Weapon weapon : weapons) {
//            if (!weapon.name.isEmpty() && (minfo.mod == null || weapon.name.startsWith(minfo.mod.name))) {
//                makeOutline(packer, weapon.region, weapon.name + "-outline", outlineColor, outlineRadius);
//            }
//        }
//    }
    @Override
    public <T extends Unit & Legsc> void drawLegs(T unit) {
        applyColor(unit);

        Leg[] legs = unit.legs();
        baseRotation = lockLegBase ? unit.rotation : unit.baseRotation();

        for (int i = 0; i < legs.length; i++) {
            Leg leg = legs[i];
            Vec2 basePosition = unit.legOffset(legOffset, i).add(unit.x, unit.y);
            Vec2 targetPosition = leg.base;
            boolean flip = i >= legs.length / 2f;
            int flips = Mathf.sign(flip);
            Segment[] segments = generateSegments(basePosition, targetPosition);

            for (int j = 0; j < segments.length - 1; j++) {
                int f = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
                Segment segmentA = segments[j];
                Segment segmentB = segments[j + 1];

                TextureRegion segmentSprite = segmentSprites[j % legSegments];
                TextureRegion segmentOutline = segmentSpritesOutline[j % legSegments];

                float angle = segmentA.position.angleTo(segmentB.position);

                Vec2 midPosition = Tmp.v1.set(segmentA.position).add(segmentB.position).scl(0.5f);

                float segmentDistance = segmentA.position.dst(segmentB.position);
                float lineStroke = Math.min(segmentDistance / segmentLength, 1f); // Adjust stroke based on distance
                if (segmentOutline.found()) {
                    Draw.rect(segmentOutline, midPosition.x, midPosition.y, angle);
                }
                Lines.stroke(lineStroke);
                Lines.line(segmentSprite, segmentA.position.x, segmentA.position.y, segmentB.position.x, segmentB.position.y, false);
                if (jointRegion.found()) {
                    Draw.rect(jointRegion, segmentA.position.x, segmentA.position.y);
                }
            }
            if (footRegion.found()) {
                Segment lastSegment = segments[segments.length - 1];
                Draw.rect(footRegion, lastSegment.position.x, lastSegment.position.y, basePosition.angleTo(targetPosition));
            }
        }

        Draw.reset();
    }
    private Segment[] generateSegments(Vec2 base, Vec2 target) {
        Segment[] segments = new Segment[legSegments + 1];
        float totalDistance = base.dst(target);
        float dynamicSegmentLength = Math.min(segmentLength, totalDistance / legSegments);

        for (int i = 0; i <= legSegments; i++) {
            segments[i] = new Segment(dynamicSegmentLength);
        }

        segments[legSegments].position.set(target);

        for (int iteration = 0; iteration < 5; iteration++) {
            segments[0].position.set(base);
            for (int i = 1; i <= legSegments; i++) {
                Vec2 dir = Tmp.v1.set(segments[i].position).sub(segments[i - 1].position).setLength(dynamicSegmentLength);
                segments[i].position.set(segments[i - 1].position).add(dir);
            }

            segments[legSegments].position.set(target);
            for (int i = legSegments - 1; i >= 0; i--) {
                Vec2 dir = Tmp.v1.set(segments[i].position).sub(segments[i + 1].position).setLength(dynamicSegmentLength);
                segments[i].position.set(segments[i + 1].position).add(dir);
            }
        }

        return segments;
    }

    public static class Segment {
        public float length;
        public Vec2 position;

        public Segment(float length) {
            this.length = length;
            this.position = new Vec2();
        }
    }

    public void getRegionsToOutline(Seq<TextureRegion> out) {
        for (Weapon weapon : weapons) {
            for (var part : weapon.parts) {
                part.getOutlines(out);
            }
        }
        for (var part : parts) {
            part.getOutlines(out);
        }
    }

}
