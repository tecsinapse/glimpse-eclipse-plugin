/*
 * Copyright 2012 Tecsinapse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.tecsinapse.glimpse.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import br.com.tecsinapse.glimpse.Activator;

public class GlimpsePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private List<String> pastUrls = new ArrayList<String>();
	private Text urlText;
	
	public GlimpsePreferencePage() {
		super(GRID);
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore(); 
		setPreferenceStore(preferenceStore);
		setDescription("Glimpse Preferences");
		
		String pastUrlsStr = preferenceStore.getString(GlimpsePreferenceConstants.PAST_URLS);
		if (pastUrlsStr != null) {
			pastUrls = new ArrayList<String>(Arrays.asList(pastUrlsStr.split("\\|")));
		}
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	private String selectNextUrl(String currentUrl) {
		int idx = pastUrls.indexOf(currentUrl);
		if (idx == -1) return null;
		if (idx == pastUrls.size() - 1) return currentUrl;
		return pastUrls.get(idx + 1);
	}
	
	private String selectPreviousUrl(String currentUrl) {
		int idx = pastUrls.indexOf(currentUrl);
		if (idx == -1 && !pastUrls.isEmpty()) { 
			return pastUrls.get(pastUrls.size() - 1);
		} else if (idx != 0) {
			return pastUrls.get(idx - 1);
		} else {
			return currentUrl;
		}
	}
	
	private void saveUrl() {
		String url = urlText.getText();
		if (url != null && !url.trim().equals("") && !pastUrls.contains(url)) {
			pastUrls.add(url);
			getPreferenceStore().setValue(GlimpsePreferenceConstants.PAST_URLS, join(pastUrls, "|"));
		}
	}
	
	private static String join(List<String> strings, String separator) {
		if (strings.isEmpty()) return "";
		StringBuilder result = new StringBuilder();
		for (String string : strings) {
			if (result.length() != 0) result.append(separator);
			result.append(string);
		}
		return result.toString();
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if (result) {
			saveUrl();
		}
		return result;
	}
	
	@Override
	protected void createFieldEditors() {
		StringFieldEditor urlEditor = new StringFieldEditor(
				GlimpsePreferenceConstants.URL, "Url", getFieldEditorParent());
		urlText = urlEditor.getTextControl(getFieldEditorParent());
		urlText.addListener(SWT.KeyDown, new Listener() {

			@Override
			public void handleEvent(Event e) {
				switch (e.keyCode) {
				case SWT.ARROW_DOWN:
					urlText.setText(selectNextUrl(urlText.getText()));
					break;
				case SWT.ARROW_UP:
					urlText.setText(selectPreviousUrl(urlText.getText()));
					break;
				}
			}
		});
		addField(urlEditor);
		addField(new StringFieldEditor(GlimpsePreferenceConstants.USERNAME,
				"User name", getFieldEditorParent()));
		addField(new StringFieldEditor(GlimpsePreferenceConstants.PASSWORD,
				"Password", getFieldEditorParent()));
		addField(new ComboFieldEditor(GlimpsePreferenceConstants.CONSOLE_TYPE,
				"Console type", new String[][] { { "Glimpse", "GLIMPSE" },
						{ "Eclipse", "ECLIPSE" } }, getFieldEditorParent()));
	}
}
