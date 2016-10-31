package doext.implement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import core.DoServiceContainer;
import core.helper.DoJsonHelper;
import core.helper.DoTextHelper;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_DateTimePicker_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现do_DateTimePicker_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_DateTimePicker_Model extends DoSingletonModule implements do_DateTimePicker_IMethod {

	private final long MINDATE = 0L;
	private final long MAXDATE = 4102329600000L;

	private Calendar calendar; // 通过Calendar获取系统时间
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog dialog;
	private Activity _activity;

	public do_DateTimePicker_Model() throws Exception {
		super();
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V），获取参数值使用API提供DoJsonHelper类；
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		if ("show".equals(_methodName)) { // 显示控件
			this.show(_dictParas, _scriptEngine, _callbackFuncName);
			return true;
		}
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 弹出选择日期，时间，或日期时间窗口；
	 * 
	 * @throws JSONException
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_callbackFuncName 回调函数名
	 */
	@Override
	public void show(JSONObject _dictParas, final DoIScriptEngine _scriptEngine, final String _callbackFuncName) throws Exception {
		final int _type = DoJsonHelper.getInt(_dictParas, "type", 0); // 打开窗口的类型:0表示日期及时间，1表示只有日期，2表示只有时间
		String _data = DoJsonHelper.getString(_dictParas, "data", System.currentTimeMillis() + ""); // 预设置的日期:long型时间，缺失值是当前日期时间long型
		final String _maxDate = DoJsonHelper.getString(_dictParas, "maxDate", MAXDATE + ""); // 最大日期
																								// :long型时间，缺省值是2099年对应的long型
		final String _minDate = DoJsonHelper.getString(_dictParas, "minDate", MINDATE + ""); // 最小日期:long型时间，最小日期不能大于最大日期，缺省值是1900年对应的long型
		String _title = DoJsonHelper.getString(_dictParas, "title", "日期时间选择"); // 日期时间选择:缺省值是‘时间选择’或者‘日期选择’或者‘日期时间选择’，根据type来区分
		final JSONArray _buttons = DoJsonHelper.getJSONArray(_dictParas, "buttons");

		_activity = DoServiceContainer.getPageViewFactory().getAppContext();
		calendar = Calendar.getInstance();
		// 初始化当前日期
		calendar.setTimeInMillis(DoTextHelper.strToLong(_data, System.currentTimeMillis()));
		final AlertDialog.Builder _builder = new AlertDialog.Builder(_activity);
		if (!TextUtils.isEmpty(_title)) {
			_builder.setTitle(_title);
		}

		long _realMaxDate = DoTextHelper.strToLong(_maxDate, MAXDATE);
		long _realMinDate = DoTextHelper.strToLong(_minDate, MINDATE);
		//如果是type = 1 ,	将时间转换成 00:00:00
		if (_type == 1) {
			//获取当天的整点时间2016-10-28 0:0:0
			Calendar _tempCalendar = Calendar.getInstance();

			_tempCalendar.setTimeInMillis(_realMaxDate);
			_tempCalendar.set(_tempCalendar.get(Calendar.YEAR), _tempCalendar.get(Calendar.MONTH), _tempCalendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
			_realMaxDate = _tempCalendar.getTimeInMillis();

			_tempCalendar.setTimeInMillis(_realMinDate);
			_tempCalendar.set(_tempCalendar.get(Calendar.YEAR), _tempCalendar.get(Calendar.MONTH), _tempCalendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
			_realMinDate = _tempCalendar.getTimeInMillis();
		}

		Calendar _maxCalendar = Calendar.getInstance();
		_maxCalendar.setTimeInMillis(_realMaxDate);
		Calendar _minCalendar = Calendar.getInstance();
		_minCalendar.setTimeInMillis(_realMinDate);
		if (calendar.before(_minCalendar) || calendar.after(_maxCalendar)) {
			throw new Exception("设置当前时间(" + calendar.getTimeInMillis() + ")必须在最大(" + _realMaxDate + ")最小(" + _realMinDate + ")时间之间");
		}

		final long _finalMaxDate = _realMaxDate;
		final long _finalMinDate = _realMinDate;

		final LinearLayout _childLayout = new LinearLayout(_activity, null, android.R.attr.buttonBarStyle);
		_childLayout.setOrientation(LinearLayout.VERTICAL);
		_activity.runOnUiThread(new Runnable() {
			View _childView;
			TextView _mWeekDay;

			@Override
			public void run() {
				switch (_type) {
				case 0:
					_childView = createDateAndTime(_type, _activity, _finalMaxDate, _finalMinDate, null);
					break;
				case 1:
					createDate(_activity, _finalMaxDate, _finalMinDate, null);
					_childView = datePicker;
					break;
				case 2:
					createTime(_type, _activity, _finalMaxDate, _finalMinDate);
					_childView = timePicker;
					break;
				case 3:
					_mWeekDay = new TextView(_activity);
					_mWeekDay.setText(getWeekDay(calendar));
					_mWeekDay.setTextSize(18);
					_mWeekDay.setGravity(Gravity.CENTER);
					_mWeekDay.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
					_childView = createDateAndTime(_type, _activity, _finalMaxDate, _finalMinDate, _mWeekDay);
					break;
				}

				if (_childView != null) {
					_childLayout.addView(_childView);
				}

				if (_buttons == null) { // 显示确定，取消按钮
					_builder.setPositiveButton("确定", new MyListener(_type, 1, _scriptEngine, _callbackFuncName));
					_builder.setNegativeButton("取消", new MyListener(_type, 0, _scriptEngine, _callbackFuncName));

				} else if (_buttons.length() > 0) {
					LinearLayout _ll_btns = new LinearLayout(_activity, null, android.R.attr.buttonBarStyle);
					_ll_btns.setOrientation(LinearLayout.HORIZONTAL);
					for (int i = 0; i < _buttons.length(); i++) {
						Button _btn = new Button(_activity, null, android.R.attr.buttonBarButtonStyle);
						try {
							_btn.setText(_buttons.getString(i));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						_btn.setMinHeight(px2dip(_activity, 54));
						_btn.setMaxLines(2);
						_btn.setTextSize(15);
						_btn.setOnClickListener(new MyListener(_type, i, _scriptEngine, _callbackFuncName));
						LayoutParams _btn_params = new LayoutParams(0, -2);
						_btn_params.weight = 1;
						_ll_btns.addView(_btn, _btn_params);
					}
					_childLayout.addView(_ll_btns, new LayoutParams(-1, -2));
				}

				_builder.setView(_childLayout);
				dialog = _builder.create();
				dialog.show();
			}
		});
	}

	private class MyListener implements View.OnClickListener, DialogInterface.OnClickListener {

		private int type;
		private int which;
		private DoIScriptEngine scriptEngine;
		private String callbackFuncName;

		public MyListener(int _type, int _which, DoIScriptEngine _scriptEngine, String _callbackFuncName) {
			this.type = _type;
			this.which = _which;
			this.scriptEngine = _scriptEngine;
			this.callbackFuncName = _callbackFuncName;
		}

		@Override
		public void onClick(View v) {
			callBack(type, which, scriptEngine, callbackFuncName);
		}

		@Override
		public void onClick(DialogInterface dialog, int id) {
			callBack(type, which, scriptEngine, callbackFuncName);
		}
	}

	private void callBack(int _type, int _which, DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		DoInvokeResult _result = new DoInvokeResult(getUniqueKey());
		JSONObject _value = new JSONObject();
		String _val = null;
		Calendar cd = Calendar.getInstance();
		try {
			_value.put("flag", _which);
			switch (_type) {
			case 0:
				_val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
						timePicker.getCurrentMinute());
				_value.put("time", getTime(_val) + "");
				break;
			case 1:
				cd.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
				//抹掉毫秒值
				_value.put("time", (cd.getTimeInMillis() / 1000) * 1000);
				break;
			case 2:
				cd.setTimeInMillis(System.currentTimeMillis());
				_val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, cd.get(Calendar.DAY_OF_MONTH), timePicker.getCurrentHour(),
						timePicker.getCurrentMinute());
				_value.put("time", getTime(_val) + "");
				break;
			case 3:
				_val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
						timePicker.getCurrentMinute());
				_value.put("time", getTime(_val) + "");
				break;
			}
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("do_DateTimePicker_Model onClick \n\t", e);
		}
		_result.setResultNode(_value);
		_scriptEngine.callback(_callbackFuncName, _result);
		dialog.cancel();

	}

	private class MyDateChangedListener implements OnDateChangedListener {

		private TextView mWeekDay;

		public MyDateChangedListener(TextView _mWeekDay) {
			this.mWeekDay = _mWeekDay;
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			final Calendar c = Calendar.getInstance();
			c.set(year, monthOfYear, dayOfMonth);
			String wekkDay = getWeekDay(c);
			if (null != mWeekDay) {
				mWeekDay.setText(wekkDay);
			}
		}
	}

	private String getWeekDay(Calendar c) {
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		return "星期" + mWay;
	}

	private View createDateAndTime(int _type, Activity _activity, long _maxDate, long _minDate, TextView _mWeekDay) {
		LinearLayout _layout = new LinearLayout(_activity);
		_layout.setOrientation(LinearLayout.VERTICAL);
		createDate(_activity, _maxDate, _minDate, _mWeekDay);
		_layout.addView(this.datePicker);
		if (null != _mWeekDay) {
			_layout.addView(_mWeekDay);
		}
		createTime(_type, _activity, _maxDate, _minDate);
		_layout.addView(this.timePicker);
		return _layout;
	}

	private void createDate(Activity _activity, long _maxDate, long _minDate, TextView _mWeekDay) {
		this.datePicker = new DatePicker(_activity);
		this.datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new MyDateChangedListener(_mWeekDay));
		Calendar _maxTime = Calendar.getInstance();
		_maxTime.setTimeInMillis(System.currentTimeMillis());
		//如果(选择的日期+当前时间)比最大时间还要大的话，那就把最大时间改成当前时间
		Calendar _currentTime = Calendar.getInstance();
		_currentTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), _maxTime.get(Calendar.HOUR_OF_DAY), _maxTime.get(Calendar.MINUTE),
				_maxTime.get(Calendar.SECOND));
		long _cDate = _currentTime.getTimeInMillis();
		if (_cDate > _maxDate) {
			_maxDate = _cDate;
		}
		if (_maxDate > _minDate) {
			this.datePicker.setMaxDate(_maxDate);
			this.datePicker.setMinDate(_minDate);
		}
		this.datePicker.setCalendarViewShown(false);
	}

	private void createTime(final int _type, Activity _activity, final long _maxDate, final long _minDate) {
		this.timePicker = new TimePicker(_activity);
		this.timePicker.setIs24HourView(true);
		this.timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		this.timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

		calendar.setTimeInMillis(_maxDate);
		final int _maxHour = calendar.get(Calendar.HOUR_OF_DAY);
		final int _maxMinute = calendar.get(Calendar.MINUTE);

		calendar.setTimeInMillis(_minDate);
		final int _minHour = calendar.get(Calendar.HOUR_OF_DAY);
		final int _minMinute = calendar.get(Calendar.MINUTE);

		calendar.setTimeInMillis(System.currentTimeMillis());
		final String _maxStr = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
				_maxHour, _maxMinute);
		final String _minStr = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
				_minHour, _minMinute);

		this.timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				if (_type == 0) {
					if (_maxDate > _minDate) {
						String _val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), hourOfDay, minute);
						long _currentTime = getTime(_val);
						if (_currentTime > _maxDate) { // 当前值大于最大值
							calendar.setTimeInMillis(_maxDate);
							datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
							timePicker.setCurrentHour(_maxHour);
							timePicker.setCurrentMinute(_maxMinute);
						} else if (_currentTime < _minDate) { // 当前值小于最小值
							calendar.setTimeInMillis(_minDate);
							datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
							timePicker.setCurrentHour(_minHour);
							timePicker.setCurrentMinute(_minMinute);
						}
					}
				} else {
					if (getTime(_maxStr) > getTime(_minStr)) {
						if (hourOfDay < _minHour) {
							timePicker.setCurrentHour(_minHour);
						}

						if (hourOfDay > _maxHour) {
							timePicker.setCurrentHour(_maxHour);
						}

						if (hourOfDay == _minHour) { // 如果当前选中时间的小时 =
														// 最小值，那么就比较分钟
							if (minute < _minMinute) {
								timePicker.setCurrentMinute(_minMinute);
							}
						}

						if (hourOfDay == _maxHour) { // 如果当前选中时间的小时 =
														// 最大值，那么就比较分钟
							if (minute > _maxMinute) {
								timePicker.setCurrentMinute(_maxMinute);
							}
						}
					}
				}
			}
		});

	}

	// 将字符串转为时间戳
	@SuppressLint("SimpleDateFormat")
	private static long getTime(String time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = sdf.parse(time);
			return d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	private int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}