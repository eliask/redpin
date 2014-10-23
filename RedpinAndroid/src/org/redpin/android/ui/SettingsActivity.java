/**
 *  Filename: SettingsActivity.java (in org.repin.android.ui)
 *  This file is part of the Redpin project.
 *
 *  Redpin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  Redpin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 *  (c) Copyright ETH Zurich, Luba Rogoleva, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 *
 *  www.redpin.org
 */
package org.redpin.android.ui;

import org.redpin.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Class represents an activity responsible for the changing settings.
 *
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class SettingsActivity extends Activity {

	/**
	 * Called when the activity is starting inflating the activity's UI. This is
	 * where most initialization should go.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_view);

	}


	/**
	 * Starts the setting screen
	 *
	 * @param target {@link View} that called this method
	 */
	public void button_Mapview(View target) {
		Intent intent = new Intent(this, MapViewActivity.class);
		startActivity(intent);
	}

	/**
	 * Start the server preferences activity
	 *
	 * @param target {@link View} that called this method
	 */
	public void button_ServerPreferences(View target) {
		Intent intent = new Intent(this, ServerPreferences.class);
		startActivity(intent);
	}

}
