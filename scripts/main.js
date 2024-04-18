Events.on(ClientLoadEvent, (event) => {
    Log.info("Aquaria - Sending version check");

    var req = new Http.get(
        "https://raw.githubusercontent.com/Twcash/Tantros-Test/main/mod.hjson",
        (res) => {
            var resp = res.getResultAsString();
            Log.info("Aquaria - response: \n" + resp);

            var json = Jval.read(resp);
            Log.info("Aquaria - remote ver: " + json.get("version"));

            var vers = Vars.mods.getMod("aquarion").meta.version;
            Log.info("Aquaria - local version: " + vers);

            if (!vers.equals(json.get("version"))) {
                Log.warn("Aquaria - not up to date");
                try {
                    Vars.ui.showOkText(
                        "[#22CCFF]Aquarion[white]",
                        Core.bundle.get("scripts.update-aquaria"),
                        () => {}
                    );
                } catch (err) {
                    Log.info("Error: " + err.toString());
                }
            } else {
                Log.info("Aquaria - up to date");
            }
        },
        (err) => {
            Vars.ui.showOkText(
                "[#22CCFF]Aquarion[white]",
                "[red]ERROR:[white] Cannot check for updates!",
                () => {}
            );
            Log.err("Aquaria - update check failed :(");
        }
    );
});
