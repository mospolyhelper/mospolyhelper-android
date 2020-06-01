package com.mospolytech.mospolyhelper.ui.addresses

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mospolytech.mospolyhelper.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.repository.models.Addresses
import com.mospolytech.mospolyhelper.ui.common.FragmentBase
import com.mospolytech.mospolyhelper.ui.common.Fragments

class AddressesFragment : FragmentBase(Fragments.Addresses) {

    companion object {
        fun newInstance() = AddressesFragment()
    }

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var toolbar: Toolbar
    var accumulator = 0

    private val viewModel by viewModels<AddressesViewModel>()

    fun setUpBuildings(buildings: Addresses?) {
        recyclerView.adapter = if (buildings == null) null else AddressesAdapter(buildings)
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.addresses_title)
        (activity as MainActivity).setSupportActionBar(this.toolbar)
        //(this.Activity as MainView).SupportActionBar.SetDisplayShowTitleEnabled(false)
        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(activity, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        if (this.recyclerView.adapter == null) {
            setUpBuildings(viewModel.addresses.value)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addresses, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_addresses)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val scale = view.context.resources.displayMetrics.density
        recyclerView.addItemDecoration(AddressesAdapter.ItemDecoration((8 * scale + 0.5f).toInt()))
        val dp8 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, view.context.resources.displayMetrics)
        val dp32 = dp8 * 4
        this.recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (recyclerView.canScrollVertically(-1)) {
                accumulator -= oldScrollY
                toolbar.elevation =
                    if(accumulator > dp32) dp8 else this.accumulator / 4f
            } else {
                toolbar.elevation = 0f
                accumulator = 0
            }
        }
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.addresses_update)
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.viewModel.setUpAddresses()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
        viewModel.addresses.observe(viewLifecycleOwner, Observer<Addresses?> { setUpBuildings(it) }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
