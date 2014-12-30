package freelancer.worldvideo.util;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;
/**
 * 
 * 振动提醒
 * @author jiangbing
 *
 */
public class TipHelper {
	public static void Vibrate(final Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	public static void Vibrate(final Activity activity, long[] pattern,
			boolean isRepeat) {
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}
	public static void VibrateOne(final Activity activity) {
		long [] pattern = {100,200};
		Vibrator vib = (Vibrator) activity
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, -1);
	}
}
