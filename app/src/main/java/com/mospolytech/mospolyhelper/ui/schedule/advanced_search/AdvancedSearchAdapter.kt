package com.mospolytech.mospolyhelper.ui.schedule.advanced_search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.mospolytech.mospolyhelper.R
import java.util.*
import kotlin.collections.ArrayList

// TODO: add id to viewHolder
class AdvancedSearchAdapter(var filter: AdvancedSearchFilter)
    : RecyclerView.Adapter<AdvancedSearchAdapter.ViewHolder>() {

    var dataSet: List<Int> = filter.getFiltered("")

    private val allCheckedChanged = mutableListOf<(Boolean) -> Unit>()

    override fun getItemCount() = dataSet.size

    fun isAllChecked() = filter.isAllChecked(dataSet)

    fun addAllCheckedChanged(block: (Boolean) -> Unit) {
        allCheckedChanged.add(block)
    }

    fun updateTemplate(template: String) {
        dataSet = filter.getFiltered(template)
        notifyDataSetChanged()
        allCheckedChanged.forEach {
            it(isAllChecked())
        }
    }

    fun setCheckAll(flag: Boolean) {
        for (i in this.dataSet.indices) {
            filter.setChecked(dataSet[i], flag)
        }
        notifyDataSetChanged()
    }

    fun checkBoxChanged(position: Int, isChecked: Boolean) {
        filter.setChecked(dataSet[position], isChecked)
        allCheckedChanged.forEach {
            it(isAllChecked())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_advanced_search, parent, false);
        return ViewHolder(view, this::checkBoxChanged)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.checkBox.text = filter.getValue(dataSet[position])
        viewHolder.checkBox.isChecked = filter.isChecked(dataSet[position])
    }

    class ViewHolder(view: View, checkedChanged: (Int, Boolean) -> Unit) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        init {
            this.checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkedChanged(layoutPosition, isChecked)
            }
        }
    }
}
// TODO checkedIndexes: ObservableList
class AdvancedFilter(
    val originDataSet: List<String>,
    val checkedList: MutableList<Int>
) : AdvancedSearchFilter {
    var checkedArray = mutableListOf<Boolean>()
    var normalized = mutableListOf<String>()
    var  checkedIndexes = mutableListOf<Int>()

    override fun isAllChecked(localDataSet: List<Int>): Boolean {
        if (localDataSet.size > checkedIndexes.size) {
            return false
        }
        for (index in localDataSet) {
            if (!isChecked(index)) {
                return false
            }
        }
        return true
    }

    init {   // TODO Right here
        this.normalized = MutableList(originDataSet.size) { idx ->
            String(
                originDataSet[idx].filter { it.isLetterOrDigit() }.toCharArray()
            )
        }


        checkedArray = MutableList(originDataSet.size) { false }
        checkedIndexes = checkedList
        for (value in checkedList) {
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
            checkedIndexes.add(index)
        else
            checkedIndexes.remove(index)
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


    fun buildRegex(str: String): Regex {
        val str = Regex.escape(str)
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
    val originDataSet: List<String>,
    val checkedList: ObservableList<Int>
) : AdvancedSearchFilter {
    var checkedArray = mutableListOf<Boolean>()

    override fun isAllChecked(localDataSet: List<Int>): Boolean {
        if (localDataSet.size > checkedList.size) {
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
        checkedArray = MutableList(originDataSet.size) { false }
        for (value in checkedList) {
            checkedArray[value] = true
        }
    }

    override fun getValue(index: Int) = originDataSet[index]

    override fun isChecked(index: Int) = checkedArray[index]

    override fun setChecked(index: Int, isChecked: Boolean) {
        if (checkedArray[index] == isChecked) {
            return
        }
        this.checkedArray[index] = isChecked
        if (isChecked) {
            checkedList.add(index)
        } else {
            checkedList.remove(index)
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
        var newList = ArrayList<Int>(capacity)
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