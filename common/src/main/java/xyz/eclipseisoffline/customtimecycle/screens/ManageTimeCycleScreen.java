package xyz.eclipseisoffline.customtimecycle.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import xyz.eclipseisoffline.customtimecycle.TimeManagerConfiguration;

import java.util.function.Consumer;

public class ManageTimeCycleScreen extends Screen {
    private static final Component EDIT_TITLE = Component.translatable("gui.customtimecycle.edit_title");

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final Screen parent;
    private final Consumer<PreconfiguredTimeCycle> doneConsumer;
    private final boolean global;

    private PreconfiguredTimeCycle configured;

    private StringWidget dayLabel;
    private StringWidget nightLabel;

    private Button done;

    public ManageTimeCycleScreen(Screen parent, PreconfiguredTimeCycle configured, Consumer<PreconfiguredTimeCycle> doneConsumer, boolean global) {
        super(EDIT_TITLE);
        this.parent = parent;
        this.configured = configured;
        this.doneConsumer = doneConsumer;
        this.global = global;
    }

    public ManageTimeCycleScreen(Screen parent, Consumer<PreconfiguredTimeCycle> doneConsumer) {
        this(parent, TimeManagerConfiguration.getLoaded().toPreconfigured(), doneConsumer, true);
    }

    @Override
    protected void init() {
        layout.addTitleHeader(title, font);

        LinearLayout footer = LinearLayout.horizontal().spacing(8);
        done = footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> {
            doneConsumer.accept(configured);
            onClose();
        }).build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).build());

        layout.addToFooter(footer);

        LinearLayout contents = LinearLayout.vertical().spacing(5);
        contents.defaultCellSetting().alignHorizontallyCenter();

        dayLabel = contents.addChild(new StringWidget(dayInputLabel(configured.dayTime()), font), settings -> settings.alignHorizontallyLeft().paddingLeft(2));
        EditBox dayTime = contents.addChild(new EditBox(font, 240, 20, Component.translatable("gui.customtimecycle.day_input")));
        dayTime.setResponder(validateTime(false));
        dayTime.setValue(configured.dayTimeInput());

        nightLabel = contents.addChild(new StringWidget(nightInputLabel(configured.nightTime()), font), settings -> settings.alignHorizontallyLeft().paddingLeft(2));
        EditBox nightTime = contents.addChild(new EditBox(font, 240, 20, Component.translatable("gui.customtimecycle.night_input")));
        nightTime.setResponder(validateTime(true));
        nightTime.setValue(configured.nightTimeInput());

        MutableComponent explanation = global ? Component.translatable("gui.customtimecycle.explanation_global") : Component.translatable("gui.customtimecycle.explanation");
        contents.addChild(new MultiLineTextWidget(explanation.withColor(0xFFA0A0A0), font))
                .setMaxWidth(230);

        layout.addToContents(contents);

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    public PreconfiguredTimeCycle getConfigured() {
        return configured;
    }

    private Consumer<String> validateTime(boolean nightTime) {
        return input -> {
            if (nightTime) {
                configured = configured.withNightTime(input);
                nightLabel.setMessage(nightInputLabel(configured.nightTime()));
            } else {
                configured = configured.withDayTime(input);
                dayLabel.setMessage(dayInputLabel(configured.dayTime()));
            }
            done.active = configured.isValid();
        };
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private static Component dayInputLabel(int time) {
        return timeInputLabel("gui.customtimecycle.day_input_label", time);
    }

    private static Component nightInputLabel(int time) {
        return timeInputLabel("gui.customtimecycle.night_input_label", time);
    }

    private static Component timeInputLabel(String translation, int time) {
        Component formatted = time <= 0 ? Component.translatable("gui.customtimecycle.invalid") : Component.literal(StringUtil.formatTickDuration(time, 20.0F));
        return Component.translatable(translation, formatted).withColor(0xFFA0A0A0);
    }

}
