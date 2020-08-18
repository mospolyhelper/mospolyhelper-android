package com.mospolytech.mospolyhelper.features.ui.addresses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.mospolytech.mospolyhelper.NavGraphDirections
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.addresses.model.Addresses
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressesFragment : Fragment() {

    companion object {
        fun newInstance() = AddressesFragment()
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var addressesTypeSpinner: Spinner

    private val viewModel by viewModel<AddressesViewModel>()

    private fun setUpBuildings(buildings: Addresses?) {
        recyclerView.adapter = if (buildings == null) null else AddressesAdapter(buildings, viewModel.addressesType.value!!)
        recyclerView.adapter?.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressesTypeSpinner = view.findViewById(R.id.spinner_addresses)

        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        bottomAppBar.setNavigationOnClickListener {
            findNavController().navigate(NavGraphDirections.actionGlobalMainMenuFragment())
        }

        if (this.recyclerView.adapter == null) {
            setUpBuildings(viewModel.addresses.value)
        }

        addressesTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view != null) {
                    viewModel.addressesType.value = (view as TextView).text as String
                }
            }
        }

        viewModel.addressesType.observe(viewLifecycleOwner, Observer {
            val type = (recyclerView.adapter as? AddressesAdapter)?.type
            if (type != null && type != it) {
                setUpBuildings(viewModel.addresses.value!!)
            }
        })

        viewModel.addresses.observe(viewLifecycleOwner, Observer<Addresses?> {
            addressesTypeSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it!!.keys.toTypedArray())
            val prevType = (recyclerView.adapter as? AddressesAdapter)?.type
            if (prevType != null && it!!.containsKey(prevType)) {
                viewModel.addressesType.value = prevType
            } else {
                viewModel.addressesType.value = it!!.keys.first()
            }
            setUpBuildings(it)
        })
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
}
