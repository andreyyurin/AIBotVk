package ltc.aibotvk;

import com.vk.sdk.VKSdk;

/**
 * Created by admin on 28.06.2018.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
