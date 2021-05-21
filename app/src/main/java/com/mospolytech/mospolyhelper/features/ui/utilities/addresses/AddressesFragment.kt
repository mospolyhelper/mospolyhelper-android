package com.mospolytech.mospolyhelper.features.ui.utilities.addresses

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.FragmentAddressesBinding
import com.mospolytech.mospolyhelper.domain.addresses.model.AddressMap
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressesFragment : Fragment(R.layout.fragment_addresses) {

    private val viewModel by viewModel<AddressesViewModel>()
    private val viewBinding by viewBinding(FragmentAddressesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.addressesUpdate.setOnRefreshListener { viewModel.refresh() }

        viewBinding.viewpagerAddresses.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                viewBinding.addressesUpdate.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
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
                viewBinding.addressesUpdate.isRefreshing = false
            }
        }

        this.viewModel.setUpAddresses()
    }

    private fun setUpBuildings(addressMap: AddressMap?) {
        if (addressMap == null) return
        viewBinding.viewpagerAddresses.adapter = AddressesPageAdapter(addressMap)
        TabLayoutMediator(viewBinding.tablayoutAddresses, viewBinding.viewpagerAddresses) { tab, position->
            tab.text = addressMap.entries.toList()[position].key
        }.attach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
}
