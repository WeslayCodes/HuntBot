package dev.huntbot.util.interactive;

import dev.huntbot.bot.config.components.IndivComponentConfig;
import dev.huntbot.bot.config.components.SelectOptionConfig;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;
import net.dv8tion.jda.internal.components.selections.EntitySelectMenuImpl;
import net.dv8tion.jda.internal.components.selections.StringSelectMenuImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class InteractiveUtil {
    public static List<ActionRowChildComponent> makeComponents(String id, IndivComponentConfig... components) {
        return InteractiveUtil.makeComponents(id, "", components);
    }

    public static List<ActionRowChildComponent> makeComponents(String id, List<IndivComponentConfig> components) {
        return InteractiveUtil.makeComponents(id, "", components.toArray(new IndivComponentConfig[0]));
    }

    public static List<ActionRowChildComponent> makeComponents(String id, String extra, IndivComponentConfig... components) {
        List<ActionRowChildComponent> madeComponents = new ArrayList<>();

        if (!extra.isEmpty()) {
            extra = "_" + extra;
        }

        for (IndivComponentConfig component : components) {
            String newCustomID = id + "," + component.getCustom_id() + extra;

            switch (component.getType()) {
                case 2 -> {
                    Button btn = new ButtonImpl(
                        newCustomID,
                        component.getLabel(),
                        ButtonStyle.fromKey(component.getStyle()),
                        component.isDisabled(),
                        InteractiveUtil.parseEmoji(component.getEmoji())
                    );

                    madeComponents.add(btn);
                }

                case 3 -> {
                    List<SelectOption> selectOptions = new ArrayList<>();

                    if (component.getOptions() != null) {
                        for (SelectOptionConfig option : component.getOptions()) {
                            SelectOption newOption = SelectOption.of(option.getLabel(), option.getValue())
                                .withEmoji(InteractiveUtil.parseEmoji(option.getEmoji()))
                                .withDescription(option.getDescription());
                            selectOptions.add(newOption);
                        }
                    }

                    SelectMenu select = new StringSelectMenuImpl(
                        newCustomID,
                        (int) (Math.random() * Integer.MAX_VALUE),
                        component.getPlaceholder(),
                        component.getMin_values(),
                        component.getMax_values(),
                        component.isDisabled(),
                        selectOptions,
                        component.getRequired()
                    );

                    madeComponents.add(select);
                }

                case 8 -> {
                    SelectMenu select = new EntitySelectMenuImpl(
                        newCustomID,
                        (int) (Math.random() * Integer.MAX_VALUE),
                        component.getPlaceholder(),
                        component.getMin_values(),
                        component.getMax_values(),
                        component.isDisabled(),
                        Component.Type.CHANNEL_SELECT,
                        EnumSet.of(ChannelType.TEXT),
                        new ArrayList<>(),
                        component.getRequired()
                    );

                    madeComponents.add(select);
                }
            }
        }

        return madeComponents;
    }

    public static Emoji parseEmoji(String emojiStr) {
        Emoji emoji = null;

        if (emojiStr != null && emojiStr.contains("<:")) {
            String emojiName = emojiStr.substring(2, emojiStr.indexOf(":", 2));
            long emojiID = Long.parseLong(emojiStr.substring(
                emojiStr.indexOf(":", 2) + 1, emojiStr.indexOf(">")
            ));

            emoji = Emoji.fromCustom(emojiName, emojiID, false);
        } else if (emojiStr != null) {
            emoji = Emoji.fromUnicode(emojiStr);
        }

        return emoji;
    }
}
