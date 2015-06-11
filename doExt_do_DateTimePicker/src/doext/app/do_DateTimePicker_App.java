package doext.app;
import android.content.Context;
import core.interfaces.DoIAppDelegate;

/**
 * APP启动的时候会执行onCreate方法；
 *
 */
public class do_DateTimePicker_App implements DoIAppDelegate {

	private static do_DateTimePicker_App instance;
	
	private do_DateTimePicker_App(){
		
	}
	
	public static do_DateTimePicker_App getInstance() {
		if(instance == null){
			instance = new do_DateTimePicker_App();
		}
		return instance;
	}
	
	@Override
	public void onCreate(Context context) {
		// ...do something
	}
	
	public String getModuleTypeID() {
		return "do_DateTimePicker";
	}
}
