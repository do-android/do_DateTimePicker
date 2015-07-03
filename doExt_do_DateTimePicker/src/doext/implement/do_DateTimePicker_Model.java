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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
	public void show(JSONObject _dictParas, final DoIScriptEngine _scriptEngine, final String _callbackFuncName) throws JSONException {
		final int _type = DoJsonHelper.getInt(_dictParas, "type", 0); // 打开窗口的类型:0表示日期及时间，1表示只有日期，2表示只有时间
		String _data = DoJsonHelper.getString(_dictParas, "data", System.currentTimeMillis() + ""); // 预设置的日期:long型时间，缺失值是当前日期时间long型
		final String _maxDate = DoJsonHelper.getString(_dictParas, "maxDate", MAXDATE + ""); // 最大日期
																								// :long型时间，缺省值是2099年对应的long型
		final String _minDate = DoJsonHelper.getString(_dictParas, "minDate", MINDATE + ""); // 最小日期:long型时间，最小日期不能大于最大日期，缺省值是1900年对应的long型
		String _title = DoJsonHelper.getString(_dictParas, "title", "日期时间选择"); // 日期时间选择:缺省值是‘时间选择’或者‘日期选择’或者‘日期时间选择’，根据type来区分
		final JSONArray _buttons = DoJsonHelper.getJSONArray(_dictParas, "buttons");

		final Activity _activity = DoServiceContainer.getPageViewFactory().getAppContext();
		calendar = Calendar.getInstance();
		// 初始化当前日期
		calendar.setTimeInMillis(DoTextHelper.strToLong(_data, System.currentTimeMillis()));
		final AlertDialog.Builder _builder = new AlertDialog.Builder(_activity);
		if (!TextUtils.isEmpty(_title)) {
			_builder.setTitle(_title);
		}
		final LinearLayout _childLayout = new LinearLayout(_activity, null, android.R.attr.buttonBarStyle);
		_childLayout.setOrientation(LinearLayout.VERTICAL);
		_activity.runOnUiThread(new Runnable() {
			View _childView;

			@Override
			public void run() {
				switch (_type) {
				case 0:
					_childView = createDateAndTime(_type, _activity, _maxDate, _minDate);
					break;
				case 1:
					createDate(_activity, _maxDate, _minDate);
					_childView = datePicker;
					break;
				case 2:
					createTime(_type, _activity, _maxDate, _minDate);
					_childView = timePicker;
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
					// }
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
		try {
			switch (_type) {
			case 0:
				_val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
						timePicker.getCurrentMinute());
				break;
			case 1:
				_val = String.format(Locale.getDefault(), "%d-%02d-%02d 00:00:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
				break;
			case 2:
				Calendar cd = Calendar.getInstance();
				cd.setTimeInMillis(System.currentTimeMillis());
				_val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, cd.get(Calendar.DAY_OF_MONTH), timePicker.getCurrentHour(),
						timePicker.getCurrentMinute());
				break;
			}
			_value.put("flag", _which);
			_value.put("time", getTime(_val) + "");
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("do_DateTimePicker_Model onClick \n\t", e);
		}
		_result.setResultNode(_value);
		_scriptEngine.callback(_callbackFuncName, _result);
		dialog.cancel();

	}

	private View createDateAndTime(int _type, Activity _activity, String _maxDate, String _minDate) {
		LinearLayout _layout = new LinearLayout(_activity);
		_layout.setOrientation(LinearLayout.VERTICAL);
		createDate(_activity, _maxDate, _minDate);
		_layout.addView(this.datePicker);
		createTime(_type, _activity, _maxDate, _minDate);
		_layout.addView(this.timePicker);
		return _layout;
	}

	private void createDate(Activity _activity, String _maxDate, String _minDate) {
		this.datePicker = new DatePicker(_activity);
		this.datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
		final long _maxData = DoTextHelper.strToLong(_maxDate, MAXDATE);
		final long _minData = DoTextHelper.strToLong(_minDate, MINDATE);
		if (_maxData > _minData) {
			this.datePicker.setMaxDate(_maxData);
			this.datePicker.setMinDate(_minData);
		}
		this.datePicker.setCalendarViewShown(false);
	}

	private void createTime(final int _type, Activity _activity, String _maxDate, String _minDate) {
		this.timePicker = new TimePicker(_activity);
		this.timePicker.setIs24HourView(true);
		this.timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		this.timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

		final long _maxData = DoTextHelper.strToLong(_maxDate, MAXDATE);
		calendar.setTimeInMillis(_maxData);
		final int _maxHour = calendar.get(Calendar.HOUR_OF_DAY);
		final int _maxMinute = calendar.get(Calendar.MINUTE);

		final long _minData = DoTextHelper.strToLong(_minDate, MINDATE);
		calendar.setTimeInMillis(_minData);
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
					if (_maxData > _minData) {
						String _val = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d:00", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), hourOfDay, minute);
						long _currentTime = getTime(_val);
						if (_currentTime > _maxData) { // 当前值大于最大值
							calendar.setTimeInMillis(_maxData);
							datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
							timePicker.setCurrentHour(_maxHour);
							timePicker.setCurrentMinute(_maxMinute);
						} else if (_currentTime < _minData) { // 当前值小于最小值
							calendar.setTimeInMillis(_minData);
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