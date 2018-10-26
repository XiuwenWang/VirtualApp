package com.lody.virtual.helper.ipcbus;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Lody
 */
public class IPCInvocationBridge implements InvocationHandler {

    private ServerInterface serverInterface;
    private IBinder binder;

    public IPCInvocationBridge(ServerInterface serverInterface, IBinder binder) {
        this.serverInterface = serverInterface;
        this.binder = binder;
    }
    public static final String TAG = "startActivity";
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        long time = System.currentTimeMillis();
        Log.d(TAG, "invoke: 1:" + (System.currentTimeMillis() - time));
        IPCMethod ipcMethod = serverInterface.getIPCMethod(method);
        Log.d(TAG, "invoke: 2:" + (System.currentTimeMillis() - time));
        if (ipcMethod == null) {
            throw new IllegalStateException("Can not found the ipc method : " + method.getDeclaringClass().getName() + "@" +  method.getName());
        }
        Object remote = ipcMethod.callRemote(binder, args);
        Log.d(TAG, "invoke: 3:" + (System.currentTimeMillis() - time));
        return remote;
    }
}
