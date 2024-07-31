package aquarion;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.Disposable;

public class LoadRenderer implements Disposable {
        public void draw() {
               Draw.color(Color.black);
        }

        @Override
        public void dispose() {
            // Dispose of any resources if necessary
        }
    }
