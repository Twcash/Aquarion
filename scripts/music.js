var musicRoot = Vars.mods.locateMod("aquarion").root.child("music");

var modAmbient = [];
var modDark = [];
var modBoss = [];

var control = Vars.control.sound;

var vAmbient;
var vDark;
var vBoss;

function loadMusic(name) {
    return Vars.tree.loadMusic(name);
}

musicRoot.list().forEach((cat) => {
    Log.info(cat);
    cat.findAll((f) => {
        return f.extEquals("ogg") || f.extEquals("mp3");
    }).forEach((mFile) => {
        Log.info(mFile);
        var music = loadMusic(cat.name() + "/" + mFile.nameWithoutExtension());
        switch (cat.name()) {
            case "ambient":
                modAmbient.push(music);
                break;
            case "dark":
                modDark.push(music);
                break;
            case "boss":
                modBoss.push(music);
                break;
        }
    });
});

Events.on(MusicRegisterEvent, (e) => {
    vAmbient = control.ambientMusic.copy();
    vDark = control.darkMusic.copy();
    vBoss = control.bossMusic.copy();
});

Events.on(WorldLoadEvent, (e) => {
    if (Vars.state.rules.planet == Vars.content.planet("aquarion-tantros")) {
        control.ambientMusic = Seq.with(modAmbient).addAll(vAmbient);
        control.darkMusic = Seq.with(modDark).addAll(vDark);
        control.bossMusic = Seq.with(modBoss).addAll(vBoss);
    }
});

Events.on(StateChangeEvent, (e) => {
    if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
        control.ambientMusic = vAmbient;
        control.darkMusic = vDark;
        control.bossMusic = vBoss;
    }
});
