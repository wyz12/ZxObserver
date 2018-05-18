package com.zxwl.zxobserver;

import java.util.Collection;

/**
 * Created by sks on 2018/5/3.
 */

public interface PermissionRequestListener {

    /**
     * 所有申请的权限均被允许的回调
     */
    void onAllowAllPermissions();

    /**
     * 申请的权限中有权限被拒绝的回调
     */
    void onDenySomePermissions(Collection<String> denyPermissions);

    /**
     * 申请的权限中有权限被拒绝并勾选了不再提示的回调
     */
    void onDenyAndNeverAskAgainSomePermissions(Collection<String> denyAndNeverAskAgainPermissions);

}
