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
    cat.findAll((f) => {
        return f.extEquals("ogg") || f.extEquals("mp3");
    }).forEach((mFile) => {
        var music = loadMusic(cat.name() + "/" + mFile.nameWithoutExtension());
        switch (cat.name()) {
            case "ambient":
                modAmbient.add(music);
                break;
            case "dark":
                modDark.add(music);
                break;
            case "boss":
                modBoss.add(music);
                break;
        }
    });
});

Events.on(MusicRegisterEvent, (e) => {
    vAmbient = control.ambientMusic.copy().toArray();
    vDark = control.darkMusic.copy().toArray();
    vBoss = control.bossMusic.copy().toArray();

    vAmbient = [];
});

Events.on(WorldLoadEvent, (e) => {
    if (Vars.state.rules.planet == Vars.content.planet("aquarion-tantros")) {
        control.ambientMusic = modAmbient.concat(vAmbient);
        control.darkMusic = modDark.concat(vDark);
        control.bossMusic = modBoss.concat(vBoss);
    }
});

Events.on(StateChangeEvent, (e) => {
    if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
        control.ambientMusic = vAmbient;
        control.darkMusic = vDark;
        control.bossMusic = vBoss;
    }
});
