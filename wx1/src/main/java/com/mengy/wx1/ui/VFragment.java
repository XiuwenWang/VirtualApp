package com.mengy.wx1.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

import org.jdeferred.android.AndroidDeferredManager;

/**
 * @author Lody
 */
public class VFragment extends Fragment {

	protected AndroidDeferredManager defer() {
		return VUiKit.defer();
	}

	public void finishActivity() {
		Activity activity = getActivity();
		if (activity != null) {
			activity.finish();
		}
	}

	public void destroy() {
		finishActivity();
	}
}
