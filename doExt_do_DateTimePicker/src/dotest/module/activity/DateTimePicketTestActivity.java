package dotest.module.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.view.View;
import core.DoServiceContainer;
import doext.implement.do_DateTimePicker_Model;
import dotest.module.frame.debug.DoService;

public class DateTimePicketTestActivity extends DoTestActivity {

	@Override
	protected void initUIView() throws Exception {
		
	}

	@Override
	protected void initModuleModel() throws Exception {
		this.model = new do_DateTimePicker_Model();
	}

	@Override
	public void doTestProperties(View view) {
	}

	@Override
	protected void doTestSyncMethod() {
	}

	@Override
	protected void doTestAsyncMethod() {
		Map<String, Object>  _paras_loadString = new HashMap<String, Object>();
        _paras_loadString.put("type", 2);
        _paras_loadString.put("minDate", 631167000000L );//631152000000
        _paras_loadString.put("maxDate", 636259800000L );//1609344000000
        
        try {
			_paras_loadString.put("buttons", new JSONArray("[\"取消\",\"知道了\",\"确定\"]"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
//      int  _type  = DoJsonHelper.getInt(_dictParas, "type", 0);  //打开窗口的类型  : 0表示日期及时间，1表示只有日期，2表示只有时间
//	    String _data = DoJsonHelper.getString(_dictParas, "data", System.currentTimeMillis()+""); //预设置的日期 : long型时间，缺失值是当前日期时间long型
//	    String _maxDate = DoJsonHelper.getString(_dictParas, "maxDate", MAXDATE +""); //最大日期 : long型时间，缺省值是2099年对应的long型
//	    String _minDate = DoJsonHelper.getString(_dictParas, "minDate", MINDATE +""); //最小日期: long型时间，最小日期不能大于最大日期，缺省值是1900年对应的long型
//	    String _title = DoJsonHelper.getString(_dictParas, "title", "日期时间选择"); //日期时间选择: 缺省值是‘时间选择’或者‘日期选择’或者‘日期时间选择’，根据type来区分
//	    JSONArray _buttons = DoJsonHelper.getJSONArray(_dictParas, "buttons");
        
        DoService.ansyncMethod(this.model, "show", _paras_loadString, new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {//回调函数
				DoServiceContainer.getLogEngine().writeDebug("异步方法回调：" + _data);
			}
		});
	}

	@Override
	protected void onEvent() {
	}

	@Override
	public void doTestFireEvent(View view) {
		
	}

}
