package lib.brainsynder.utils;

import com.google.gson.stream.JsonWriter;
import lib.brainsynder.ServerVersion;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public final class MessagePart {
    public ChatColor color = null;
    public Color customColor = null;
    public ChatColor[] styles = null;
    public String clickActionName = null;
    public String clickActionData = null;
    public String hoverActionName = null;
    public String hoverActionData = null;
    public final String text;
    public String font = null;

    public MessagePart(String text) {
        this.text = text;
    }

    public JsonWriter writeJson(JsonWriter json) {
        try {
            json.beginObject().name("text").value(this.text);
            if (this.color != null) {
                // Uses the ChatColor variable (Default MC colors)
                json.name("color").value(this.color.name().toLowerCase());
            }else if ((customColor != null) && ServerVersion.isEqualNew(ServerVersion.v1_16_R1)) {
                // Uses the Color (Allows RGB/HEX colors) [1.16+]
                // Since 1.16 added the ability to have RGB/HEX colored messages via TellRaw
                json.name("color").value(toHex(customColor.getRed(), customColor.getGreen(), customColor.getBlue()));
            }
            if (this.font != null) {
                json.name("font").value(font.toLowerCase());
            }
            if (this.styles != null) {
                for (ChatColor style : this.styles) {
                    json.name(style.name().toLowerCase()).value(true);
                }
            }
            if ((this.clickActionName != null) && (this.clickActionData != null)) {
                json.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value").value(this.clickActionData).endObject();
            }

            if ((this.hoverActionName != null) && (this.hoverActionData != null)) {
                json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value").value(this.hoverActionData).endObject();
            }

            return json.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }
}