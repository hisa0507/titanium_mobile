package org.appcelerator.titanium.util;

import org.appcelerator.titanium.TiDict;
import org.appcelerator.titanium.kroll.KrollCallback;
import org.appcelerator.titanium.view.Ti2DMatrix;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class TiAnimationBuilder
{
	protected double anchorX;
	protected double anchorY;

	protected Ti2DMatrix tdm = null;
	protected Double delay = null;
	protected Double duration = null;
	protected Double toOpacity = null;
	protected Double fromOpacity = null;

	protected KrollCallback startCallback;
	protected KrollCallback stopCallback;

	public TiAnimationBuilder()
	{
		// Defaults
		anchorX = 0.5;
		anchorY = 0.5;
	}

	public void applyOptions(TiDict options)
	{
		if (options == null) {
			return;
		}

		if (options.containsKey("anchorPoint")) {
			TiDict point = (TiDict) options.get("anchorPoint");
			anchorX = TiConvert.toDouble(point, "x");
			anchorY = TiConvert.toDouble(point, "y");
		}

		if (options.containsKey("transform")) {
			tdm = (Ti2DMatrix) options.get("transform");
		}
		if (options.containsKey("delay")) {
			delay = TiConvert.toDouble(options, "delay");
		}
		if (options.containsKey("duration")) {
			duration = TiConvert.toDouble(options, "duration");
		}
		if (options.containsKey("opacity")) {
			toOpacity = TiConvert.toDouble(options, "opacity");
			fromOpacity = 1.0 - toOpacity;
		}

	}

	public void setStartCallback(KrollCallback startCallback) {
		this.startCallback = startCallback;
	}

	public void setStopCallback(KrollCallback stopCallback) {
		this.stopCallback = stopCallback;
	}

	public AnimationSet render(int w, int h)
	{
		float anchorPointX = (float)((w * anchorX));
		float anchorPointY = (float)((h * anchorY));

		AnimationSet as = new AnimationSet(false);

		if (toOpacity != null) {
			Animation a = new AlphaAnimation(fromOpacity.floatValue(), toOpacity.floatValue());
			as.addAnimation(a);
		}

		if (tdm != null) {
			as.setFillAfter(true);
			if (tdm.hasRotation()) {
				Animation a = new RotateAnimation(0,tdm.getRotation(), anchorPointX, anchorPointY);
				as.addAnimation(a);
			}
			if (tdm.hasScaleFactor()) {
				Animation a = new ScaleAnimation(1, tdm.getScaleFactor(), 1, tdm.getScaleFactor(), anchorPointX, anchorPointY);
				as.addAnimation(a);
			}
			if (tdm.hasTranslation()) {
				Animation a = new TranslateAnimation(
					0,
					anchorPointX + tdm.getXTranslation(),
					0,
					anchorPointY + tdm.getYTranslation()
					);
				as.addAnimation(a);
			}
		}

		// Set duration after adding children.
		if (duration != null) {
			as.setDuration(duration.longValue());
		}
		if (delay != null) {
			as.setStartTime(delay.longValue());
		}

		if (startCallback != null || stopCallback != null) {
			as.setAnimationListener(new Animation.AnimationListener(){

				@Override
				public void onAnimationEnd(Animation a)
				{
					if (stopCallback != null) {
						stopCallback.call();
					}
				}

				@Override
				public void onAnimationRepeat(Animation a) {
				}

				@Override
				public void onAnimationStart(Animation a)
				{
					if (startCallback != null) {
						startCallback.call();
					}
				}

			});
		}

		return as;
	}
}
