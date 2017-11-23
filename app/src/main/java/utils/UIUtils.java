package utils;

import android.util.TypedValue;

import app.App;
import app.Contacts;

/**
 * Created by wuming on 16/10/14.
 */

public class UIUtils {
	private static long lastClickTime;

	public static boolean isDoubleClick(int interval) {
		long currentClickTime = System.currentTimeMillis();
		if ((currentClickTime - lastClickTime) > interval) {
			lastClickTime = currentClickTime;
			return false;
		}
		return true;
	}

	public static Float dp2px(float dipValue) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, App.getAppContext().getResources().getDisplayMetrics());
		return value;
	}
}
