package space.clevercake.daysuntilnewyear;

import android.graphics.Color;

import java.util.Random;


public class ColorUtil {

    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(200);
        int green = random.nextInt(200);
        int blue = random.nextInt(200);
        return Color.rgb(red, green, blue);
    }

    public static int randomWhiteColor() {
        Random random = new Random();
        int a = random.nextInt(200);

        return Color.argb(a, 255, 255, 255);
    }
}
