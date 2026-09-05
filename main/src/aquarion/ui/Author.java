package aquarion.ui;

import arc.scene.style.Drawable;
import arc.struct.Seq;
import mindustry.gen.Icon;

public class Author {
    public String name;
    public String descKey;
    public String url;
    public String texture;
    public Drawable fallback;
    public boolean hasProfile;

    public Author(String name, String descKey, String url, String texture, Drawable fallback, boolean hasProfile) {
        this.name = name;
        this.descKey = descKey;
        this.url = url;
        this.texture = texture;
        this.fallback = fallback;
        this.hasProfile = hasProfile;
    }

    public static Seq<Author> getHelpers() {
        return Seq.with(
                new Author("NikolayKot02", "aquarion.menu.desc_NikolayKot", "https://github.com/NikolayKot02", "nikolaykot", Icon.players, true),
                new Author("OwO (Sentinel)", "aquarion.menu.desc_OwO", "https://github.com/SentinelDart919", "OwO", Icon.players, true),
                new Author("Alecthe2nd", "aquarion.menu.desc_Alecthe2nd", "https://github.com/alecthe2nd", "Alecthe2nd", Icon.players, true),
                new Author("cupcakerouter", "aquarion.menu.desc_cupcakerouter", "", "cupcakerouter", Icon.players, false),
                new Author("Vire", "aquarion.menu.desc_Vire", "https://github.com/VireVeonix", "Vire", Icon.players, true),
                new Author("ItsKirby", "aquarion.menu.desc_ItsKirby", "https://github.com/ItsKirby69", "ItsKirby", Icon.players, true),
                new Author("Plooey", "aquarion.menu.desc_Thinkerdoodle", "https://github.com/BSp-2", "thinkerdoodle", Icon.players, true),
                new Author("Leo", "aquarion.menu.desc_Leo", "https://github.com/Leo-MathGuy", "Leo", Icon.players, true),
                new Author("Mythril", "aquarion.menu.desc_Mythril", "https://github.com/Mythril382", "Mythril", Icon.players, true),
                new Author("Andromeda-Galaxy29", "aquarion.menu.desc_Andromeda-Galaxy29", "https://github.com/Andromeda-Galaxy29", "Andromeda-Galaxy29", Icon.players, true),
                new Author("Sputnuc", "aquarion.menu.desc_Sputnuc", "https://github.com/Sputnuc", "Sputnuc", Icon.players, true),
                new Author("nullotte", "aquarion.menu.desc_nullotte", "https://github.com/nullotte", "nullotte", Icon.players, true),
                new Author("kapzduke", "aquarion.menu.desc_kapzduke", "https://github.com/kapzduke", "kapzduke", Icon.players, true),
                new Author("camelStyleUser", "aquarion.menu.desc_camelStyleUser", "https://github.com/camelStyleUser", "camelStyleUser", Icon.players, true),
                new Author("Henan-CN-0921", "aquarion.menu.desc_Henan-CN-0921", "https://github.com/Henan-CN-0921", "Henan-CN-0921", Icon.players, true),
                new Author("Norax", "aquarion.menu.desc_Norax", "https://github.com/Noraxx1", "Norax", Icon.players, true)
        );
    }
}