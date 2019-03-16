package cn.arsenals.sos.wlan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import cn.arsenals.sos.util.SosLog

class WifiDirectBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "WifiDirectBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {

                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED)
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        // Wifi P2P is enabled
                        SosLog.d(TAG, "WIFI_P2P_STATE_CHANGED : WIFI_P2P_STATE_ENABLED")
                    }
                    else -> {
                        // Wi-Fi P2P is not enabled
                        SosLog.d(TAG, "WIFI_P2P_STATE_CHANGED : WIFI_P2P_STATE_NOT_ENABLED")
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                SosLog.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION, startRequestPeers")
                WifiDirectMgr.startRequestPeers()
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
                SosLog.d(TAG, "WIFI_P2P_CONNECTION_CHANGED")
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                SosLog.d(TAG, "networkInfo : $networkInfo")
                if (networkInfo.isConnected) {
                    WifiDirectMgr.requestConnectionInfo()
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
                SosLog.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED")
            }
            else -> {
                SosLog.w(TAG, "unknown wifip2p action" + intent.action)
            }
        }
    }
}
