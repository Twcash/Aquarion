package aquarion.world.graphs;


import arc.util.io.*;
import mindustry.entities.*;
import mindustry.gen.*;

public class GraphUpdater implements Entityc {
    public transient boolean added;
    public transient int id = EntityGroup.nextId();
    public transient Graph graph;
    public transient int id_all = -1;


    @Override public <T extends Entityc> T self() {
        return (T)this;
    }
    @Override public <T> T as() {
        return (T)this;
    }

    @Override public boolean isAdded() {
        try {
            return Groups.all.index(id_all) == this;
        } catch (Exception e) {
            return false;
        }
    }
    @Override public boolean isLocal() {
        return true;
    }
    @Override public boolean isNull() {
        return false;
    }
    @Override public boolean isRemote() {
        return false;
    }
    @Override public boolean serialize() {
        return false;
    }

    @Override
    public void add() {
        if (!isAdded()) {
            id_all = Groups.all.addIndex(this);
            added = true;
        }
    }
    @Override
    public void remove() {
        if (added) {
            Groups.all.removeIndex(this, id_all);
            id_all = -1;
            added = false;
        }
    }

    @Override public void afterRead() {}
    @Override public void read(Reads read) {}
    @Override public void write(Writes write) {}

    @Override
    public void update() {
        if (graph != null) {
            graph.update();
        }
    }

    @Override public int id() {
        return id;
    }
    @Override public void id(int i) {
        id = i;
    }
    @Override public int classId() {
        return EntityMapping.idMap.get(GraphUpdater.class);
    }

    @Override
    public String toString() {
        return "ForceGraphUpdater#"+id;
    }
}