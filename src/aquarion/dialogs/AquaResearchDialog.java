package aquarion.dialogs;

import aquarion.ModEventHandler;
import aquarion.planets.AquaPlanets;
import aquarion.ui.AquaStyles;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.actions.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.TextButton.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.game.Objectives.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.layout.TreeLayout.*;

import static mindustry.Vars.*;
import static mindustry.gen.Tex.*;

public class AquaResearchDialog extends BaseDialog {
    public boolean showing = false;
    public static boolean debugShowRequirements = false;

    public final float nodeSize = Scl.scl(60f);
    public ObjectSet<TechTreeNode> nodes = new ObjectSet<>();
    public TechTreeNode root = new TechTreeNode(TechTree.roots.first(), null);
    public TechNode lastNode = root.node;
    public Rect bounds = new Rect();
    public ItemsDisplay itemDisplay;
    public View view;

    public ItemSeq items;

    private boolean showTechSelect;
    private boolean needsRebuild;

    public AquaResearchDialog() {
        super("");
        Vars.ui.research.fill(t -> t.update(() -> {
            if (Vars.ui.research.root.node == AquaPlanets.tantros2.techTree && Vars.ui.research.root.node == AquaPlanets.fakeSerpulo.techTree) {
                Vars.ui.research.hide(Actions.fadeOut(0f));
                ModEventHandler.techDialog.show();
            }
        }));
        Events.on(ResetEvent.class, e -> {
            hide();
        });

        Events.on(UnlockEvent.class, e -> {
            if (net.client() && !needsRebuild) {
                needsRebuild = true;
                Core.app.post(() -> {
                    needsRebuild = false;

                    checkNodes(root);
                    view.hoverNode = null;
                    treeLayout();
                    view.rebuild();
                    Core.scene.act();
                });
            }
        });

        titleTable.remove();
        titleTable.clear();
        titleTable.top();
        titleTable.button(b -> {
            //TODO custom icon here.
            b.imageDraw(() -> root.node.icon()).padRight(8).size(iconMed);
            b.add().growX();
            b.label(() -> root.node.localizedName()).color(Pal.heal);
            b.add().growX();
            b.add().size(iconMed);
        }, () -> {
            new BaseDialog("@techtree.select") {{
                cont.pane(t -> {
                    t.table(Tex.button, in -> {
                        in.defaults().width(300f).height(60f);
                        for (TechNode node : TechTree.roots) {
                            if (node.requiresUnlock && !node.content.unlockedHost() && node != getPrefRoot()) continue;

                            //TODO toggle
                            in.button(node.localizedName(), node.icon(), Styles.flatTogglet, iconMed, () -> {
                                if (node == lastNode) {
                                    return;
                                }

                                rebuildTree(node);
                                hide();
                            }).marginLeft(12f).checked(node == lastNode).row();
                        }
                    });
                });

                addCloseButton();
            }}.show();
        }).visible(() -> showTechSelect = TechTree.roots.count(node -> !(node.requiresUnlock && !node.content.unlockedHost())) > 1).minWidth(300f);

        margin(0f).marginBottom(8);
        cont.stack(titleTable, view = new View(), itemDisplay = new ItemsDisplay()).grow();
        itemDisplay.visible(() -> !net.client());

        titleTable.toFront();

        shouldPause = true;

        onResize(this::checkMargin);

        shown(() -> {
            checkMargin();
            Core.app.post(this::checkMargin);

            Planet currPlanet = ui.planet.isShown() ?
                    ui.planet.state.planet :
                    state.isCampaign() ? state.rules.sector.planet : null;

            if (currPlanet != null && currPlanet.techTree != null) {
                switchTree(currPlanet.techTree);
            }
            rebuildItems();

            checkNodes(root);
            treeLayout();

            view.hoverNode = null;
            view.infoTable.remove();
            view.infoTable.clear();
        });


        addCloseButton();


        buttons.button("@database", Icon.book, () -> {
            hide();
            ui.database.show();
        }).size(210f, 64f).name("database");

        //scaling/drag input
        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                view.setScale(Mathf.clamp(view.scaleX - amountY / 10f * view.scaleX, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                view.requestScroll();
                return super.mouseMoved(event, x, y);
            }
        });

        touchable = Touchable.enabled;

        addCaptureListener(new ElementGestureListener() {
            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                if (view.lastZoom < 0) {
                    view.lastZoom = view.scaleX;
                }

                view.setScale(Mathf.clamp(distance / initialDistance * view.lastZoom, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                view.lastZoom = view.scaleX;
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                view.panX += deltaX / view.scaleX;
                view.panY += deltaY / view.scaleY;
                view.moved = true;
                view.clamp();
            }
        });
    }

    void checkMargin() {
        if (Core.graphics.isPortrait() && showTechSelect) {
            itemDisplay.marginTop(60f);
        } else {
            itemDisplay.marginTop(0f);
        }
        itemDisplay.invalidate();
        itemDisplay.layout();
    }

    public void rebuildItems() {
        items = new ItemSeq() {
            // Store sector item amounts for modifications
            ObjectMap<Sector, ItemSeq> cache = new ObjectMap<>();

            {
                //First get all the planets with the same techtree/root node
                Seq<Planet> rootPlanets = content.planets().select(p -> p.techTree == lastNode);

                // Fallback if none found
                if (rootPlanets.isEmpty()) {
                    rootPlanets = Seq.with(Planets.serpulo);
                }

                // Add global counts from each planet's sectors
                for (Planet planet : rootPlanets) {
                    for (Sector sector : planet.sectors) {
                        if (sector.hasBase()) {
                            ItemSeq cached = sector.items();
                            cache.put(sector, cached);
                            cached.each((item, amount) -> {
                                values[item.id] += Math.max(amount, 0);
                                total += Math.max(amount, 0);
                            });
                        }
                    }
                }
            }

            @Override
            public void add(Item item, int amount) {
                if (amount < 0) {
                    amount = -amount;
                    double percentage = (double) amount / get(item);
                    int[] counter = {amount};

                    cache.each((sector, seq) -> {
                        if (counter[0] == 0) return;

                        int toRemove = Math.min((int) Math.ceil(percentage * seq.get(item)), counter[0]);

                        sector.removeItem(item, toRemove);
                        seq.remove(item, toRemove);

                        counter[0] -= toRemove;
                    });

                    amount = -amount;
                }

                super.add(item, amount);
            }
        };

        itemDisplay.rebuild(items);
    }

    public @Nullable TechNode getPrefRoot() {
        Planet currPlanet = ui.planet.isShown() ?
                ui.planet.state.planet :
                state.isCampaign() ? state.rules.sector.planet : null;
        return currPlanet == null ? null : currPlanet.techTree;
    }

    public void switchTree(TechNode node) {
        if (lastNode == node || node == null) return;
        nodes.clear();
        root = new TechTreeNode(node, null);
        lastNode = node;
        view.rebuildAll();

        rebuildItems();
    }

    public void rebuildTree(TechNode node) {
        switchTree(node);
        view.panX = 0f;
        view.panY = -200f;
        view.setScale(1f);

        view.hoverNode = null;
        view.infoTable.remove();
        view.infoTable.clear();

        checkNodes(root);
        treeLayout();
    }

    void assignDepths(TechTreeNode node, int depth, ObjectMap<Integer, Seq<TechTreeNode>> map) {
        map.get(depth, Seq::new).add(node);
        for (TechTreeNode child : node.children) {
            assignDepths(child, depth + 1, map);
        }
    }
    int getSubtreeSize(TechTreeNode node){
        if(node.children.length == 0) return 1;

        int sum = 0;
        for(TechTreeNode child : node.children){
            sum += getSubtreeSize(child);
        }
        return sum;
    }
    void collectDescendants(TechTreeNode node, Seq<TechTreeNode> out){
        for(TechTreeNode child : node.children){
            out.add(child);
            collectDescendants(child, out);
        }
    }
    void layoutRadialSmart(TechTreeNode node, float startAngle, float endAngle, int depth, float spacing){
        float radius = spacing * depth;
        if(node.children.length == 0) return;

        // First, calculate total weight
        int totalWeight = 0;
        int[] weights = new int[node.children.length];
        for(int i = 0; i < node.children.length; i++){
            weights[i] = getSubtreeSize(node.children[i]);
            totalWeight += weights[i];
        }

        float angleCursor = startAngle;

        for(int i = 0; i < node.children.length; i++){
            TechTreeNode child = node.children[i];
            float weightRatio = (float)weights[i] / totalWeight;

            float angleWidth = (endAngle - startAngle) * weightRatio;
            float angle = angleCursor + angleWidth / 2f;
            float rad = angle * Mathf.degreesToRadians;

            child.x = Mathf.cos(rad) * radius;
            child.y = Mathf.sin(rad) * radius;

            layoutRadialSmart(child, angleCursor, angleCursor + angleWidth, depth + 1, spacing);

            angleCursor += angleWidth;
        }
    }

    int getMaxDepth(TechTreeNode node, int depth){
        int max = depth;
        for(TechTreeNode child : node.children){
            max = Math.max(max, getMaxDepth(child, depth + 1));
        }
        return max;
    }
    void treeLayout(){
        float ringSpacing = 500f;     // Spacing between rings

        // Center root
        root.x = 0f;
        root.y = 0f;

        // Big brain moment here folks
        layoutRadialSmart(root, 0f, 360f, 1, ringSpacing);

        // Recalculate bounds
        float minx = 0f, miny = 0f, maxx = 0f, maxy = 0f;
        for(TechTreeNode n : nodes){ //SUCKS TODO REMAKE
            if(!n.visible) continue;
            minx = Math.min(n.x - n.width / 2f, minx);
            maxx = Math.max(n.x + n.width / 2f, maxx);
            miny = Math.min(n.y - n.height / 2f, miny);
            maxy = Math.max(n.y + n.height / 2f, maxy);
        }
        bounds = new Rect(minx, miny, maxx - minx, maxy - miny);
    }

    void shift(LayoutNode[] children, float amount) {
        for (LayoutNode node : children) {
            node.y += amount;
            if (node.children != null && node.children.length > 0) shift(node.children, amount);
        }
    }

    void copyInfo(LayoutNode node) {
        node.node.x = node.x;
        node.node.y = node.y;
        if (node.children != null) {
            for (LayoutNode child : node.children) {
                copyInfo(child);
            }
        }
    }

    void checkNodes(TechTreeNode node) {
        boolean locked = locked(node.node);
        if (!locked && (node.parent == null || node.parent.visible)) node.visible = true;
        node.selectable = selectable(node.node);
        for (TechTreeNode l : node.children) {
            l.visible = !locked && l.parent.visible;
            checkNodes(l);
        }

        itemDisplay.rebuild(items);
    }

    boolean selectable(TechNode node) {
        //there's a desync here as far as sectors go, since the client doesn't know about that, but I'm not too concerned
        return node.content.unlockedHost() || !node.objectives.contains(i -> !i.complete());
    }

    boolean locked(TechNode node) {
        return !node.content.unlockedHost();
    }

    class LayoutNode extends TreeNode<LayoutNode> {
        final TechTreeNode node;

        LayoutNode(TechTreeNode node, LayoutNode parent) {
            this.node = node;
            this.parent = parent;
            this.width = this.height = nodeSize;
            if (node.children != null) {
                children = Seq.with(node.children).map(t -> new LayoutNode(t, this)).toArray(LayoutNode.class);
            }
        }
    }

    public class TechTreeNode extends TreeNode<TechTreeNode> {
        public final TechNode node;
        public boolean visible = true, selectable = true;

        public TechTreeNode(TechNode node, TechTreeNode parent) {
            this.node = node;
            this.parent = parent;
            this.width = this.height = nodeSize;
            nodes.add(this);
            children = new TechTreeNode[node.children.size];
            for (int i = 0; i < children.length; i++) {
                children[i] = new TechTreeNode(node.children.get(i), this);
            }
        }
    }


    public class View extends Group {
        public float panX = 0, panY = -200, lastZoom = -1;
        public boolean moved = false;
        public ImageButton hoverNode;
        public Table infoTable = new Table();

        {
            rebuildAll();
        }

        public void rebuildAll() {
            clear();
            hoverNode = null;
            infoTable.clear();
            infoTable.touchable = Touchable.enabled;

            for (TechTreeNode node : nodes) {
                ImageButton button = new ImageButton(node.node.content.uiIcon, AquaStyles.technodeFull);
                button.resizeImage(32f);
                button.getImage().setScaling(Scaling.fit);
                button.visible(() -> node.visible);
                if (!net.client()) {
                    button.clicked(() -> {
                        if (moved) return;

                        if (mobile) {
                            hoverNode = button;
                            rebuild();
                            float right = infoTable.getRight();
                            if (right > Core.graphics.getWidth()) {
                                float moveBy = right - Core.graphics.getWidth();
                                addAction(new RelativeTemporalAction() {
                                    {
                                        setDuration(0.1f);
                                        setInterpolation(Interp.fade);
                                    }

                                    @Override
                                    protected void updateRelative(float percentDelta) {
                                        panX -= moveBy * percentDelta;
                                    }
                                });
                            }
                        } else if (canSpend(node.node) && locked(node.node)) {
                            spend(node.node);
                        }
                    });
                }

                button.hovered(() -> {
                    if (!mobile && hoverNode != button && node.visible) {
                        hoverNode = button;
                        rebuild();
                    }
                });
                button.exited(() -> {
                    if (!mobile && hoverNode == button && !infoTable.hasMouse() && !hoverNode.hasMouse()) {
                        hoverNode = null;
                        rebuild();
                    }
                });
                button.touchable(() -> !node.visible ? Touchable.disabled : Touchable.enabled);
                button.userObject = node.node;
                button.setSize(nodeSize);
                button.update(() -> {
                    button.setDisabled(net.client() && !mobile);
                    float offset = (Core.graphics.getHeight() % 2) / 2f;
                    button.setPosition(node.x + panX + width / 2f, node.y + panY + height / 2f + offset, Align.center);
                    button.getStyle().up = !locked(node.node) ? Tex.buttonOver : !selectable(node.node) || (!canSpend(node.node) && !net.client()) ? Tex.buttonRed : Tex.button;

                    ((TextureRegionDrawable) button.getStyle().imageUp).setRegion(node.selectable ? node.node.content.uiIcon : Icon.lock.getRegion());
                    button.getImage().setColor(!locked(node.node) ? Color.white : node.selectable ? Color.gray : Pal.gray);
                    button.getImage().layout();
                });
                addChild(button);
            }

            if (mobile) {
                tapped(() -> {
                    Element e = Core.scene.getHoverElement();
                    if (e == this) {
                        hoverNode = null;
                        rebuild();
                    }
                });
            }

            setOrigin(Align.center);
            setTransform(true);
            released(() -> moved = false);
        }

        void clamp() {
            float pad = nodeSize;

            float ox = width / 2f, oy = height / 2f;
            float rx = bounds.x + panX + ox, ry = panY + oy + bounds.y;
            float rw = bounds.width, rh = bounds.height;
            rx = Mathf.clamp(rx, -rw + pad, Core.graphics.getWidth() - pad);
            ry = Mathf.clamp(ry, -rh + pad, Core.graphics.getHeight() - pad);
            panX = rx - bounds.x - ox;
            panY = ry - bounds.y - oy;
        }

        boolean canSpend(TechNode node) {
            if (!selectable(node) || net.client()) return false;

            if (node.requirements.length == 0) return true;

            //can spend when there's at least 1 item that can be spent (non complete)
            for (int i = 0; i < node.requirements.length; i++) {
                if (node.finishedRequirements[i].amount < node.requirements[i].amount && items.has(node.requirements[i].item)) {
                    return true;
                }
            }

            //can always spend when locked
            return node.content.locked();
        }

        void spend(TechNode node) {
            if (net.client()) return;

            boolean complete = true;

            boolean[] shine = new boolean[node.requirements.length];
            boolean[] usedShine = new boolean[content.items().size];

            for (int i = 0; i < node.requirements.length; i++) {
                ItemStack req = node.requirements[i];
                ItemStack completed = node.finishedRequirements[i];

                //amount actually taken from inventory
                int used = Math.max(Math.min(req.amount - completed.amount, items.get(req.item)), 0);
                items.remove(req.item, used);
                completed.amount += used;

                if (used > 0) {
                    shine[i] = true;
                    usedShine[req.item.id] = true;
                }

                //disable completion if the completed amount has not reached requirements
                if (completed.amount < req.amount) {
                    complete = false;
                }
            }

            if (complete) {
                unlock(node);
            }

            node.save();

            //??????
            Core.scene.act();
            rebuild(shine);
            itemDisplay.rebuild(items, usedShine);
            checkMargin();
        }

        void unlock(TechNode node) {
            node.content.unlock();

            //unlock parent nodes in multiplayer.
            TechNode parent = node.parent;
            while (parent != null) {
                parent.content.unlock();
                parent = parent.parent;
            }

            checkNodes(root);
            hoverNode = null;
            treeLayout();
            rebuild();
            Core.scene.act();
            Sounds.unlock.play();
            Events.fire(new ResearchEvent(node.content));
        }

        void rebuild() {
            rebuild(null);
        }

        //pass an array of stack indexes that should shine here
        void rebuild(@Nullable boolean[] shine) {
            ImageButton button = hoverNode;

            infoTable.remove();
            infoTable.clear();
            infoTable.update(null);
            infoTable.touchable = Touchable.enabled;

            if (button == null) return;

            TechNode node = (TechNode) button.userObject;

            infoTable.addListener(new InputListener() {
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Element fromActor) {
                    Element e = Core.scene.hit(Core.input.mouseX(pointer == -1 ? 0 : pointer), Core.input.mouseY(pointer == -1 ? 0 : pointer), true);

                    if (hoverNode == button && !(e != null && (e == infoTable || e.isDescendantOf(infoTable) || e == hoverNode || e.isDescendantOf(hoverNode))) && (Core.app.isDesktop() || pointer == 0)) {
                        hoverNode = null;
                        rebuild();
                    }
                }
            });

            infoTable.update(() -> infoTable.setPosition(button.x + button.getWidth(), button.y + button.getHeight(), Align.topLeft));

            infoTable.left();
            infoTable.background(Tex.button).margin(8f);

            boolean selectable = selectable(node);

            infoTable.table(b -> {
                b.margin(0).left().defaults().left();

                if (selectable) {
                    b.button(Icon.info, Styles.flati, () -> ui.content.show(node.content)).growY().width(50f);
                }
                b.add().grow();
                b.table(desc -> {
                    desc.left().defaults().left();
                    desc.add(selectable ? node.content.localizedName : "[accent]???");
                    desc.row();
                    if (locked(node) || (debugShowRequirements && !net.client())) {

                        if (net.client()) {
                            desc.add("@locked").color(Pal.remove);
                        } else {
                            desc.table(t -> {
                                t.left();
                                if (selectable) {

                                    //check if there is any progress, add research progress text
                                    if (Structs.contains(node.finishedRequirements, s -> s.amount > 0)) {
                                        float sum = 0f, used = 0f;
                                        boolean shiny = false;

                                        for (int i = 0; i < node.requirements.length; i++) {
                                            sum += node.requirements[i].item.cost * node.requirements[i].amount;
                                            used += node.finishedRequirements[i].item.cost * node.finishedRequirements[i].amount;
                                            if (shine != null) shiny |= shine[i];
                                        }

                                        Label label = t.add(Core.bundle.format("research.progress", Math.min((int) (used / sum * 100), 99))).left().get();

                                        if (shiny) {
                                            label.setColor(Pal.accentBack);
                                            label.actions(Actions.color(Pal.accent, 0.75f, Interp.fade));
                                        } else {
                                            label.setColor(Pal.darkerGray);
                                        }

                                        t.row();
                                    }

                                    for (int i = 0; i < node.requirements.length; i++) {
                                        ItemStack req = node.requirements[i];
                                        ItemStack completed = node.finishedRequirements[i];

                                        //skip finished stacks
                                        if (req.amount <= completed.amount && !debugShowRequirements) continue;
                                        boolean shiny = shine != null && shine[i];

                                        t.table(list -> {
                                            int reqAmount = debugShowRequirements ? req.amount : req.amount - completed.amount;

                                            list.left();
                                            list.image(req.item.uiIcon).size(8 * 3).padRight(3);
                                            list.add(req.item.localizedName).color(Color.lightGray);
                                            Label label = list.label(() -> " " +
                                                    UI.formatAmount(Math.min(items.get(req.item), reqAmount)) + " / "
                                                    + UI.formatAmount(reqAmount)).get();

                                            Color targetColor = items.has(req.item) ? Pal.lightishGray : Pal.darkerGray;

                                            if (shiny) {
                                                label.setColor(Color.white);
                                                label.actions(Actions.color(targetColor, 0.75f, Interp.fade));
                                            } else {
                                                label.setColor(targetColor);
                                            }

                                        }).fillX().left();
                                        t.row();
                                    }
                                } else if (node.objectives.size > 0) {
                                    t.table(r -> {
                                        r.add("@complete").colspan(2).left();
                                        r.row();
                                        for (Objective o : node.objectives) {
                                            if (o.complete()) continue;

                                            r.add("> " + o.display()).color(Color.lightGray).left();
                                            r.image(o.complete() ? Icon.ok : Icon.cancel, o.complete() ? Color.lightGray : Color.darkGray).padLeft(3);
                                            r.row();
                                        }
                                    });
                                    t.row();
                                }
                            });
                        }
                    } else {
                        desc.add("@completed");
                    }
                }).pad(9);

                if (mobile && locked(node) && !net.client()) {
                    b.row();
                    b.button("@research", Icon.ok, new TextButtonStyle() {{
                        disabled = Tex.button;
                        font = Fonts.def;
                        fontColor = Color.white;
                        disabledFontColor = Color.gray;
                        up = buttonOver;
                        over = buttonDown;
                    }}, () -> spend(node)).disabled(i -> !canSpend(node)).growX().height(44f).colspan(3);
                }
            });

            infoTable.row();
            if (node.content.description != null && node.content.inlineDescription && selectable) {
                infoTable.table(t -> t.margin(3f).left().labelWrap(node.content.displayDescription()).color(Color.lightGray).growX()).fillX();
            }

            addChild(infoTable);

            checkMargin();
            Core.app.post(() -> checkMargin());

            infoTable.pack();
            infoTable.act(Core.graphics.getDeltaTime());
        }

        @Override
        public void drawChildren() {
            clamp();
            Draw.sort(true);
            float offsetX = panX + width / 2f, offsetY = panY + height / 2f;
            int maxDepth = getMaxDepth(root, 0);
            float spacing = 500; // same as layout spacing

            Draw.z(0f);


            for(int i = 1; i <= maxDepth; i++){
                float radius = spacing * i;
                float cx = panX + width / 2f;
                float cy = panY + height / 2f;

                Draw.color(Pal.darkerGray);
                Lines.stroke(12f);
                Lines.circle(cx, cy, radius);

                // Dashed echo rings — apply parallax scaling
                for(int e = 1; e <= 3; e++){
                    float parallax = 1f / (e + 1f);
                    float px = panX * parallax + width / 2f;
                    float py = panY * parallax + height / 2f;

                    Draw.color(Pal.darkestGray.a(1f / (e * 1.5f)));
                    Lines.stroke(10f / e);

                    Lines.dashCircle(px, py, radius);
                }
            }
            Draw.color();
            for (TechTreeNode node : nodes) {
                if (!node.visible) continue;
                for (TechTreeNode child : node.children) {
                    if (!child.visible) continue;
                    boolean lock = locked(node.node) || locked(child.node);
                    Draw.z(lock ? 1f : 2f);

                    Lines.stroke(Scl.scl(4f), lock ? Pal.darkerGray : Pal.accent);
                    Draw.alpha(parentAlpha);
                    float dist = Mathf.dst(node.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                    int divisions = Math.max(1, (int) (dist / 20f));
                    if (lock) {
                        Lines.dashLine(node.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY, divisions);
                    } else {
                        //Was this actually worth it? I could've done a straight line nothing special
                        Lines.line(node.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                        float progress = (Time.time % (60 * 4)) / (60 * 4);
                        float arrowX = Mathf.lerp(node.x + offsetX, child.x + offsetX, progress);
                        float arrowY = Mathf.lerp(node.y + offsetY, child.y + offsetY, progress);
                        float angle = Angles.angle(node.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                        float size = 18f;
                        float base = size * 0.5f;
                        Drawf.tri(arrowX, arrowY, size, base, angle);
                    }
                }
            }

            Draw.sort(false);
            Draw.reset();
            super.drawChildren();
        }
    }
}