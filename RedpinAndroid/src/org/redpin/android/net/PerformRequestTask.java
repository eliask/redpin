/**
 *  Filename: PerformRequestTask.java (in org.repin.android.net)
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
 *  (c) Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS RESERVED.
 *
 *  www.redpin.org
 */
package org.redpin.android.net;

import android.os.AsyncTask;
import android.util.Log;

/**
 * PerformRequestTask is a specialized {@link AsyncTask} to perform an server
 * request on the background
 *
 * @see AsyncTask
 * @author Pascal Brogle (broglep@student.ethz.ch)
 *
 */
public class PerformRequestTask extends
		AsyncTask<Request<?>, Void, Response<?>> {

	private static final String TAG = PerformRequestTask.class.getSimpleName();
	private PerformRequestTaskCallback taskCallback;

	/**
	 * Creates a {@link PerformRequestTask} with no
	 * {@link PerformRequestTaskCallback}
	 */
	public PerformRequestTask() {
	}

	/**
	 * Creates a {@link PerformRequestTask} with setting a
	 * {@link PerformRequestTaskCallback}
	 *
	 * @param callback
	 *            {@link PerformRequestTaskCallback} for the current task
	 */
	public PerformRequestTask(PerformRequestTaskCallback callback) {
		super();
		this.taskCallback = callback;
	}

	/**
	 * Creates a {@link PerformRequestTask} by copying the
	 * {@link PerformRequestTaskCallback} from task
	 *
	 * @param task
	 *            {@link PerformRequestTask} with
	 *            {@link PerformRequestTaskCallback} to be used
	 */
	public PerformRequestTask(PerformRequestTask task) {
		super();
		this.taskCallback = task.taskCallback;
	}

	private Request<?> request;

	/**
	 * Performs an server request on the background
	 *
	 * @param params
	 *            Request to be performed (only the first is used)
	 * @return {@link Response} from the server
	 */
	@Override
	protected Response<?> doInBackground(Request<?>... params) {

		request = params[0];

		Response<?> response = RequestHandler.performRequest(request);

		if (taskCallback != null) {
			try {
				taskCallback.onPerformedBackground(request, response, this);
			} catch (Exception e) {
				Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
			}
		}
		return response;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPostExecute(Response<?> result) {

		if (taskCallback != null) {
			try {
				taskCallback.onPerformedForeground(request, result, this);
			} catch (Exception e) {
				Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
			}
		}

		cleanup();
	}

	@Override
	protected void onCancelled() {
		if (taskCallback != null) {
			try {
				taskCallback.onCanceledForeground(request, this);
			} catch (Exception e) {
				Log.w(TAG, "Callback failed, caught Exception: " + e.getMessage(), e);
			}
		}

		cleanup();
	}

	/**
	 * Cleans up the references
	 */
	private void cleanup() {
		taskCallback = null;
		request = null;
	}

}
