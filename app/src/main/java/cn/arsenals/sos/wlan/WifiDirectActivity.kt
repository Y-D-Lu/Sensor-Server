package cn.arsenals.sos.wlan

import android.net.wifi.p2p.WifiP2pDevice
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import cn.arsenals.sos.util.SosLog
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class WifiDirectActivity : AppCompatActivity(), WifiDirectMgr.DeviceListUpdated {
    companion object {
        private const val TAG = "WifiDirectActivity"
    }

    lateinit var mListView: ListView

    var devNameList: MutableList<String> = mutableListOf()
    var devList: MutableList<WifiP2pDevice> = mutableListOf()

    override fun updateDevList(wifiP2pDevice: MutableList<WifiP2pDevice>) {
        devList = wifiP2pDevice
        SosLog.d(TAG, "receive updateDevList callback, devList size : " + devList.size)
        devNameList.clear()
        for (dev in devList) {
            devNameList.add(dev.deviceName)
        }
        updateListView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WifiDirectActivityUI().setContentView(this)

        WifiDirectMgr.registerDevListUpdatedCallback(this)
        WifiDirectMgr.init()
    }

    override fun onResume() {
        super.onResume()
        WifiDirectMgr.registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        WifiDirectMgr.unregisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        WifiDirectMgr.unregisterDevListUpdatedCallback()
        WifiDirectMgr.uninit()
    }

    fun onDiscoverPeersClicked() {
        SosLog.d(TAG, "onDiscoverPeersClicked")
        WifiDirectMgr.discoverPeers()
    }

    fun onCreateGroupClicked() {
        SosLog.d(TAG, "onCreateGroupClicked")
        WifiDirectMgr.createGroup()

    }

    private fun updateListView() {
        SosLog.d(TAG, "updateListView")
        mListView.invalidateViews()
    }
}

class WifiDirectActivityUI : AnkoComponent<WifiDirectActivity> {
    companion object {
        private const val TAG = "WifiDirectActivityUI"
    }

    override fun createView(ui: AnkoContext<WifiDirectActivity>) = with(ui) {
        verticalLayout {
            button("discover peers") {
                onClick {
                    ui.owner.onDiscoverPeersClicked()
                    ctx.toast("start discovering peers!")
                }
            }
            button("create group") {
                onClick {
                    ui.owner.onCreateGroupClicked()
                    ctx.toast("create group!")
                }
            }
            ui.owner.mListView = listView {
                val devNameList = ui.owner.devNameList
                adapter = ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, devNameList)
                onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val dev = ui.owner.devList[position]
                    SosLog.d(TAG, "click item$position deviceName : ${dev.deviceName}")
                    alert("deviceAddress : ${dev.deviceAddress}" +
                            "\nprimaryDeviceType : ${dev.primaryDeviceType}",
                            "Device : ${dev.deviceName}") {
                        positiveButton("Connect") {
                            WifiDirectMgr.connect(dev)
                        }
                        negativeButton("Cancel") {}
                    }.show()
                }

            }.lparams(width = matchParent, height = matchParent) {
            }
        }
    }
}
