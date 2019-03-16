package cn.arsenals.sos.wlan

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.*
import android.os.Looper
import cn.arsenals.sos.SosApplication
import cn.arsenals.sos.util.SosLog

object WifiDirectMgr {
    private const val TAG = "WifiDirectMgr"
    private const val MAX_GROUP_OWNER_INTENT = 15
    var mReceiver: WifiDirectBroadcastReceiver? = null
    val mManager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        SosApplication.context?.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }
    var mChannel: WifiP2pManager.Channel? = null

    var mWifiP2pDevice: MutableList<WifiP2pDevice> = mutableListOf()
    var mWifiP2pInfo: WifiP2pInfo? = null
    var mWifiP2pGroup: WifiP2pGroup? = null

    private val mIntentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private var devListUpdatedCallback: DeviceListUpdated? = null

    interface DeviceListUpdated {
        fun updateDevList(wifiP2pDevice: MutableList<WifiP2pDevice>)
    }

    fun registerDevListUpdatedCallback(callBack: DeviceListUpdated) {
        devListUpdatedCallback = callBack
    }

    fun unregisterDevListUpdatedCallback() {
        devListUpdatedCallback = null
    }

    fun init() {
        val context = SosApplication.context
        mChannel = mManager?.initialize(context, Looper.getMainLooper()) {
            SosLog.i(TAG, "onChannelDisconnected")
            uninit()
        }

        registerReceiver()
    }

    fun uninit() {
        unregisterReceiver()
        stopPeersDiscovery()
        stopRequestPeers()
        removeGroup()
        cancelConnect()
    }

    fun registerReceiver() {
        mReceiver = WifiDirectBroadcastReceiver()
        SosApplication.context?.registerReceiver(mReceiver, mIntentFilter)
    }

    fun unregisterReceiver() {
        mReceiver ?: return
        SosApplication.context?.unregisterReceiver(mReceiver)
        mReceiver = null
    }

    fun discoverPeers() {
        mManager?.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "discoverPeers succeed")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "discoverPeers failed, reasonCode : $reasonCode")
            }
        })
    }

    fun stopPeersDiscovery() {
        mManager?.stopPeerDiscovery(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "stopPeersDiscovery succeed")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "stopPeersDiscovery failed, reasonCode : $reasonCode")
            }
        })
    }

    fun createGroup() {
        mManager?.createGroup(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "createGroup succeed, now you are the GO ")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "createGroup failed, reasonCode : $reasonCode")
            }
        })
    }

    fun removeGroup() {
        mManager?.removeGroup(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "removeGroup succeed")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "removeGroup failed, reasonCode : $reasonCode")
            }
        })
    }

    fun startRequestPeers() {
        mManager?.requestPeers(mChannel) { peers: WifiP2pDeviceList? ->
            SosLog.d(TAG, "requestPeers \n peers : \n$peers\n\n")
            val deviceList = peers?.deviceList ?: return@requestPeers
            for (device in deviceList) {
                if (!mWifiP2pDevice.contains(device)) {
                    mWifiP2pDevice.add(device)
                }
            }
            if (mWifiP2pDevice.isNotEmpty()) {
                for (device in mWifiP2pDevice) {
                    if (!deviceList.contains(device)) {
                        mWifiP2pDevice.remove(device)
                    }
                }
            }

            devListUpdatedCallback?.updateDevList(mWifiP2pDevice)
            SosLog.d(TAG, "mWifiP2pDevice.size : " + mWifiP2pDevice.size)
        }
    }

    fun stopRequestPeers() {
        mManager?.stopPeerDiscovery(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "stopRequestPeers succeed")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "stopRequestPeers failed, reasonCode : $reasonCode")
            }
        })
    }

    fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.groupOwnerIntent = MAX_GROUP_OWNER_INTENT
        mManager?.connect(mChannel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "connect succeed")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "connect failed, reasonCode : $reasonCode")
            }
        })
    }

    fun cancelConnect() {
        mManager?.cancelConnect(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                SosLog.d(TAG, "cancelConnect succeed")
            }

            override fun onFailure(reasonCode: Int) {
                SosLog.w(TAG, "cancelConnect failed, reasonCode : $reasonCode")
            }
        })
    }

    fun requestGroupInfo() {
        mManager?.requestGroupInfo(mChannel) { group ->
            mWifiP2pGroup = group
        }
    }

    fun requestConnectionInfo() {
        mManager?.requestConnectionInfo(mChannel) { info ->
            SosLog.d(TAG, "groupOwnerAddress : ${info?.groupOwnerAddress}")
            SosLog.d(TAG, "isGroupOwner : ${info?.isGroupOwner}")
            SosLog.d(TAG, "groupFormed : ${info?.groupFormed}")
            mWifiP2pInfo = info
        }
    }
}
