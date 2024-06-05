package com.igetcool.icodetest.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SettingsConfigurable implements Configurable {

    private SettingsPanel settingsPanel;
    private JScrollPane scrollPane;


    @Override
    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new SettingsPanel();
            scrollPane = new JBScrollPane(settingsPanel);
        }
        return scrollPane;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return Configurable.super.getPreferredFocusedComponent();
    }

    @Override
    public boolean isModified() {
        return settingsPanel.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsPanel.apply();
    }

    @Override
    public void reset() {
        if (scrollPane != null) {
            scrollPane.getVerticalScrollBar().setValue(0);
        }
        settingsPanel.reset();
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
        scrollPane = null;
    }

    @Override
    public void cancel() {
        Configurable.super.cancel();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "iCodeTest";
    }
}