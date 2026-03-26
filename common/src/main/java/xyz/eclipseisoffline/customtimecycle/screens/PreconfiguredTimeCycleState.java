package xyz.eclipseisoffline.customtimecycle.screens;

import org.jspecify.annotations.Nullable;

public interface PreconfiguredTimeCycleState {

    @Nullable PreconfiguredTimeCycle customTimeCycle$getPreconfiguredTimeCycle();

    void customTimeCycle$setPreconfiguredTimeCycle(@Nullable PreconfiguredTimeCycle timeCycle);
}
