package com.example.mynotifications

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotifications.databinding.ActivityFilterBinding


class FilterActivity : AppCompatActivity() {
    private lateinit var notiAdapter: NotificationAdapter
    private lateinit var installedAppList: MutableList<NotificationData>
    private lateinit var bView: ActivityFilterBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bView = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(bView.root)

        bView.installedAppsRecycler.setHasFixedSize(true)
        bView.installedAppsRecycler.layoutManager = LinearLayoutManager(this)

        val appList = mutableListOf<String>()
        //val list2 = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        val list2 = packageManager.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(
            PackageManager.GET_META_DATA.toLong()
        ))
        for (i in list2.indices) {
            val packageInfo = list2[i]
            //if (packageInfo!!.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
            //    val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            //    appList.add(appName)
            //}
        }

        //notiAdapter = InstalledAppAdapter(notiList)
        bView.installedAppsRecycler.adapter = notiAdapter
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}