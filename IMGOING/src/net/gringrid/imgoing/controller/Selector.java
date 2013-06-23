package net.gringrid.imgoing.controller;

import net.gringrid.imgoing.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Selector extends LinearLayout implements OnClickListener {

	private Object			value;

	private OnClickListener	l;

	public Selector(Context context)
	{
		super(context);
		applyLayout("", "13sp", R.layout.custom_selector_white, false);
	}

	public Selector(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		int textResId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", 0);
		String text = "";
		if (textResId == 0)
		{
			text = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
		}
		else
		{
			text = context.getString(textResId);
		}
		String textSize = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize");

		// 커스텀 레이아웃을 사용하는 경우를 위하여 추가함.
		// edit by oops... 2011/12/15
//		int layoutResId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/kr.co.koreastock.mts.android", "selector_layout", 0);
		int layoutResId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/mysmartw.android", "selector_layout", 0);
		if (layoutResId == 0)
		{
			layoutResId = R.layout.custom_selector_white;
		}
		// edit by oops... 2011/12/15
//		boolean singleLine = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/kr.co.koreastock.mts.android", "singleline", false);
		boolean singleLine = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/mysmartw.android", "singleline", false);

		applyLayout(text, textSize, layoutResId, singleLine);
	}

	private void applyLayout(CharSequence text, CharSequence textSize, int layout, boolean singleLine)
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(layout, this);
		setText(text);
		setTextSize(textSize);
		setSingleLine(singleLine);
		findViewById(R.id.custom_selector_btn).setOnClickListener(this);
		findViewById(R.id.custom_selector_right_btn).setOnClickListener(this);
	}

	public void SetEnabled(boolean enabled)
	{
		((LinearLayout)findViewById(R.id.custom_selector_btn)).setEnabled(enabled);
		((ImageView)findViewById(R.id.custom_selector_right_btn)).setEnabled(enabled);
	}
	
	public CharSequence getText()
	{
		TextView textView = ((TextView) findViewById(R.id.custom_selector_name));
		return textView.getText();
	}

	public void setText(CharSequence text)
	{
		TextView textView = ((TextView) findViewById(R.id.custom_selector_name));
		textView.setText(text);
	}
	
	public void setText(CharSequence text, CharSequence nicName)
	{
		TextView textView = ((TextView) findViewById(R.id.custom_selector_name));
		textView.setText(text);
	}

	public void setSingleLine(boolean value)
	{
		if (value)
		{
			TextView textView = ((TextView) findViewById(R.id.custom_selector_name));
			textView.setSingleLine(true);
			textView.setSingleLine();
		}
	}

	public float getTextSize()
	{
		TextView textView = ((TextView) findViewById(R.id.custom_selector_name));
		return textView.getTextSize();
	}

	public void setTextSize(CharSequence textSize)
	{
		TextView textView = ((TextView) findViewById(R.id.custom_selector_name));
		float size = 13.0f;
		int unit = TypedValue.COMPLEX_UNIT_SP;

		try
		{
			String value = textSize.toString().toLowerCase();
			if (value.indexOf("sp") >= 0)
			{
				value = value.replace("sp", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_SP;
			}
			else if (value.indexOf("dp") >= 0)
			{
				value = value.replace("dp", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_DIP;
			}
			else if (value.indexOf("dip") >= 0)
			{
				value = value.replace("dip", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_DIP;
			}
			else if (value.indexOf("px") >= 0)
			{
				value = value.replace("px", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_PX;
			}
		}
		catch (Exception e)
		{

		}

		textView.setTextSize(unit, size);
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public void setOnClickListener(OnClickListener l)
	{
		this.l = l;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.custom_selector_btn:
			case R.id.custom_selector_right_btn:
				if (l != null)
				{
					l.onClick(this);
				}
				break;
		}
	}
}
