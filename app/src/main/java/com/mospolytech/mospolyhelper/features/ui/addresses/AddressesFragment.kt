package com.mospolytech.mospolyhelper.features.ui.addresses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mospolytech.mospolyhelper.NavGraphDirections
import com.mospolytech.mospolyhelper.features.ui.main.MainActivity

import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import com.mospolytech.mospolyhelper.utils.safe
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressesFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var addressesViewPager: ViewPager2
    private lateinit var addressesTabLayout: TabLayout

    private val viewModel by viewModel<AddressesViewModel>()

    private fun setUpBuildings(addressMap: AddressMap?) {
        if (addressMap == null) return
        addressesViewPager.adapter = AddressesPageAdapter(addressMap)
        TabLayoutMediator(addressesTabLayout, addressesViewPager) { tab, position->
            tab.text = addressMap.entries.toList()[position].key
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_addresses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressesViewPager = view.findViewById(R.id.viewpager_addresses)
        addressesTabLayout = view.findViewById(R.id.tablayout_addresses)
        swipeRefreshLayout = view.findViewById(R.id.addresses_update)


        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }

        val bottomAppBar = view.findViewById<BottomAppBar>(R.id.bottomAppBar)
        (activity as MainActivity).setSupportActionBar(bottomAppBar)
        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
        bottomAppBar.setNavigationOnClickListener {
            findNavController().safe {
                navigate(NavGraphDirections.actionGlobalMainMenuFragment())
            }
        }

        addressesViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                swipeRefreshLayout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }
        })


//        addressesTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
//            if (checkedId != View.NO_ID) {
//                viewModel.addressesType.value = group.findViewById<Chip>(checkedId).text.toString()
//            }
//        }

        lifecycleScope.launchWhenResumed {
            viewModel.addressesType.collect {
//                val type = (recyclerView.adapter as? AddressesAdapter)?.type
//                if (type != null && type != it) {
//                    setUpBuildings(viewModel.addresses.value!!)
//                }
            }
        }


        lifecycleScope.launchWhenResumed {
            viewModel.addresses.collect {
                if (it == null) return@collect
//                val prevType = (recyclerView.adapter as? AddressesAdapter)?.type
//                if (prevType != null && it.containsKey(prevType)) {
//                    viewModel.addressesType.value = prevType
//                } else {
//                    viewModel.addressesType.value = it.keys.first()
//                }
//                addressesTypeChipGroup.isSingleSelection = false
//                addressesTypeChipGroup.removeAllViews()
//                var checkedId = -1
//                for (type in it.keys.withIndex()) {
//                    val chip = createChip(requireContext(), type.value, type.index + 1)
//                    if (type.value == viewModel.addressesType.value) {
//                        checkedId = chip.id
//                    }
//                    addressesTypeChipGroup.addView(chip)
//                }
//                try {
//                    addressesTypeChipGroup.check(checkedId)
//                    addressesTypeChipGroup.isSingleSelection = true
//                    addressesTypeChipGroup.isSelectionRequired = true
//                } catch (e: Exception) {
//                    val q = 1
//                    val r = q + 1
//                }
                setUpBuildings(it)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        this.viewModel.setUpAddresses()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}
