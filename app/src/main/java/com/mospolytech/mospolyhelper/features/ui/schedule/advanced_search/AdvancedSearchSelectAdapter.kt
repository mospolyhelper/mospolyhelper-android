package com.mospolytech.mospolyhelper.features.ui.schedule.advanced_search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mospolytech.mospolyhelper.R
import com.mospolytech.mospolyhelper.databinding.ItemAdvancedSearchBinding
import com.mospolytech.mospolyhelper.utils.Action1
import com.mospolytech.mospolyhelper.utils.Event1

// TODO: add id to viewHolder
class AdvancedSearchAdapter(
    var filter: AdvancedSearchFilter
    ) : RecyclerView.Adapter<AdvancedSearchAdapter.ViewHolder>() {

    private var dataSet: List<Int> = filter.getFiltered("")

    val allCheckedChanged: Event1<Boolean> = Action1()

    override fun getItemCount() = dataSet.size

    private fun isAllChecked() = filter.isAllChecked(dataSet)


    fun updateTemplate(template: String) {
        dataSet = filter.getFiltered(template)
        notifyDataSetChanged()
        val isAllChecked = isAllChecked()
        (allCheckedChanged as Action1).invoke(isAllChecked)
    }

    fun setCheckAll(flag: Boolean) {
        for (i in this.dataSet.indices) {
            filter.setChecked(dataSet[i], flag)
        }
        notifyDataSetChanged()
    }

    private fun checkBoxChanged(position: Int, isChecked: Boolean) {
        filter.setChecked(dataSet[position], isChecked)
        val isAllChecked = isAllChecked()
        (allCheckedChanged as Action1).invoke(isAllChecked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_advanced_search, parent, false);
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(
            filter.getValue(dataSet[position]),
            filter.isChecked(dataSet[position]),
            ::checkBoxChanged
        )
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding by viewBinding(ItemAdvancedSearchBinding::bind)

        fun bind(text: String, isChecked: Boolean, checkedChanged: (Int, Boolean) -> Unit) {
            viewBinding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkedChanged(bindingAdapterPosition, isChecked)
            }
            viewBinding.checkBox.text = text
            viewBinding.checkBox.isChecked = isChecked
        }
    }
}

class AdvancedFilter(
    private val originDataSet: List<String>,
    private val checkedIndices: ObservableList<Int>
) : AdvancedSearchFilter {
    private var checkedArray = mutableListOf<Boolean>()
    private var normalized = mutableListOf<String>()

    override fun isAllChecked(localDataSet: List<Int>): Boolean {
        if (localDataSet.size > checkedIndices.size) {
            return false
        }
        for (index in localDataSet) {
            if (!isChecked(index)) {
                return false
            }
        }
        return true
    }

    init {
        this.normalized = MutableList(originDataSet.size) { idx ->
            String(
                originDataSet[idx].filter { it.isLetterOrDigit() }.toCharArray()
            )
        }

        checkedArray = MutableList(originDataSet.size) { false }
        for (value in checkedIndices) {
            checkedArray[value] = true
        }
    }

    override fun getValue(index: Int) = originDataSet[index]

    override fun isChecked(index: Int) = checkedArray[index]

    override fun setChecked(index: Int, isChecked: Boolean) {
        if (checkedArray[index] == isChecked) {
            return
        }
        checkedArray[index] = isChecked
        if (isChecked)
            checkedIndices.add(index)
        else
            checkedIndices.remove(index)
    }

    override fun getFiltered(template: String): List<Int> {
        if (template.isEmpty() || originDataSet.isEmpty()) {
            val array = MutableList(originDataSet.size) { 0 }
            var j = 0
            for (i in array.indices) {
                if (isChecked(i)) {
                    array[i] = array[j]
                    array[j] = i
                    j++
                } else {
                    array[i] = i
                }
            }
            return array
        }
        val templateRegex = buildRegex(template)

        var capacity = originDataSet.size / 4 / template.length
        if (capacity < 4) {
            capacity = 4
        }
        val newList = ArrayList<Int>(capacity)
        var j = 0
        for (i in originDataSet.indices) {
            if (templateRegex.containsMatchIn(this.normalized[i])) {
                newList.add(i)
                if (isChecked(newList[newList.lastIndex])) {
                    val buf = newList[j]
                    newList[j] = newList[newList.lastIndex]
                    newList[newList.lastIndex] = buf
                    j++
                }
            }
        }
        return newList
    }


    private fun buildRegex(str: String): Regex {
        val str = Regex.escapeReplacement(str)
        val res = ArrayList<Char>(str.length)
        var i = 0
        while (i < str.length && !str[i].isLetterOrDigit()) {
            i++
        }
        while (i in i until str.length) {
            if (str[i].isLetterOrDigit()) {
                res.add(str[i])
                if (i + 1 < str.length && str[i].isUpperCase() && str[i + 1].isUpperCase()) {
                    res.add('.')
                    res.add('*')
                    res.add('?')
                }
            } else {
                while (++i < str.length && !str[i].isLetterOrDigit())
                { }
                i--
                res.add('.')
                res.add('*')
                res.add('?')
            }
            i++
        }
        if (res[str.lastIndex] != '?') {
            res.add('.');
            res.add('*');
            res.add('?');
        }
        return Regex(String(res.toCharArray()), RegexOption.IGNORE_CASE)
    }
}

class SimpleFilter(
    private val originDataSet: List<String>,
    private val checkedIndices: ObservableList<Int>
) : AdvancedSearchFilter {
    private var checkedArray = mutableListOf<Boolean>()

    init {
        checkedArray = MutableList(originDataSet.size) { false }
        for (value in checkedIndices) {
            checkedArray[value] = true
        }
    }

    override fun isAllChecked(localDataSet: List<Int>): Boolean {
        if (localDataSet.size > checkedIndices.size) {
            return false
        }
        for (index in localDataSet) {
            if (!isChecked(index)) {
                return false
            }
        }
        return true
    }

    override fun getValue(index: Int) = originDataSet[index]

    override fun isChecked(index: Int) = checkedArray[index]

    override fun setChecked(index: Int, isChecked: Boolean) {
        if (checkedArray[index] == isChecked) {
            return
        }
        this.checkedArray[index] = isChecked
        if (isChecked) {
            checkedIndices.add(index)
        } else {
            checkedIndices.remove(index)
        }
    }

    override fun getFiltered(template: String): List<Int> {
        if (template.isEmpty() || originDataSet.isEmpty()) {
            val array = MutableList(originDataSet.size) { 0 }
            var j = 0
            for (i in array.indices) {
                if (isChecked(i)) {
                    array[i] = array[j]
                    array[j] = i
                    j++
                } else {
                    array[i] = i
                }
            }
            return array
        }


        var capacity = this.originDataSet.size / 4 / template.length
        if (capacity < 4) {
            capacity = 4
        }
        val newList = ArrayList<Int>(capacity)
        var j = 0
        for (i in originDataSet.indices) {
            if (this.originDataSet[i].contains(template, true)) {
                newList.add(i)
                if (isChecked(newList[newList.lastIndex])) {
                    val buf = newList[j]
                    newList[j] = newList[newList.lastIndex]
                    newList[newList.lastIndex] = buf
                    j++
                }
            }
        }
        return newList
    }
}

interface AdvancedSearchFilter {
    fun isAllChecked(localDataSet: List<Int>): Boolean
    fun getValue(index: Int): String
    fun isChecked(index: Int): Boolean
    fun setChecked(index: Int, isChecked: Boolean)
    fun getFiltered(template: String): List<Int>
}