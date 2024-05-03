Events.on(ClientLoadEvent, (event) => {
    Log.info("Aquarion - Sending version check");
    var req = new Http.get(
        "https://raw.githubusercontent.com/Twcash/Tantros-Test/main/mod.hjson",
        (res) => {
            var resp = res.getResultAsString();
            Log.info("Aquaron - response: \n" + resp);

            var json = Jval.read(resp);
            Log.info("Aquarion - remote ver: " + json.get("version"));

            var vers = Vars.mods.getMod("aquarion").meta.version;
            Log.info("Aquarion - local version: " + vers);

            if (!vers.equals(json.get("version"))) {
                Log.warn("Aquarion - not up to date");
                try {
                    Vars.ui.showOkText(
                        "[#22CCFF]Aquarion[white]",
                        Core.bundle.get("scripts.update-aquaria"),
                        "[green]Update available[white], please reinstall Aquarion for latest content!",
                        () => {}
                    );
                } catch (err) {
                    Log.info("Error: " + err.toString());
                }
            } else {
                Log.info("Aquarion - up to date");
            }
        },
        (err) => {
            Vars.ui.showOkText(
                "[#22CCFF]Aquarion[white]",
                "[red]ERROR:[white] Cannot check for updates!",
                () => {}
            );
            Log.err("Aquarion - update check failed :(");
        }
    );
});
