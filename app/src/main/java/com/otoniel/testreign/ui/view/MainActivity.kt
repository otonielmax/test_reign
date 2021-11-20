package com.otoniel.testreign.ui.view

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.otoniel.testreign.databinding.ActivityMainBinding
import com.otoniel.testreign.ui.adapter.HitsAdapter
import com.otoniel.testreign.ui.viewmodel.HitsViewModel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.otoniel.testreign.R
import com.otoniel.testreign.data.model.HitsModel

import android.net.ConnectivityManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.lang.Exception


class MainActivity : AppCompatActivity(), HitsAdapter.HitsListener {

    private val TAG: String = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    private val hitsViewModel: HitsViewModel by viewModels()
    private var list: MutableList<HitsModel> = mutableListOf()

    // Recycler
    private lateinit var adapter: HitsAdapter
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#f44336")
    private lateinit var deleteIcon: Drawable
    private var intrinsicWidth: Int = 0
    private var intrinsicHeight: Int = 0

    // Permissions
    private val REQUEST_PERMISSION: Int = 1
    private val PERMISSIONS= arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hitsViewModel.hitsModel.observe(this, Observer {
            list = it.toMutableList()
            if (adapter != null) {
                adapter.updateData(it)
            } else {
                adapter = HitsAdapter(it, this, applicationContext)
            }
        })

        hitsViewModel.loading.observe(this, Observer {
            binding.swipeRefresh.isRefreshing = it
        })

        deleteIcon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_delete)!!
        intrinsicWidth = deleteIcon.intrinsicWidth
        intrinsicHeight = deleteIcon.intrinsicHeight
    }

    override fun onStart() {
        super.onStart()

        loadView()
    }

    override fun onResume() {
        super.onResume()

        verifyPermissions()
    }

    private fun loadView() {
        binding.recycler.layoutManager = LinearLayoutManager(this)

        adapter = HitsAdapter(emptyList(), this, applicationContext)
        binding.recycler.adapter = adapter

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Toast.makeText(this@MainActivity, "on Move", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onMove")
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                // Toast.makeText(this@MainActivity, "on Swiped ", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onSwiped")
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                // list.removeAt(position)
                // hitsViewModel.hitsModel.postValue(list)
                hitsViewModel.deleteHits(applicationContext, position)
                adapter.notifyDataSetChanged()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top

                // Draw the red delete background
                background.color = backgroundColor
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                // Calculate position of delete icon
                val iconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
                val iconMargin = (itemHeight - intrinsicHeight) / 2
                val iconLeft = itemView.right - iconMargin - intrinsicWidth!!
                val iconRight = itemView.right - iconMargin
                val iconBottom = iconTop + intrinsicHeight

                // Draw the delete icon
                if (deleteIcon != null) {
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recycler)

        binding.swipeRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            // hitsViewModel.getHits(applicationContext)
            verifyPermissions()
        })
    }

    override fun selectedHitsItem(position: Int) {
        // Toast.makeText(this, "Seleccionado $position", Toast.LENGTH_LONG).show()
        val intent = Intent(this@MainActivity, ShowWebViewActivity::class.java)
        intent.putExtra("url", list[position].story_url)
        startActivity(intent)
    }

    fun verifyPermissions() {
        try {
            val accessNetworkState =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)

            if (accessNetworkState != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_PERMISSION
                )
            } else {
                getData()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                getData()
            } else {
                // Permission Denied
                Log.e(TAG, "Permission denied")
            }
            else -> {}
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getData() {
        if (isNetEnabled() && isOnlineNet() == true) {
            hitsViewModel.getHits(applicationContext)
        } else {
            Toast.makeText(applicationContext, "Sin conexion a internet", Toast.LENGTH_LONG).show()
            hitsViewModel.getHitsLocal(applicationContext)
        }
    }

    private fun isNetEnabled(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val actNetInfo = connectivityManager.activeNetworkInfo
        return actNetInfo != null && actNetInfo.isConnected
    }

    fun isOnlineNet(): Boolean? {
        try {
            val p =
                Runtime.getRuntime().exec("ping -c 1 www.google.es")
            val `val` = p.waitFor()
            return `val` == 0
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return false
    }
}